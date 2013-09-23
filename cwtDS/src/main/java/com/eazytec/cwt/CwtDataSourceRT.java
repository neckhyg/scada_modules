package com.eazytec.cwt;

import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.dataSource.EventDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CwtDataSourceRT extends EventDataSource implements Runnable {

    public static final int DATA_SOURCE_EXCEPTION_EVENT = 1;
    private final Log log = LogFactory.getLog(CwtDataSourceRT.class);
    private final CwtDataSourceVO vo;
    private final Map<String, DataPointRT> pointXids = new ConcurrentHashMap<String, DataPointRT>();
    volatile IoAcceptor acceptor;

    public CwtDataSourceRT(CwtDataSourceVO vo) {
        super(vo);
        this.vo = vo;
    }

    public void run() {
        try {
            acceptor.bind(new InetSocketAddress(vo.getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void beginPolling() {
        if (this.acceptor != null)
            new Thread(this, "CWT IO data source").start();
    }

    public void initialize() {
        this.log.info("Initializing");
        super.initialize();

        acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast("logger", new LoggingFilter(CwtDataSourceRT.class));
//        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
        acceptor.setHandler(new CwtHandler());
        ((CwtHandler) acceptor.getHandler()).setDataSourceRT(this);

        acceptor.getSessionConfig().setReadBufferSize(vo.getBufferSize());
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, vo.getIdleTime());
    }

    public void terminate() {
        this.log.info("Terminating");
        super.terminate();

        if (acceptor != null) {
            acceptor.unbind();
            acceptor.getFilterChain().clear();
            acceptor.dispose();
            acceptor = null;
        }
    }

    @Override
    public void addDataPoint(DataPointRT dataPoint) {
        super.addDataPoint(dataPoint);
        this.pointXids.put(dataPoint.getVO().getXid(), dataPoint);
    }

    @Override
    public void removeDataPoint(DataPointRT dataPoint) {
        super.removeDataPoint(dataPoint);
        this.pointXids.remove(dataPoint.getVO().getXid());
    }

    public void writeDataPoints(CwtData cwtData) {
        long time = System.currentTimeMillis();

        synchronized (this.pointListChangeLock) {
//            boolean[] diData = cwtData.getDiData();
//            System.out.println("DI Data:" + diData[0] + " " + diData[1] + " " + diData[2] + " " + diData[3]);
            for (DataPointRT dataPointRT : this.dataPoints) {
                CwtPointLocatorVO locatorVO = ((CwtPointLocatorRT) dataPointRT.getPointLocator()).getPointLocatorVO();
                String deviceId = locatorVO.getDeviceId();
                String dataId = locatorVO.getDataId();
                int dataTypeId = locatorVO.getDataTypeId();
                if (deviceId.equals(cwtData.getDeviceNo())) {
                    try {
                        if (dataId.startsWith("DI")) {
                            int num = Integer.parseInt(dataId.substring(2));
                            boolean value = cwtData.getDiData()[num];
                            dataPointRT.updatePointValue(new PointValueTime(value, time));
//                            System.out.println("DI Value:" + time + " number: " + num + " - " + value);
                        } else if (dataId.startsWith("DO")) { //boolean value
                            int num = Integer.parseInt(dataId.substring(2));
                            boolean value = cwtData.getDoData()[num];
                            dataPointRT.updatePointValue(new PointValueTime(value, time));
//                            System.out.println("DO Value:" + time + " number: " + num + " - " + value);
                        } else if (dataId.startsWith("AD")) {
                            int num = Integer.parseInt(dataId.substring(2));
                            double value = cwtData.getAdData()[num];
                            value = value * locatorVO.getMultiplier() + locatorVO.getAdditive();
                            dataPointRT.updatePointValue(new PointValueTime(value, time));
//                           System.out.println("AD Value:" + time + " number: " + num + " - " + value);
                        }

                    } catch (Exception e) {
                        log.error("cannot update point value");
                    }
                }
            }
        }
    }
}
