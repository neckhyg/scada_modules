package com.eazy.eazySerial;

import com.eazy.communication.ReadSerial;
import com.eazy.communication.SerialBuffer;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.dataImage.SetPointSource;
import com.serotonin.m2m2.rt.dataSource.EventDataSource;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TooManyListenersException;

import com.serotonin.m2m2.rt.dataSource.PollingDataSource;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;
import gnu.io.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EazySerialDataSourceRT extends PollingDataSource implements Runnable, SerialPortEventListener
{
    private final EazySerialDataSourceVO vo;
    CommPortIdentifier portId;
    SerialPort serialPort;
    static OutputStream outputStream;
    static InputStream inputStream;
    SerialBuffer serialBuffer;
    ReadSerial readSerial;
    private UPSData upsData = new UPSData();

    static String sendCmd="Q1";

    public EazySerialDataSourceRT(EazySerialDataSourceVO vo) {
        super(vo);
        this.vo = vo;
        setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), false);
    }

    @Override
    public void initialize() {
        super.initialize();    //To change body of overridden methods use File | Settings | File Templates.
        int InitSuccess = 1;
        int InitFail    = -1;
        try
        {
            portId = CommPortIdentifier.getPortIdentifier(vo.getCommPortId());
            try
            {
                serialPort = (SerialPort)
                        portId.open("Serial_Communication", 2000);
            } catch (PortInUseException e)
            {
                e.printStackTrace();
//                return InitFail;
            }
            try {
                serialPort.addEventListener(this);
            } catch (TooManyListenersException e) {}
            serialPort.notifyOnDataAvailable(true);
            try
            {

                inputStream  = serialPort.getInputStream();
                outputStream = serialPort.getOutputStream();
            } catch (IOException e)
            {
                serialPort.close();
                e.printStackTrace();
//                return InitFail;
            }
            try
            {
                serialPort.setSerialPortParams(vo.getBaudRate(),vo.getDataBits(),
                      vo.getStopBits(),vo.getParity());
            } catch (UnsupportedCommOperationException e)
            {
                serialPort.close();
                e.printStackTrace();
//                return InitFail;
            }
        } catch (NoSuchPortException e)
        {
            e.printStackTrace();
//            return InitFail;
        }

    }

    @Override
    public void beginPolling() {
        super.beginPolling();    //To change body of overridden methods use File | Settings | File Templates.
        serialBuffer = new SerialBuffer();
        readSerial = new ReadSerial(serialBuffer,inputStream);
        if(readSerial !=null)
        readSerial.start();
    }
    public String ReadPort(int Length)
    {
        String Msg;
        Msg = serialBuffer.GetMsg(Length);
        return Msg;
    }

    @Override
    public void run() {
        //To change body of implemented methods use File | Settings | File Templates.
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {}
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
        switch(serialPortEvent.getEventType()) {
            case SerialPortEvent.BI:
            case SerialPortEvent.OE:
            case SerialPortEvent.FE:
            case SerialPortEvent.PE:
            case SerialPortEvent.CD:
            case SerialPortEvent.CTS:
            case SerialPortEvent.DSR:
            case SerialPortEvent.RI:
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                break;
            case SerialPortEvent.DATA_AVAILABLE:
                byte[] readBuffer = new byte[100];

                try {

                    while (inputStream.available() > 0) {
                        int numBytes = inputStream.read(readBuffer);
                    }
                    String receivMsg =  new String(readBuffer);
                    System.out.println(receivMsg);
//                    String subMsg =  receivMsg.substring(1);
//                    parseMsg(subMsg.split(" "));
                    parseUPSData(receivMsg) ;
                } catch (IOException e) {}
                break;
        }
    }

    @Override
    protected void doPoll(long time) {
        //To change body of implemented methods use File | Settings | File Templates.
       try{
             byte[] newCmd = new byte[4];
           newCmd[0]=0x51;
           newCmd[1]=0x31;
           newCmd[2]=0x0d;
           newCmd[3]=0x0a;
//           outputStream.write(sendCmd.getBytes());
           outputStream.write(newCmd);
//         String  Msg = serialBuffer.GetMsg(10);
////           String  Msg = "230 140 210 40 50 12 23";
//           System.out.println(Msg);
//           parseMsg(Msg.split(" "));
       }  catch (Exception e)     {
             e.printStackTrace();
       }


    }
    private void parseUPSData(String receivedStr){
        if(receivedStr.startsWith("(")){
            String subMsg =  receivedStr.substring(1);
            String[] msgArray =  subMsg.split(" ");
            if(msgArray.length != 8){
                 System.out.println("没有接收到全部数据！");

            }  else{
             for(int i = 0; i< 7; i++)   {
                 upsData.getMonitorData()[i]  =Double.parseDouble(msgArray[i]) ;
             }

            for(int j = 0; j< 8; j++)   {
                    upsData.getMonitorStatus()[j]  = Integer.parseInt(msgArray[7].substring(j,j+1)) ;
                }
                updateUPSData();
            }
//            parseMsg(subMsg.split(" "));
        }  else{
            System.out.println("接收数据出错！");
        }

    }
    private void updateUPSData(){
        TranslatableMessage error = null;
        long time = System.currentTimeMillis();

        synchronized (this.pointListChangeLock) {
            for (DataPointRT dp : this.dataPoints) {
                EazySerialPointLocatorVO locator = ((EazySerialPointLocatorRT)dp.getPointLocator()).getPointLocatorVO();

                Integer position = locator.getAttributeId();
                Integer dataTypeId = locator.getDataTypeId();
                if (position == null) {
                    if (error != null)
                        error = new TranslatableMessage("event.eazySerial.attributeNotFound", new Object[] { locator.getConfigurationDescription() });
                }
                else {
                    try
                    {
                        if( position.intValue() > 0)  {
                            if( dataTypeId == 3){
                                Double value = upsData.getMonitorData()[position.intValue()-1];
                                dp.updatePointValue(new PointValueTime(value.doubleValue(), time));
                            } else if( dataTypeId == 2)  {
                                //                            String data = parts[position.intValue()-1];
                                Integer value = upsData.getMonitorStatus()[position.intValue()-10];
                                dp.updatePointValue(new PointValueTime(value.intValue(), time));
                            }

                        }

                    }
                    catch (NumberFormatException e) {
//                        this.log.error(new StringBuilder().append("Weird. We couldn't parse the value ").append(parts[position.intValue()]).append(" into a double. attribute=").append(locator.getAttributeId()).toString());
                    }
                    catch (ArrayIndexOutOfBoundsException e)
                    {
//                        this.log.error(new StringBuilder().append("Weird. We need element ").append(position).append(" but the vmstat data is only ").append(parts.length).append(" elements long").toString());
                    }
                }
            }

        }

        if (error == null)
            returnToNormal(2, time);
        else
            raiseEvent(2, time, true, error);
    }
    private void parseMsg(String[] parts)
    {
        TranslatableMessage error = null;
        long time = System.currentTimeMillis();

        synchronized (this.pointListChangeLock) {
            for (DataPointRT dp : this.dataPoints) {
                EazySerialPointLocatorVO locator = ((EazySerialPointLocatorRT)dp.getPointLocator()).getPointLocatorVO();

                Integer position = locator.getAttributeId();
                if (position == null) {
                    if (error != null)
                        error = new TranslatableMessage("event.eazySerial.attributeNotFound", new Object[] { locator.getConfigurationDescription() });
                }
                else {
                    try
                    {
                        if( position.intValue() > 0)  {
                            String data = parts[position.intValue()-1];
                            Double value = new Double(data);
                            dp.updatePointValue(new PointValueTime(value.doubleValue(), time));
                        }

                    }
                    catch (NumberFormatException e) {
//                        this.log.error(new StringBuilder().append("Weird. We couldn't parse the value ").append(parts[position.intValue()]).append(" into a double. attribute=").append(locator.getAttributeId()).toString());
                    }
                    catch (ArrayIndexOutOfBoundsException e)
                    {
//                        this.log.error(new StringBuilder().append("Weird. We need element ").append(position).append(" but the vmstat data is only ").append(parts.length).append(" elements long").toString());
                    }
                }
            }

        }

        if (error == null)
            returnToNormal(2, time);
        else
            raiseEvent(2, time, true, error);
    }

    @Override
    public void terminate() {
        super.terminate();    //To change body of overridden methods use File | Settings | File Templates.

        if(serialPort !=null){
            serialPort.close();
            serialPort = null;
        }
        if(readSerial !=null){
            readSerial.stop();
            readSerial = null;
        }
    }

    @Override
    public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}