package com.serotonin.m2m2.persistent.pub;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.db.dao.DataPointDao;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.persistent.common.DataPointInfo;
import com.serotonin.m2m2.persistent.common.Packet;
import com.serotonin.m2m2.persistent.common.PacketType;
import com.serotonin.m2m2.persistent.common.PersistentAbortException;
import com.serotonin.m2m2.persistent.common.PersistentProtocolException;
import com.serotonin.m2m2.rt.EventManager;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.dataImage.types.DataValue;
import com.serotonin.m2m2.rt.dataImage.types.ImageValue;
import com.serotonin.m2m2.rt.publish.PublishQueue;
import com.serotonin.m2m2.rt.publish.PublishQueueEntry;
import com.serotonin.m2m2.rt.publish.SendThread;
import com.serotonin.m2m2.util.log.ProcessLog;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.m2m2.vo.hierarchy.PointFolder;
import com.serotonin.m2m2.vo.hierarchy.PointHierarchy;
import com.serotonin.m2m2.vo.hierarchy.PointHierarchyEventDispatcher;
import com.serotonin.m2m2.vo.hierarchy.PointHierarchyListener;
import com.serotonin.timer.CronTimerTrigger;
import com.serotonin.timer.RealTimeTimer;
import com.serotonin.timer.TimerTask;
import com.serotonin.timer.TimerTrigger;
import com.serotonin.util.queue.ByteQueue;
import com.serotonin.util.queue.LongQueue;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.lang3.StringUtils;

class PersistentSendThread extends SendThread
{
  final PersistentSenderRT publisher;
  final ProcessLog log;
  Socket socket;
  int connecting = -1;
  InputStream in;
  OutputStream out;
  ByteQueue writeBuffer = new ByteQueue();
  int version = 5;
  SyncTimer syncTimer;
  SyncHandler syncHandler;
  PointHierarchySync pointHierarchySync;
  final List<Packet> packetsToSend = new CopyOnWriteArrayList();
  private long lastTestPacket;
  private final LongQueue packetSendTimes = new LongQueue();

  private final long packetSendTimeInterval = 300000L;

  public PersistentSendThread(PersistentSenderRT publisher) {
    super("PersistentSenderRT.SendThread");
    this.publisher = publisher;
    this.log = publisher.log;
  }

  public boolean isConnected() {
    return this.socket != null;
  }

  public int getConnectionPort() {
    Socket localSocket = this.socket;
    if (localSocket == null)
      return -1;
    return localSocket.getLocalPort();
  }

  public int getConnectingIndex() {
    return this.connecting;
  }

  public int getPacketsSentInInterval() {
    return this.packetSendTimes.size();
  }

  public void initialize()
  {
    super.initialize();

    if (this.publisher.vo.getSyncType() != 0)
    {
      String pattern;
      if (this.publisher.vo.getSyncType() == 1) {
        pattern = "0 0 1 * * ?";
      }
      else
      {
        if (this.publisher.vo.getSyncType() == 2) {
          pattern = "0 0 1 ? * MON";
        }
        else
        {
          if (this.publisher.vo.getSyncType() == 3)
            pattern = "0 0 1 1 * ?";
          else
            throw new ShouldNeverHappenException("Invalid sync type: " + this.publisher.vo.getSyncType());
        }
      }
      try
      {
        this.syncTimer = new SyncTimer(new CronTimerTrigger(pattern));
      }
      catch (ParseException e) {
        throw new ShouldNeverHappenException(e);
      }

      Common.timer.schedule(this.syncTimer);
    }

    this.pointHierarchySync = new PointHierarchySync();
    PointHierarchyEventDispatcher.addListener(this.pointHierarchySync);
  }

  public void terminate()
  {
    PointHierarchyEventDispatcher.removeListener(this.pointHierarchySync);

    super.terminate();

    if (this.syncTimer != null)
    {
      this.syncTimer.cancel();
    }
  }

  protected void runImpl()
  {
    while (isRunning()) {
      if (this.socket == null) {
        try {
          openConnection();
        }
        catch (IOException e) {
          this.log.warn(e);
          Common.eventManager.returnToNormal(this.publisher.connectionAbortedEventType, System.currentTimeMillis());

          Common.eventManager.returnToNormal(this.publisher.protocolFailureEventType, System.currentTimeMillis());
          this.publisher.raiseConnectionEvent(this.publisher.connectionFailedEventType, e);
          closeConnection(2000);
        }
        catch (PersistentAbortException e) {
          this.log.warn(e.getTranslatableMessage().translate(Common.getTranslations()), e);
          Common.eventManager.returnToNormal(this.publisher.protocolFailureEventType, System.currentTimeMillis());
          this.publisher.raiseConnectionEvent(this.publisher.connectionAbortedEventType, e);
          closeConnection(10000);
        }
        catch (PersistentProtocolException e) {
          this.log.warn(e);
          Common.eventManager.returnToNormal(this.publisher.connectionAbortedEventType, System.currentTimeMillis());

          this.publisher.raiseConnectionEvent(this.publisher.protocolFailureEventType, e);
          closeConnection(60000);
        }

        if (this.socket == null) {
          continue;
        }
        Common.eventManager.returnToNormal(this.publisher.connectionAbortedEventType, System.currentTimeMillis());
        Common.eventManager.returnToNormal(this.publisher.protocolFailureEventType, System.currentTimeMillis());
        Common.eventManager.returnToNormal(this.publisher.connectionLostEventType, System.currentTimeMillis());

        writePointHierarchy();
      }

      PublishQueueEntry entry = this.publisher.getPublishQueue().next();

      if (entry != null) {
        try {
          send(entry);
          this.publisher.getPublishQueue().remove(entry);

          long now = System.currentTimeMillis();
          long cutoff = now - 300000L;
          while ((this.packetSendTimes.size() > 0) && (this.packetSendTimes.peek(0) < cutoff))
            this.packetSendTimes.pop();
          this.packetSendTimes.push(now);
        }
        catch (IOException e) {
          this.publisher.raiseConnectionEvent(this.publisher.connectionLostEventType, e);

          closeConnection(0);
        }
      }
      else if (this.packetsToSend.size() > 0) {
        Packet packet = (Packet)this.packetsToSend.remove(0);
        try {
          Packet.writePacket(this.out, this.version, packet);
        }
        catch (IOException e) {
          this.publisher.raiseConnectionEvent(this.publisher.connectionLostEventType, e);

          closeConnection(0);
        }
      }
      else
      {
        try {
          Packet packet = Packet.readPacketNoBlock(this.in, this.version);
          if (packet != null)
          {
            if (packet.getType() == PacketType.RANGE_COUNT) {
              if (this.syncHandler != null)
                this.syncHandler.responseReceived(packet);
            }
            else
              this.log.error("Unexpected packet type: " + packet.getType());
          }
          else if (this.lastTestPacket + 60000L < System.currentTimeMillis())
          {
            Packet.writePacket(this.out, this.version, PacketType.TEST, Packet.EMPTY);
            this.lastTestPacket = System.currentTimeMillis();
          }
          else
          {
            waitImpl(20L);
          }
        } catch (IOException e) {
          this.publisher.raiseConnectionEvent(this.publisher.connectionLostEventType, e);
          closeConnection(0);
        }
        catch (PersistentAbortException e) {
          this.publisher.raiseConnectionEvent(this.publisher.connectionLostEventType, e.getTranslatableMessage());
          closeConnection(0);
        }
        catch (PersistentProtocolException e) {
          this.publisher.raiseConnectionEvent(this.publisher.connectionLostEventType, e);
          closeConnection(0);
        }
      }
    }

    closeConnection(0);
  }

  void sendPacket(Packet packet) {
    this.packetsToSend.add(packet);
    synchronized (this) {
      notify();
    }
  }

  private void send(PublishQueueEntry<PersistentPointVO> entry)
    throws IOException
  {
    this.writeBuffer.pushU2B(((PersistentPointVO)entry.getVo()).getIndex());
    DataValue value = entry.getPvt().getValue();
    this.writeBuffer.push(value.getDataType());
    switch (entry.getPvt().getValue().getDataType()) {
    case 1:
      this.writeBuffer.push(value.getBooleanValue() ? 1 : 0);
      break;
    case 2:
      this.writeBuffer.pushS4B(value.getIntegerValue());
      break;
    case 3:
      Packet.pushDouble(this.writeBuffer, value.getDoubleValue());
      break;
    case 4:
      Packet.pushString(this.writeBuffer, value.getStringValue());
      break;
    case 5:
      byte[] data;
      try { data = ((ImageValue)value).getImageData();
      } catch (IOException e)
      {
        this.log.warn("Error reading image data", e);

        return;
      }
      this.writeBuffer.pushS4B(((ImageValue)value).getType());
      this.writeBuffer.pushS4B(data.length);
      this.writeBuffer.push(data);
    }

    Packet.pushLong(this.writeBuffer, entry.getPvt().getTime());

    Packet.writePacket(this.out, this.version, PacketType.DATA, this.writeBuffer);
  }

  private void openConnection() throws IOException, PersistentProtocolException, PersistentAbortException {
    this.connecting = 0;
    int receipts = 0;
    try {
      Socket localSocket = new Socket(this.publisher.vo.getHost(), this.publisher.vo.getPort());
      localSocket.setSoTimeout(30000);
      Common.eventManager.returnToNormal(this.publisher.connectionFailedEventType, System.currentTimeMillis());
      this.in = localSocket.getInputStream();
      this.out = new TimedOutputStream(Thread.currentThread().getName(), localSocket.getOutputStream(), Common.timer, 30000L);

      Packet.writePacket(this.out, 1, PacketType.VERSION, new byte[] { (byte)this.version });

      Packet packet = Packet.readPacket(this.in, 1);
      if (packet.getType() != PacketType.VERSION)
        throw new PersistentProtocolException("Expected version, got " + packet.getType());
      this.version = packet.getPayload().popU1B();

      Packet.pushString(this.writeBuffer, this.publisher.vo.getAuthorizationKey());
      Packet.writePacket(this.out, this.version, PacketType.AUTH_KEY, this.writeBuffer);

      packet = Packet.readPacket(this.in, this.version);
      if (packet.getType() != PacketType.AUTH_KEY)
        throw new PersistentProtocolException("Expected auth key, got " + packet.getType());
      if (packet.getPayload().size() != 0) {
        throw new PersistentProtocolException("Expected empty payload");
      }

      String prefix = "";
      if (!StringUtils.isBlank(this.publisher.vo.getXidPrefix())) {
        prefix = this.publisher.vo.getXidPrefix();
      }
      for (PersistentPointVO point : this.publisher.vo.getPoints()) {
        this.connecting += 1;
        DataPointVO vo = point.getVo();
        Packet.pushString(this.writeBuffer, prefix + vo.getXid());
        if (this.version >= 5)
          Packet.pushDataPointInfo(this.writeBuffer, new DataPointInfo(point.getVo()));
        else
          Packet.pushDataPointVO(this.writeBuffer, this.version, point.getVo(), point.getSerializedDataPoint());
        Packet.writePacket(this.out, this.version, PacketType.POINT, this.writeBuffer);

        while (getPointReceipt(false)) {
          receipts++;
        }

      }

      Packet.writePacket(this.out, this.version, PacketType.POINT, Packet.EMPTY);

      while (receipts < this.connecting) {
        getPointReceipt(true);
        receipts++;
      }

      getPointsCompleteResponse();

      this.socket = localSocket;
    }
    finally {
      this.connecting = -1;
    }
  }

  private boolean getPointReceipt(boolean block)
    throws IOException, PersistentAbortException, PersistentProtocolException
  {
    Packet packet;
    if (block) {
      packet = Packet.readPacket(this.in, this.version);
    } else {
      packet = Packet.readPacketNoBlock(this.in, this.version);
      if (packet == null) {
        return false;
      }
    }

    if (packet.getType() != PacketType.POINT) {
      throw new PersistentProtocolException("Expected points, got " + packet.getType());
    }
    if (packet.getPayload().size() == 0) {
      throw new PersistentProtocolException("Expected XID payload");
    }
    return true;
  }

  private void getPointsCompleteResponse() throws IOException, PersistentAbortException, PersistentProtocolException {
    Packet packet = Packet.readPacket(this.in, this.version);

    if (packet.getType() != PacketType.POINT) {
      throw new PersistentProtocolException("Expected points, got " + packet.getType());
    }
    if (packet.getPayload().size() != 0)
      throw new PersistentProtocolException("Expected empty payload");
  }

  private void closeConnection(int sleep) {
    if (this.socket != null)
    {
      try
      {
        this.log.info("Sending close packet");
        Packet.writePacket(this.out, this.version, PacketType.CLOSE, Packet.EMPTY);
        this.socket.close();
      }
      catch (IOException e)
      {
      }
      finally {
        this.socket = null;
        this.in = null;
        this.out = null;
      }
    }

    if (this.out != null) {
      try {
        this.out.close();
      }
      catch (IOException e) {
        this.log.info(e);
      }
    }

    SyncHandler sh = this.syncHandler;
    if (sh != null)
    {
      sh.cancel();
      synchronized (sh) {
        sh.notify();
      }
    }

    if (sleep > 0)
      sleepImpl(sleep);
  }

  synchronized boolean startSync()
  {
    if (this.syncHandler != null) {
      this.log.warn("A data synchronization run was not started because a previous one is still running");
      return false;
    }

    this.syncHandler = new SyncHandler(this);
    Common.timer.execute(this.syncHandler);

    return true;
  }

  void endSyncHandler() {
    this.syncHandler = null;
  }

  void writePointHierarchy()
  {
    Common.timer.execute(new Runnable()
    {
      public void run() {
        PersistentSendThread.this.writePointHierarchy(new DataPointDao().getPointHierarchy(true));
      } } );
  }

  synchronized void writePointHierarchy(PointHierarchy hierarchy) {
    if (!isConnected()) {
      return;
    }
    List<PersistentPointVO> points = new ArrayList(this.publisher.vo.getPoints());

    ByteQueue queue = new ByteQueue();

    queue.pushU2B(points.size());

    for (PersistentPointVO p : points) {
      List<String> path = hierarchy.getPath(p.getDataPointId());

      queue.pushU2B(p.getIndex());
      queue.pushU2B(path.size());
      for (String s : path) {
        Packet.pushString(queue, s);
      }
    }
    sendPacket(new Packet(PacketType.POINT_HIERARCHY, queue));
  }

  class PointHierarchySync extends PointHierarchyListener {
    PointHierarchySync()
    {
    }

    public void pointHierarchySaved(PointFolder root)
    {
      PointHierarchy hierarchy = new PointHierarchy(root);
      PersistentSendThread.this.writePointHierarchy(hierarchy);
    }
  }

  class SyncTimer extends TimerTask
  {
    public SyncTimer(TimerTrigger trigger)
    {
      super(trigger);
    }

    public void run(long runtime)
    {
      PersistentSendThread.this.startSync();
    }
  }
}