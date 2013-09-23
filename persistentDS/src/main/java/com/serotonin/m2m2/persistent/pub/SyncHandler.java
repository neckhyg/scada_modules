package com.serotonin.m2m2.persistent.pub;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.db.dao.PointValueDao;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.persistent.common.Packet;
import com.serotonin.m2m2.persistent.common.PacketType;
import com.serotonin.m2m2.rt.publish.PublishedPointRT;
import com.serotonin.m2m2.util.DateUtils;
import com.serotonin.m2m2.util.log.ProcessLog;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.timer.sync.Synchronizer;
import com.serotonin.util.queue.ByteQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class SyncHandler
  implements Runnable
{
  private static final String START_TIMES_KEY = "startTimes";
  final PersistentSendThread sendThread;
  final ProcessLog log;
  final PointValueDao pointValueDao = new PointValueDao();
  long cutoff;
  int recordsSynced;
  final Set<Integer> targetOvercountPoints = new HashSet();
  int responseErrors;
  Map<Integer, Long> startTimes;
  int requestsSent;
  int pointsCompleted;
  volatile boolean cancelled;
  List<PublishedPointRT<PersistentPointVO>> pointsToCheck;
  PointSync[] pointSyncs;

  SyncHandler(PersistentSendThread sendThread)
  {
    this.sendThread = sendThread;
    this.log = sendThread.log;
  }

  public int getPointsCompleted() {
    return this.pointsCompleted;
  }

  public int getRequestsSent() {
    return this.requestsSent;
  }

  synchronized void cancel() {
    this.cancelled = true;

    if (this.pointSyncs != null)
      for (PointSync pointSync : this.pointSyncs)
        synchronized (pointSync) {
          pointSync.notify();
        }
  }

  public void run()
  {
    this.startTimes = ((Map)this.sendThread.publisher.getPersistentData("startTimes"));
    if (this.startTimes == null) {
      this.startTimes = new HashMap();
    }
    long start = System.currentTimeMillis();
    try
    {
      this.cutoff = (System.currentTimeMillis() - 7200000L);

      this.log.info("Sync handler running with cutoff: " + this.cutoff);

      this.pointsToCheck = new ArrayList(this.sendThread.publisher.getPointRTs());

      int tasks = 10;
      if (this.pointsToCheck.size() < tasks) {
        tasks = this.pointsToCheck.size();
      }
      Synchronizer sync = new Synchronizer();
      this.pointSyncs = new PointSync[tasks];
      for (int i = 0; i < tasks; i++) {
        this.pointSyncs[i] = new PointSync(i);
        sync.addTask(this.pointSyncs[i]);
      }

      if (!this.cancelled) {
        sync.executeAndWait(Common.timer);
      }

      saveStartTimes();
    }
    finally {
      this.sendThread.endSyncHandler();
      this.log.info("Sync handler run completed");
    }

    TranslatableMessage lm = new TranslatableMessage("event.pb.persistent.syncCompleted.details", new Object[] { Integer.valueOf(this.sendThread.publisher.getPointRTs().size()), Integer.valueOf(this.requestsSent), Integer.valueOf(this.recordsSynced), Integer.valueOf(this.targetOvercountPoints.size()), Integer.valueOf(this.responseErrors), DateUtils.getDuration(System.currentTimeMillis() - start) });

    this.sendThread.publisher.raiseSyncCompletionEvent(lm);
  }

  void saveStartTimes()
  {
    synchronized (this.startTimes) {
      this.sendThread.publisher.setPersistentData("startTimes", this.startTimes);
    }
  }

  void responseReceived(Packet packet) {
    int responseId = packet.getPayload().popU3B();
    int syncId = responseId >> 16 & 0xFF;

    if ((syncId < 0) || (syncId >= this.pointSyncs.length))
      this.log.info("Invalid sync id " + syncId);
    else
      this.pointSyncs[syncId].responseReceived(responseId & 0xFFFF, packet.popLong());
  }

  void sendRequest(int id, int requestId, int pointIndex, long from, long to) {
    int realRequestId = id << 16 | requestId;

    ByteQueue queue = new ByteQueue();
    queue.pushU3B(realRequestId);
    queue.pushU2B(pointIndex);
    Packet.pushLong(queue, from);
    Packet.pushLong(queue, to);

    Packet packet = new Packet(PacketType.RANGE_COUNT, queue);
    this.sendThread.sendPacket(packet); } 
  class PointSync implements Runnable { private final int id;
    private String xid;
    private long diff;
    private int nextRequestId;
    private volatile int responseId = -1;
    private volatile long responseCount;

    public PointSync(int id) { this.id = id;
    }

    public void run()
    {
      SyncHandler.this.log.info("PointSync " + this.id + " started");

      while ((!SyncHandler.this.cancelled) && (SyncHandler.this.sendThread.isConnected()))
      {
        PublishedPointRT point;
        synchronized (SyncHandler.this.pointsToCheck) {
          if (SyncHandler.this.pointsToCheck.isEmpty()) {
            break;
          }
          point = (PublishedPointRT)SyncHandler.this.pointsToCheck.remove(0);
        }

        if (point.isPointEnabled()) {
          checkPoint((PersistentPointVO)point.getVo(), SyncHandler.this.cutoff);
        }
      }
      SyncHandler.this.log.info("PointSync " + this.id + " completed");
    }

    void checkPoint(PersistentPointVO point, long to) {
      this.xid = point.getVo().getXid();
      Long from;
      synchronized (SyncHandler.this.startTimes) {
        from = (Long)SyncHandler.this.startTimes.get(Integer.valueOf(point.getDataPointId()));
      }
      if (from == null) {
        from = Long.valueOf(SyncHandler.this.pointValueDao.getInceptionDate(point.getDataPointId()));
        SyncHandler.this.log.info("PointSync(" + this.xid + ") starting from inception at " + from);
        updatePointStartTime(point, from.longValue());
      }
      else {
        SyncHandler.this.log.info("PointSync(" + this.xid + ") starting from saved time at " + from);
      }
      if (from.longValue() == -1L)
      {
        SyncHandler.this.log.info("PointSync(" + this.xid + ") no values for this point");
        return;
      }

      if (from.longValue() > to)
      {
        SyncHandler.this.log.info("PointSync(" + this.xid + ") no values in range for this point");
        return;
      }

      try
      {
        checkRange(point, from.longValue(), to, 0);
        SyncHandler.this.saveStartTimes();
      }
      catch (PointSyncException e) {
        SyncHandler.this.log.error("PointSync(" + this.xid + ") " + e.getMessage());
      }

      SyncHandler.this.log.info("PointSync(" + this.xid + ") completed to " + to);
      SyncHandler.this.pointsCompleted += 1;
    }

    void checkRange(PersistentPointVO point, long from, long to, int level)
      throws SyncHandler.PointSync.PointSyncException
    {
      int attempts = 3;

      while (attempts > 0) {
        try {
          if (checkRangeImpl(point, from, to, level))
          {
            updatePointStartTime(point, to + 1L);
          }
        }
        catch (PointSyncResponseException e) {
          SyncHandler.this.responseErrors += 1;
          SyncHandler.this.log.warn("PointSync(" + this.xid + "/" + level + "/" + e.getRequestId() + ") " + e.getMessage());

          attempts--;
        }if (attempts <= 0)
          throw new PointSyncException("PointSync(" + this.xid + "/" + level + ") used all attempts to check range from=" + from + ", to=" + to);
      }
    }

    boolean checkRangeImpl(PersistentPointVO point, long from, long to, int level)
      throws SyncHandler.PointSync.PointSyncException, SyncHandler.PointSync.PointSyncResponseException
    {
      if ((SyncHandler.this.cancelled) || (!SyncHandler.this.sendThread.isConnected()))
      {
        return false;
      }

      this.responseId = -1;
      int requestId = this.nextRequestId++;
      if (this.nextRequestId > 65535) {
        this.nextRequestId = 0;
      }
      long start = System.currentTimeMillis();
      SyncHandler.this.sendRequest(this.id, requestId, point.getIndex(), from, to);
      SyncHandler.this.requestsSent += 1;

      long count = SyncHandler.this.pointValueDao.dateRangeCount(point.getDataPointId(), from, to);
      if (SyncHandler.this.log.isDebugEnabled()) {
        SyncHandler.this.log.debug("PointSync(" + this.xid + "/" + level + "/" + requestId + ") locally counted " + count + " rows in " + (System.currentTimeMillis() - start) + "ms");
      }

      synchronized (this)
      {
        if (SyncHandler.this.cancelled) {
          return false;
        }

        if (this.responseId == -1) {
          try
          {
            wait(1200000L);
          }
          catch (InterruptedException e)
          {
          }

        }

        if (SyncHandler.this.cancelled) {
          return false;
        }
      }

      if (this.responseId != requestId) {
        if (this.responseId == -1)
          throw new PointSyncResponseException("no response received for request", requestId);
        throw new PointSyncResponseException("request/response id mismatch: response=" + this.responseId, requestId);
      }

      if (SyncHandler.this.log.isDebugEnabled()) {
        SyncHandler.this.log.debug("PointSync(" + this.xid + "/" + level + "/" + requestId + ") remotely counted " + this.responseCount + " rows in " + (System.currentTimeMillis() - start) + "ms");
      }

      if (this.responseCount == -1L)
      {
        throw new PointSyncException("point in unavailable");
      }
      if (count == this.responseCount)
      {
        return true;
      }
      if (level == 0) {
        this.diff = (count - this.responseCount);
        SyncHandler.this.log.info("PointSync(" + this.xid + "/" + level + "/" + requestId + ") local=" + count + ", remote=" + this.responseCount + ", remaining=" + this.diff);
      }

      if (this.responseCount == 0L) {
        this.diff -= count;

        List pvts = SyncHandler.this.pointValueDao.getPointValuesBetween(point.getDataPointId(), from, to + 1L);
        if (SyncHandler.this.log.isInfoEnabled()) {
          SyncHandler.this.log.info("PointSync(" + this.xid + "/" + level + "/" + requestId + ") syncing records: count=" + count + ", queried=" + pvts.size() + ", from=" + from + ", to=" + to + ", remaining=" + this.diff);
        }
        SyncHandler.this.sendThread.publisher.publish(point, pvts);
        SyncHandler.this.recordsSynced += pvts.size();
        return true;
      }

      if (count < this.responseCount)
      {
        SyncHandler.this.targetOvercountPoints.add(Integer.valueOf(point.getDataPointId()));
      }
      if (count == 0L) {
        this.diff += this.responseCount;
        if (SyncHandler.this.log.isInfoEnabled()) {
          SyncHandler.this.log.info("PointSync(" + this.xid + "/" + level + "/" + requestId + ") overcount detected: local=" + count + ", target=" + this.responseCount + ", from=" + from + ", to=" + to + ", remaining=" + this.diff);
        }

        return true;
      }

      if (from == to)
      {
        return true;
      }

      long mid = (to - from >> 1) + from;
      checkRange(point, from, mid, level + 1);
      checkRange(point, mid + 1L, to, level + 1);

      return false;
    }

    private void updatePointStartTime(PersistentPointVO point, long time) {
      synchronized (SyncHandler.this.startTimes) {
        SyncHandler.this.startTimes.put(Integer.valueOf(point.getDataPointId()), Long.valueOf(time));
      }
    }

    void responseReceived(int responseId, long responseCount) {
      synchronized (this) {
        this.responseId = responseId;
        this.responseCount = responseCount;

        notify();
      }
    }

    class PointSyncException extends Exception
    {
      private static final long serialVersionUID = 1L;

      public PointSyncException(String message)
      {
        super();
      }
    }

    class PointSyncResponseException extends Exception
    {
      private static final long serialVersionUID = 1L;
      private final int requestId;

      public PointSyncResponseException(String message, int requestId)
      {
        super();
        this.requestId = requestId;
      }

      public int getRequestId() {
        return this.requestId;
      }
    }
  }
}