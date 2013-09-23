package com.serotonin.ma.ascii.serial;

import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.dataImage.SetPointSource;
import com.serotonin.m2m2.rt.dataImage.types.DataValue;
import com.serotonin.m2m2.rt.dataSource.PollingDataSource;
import com.serotonin.m2m2.vo.DataPointVO;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SerialDataSourceRT extends PollingDataSource
{
  private final Log LOG = LogFactory.getLog(SerialDataSourceRT.class);
  public static final int POINT_READ_EXCEPTION_EVENT = 1;
  public static final int DATA_SOURCE_EXCEPTION_EVENT = 2;
  private final SerialDataSourceVO vo;
  private Enumeration<?> portList;
  private InputStream inSerialStream;
  private OutputStream outSerialStream;
  private SerialPort sPort;

  public SerialDataSourceRT(SerialDataSourceVO vo)
  {
    super(vo);
    this.vo = vo;
    setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), vo.isQuantize());

    this.portList = CommPortIdentifier.getPortIdentifiers();
    getPort(vo.getCommPortId());
    configurePort(this.sPort);
  }

  private boolean reconnect() {
    try {
      while (true) {
        Thread.sleep(5000L);
        this.portList = CommPortIdentifier.getPortIdentifiers();
        SerialPort p = getPort(this.vo.getCommPortId());
        if (p != null) {
          configurePort(this.sPort);
          beginPolling();
          return true;
        }
      }
    } catch (Exception e) {
    }
    return false;
  }

  protected void doPoll(long time)
  {
    try
    {
      if (this.inSerialStream.available() == 0) {
        for (DataPointRT dataPoint : this.dataPoints) {
          SerialPointLocatorVO dataPointVO = (SerialPointLocatorVO)dataPoint.getVO().getPointLocator();
          if (!dataPointVO.getCommand().equals(null))
            this.outSerialStream.write(dataPointVO.getCommand().getBytes());
        }
        raiseEvent(2, time, true, new TranslatableMessage("ascii.file.rt.noData", new Object[] { this.vo.getName() }));
      }
      else if (this.inSerialStream.available() > 0) {
        byte[] readBuffer = new byte[this.vo.getBufferSize()];
        try {
          while (this.inSerialStream.available() > 0) {
            this.inSerialStream.read(readBuffer);
          }
          String result = new String(readBuffer);

          String[] posResults = result.split(this.vo.getCharX());

          if (this.LOG.isDebugEnabled()) {
            for (int i = 0; i < posResults.length; i++) {
              this.LOG.debug("posResult: " + posResults[i]);
            }
          }
          if (!this.vo.getCharX().equals(null)) {
            posResults = result.split(this.vo.getCharX());

            if ((posResults.length != 0) && 
              (this.LOG.isDebugEnabled())) {
              for (int i = 0; i < posResults.length; i++) {
                this.LOG.debug(posResults[i]);
              }
            }
          }

          for (DataPointRT dataPoint : this.dataPoints)
            try {
              SerialPointLocatorVO dataPointVO = (SerialPointLocatorVO)dataPoint.getVO().getPointLocator();
              if (!dataPointVO.getCommand().equals(null)) {
                this.outSerialStream.write(dataPointVO.getCommand().getBytes());
              }
              DataValue value = getValue(dataPointVO, result);
              long timestamp = time;

              if (dataPointVO.isCustomTimestamp()) {
                try {
                  timestamp = getTimestamp(dataPointVO, result);
                }
                catch (Exception e) {
                  raiseEvent(1, time, true, new TranslatableMessage("event.exception2", new Object[] { this.vo.getName(), e.getMessage() }));

                  timestamp = time;
                }
              }

              dataPoint.updatePointValue(new PointValueTime(value, timestamp));
            }
            catch (Exception e) {
              raiseEvent(1, time, true, new TranslatableMessage("event.exception2", new Object[] { this.vo.getName(), e.getMessage() }));

              this.LOG.error("Read error", e);
            }
        }
        catch (Exception e)
        {
          String result;
          this.LOG.error("Read error", e);
        }
      }
    }
    catch (IOException io) {
      this.sPort.close();
      reconnect();
    }
    catch (Exception e) {
      this.LOG.error("Read error", e);
    }
  }

  private DataValue getValue(SerialPointLocatorVO point, String arquivo) throws Exception {
    String valueRegex = point.getValueRegex();
    Pattern valuePattern = Pattern.compile(valueRegex);
    Matcher matcher = valuePattern.matcher(arquivo);
    DataValue value = null;
    String strValue = null;
    boolean found = false;
    if (matcher.find()) {
      found = true;
      strValue = matcher.group();
      value = DataValue.stringToValue(strValue, point.getDataTypeId());
    }
    if (!found) {
      throw new Exception("Value string not found (regex: " + valueRegex + ")");
    }
    return value;
  }

  private long getTimestamp(SerialPointLocatorVO point, String arquivo) throws Exception {
    long timestamp = System.currentTimeMillis();
    String dataFormat = point.getTimestampFormat();
    String tsRegex = point.getTimestampRegex();
    Pattern tsPattern = Pattern.compile(tsRegex);
    Matcher tsMatcher = tsPattern.matcher(arquivo);

    boolean found = false;
    while (tsMatcher.find()) {
      found = true;
      String tsValue = tsMatcher.group();
      timestamp = new SimpleDateFormat(dataFormat).parse(tsValue).getTime();
    }

    if (!found) {
      throw new Exception("Timestamp string not found (regex: " + tsRegex + ")");
    }
    return timestamp;
  }

  public void terminate()
  {
    super.terminate();
    this.sPort.close();
  }

  public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source)
  {
    throw new RuntimeException("Not implemented");
  }

  private void configurePort(SerialPort port) {
    try {
      this.inSerialStream = port.getInputStream();
      this.outSerialStream = port.getOutputStream();
    }
    catch (Exception e) {
      this.LOG.error("Error configuring port 1", e);
    }

    port.notifyOnDataAvailable(true);
    try
    {
      port.setSerialPortParams(this.vo.getBaudRate(), this.vo.getDataBits(), this.vo.getStopBits(), this.vo.getParity());
    }
    catch (Exception e) {
      this.LOG.error("Error configuring port 2", e);
    }
  }

  private SerialPort getPort(String port) {
    SerialPort serialPort = null;
    while (this.portList.hasMoreElements()) {
      CommPortIdentifier portId = (CommPortIdentifier)this.portList.nextElement();
      if ((portId.getPortType() == 1) && 
        (portId.getName().equals(port))) {
        try {
          serialPort = (SerialPort)portId.open(getName(), 10000);
          this.sPort = serialPort;
        }
        catch (Exception e) {
          System.out.println("Erro ao abrir a porta !");
        }
      }

    }

    return serialPort;
  }
}