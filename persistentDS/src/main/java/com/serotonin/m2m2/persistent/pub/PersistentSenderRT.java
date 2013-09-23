package com.serotonin.m2m2.persistent.pub;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.db.dao.DataPointDao;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.persistent.common.DataPointInfo;
import com.serotonin.m2m2.persistent.common.Packet;
import com.serotonin.m2m2.persistent.common.PacketType;
import com.serotonin.m2m2.persistent.common.PersistentAbortException;
import com.serotonin.m2m2.rt.EventManager;
import com.serotonin.m2m2.rt.RuntimeManager;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.event.type.EventType;
import com.serotonin.m2m2.rt.event.type.PublisherEventType;
import com.serotonin.m2m2.rt.publish.PublishQueue;
import com.serotonin.m2m2.rt.publish.PublishedPointRT;
import com.serotonin.m2m2.rt.publish.PublisherRT;
import com.serotonin.m2m2.util.log.ProcessLog;
import com.serotonin.m2m2.util.log.ProcessLog.LogLevel;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.util.queue.ByteQueue;
import java.util.List;

public class PersistentSenderRT extends PublisherRT<PersistentPointVO>
{
  public static final int CONNECTION_FAILED_EVENT = 11;
  public static final int PROTOCOL_FAILURE_EVENT = 12;
  public static final int CONNECTION_ABORTED_EVENT = 13;
  public static final int CONNECTION_LOST_EVENT = 14;
  public static final int SYNC_COMPLETION_EVENT = 15;
  final EventType connectionFailedEventType = new PublisherEventType(getId(), 11);
  final EventType protocolFailureEventType = new PublisherEventType(getId(), 12);
  final EventType connectionAbortedEventType = new PublisherEventType(getId(), 13);
  final EventType connectionLostEventType = new PublisherEventType(getId(), 14);
  final EventType syncCompletionEventType = new PublisherEventType(getId(), 15);
  final PersistentSenderVO vo;
  final ProcessLog log;
  private PersistentSendThread sendThread;

  public PersistentSenderRT(PersistentSenderVO vo)
  {
    super(vo);
    this.vo = vo;
    this.log = new ProcessLog("PersistentSenderRT-" + vo.getId(), ProcessLog.LogLevel.INFO);
  }

  protected void finalize() throws Throwable
  {
    super.finalize();
    this.log.close();
  }

  PublishQueue<PersistentPointVO> getPublishQueue() {
    return this.queue;
  }

  List<PublishedPointRT<PersistentPointVO>> getPointRTs() {
    return this.pointRTs;
  }

  public int getPointCount() {
    return this.pointRTs.size();
  }

  public int getQueueSize() {
    return this.queue.getSize();
  }

  public boolean isConnected() {
    return this.sendThread.isConnected();
  }

  public int getConnectionPort() {
    return this.sendThread.getConnectionPort();
  }

  public int getConnectingIndex() {
    return this.sendThread.getConnectingIndex();
  }

  public int getPacketsToSend() {
    return this.sendThread.packetsToSend.size();
  }

  public int getSyncStatus() {
    SyncHandler syncHandler = this.sendThread.syncHandler;
    if (syncHandler == null)
      return -1;
    return syncHandler.getPointsCompleted();
  }

  public int getSyncRequestsSent() {
    SyncHandler syncHandler = this.sendThread.syncHandler;
    if (syncHandler == null)
      return -1;
    return syncHandler.getRequestsSent();
  }

  public boolean startSync() {
    return this.sendThread.startSync();
  }

  public int getPacketsSentInInterval() {
    return this.sendThread.getPacketsSentInInterval();
  }

  protected void pointInitialized(PublishedPointRT<PersistentPointVO> rt)
  {
    super.pointInitialized(rt);

    DataPointRT pointRT = Common.runtimeManager.getDataPoint(((PersistentPointVO)rt.getVo()).getDataPointId());
    if (pointRT != null) {
      updatePublishedPointVO((PersistentPointVO)rt.getVo(), pointRT.getVO());

      ByteQueue queue = new ByteQueue();
      queue.pushU2B(((PersistentPointVO)rt.getVo()).getIndex());
      if (this.sendThread.version >= 5)
        Packet.pushDataPointInfo(queue, new DataPointInfo(((PersistentPointVO)rt.getVo()).getVo()));
      else {
        Packet.pushDataPointVO(queue, this.sendThread.version, ((PersistentPointVO)rt.getVo()).getVo(), ((PersistentPointVO)rt.getVo()).getSerializedDataPoint());
      }
      Packet packet = new Packet(PacketType.POINT_UPDATE, queue);
      this.sendThread.sendPacket(packet);
    }
  }

  public void initialize()
  {
    this.log.info("Initializing");

    DataPointDao dataPointDao = new DataPointDao();
    int index = 0;
    for (PersistentPointVO p : this.vo.getPoints()) {
      DataPointVO dpvo = dataPointDao.getDataPoint(p.getDataPointId());
      p.setIndex(index++);
      updatePublishedPointVO(p, dpvo);
    }

    this.sendThread = new PersistentSendThread(this);
    super.initialize(this.sendThread);

    this.log.info("Initialized");
  }

  private void updatePublishedPointVO(PersistentPointVO ppvo, DataPointVO dpvo) {
    ppvo.setVo(dpvo);
    ppvo.setSerializedDataPoint(SerializationHelper.writeObjectToArray(dpvo));
  }

  void raiseConnectionEvent(EventType type, Exception e)
  {
    TranslatableMessage lm;
    if ((e instanceof PersistentAbortException))
      lm = ((PersistentAbortException)e).getTranslatableMessage();
    else {
      lm = new TranslatableMessage("common.default", new Object[] { e.getMessage() });
    }
    raiseConnectionEvent(type, lm);
  }

  void raiseConnectionEvent(EventType type, TranslatableMessage lm) {
    Common.eventManager.raiseEvent(type, System.currentTimeMillis(), true, 2, lm, createEventContext());
  }

  void raiseSyncCompletionEvent(TranslatableMessage lm)
  {
    Common.eventManager.raiseEvent(this.syncCompletionEventType, System.currentTimeMillis(), false, 0, lm, createEventContext());
  }
}