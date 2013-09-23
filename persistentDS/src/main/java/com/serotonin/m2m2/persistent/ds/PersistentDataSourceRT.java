package com.serotonin.m2m2.persistent.ds;

import com.serotonin.io.RecallableInputStream;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.DataTypes;
import com.serotonin.m2m2.db.dao.DataPointDao;
import com.serotonin.m2m2.db.dao.PointValueDao;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.persistent.common.*;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.dataImage.types.*;
import com.serotonin.m2m2.rt.dataSource.EventDataSource;
import com.serotonin.m2m2.util.log.ProcessLog;
import com.serotonin.m2m2.vo.DataPointSummary;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.m2m2.vo.hierarchy.PointFolder;
import com.serotonin.m2m2.vo.hierarchy.PointHierarchy;
import com.serotonin.timer.NamedRunnable;
import com.serotonin.util.ArrayUtils;
import com.serotonin.util.queue.ByteQueue;
import com.serotonin.validation.StringValidation;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class PersistentDataSourceRT extends EventDataSource
        implements Runnable {
    public static final int DATA_SOURCE_EXCEPTION_EVENT = 1;
    final ProcessLog log;
    final PersistentDataSourceVO vo;
    final Map<String, DataPointRT> pointXids = new ConcurrentHashMap();
    final List<TcpConnectionHandler> connectionHandlers = new CopyOnWriteArrayList();
    volatile ServerSocket serverSocket;

    public PersistentDataSourceRT(PersistentDataSourceVO vo) {
        super(vo);
        this.vo = vo;
        this.log = new ProcessLog("PersistentDataSource-" + vo.getId(), ProcessLog.LogLevel.INFO);
    }

    protected void finalize() throws Throwable {
        super.finalize();
        this.log.close();
    }

    public int getConnectionCount() {
        return this.connectionHandlers.size();
    }

    public String getConnectionAddress(int index) {
        try {
            return ((TcpConnectionHandler) this.connectionHandlers.get(index)).getSocketAddress();
        } catch (IndexOutOfBoundsException e) {
        }
        return "";
    }

    public long getConnectionTime(int index) {
        try {
            return ((TcpConnectionHandler) this.connectionHandlers.get(index)).getConnectionTime();
        } catch (IndexOutOfBoundsException e) {
        }
        return 0L;
    }

    public long getPacketsReceived(int index) {
        try {
            return ((TcpConnectionHandler) this.connectionHandlers.get(index)).getPacketsReceived();
        } catch (IndexOutOfBoundsException e) {
        }
        return 0L;
    }

    public int getTimeouts(int index) {
        try {
            return ((TcpConnectionHandler) this.connectionHandlers.get(index)).getTimeouts();
        } catch (IndexOutOfBoundsException e) {
        }
        return 0;
    }

    public int getIndexPointsCount(int index) {
        try {
            return ((TcpConnectionHandler) this.connectionHandlers.get(index)).getIndexedPointsCount();
        } catch (IndexOutOfBoundsException e) {
        }
        return 0;
    }

    public void initialize() {
        this.log.info("Initializing");

        super.initialize();
        try {
            this.serverSocket = new ServerSocket(this.vo.getPort());
            this.serverSocket.setSoTimeout(2000);

            returnToNormal(1, System.currentTimeMillis());
        } catch (IOException e) {
            this.serverSocket = null;
            raiseEvent(1, System.currentTimeMillis(), true, new TranslatableMessage("event.initializationError", new Object[]{e.getMessage()}));

            this.log.error(e);
        }

        this.log.info("Initialized");
    }

    public void terminate() {
        this.log.info("Terminating");

        super.terminate();

        if (this.serverSocket != null) {
            try {
                this.serverSocket.close();
            } catch (IOException e) {
                this.log.warn(e);
            }

            this.serverSocket = null;
        }

        this.log.info("Terminated");
    }

    public void joinTermination() {
        super.joinTermination();

        while (!this.connectionHandlers.isEmpty())
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
            }
    }

    public void beginPolling() {
        if (this.serverSocket != null)
            new Thread(this, "Persistent TCP data source").start();
    }

    public void addDataPoint(DataPointRT dataPoint) {
        super.addDataPoint(dataPoint);
        this.pointXids.put(dataPoint.getVO().getXid(), dataPoint);
    }

    public void removeDataPoint(DataPointRT dataPoint) {
        super.removeDataPoint(dataPoint);
        this.pointXids.remove(dataPoint.getVO().getXid());
    }

    public void run() {
        try {
            while (this.serverSocket != null)
                try {
                    Socket socket = this.serverSocket.accept();
                    this.log.info("Received socket from " + socket.getRemoteSocketAddress());
                    TcpConnectionHandler ch = new TcpConnectionHandler(socket);
                    this.connectionHandlers.add(ch);
                    Common.timer.execute(ch);
                } catch (SocketTimeoutException e) {
                }
        } catch (IOException e) {
        }
    }

    public class TcpConnectionHandler implements Runnable { 
        final PointValueDao pointValueDao = new PointValueDao();
        private final Socket socket;
        private final String addr;
        private final ByteQueue writeBuffer = new ByteQueue();
        private final List<String> indexedXids = new ArrayList();
        private final long connectionTime;
        int version = 5;
        private RecallableInputStream in;
        private OutputStream out;
        private long packetsReceived;
        private int timeouts;

        public TcpConnectionHandler(Socket socket) {
            this.socket = socket;
            this.addr = socket.getRemoteSocketAddress().toString();
            this.connectionTime = System.currentTimeMillis();
        }

        public String getSocketAddress() {
            return this.addr;
        }

        public long getConnectionTime() {
            return this.connectionTime;
        }

        public long getPacketsReceived() {
            return this.packetsReceived;
        }

        public int getTimeouts() {
            return this.timeouts;
        }

        public int getIndexedPointsCount() {
            return this.indexedXids.size();
        }

        public void run() {
            String oldThreadName = Thread.currentThread().getName();
            Thread.currentThread().setName(new StringBuilder().append(getClass().getName()).append(": id=").append(PersistentDataSourceRT.this.vo.getId()).toString());
            try {
                runImpl();
            } catch (IOException e) {
                PersistentDataSourceRT.this.log.warn(new StringBuilder().append(this.addr).append(": Connection handler exception: ").append(this.in.recallToString()).toString(), e);
            } catch (PersistentProtocolException e) {
                try {
                    Packet.pushString(this.writeBuffer, e.getMessage());
                    Packet.writePacket(this.out, this.version, PacketType.ABORT, this.writeBuffer);
                    PersistentDataSourceRT.this.log.warn(new StringBuilder().append(this.addr).append(": Connection handler exception: ").append(this.in.recallToString()).toString(), e);
                    sleepImpl();
                } catch (IOException e1) {
                    PersistentDataSourceRT.this.log.warn(new StringBuilder().append(this.addr).append(": Connection handler exception: ").append(this.in.recallToString()).toString(), e1);
                }
            } catch (DoAbortException e) {
                try {
                    Packet.pushString(this.writeBuffer, e.getTranslatableMessage().serialize());
                    Packet.writePacket(this.out, this.version, PacketType.ABORT, this.writeBuffer);
                    sleepImpl();
                } catch (IOException e1) {
                    PersistentDataSourceRT.this.log.warn(new StringBuilder().append(this.addr).append(": Connection handler exception: ").append(this.in.recallToString()).toString(), e1);
                }
            } catch (PersistentAbortException e) {
                PersistentDataSourceRT.this.log.warn(new StringBuilder().append(this.addr).append(": Connection handler exception: ").append(this.in.recallToString()).toString(), e);
            } catch (Exception e) {
                PersistentDataSourceRT.this.log.warn(new StringBuilder().append(this.addr).append(": Connection handler exception: ").append(this.in.recallToString()).toString(), e);
            } finally {
                PersistentDataSourceRT.this.connectionHandlers.remove(this);
                try {
                    this.socket.close();
                } catch (IOException e) {
                    PersistentDataSourceRT.this.log.warn(new StringBuilder().append(this.addr).append(": Connection handler exception: ").append(this.in.recallToString()).toString(), e);
                }

                Thread.currentThread().setName(oldThreadName);
            }
        }

        private void sleepImpl() {
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e1) {
            }
        }

        private void runImpl() throws IOException, PersistentProtocolException, PersistentAbortException, DoAbortException {
            this.socket.setSoTimeout(5000);
            this.in = new RecallableInputStream(this.socket.getInputStream(), 1000);
            this.out = this.socket.getOutputStream();

            Packet packet = Packet.readPacket(this.in, 1);
            this.packetsReceived += 1L;
            if (packet.getType() != PacketType.VERSION)
                throw new PersistentProtocolException(new StringBuilder().append("Expected version, got ").append(packet.getType()).toString());
            int requestedVersion = packet.getPayload().popU1B();

            if (requestedVersion < this.version)
                this.version = requestedVersion;
            Packet.writePacket(this.out, 1, PacketType.VERSION, new byte[]{(byte) this.version});

            packet = Packet.readPacket(this.in, this.version);
            this.packetsReceived += 1L;
            if (packet.getType() != PacketType.AUTH_KEY)
                throw new PersistentProtocolException(new StringBuilder().append("Expected auth key, got ").append(packet.getType()).toString());
            String authKey = packet.popString();
            if (!authKey.equals(PersistentDataSourceRT.this.vo.getAuthorizationKey())) {
                throw new DoAbortException(new TranslatableMessage("event.persistent.authKey"));
            }

            Packet.writePacket(this.out, this.version, PacketType.AUTH_KEY, Packet.EMPTY);
            while (true) {
                packet = Packet.readPacket(this.in, this.version);
                this.packetsReceived += 1L;
                if (packet.getType() != PacketType.POINT) {
                    throw new PersistentProtocolException(new StringBuilder().append("Expected points, got ").append(packet.getType()).toString());
                }
                if (packet.getPayload().size() == 0) {
                    break;
                }
                String xid = packet.popString();
                this.indexedXids.add(xid);
                ensurePoint(xid, packet);

                Packet.pushString(this.writeBuffer, xid);
                Packet.writePacket(this.out, this.version, PacketType.POINT, this.writeBuffer);
            }

            Packet.writePacket(this.out, this.version, PacketType.POINT, Packet.EMPTY);

            PacketInfo[] previousPackets = new PacketInfo[20];
            int nextPreviousIndex = 0;

            while (PersistentDataSourceRT.this.serverSocket != null) {
                try {
                    packet = Packet.readPacket(this.in, this.version);
                    this.packetsReceived += 1L;
                    this.timeouts = 0;
                } catch (SocketTimeoutException e) {
                    this.timeouts += 1;

                    if (this.timeouts > 50) {
                        PersistentDataSourceRT.this.log.warn(new StringBuilder().append(this.addr).append(": Too many timeouts. Closing socket").toString());
                        break;
                    }
                    continue;
                } catch (PayloadReadTimeoutException e) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < previousPackets.length; i++) {
                        int index = (nextPreviousIndex + i) % previousPackets.length;
                        if (previousPackets[index] != null) {
                            sb.append('[');
                            sb.append(previousPackets[index].type).append(',');
                            sb.append(previousPackets[index].length).append(',');
                            sb.append(previousPackets[index].receivedTime).append(']');
                        }
                    }
                    PersistentDataSourceRT.this.log.error(new StringBuilder().append(this.addr).append(": Payload read timeout: type=").append(e.getType().name()).append(", len=").append(e.getLength()).append(", payload=[").append(ArrayUtils.toHexString(e.getPayload())).append("], previous=").append(sb).toString());
                    continue;
                }

                PacketInfo pi = new PacketInfo(packet.getType().name(), packet.getPayload().size(), System.currentTimeMillis());

                previousPackets[nextPreviousIndex] = pi;
                nextPreviousIndex = (nextPreviousIndex + 1) % previousPackets.length;

                if (packet.getType() == PacketType.CLOSE) {
                    PersistentDataSourceRT.this.log.warn(new StringBuilder().append(this.addr).append(": Received close packet").toString());
                    break;
                }

                if (packet.getType() == PacketType.TEST) {
                    continue;
                }
                if (packet.getType() == PacketType.RANGE_COUNT) {
                    Common.timer.execute(new NamedRunnable(new RangeCountHandler(packet, this.out), "RangeCountHandler"));
                    continue;
                }

                if (packet.getType() == PacketType.POINT_UPDATE) {
                    int index = packet.getPayload().popU2B();
                    ensurePoint((String) this.indexedXids.get(index), packet);
                    continue;
                }

                if (packet.getType() == PacketType.POINT_HIERARCHY) {
                    updatePointHierarchy(packet);
                    continue;
                }

                if (packet.getType() != PacketType.DATA) {
                    throw new PersistentProtocolException(new StringBuilder().append("Expected data, got ").append(packet.getType()).toString());
                }
                ByteQueue payload = packet.getPayload();
                DataPointRT point = getIndexedPoint(payload.popU2B());
                if (point == null) {
                    continue;
                }
                int dataType = payload.popU1B();
                DataValue value;
                switch (dataType) {
                    case 1:
                        value = new BinaryValue(payload.pop() != 0);
                        break;
                    case 2:
                        value = new MultistateValue(payload.popS4B());
                        break;
                    case 3:
                        value = new NumericValue(packet.popDouble());
                        break;
                    case 4:
                        value = new AlphanumericValue(packet.popString());
                        break;
                    case 5:
                        int imageType = payload.popS4B();
                        byte[] imageData = new byte[payload.popS4B()];
                        payload.pop(imageData);
                        value = new ImageValue(imageData, imageType);
                        break;
                    default:
                        throw new PersistentProtocolException(new StringBuilder().append("Unknown data type: ").append(dataType).toString());
                }

                long time = packet.popLong();

                point.updatePointValue(new PointValueTime(value, time));
            }
        }

        private void ensurePoint(String xid, Packet packet)
                throws DoAbortException {
            if (this.version >= 5)
                ensurePointV5(xid, packet);
            else
                ensurePointV4(xid, packet);
        }

        private void ensurePointV5(String xid, Packet packet) throws DoAbortException {
            DataPointInfo newDpInfo = packet.popDataPointInfo();

            DataPointRT dprt = (DataPointRT) PersistentDataSourceRT.this.pointXids.get(xid);

            if (dprt != null) {
                if (dprt.getVO().getPointLocator().getDataTypeId() != newDpInfo.getDataTypeId()) {
                    TranslatableMessage lm = new TranslatableMessage("event.persistent.dataTypeMismatch", new Object[]{xid, DataTypes.getDataTypeMessage(newDpInfo.getDataTypeId()), dprt.getVO().getDataTypeMessage()});

                    throw new DoAbortException(lm);
                }

                updatePointV5(dprt.getVO(), newDpInfo);
                return;
            }

            DataPointVO oldDpvo = new DataPointDao().getDataPoint(xid);

            if (oldDpvo != null) {
                if (oldDpvo.getDataSourceId() != PersistentDataSourceRT.this.vo.getId()) {
                    throw new DoAbortException(new TranslatableMessage("event.persistent.dataSourceMismatch", new Object[]{xid}));
                }

                updatePointV5(oldDpvo, newDpInfo);
            } else {
                if (StringValidation.isLengthGreaterThan(xid, 50)) {
                    throw new DoAbortException(new TranslatableMessage("event.persistent.xidTooLong", new Object[]{xid}));
                }

                DataPointVO newDpvo = newDpInfo.createDpvo(xid, PersistentDataSourceRT.this.vo.getId());
                Common.runtimeManager.saveDataPoint(newDpvo);
            }
        }

        private void updatePointV5(DataPointVO oldDpvo, DataPointInfo newDpInfo) {
            if (PersistentDataSourceRT.this.vo.isAcceptPointUpdates()) {
                newDpInfo.updateDpvo(oldDpvo);
                new DataPointDao().saveDataPoint(oldDpvo);
            }
        }

        private void ensurePointV4(String xid, Packet packet) throws DoAbortException {
            DataPointVO newDpvo = packet.popDataPointVO(this.version);

            DataPointRT dprt = (DataPointRT) PersistentDataSourceRT.this.pointXids.get(xid);

            if (dprt != null) {
                if (dprt.getVO().getPointLocator().getDataTypeId() != newDpvo.getPointLocator().getDataTypeId()) {
                    TranslatableMessage lm = new TranslatableMessage("event.persistent.dataTypeMismatch", new Object[]{xid, newDpvo.getDataTypeMessage(), dprt.getVO().getDataTypeMessage()});

                    throw new DoAbortException(lm);
                }

                updatePointV4(dprt.getVO(), newDpvo);
                return;
            }

            DataPointVO oldDpvo = new DataPointDao().getDataPoint(xid);

            if (oldDpvo != null) {
                if (oldDpvo.getDataSourceId() != PersistentDataSourceRT.this.vo.getId()) {
                    throw new DoAbortException(new TranslatableMessage("event.persistent.dataSourceMismatch", new Object[]{xid}));
                }

                updatePointV4(oldDpvo, newDpvo);
            } else {
                if (StringValidation.isLengthGreaterThan(xid, 50)) {
                    throw new DoAbortException(new TranslatableMessage("event.persistent.xidTooLong", new Object[]{xid}));
                }

                newDpvo.setId(-1);
                newDpvo.setXid(xid);
                newDpvo.setDataSourceId(PersistentDataSourceRT.this.vo.getId());
                newDpvo.setEnabled(true);
                newDpvo.setPointFolderId(0);
                newDpvo.setEventDetectors(new ArrayList());
                newDpvo.setLoggingType(2);
                PersistentPointLocatorVO locator = new PersistentPointLocatorVO();
                locator.setDataTypeId(newDpvo.getPointLocator().getDataTypeId());
                newDpvo.setPointLocator(locator);
                Common.runtimeManager.saveDataPoint(newDpvo);
            }
        }

        private void updatePointV4(DataPointVO oldDpvo, DataPointVO newDpvo) {
            if (PersistentDataSourceRT.this.vo.isAcceptPointUpdates()) {
                oldDpvo.setName(newDpvo.getName());
                oldDpvo.setDeviceName(newDpvo.getDeviceName());
                oldDpvo.setEngineeringUnits(newDpvo.getEngineeringUnits());
                oldDpvo.setTextRenderer(newDpvo.getTextRenderer());
                oldDpvo.setChartRenderer(newDpvo.getChartRenderer());
                oldDpvo.setChartColour(newDpvo.getChartColour());
                new DataPointDao().saveDataPoint(oldDpvo);
            }
        }

        private void updatePointHierarchy(Packet packet) {
            if (PersistentDataSourceRT.this.vo.isAcceptPointUpdates()) {
                DataPointDao dataPointDao = new DataPointDao();
                PointHierarchy pointHierarchy = dataPointDao.getPointHierarchy(true);

                int count = packet.getPayload().popU2B();
                boolean changes = false;

                for (int i = 0; i < count; i++) {
                    int index = packet.getPayload().popU2B();
                    String xid = (String) this.indexedXids.get(index);
                    int pathLength = packet.getPayload().popU2B();

                    List<String> path = new ArrayList(pathLength);
                    for (int j = 0; j < pathLength; j++) {
                        path.add(packet.popString());
                    }

                    DataPointRT dprt = (DataPointRT) PersistentDataSourceRT.this.pointXids.get(xid);
                    DataPointVO dpvo;
                    if (dprt == null) {
                        dpvo = dataPointDao.getDataPoint(xid);
                    } else dpvo = dprt.getVO();

                    if (dpvo == null) {
                        continue;
                    }
                    List folders = pointHierarchy.getFolderPath(dpvo.getId());
                    PointFolder oldFolder = (PointFolder) folders.get(folders.size() - 1);

                    PointFolder newFolder = pointHierarchy.getRoot();
                    for (String s : path) {
                        PointFolder sub = newFolder.getSubfolder(s);
                        if (sub == null) {
                            sub = new PointFolder();
                            sub.setName(s);
                            newFolder.addSubfolder(sub);
                        }
                        newFolder = sub;
                    }

                    if (oldFolder != newFolder) {
                        oldFolder.removeDataPoint(dpvo.getId());
                        newFolder.addDataPoint(new DataPointSummary(dpvo));
                        changes = true;
                    }
                }

                if (changes) {
                    dataPointDao.savePointHierarchy(pointHierarchy.getRoot());
                }
            }
        }

        DataPointRT getIndexedPoint(int index) {
            try {
                return (DataPointRT) PersistentDataSourceRT.this.pointXids.get(this.indexedXids.get(index));
            } catch (IndexOutOfBoundsException e) {
                PersistentDataSourceRT.this.log.error(new StringBuilder().append(this.addr).append(": Received invalid point index: ").append(index).append(", indexedXids: ").append(this.indexedXids).append(", pointXids: ").append(PersistentDataSourceRT.this.pointXids).append(", recall: ").append(this.in.recallToString()).toString());
            }
            return null;
        }

        private class RangeCountHandler implements Runnable {
            private final int requestId;
            private final int index;
            private final long from;
            private final long to;
            private final OutputStream out;

            RangeCountHandler(Packet packet, OutputStream out) {
                this.requestId = packet.getPayload().popU3B();
                this.index = packet.getPayload().popU2B();
                this.from = packet.popLong();
                this.to = packet.popLong();
                this.out = out;
            }

            public void run() {
                if (PersistentDataSourceRT.this.log.isInfoEnabled())
                    PersistentDataSourceRT.this.log.info(PersistentDataSourceRT.TcpConnectionHandler.this.addr + ": Processing range count request " + this.requestId);
                try {
                    runImpl();
                    if (PersistentDataSourceRT.this.log.isInfoEnabled())
                        PersistentDataSourceRT.this.log.info(PersistentDataSourceRT.TcpConnectionHandler.this.addr + ": Finished range count request " + this.requestId);
                } catch (Exception e) {
                    PersistentDataSourceRT.this.log.error(PersistentDataSourceRT.TcpConnectionHandler.this.addr + ": Error while processing range count request " + this.requestId, e);
                }
            }

            private void runImpl() {
                DataPointRT dprt = PersistentDataSourceRT.TcpConnectionHandler.this.getIndexedPoint(this.index);
                long result;
                if (dprt == null)
                    result = -1L;
                else {
                    result = PersistentDataSourceRT.TcpConnectionHandler.this.pointValueDao.dateRangeCount(dprt.getId(), this.from, this.to);
                }
                ByteQueue queue = new ByteQueue();
                queue.pushU3B(this.requestId);
                Packet.pushLong(queue, result);
                try {
                    synchronized (this.out) {
                        Packet.writePacket(this.out, PersistentDataSourceRT.TcpConnectionHandler.this.version, PacketType.RANGE_COUNT, queue);
                    }
                } catch (IOException e) {
                }
            }
        }

        private class PacketInfo {
            String type;
            int length;
            long receivedTime;

            PacketInfo(String type, int length, long receivedTime) {
                this.type = type;
                this.length = length;
                this.receivedTime = receivedTime;
            }
        }
    } 
}
