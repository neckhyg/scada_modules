package com.eazytec.cwt;

import com.serotonin.m2m2.rt.dataSource.DataSourceRT;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CwtHandler extends IoHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private CwtData cwtData = new CwtData();
    private DataSourceRT dataSourceRT;

    public DataSourceRT getDataSourceRT() {
        return dataSourceRT;
    }

    public void setDataSourceRT(DataSourceRT dataSourceRT) {
        this.dataSourceRT = dataSourceRT;
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        IoBuffer ioBuffer = (IoBuffer) message;
        byte[] bytes = ioBuffer.array();
        String str = new String(bytes);
        str = str.trim();
        if (str.trim().startsWith("$")) {
            logger.info("接收字符串:" + str);
            parse(str);
        }

        logger.info("连接IP: " + session.getRemoteAddress().toString());
        ((CwtDataSourceRT)dataSourceRT).writeDataPoints(cwtData);
    }

    private void parse(String receivedStr) {

        cwtData.setDeviceNo(receivedStr.substring(1, 9));
        String packageType = receivedStr.substring(9, 11).toUpperCase();
        cwtData.setPackageType(PackageType.valueOf(packageType));
        int packageNum = Integer.parseInt(receivedStr.substring(11, 13));
        cwtData.setPackageNum(packageNum);
        cwtData.setTimeStr(receivedStr.substring(13, 27));
        String dataStr = receivedStr.substring(27, receivedStr.length() - 1);
        cwtData.setDataStr(dataStr);

        int number = Integer.parseInt(receivedStr.substring(27, 29));
        cwtData.setNumber(number);
        String tmpStr = receivedStr.substring(29, receivedStr.length() - 1);
        boolean[] diData = new boolean[8];
        boolean[] doData = new boolean[8];
        double[] adData = new double[4];
        short[] adAlarm = new short[4];

        switch (cwtData.getPackageType()) {
            case PW:
                break;
            case RP:
                boolean arming = Boolean.parseBoolean(tmpStr.substring(0, 1));
                boolean power = Boolean.parseBoolean(tmpStr.substring(1, 2));
                String signal = tmpStr.substring(2, 5);
                double innerTemp = Double.parseDouble(tmpStr.substring(5, 10));
                int length = tmpStr.length();
                for (int i = 0; i < 8; i++) {
                    boolean value = Boolean.parseBoolean(tmpStr.substring(length - 16 + i, length - 15 + i + 1));
                    diData[i] = value;
                }
                for (int i = 0; i < 8; i++) {
                    boolean value = Boolean.parseBoolean(tmpStr.substring(length - 8 + i, length - 7 + i));
                    doData[i] = value;
                }
                cwtData.setArming(arming);
                cwtData.setInnerTemp(innerTemp);
                cwtData.setPower(power);
                cwtData.setSignal(signal);
                cwtData.setDiData(diData);
                cwtData.setDoData(doData);
                break;
            case DI:
                if (packageNum == 2) {//单通道DI
                    boolean value = Boolean.parseBoolean(tmpStr);
                    diData[number - 1] = value;
                    cwtData.setDiData(diData);
                } else if (packageNum == 20) {//多通道DI
                    //System.out.println("DI String:" + tmpStr);
                    for (int i = 0; i < number; i++) {
                        boolean value = Boolean.parseBoolean(tmpStr.substring(i, i + 1));
                        diData[i] = value;
                    }
                    cwtData.setDiData(diData);
                }
                break;
            case DO:
                if (packageNum == 5) {//单通道DO
                    boolean value = Boolean.parseBoolean(tmpStr);
                    doData[number - 1] = value;
                    cwtData.setDoData(doData);

                } else if (packageNum == 21) {//多通道DO
                    //System.out.println("DO String:" + tmpStr);
                    for (int i = 0; i < number; i++) {
                        boolean value = Boolean.parseBoolean(tmpStr.substring(i, i + 1));
                        doData[i] = value;
                    }
                    cwtData.setDoData(doData);
                }
                break;
            case DC:
                break;
            case AD:
                if (packageNum == 10) {//单通道AD
                    short alarm = Short.parseShort(tmpStr.substring(0, 1));
                    double value = Double.parseDouble(tmpStr.substring(1));
                    adAlarm[number - 1] = alarm;
                    adData[number - 1] = value;
                    cwtData.setAdData(adData);
                    cwtData.setAdAlarm(adAlarm);
                } else if (packageNum == 22) {//多通道AD
                    String[] tempDataStr = tmpStr.split(",");
                    for (int i = 0; i < number; i++) {
                        short alarm = Short.parseShort(tempDataStr[i].substring(0, 1));
                        double value = Double.parseDouble(tempDataStr[i].substring(1));
                        adAlarm[i] = alarm;
                        adData[i] = value;
                    }
                    cwtData.setAdData(adData);
                    cwtData.setAdAlarm(adAlarm);
                }
                break;
            case DF:
                break;
            case TM:
                break;
            case HM:
                break;
            case SI:
                break;
            default:
                break;
        }

    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
//        System.out.println("IDLE " + session.getIdleCount(status));
    }

    public CwtData getCwtData() {
        return cwtData;
    }

    public void setCwtData(CwtData cwtData) {
        this.cwtData = cwtData;
    }

}
