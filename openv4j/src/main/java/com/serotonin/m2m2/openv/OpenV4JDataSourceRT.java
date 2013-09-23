package com.serotonin.m2m2.openv;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.dataImage.SetPointSource;
import com.serotonin.m2m2.rt.dataImage.types.DataValue;
import com.serotonin.m2m2.rt.dataSource.PollingDataSource;
import gnu.io.SerialPort;
import java.util.Date;
import net.sf.openv4j.CycleTimes;
import net.sf.openv4j.DataPoint;
import net.sf.openv4j.ErrorListEntry;
import net.sf.openv4j.protocolhandlers.ProtocolHandler;
import net.sf.openv4j.protocolhandlers.SegmentedDataContainer;
import net.sf.openv4j.protocolhandlers.SimpleDataContainer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OpenV4JDataSourceRT extends PollingDataSource
{
  private static final Log LOG = LogFactory.getLog(OpenV4JDataSourceRT.class);
  public static final int DATA_SOURCE_EXCEPTION_EVENT = 1;
  public static final int POINT_READ_EXCEPTION_EVENT = 2;
  public static final int POINT_WRITE_EXCEPTION_EVENT = 3;
  private final OpenV4JDataSourceVO vo;
  private SerialPort sPort;
  private final ProtocolHandler protocolHandler = new ProtocolHandler();

  public OpenV4JDataSourceRT(OpenV4JDataSourceVO vo) {
    super(vo);
    this.vo = vo;
    setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), false);
  }

  public void initialize()
  {
    LOG.info("INITIALIZE");
    super.initialize();
  }

  public void terminate()
  {
    LOG.info("TERMINATE");
    super.terminate();
  }

  protected synchronized void doPoll(long time)
  {
    SegmentedDataContainer dc = new SegmentedDataContainer();
    for (DataPointRT point : this.dataPoints) {
      OpenV4JPointLocatorRT locator = (OpenV4JPointLocatorRT)point.getPointLocator();
      dc.addToDataContainer(locator.getDataPoint());
    }

    if (openSerialPort())
      try {
        this.protocolHandler.setReadRequest(dc);
        synchronized (dc) {
          try {
            dc.wait(4000 * dc.getDataBlockCount());
          }
          catch (InterruptedException ex) {
            raiseEvent(2, System.currentTimeMillis(), true, new TranslatableMessage("event.exception2", new Object[] { this.vo.getName(), ex.getMessage(), "HALLO" }));
          }
        }

        for (DataPointRT point : this.dataPoints) {
          OpenV4JPointLocatorRT locator = (OpenV4JPointLocatorRT)point.getPointLocator();
          Object decodedValue = locator.getDataPoint().decode(dc);
          try {
            if (decodedValue == null) {
              throw new ShouldNeverHappenException("Got null value from " + locator.getVo().getDataPointName());
            }

            if ((decodedValue instanceof Number)) {
              if ((decodedValue instanceof Double)) {
                point.updatePointValue(new PointValueTime(((Double)decodedValue).doubleValue(), time));
              }
              else if ((decodedValue instanceof Byte)) {
                point.updatePointValue(new PointValueTime(((Byte)decodedValue).doubleValue(), time));
              }
              else if ((decodedValue instanceof Short)) {
                point.updatePointValue(new PointValueTime(((Short)decodedValue).doubleValue(), time));
              }
              else if ((decodedValue instanceof Integer)) {
                point.updatePointValue(new PointValueTime(((Integer)decodedValue).doubleValue(), time));
              }
            }
            else if ((decodedValue instanceof Boolean)) {
              point.updatePointValue(new PointValueTime(((Boolean)decodedValue).booleanValue(), time));
            }
            else if ((decodedValue instanceof CycleTimes)) {
              point.updatePointValue(new PointValueTime(decodedValue.toString(), time));
            }
            else if ((decodedValue instanceof ErrorListEntry)) {
              point.updatePointValue(new PointValueTime(decodedValue.toString(), time));
            }
            else if ((decodedValue instanceof Date)) {
              point.updatePointValue(new PointValueTime(decodedValue.toString(), time));
            }
          }
          catch (Exception ex)
          {
            LOG.fatal("Error during saving: " + locator.getDataPoint(), ex);
          }
        }

        returnToNormal(2, time);
        returnToNormal(1, time);
      }
      finally
      {
        closePort();
      }
  }

  public synchronized void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source)
  {
    SimpleDataContainer dc = new SimpleDataContainer();
    OpenV4JPointLocatorRT locator = (OpenV4JPointLocatorRT)dataPoint.getPointLocator();

    dc.addToDataContainer(locator.getDataPoint());
    if (locator.getVo().getDataTypeId() == 3) {
      locator.getDataPoint().encode(dc, Double.valueOf(valueTime.getValue().getDoubleValue()));
      dataPoint.setPointValue(valueTime, source);
    }
    else {
      throw new IllegalArgumentException("Only Numeric datatypes are supported");
    }

    if (openSerialPort())
      try {
        this.protocolHandler.setWriteRequest(dc);
        synchronized (dc) {
          try {
            dc.wait(5000 * dc.getDataBlockCount());
          }
          catch (InterruptedException ex) {
            raiseEvent(3, System.currentTimeMillis(), true, new TranslatableMessage("openv4j.interrupted"));
          }
        }

        returnToNormal(3, System.currentTimeMillis());
        returnToNormal(1, System.currentTimeMillis());
      }
      finally {
        closePort();
      }
  }

  private boolean openSerialPort()
  {
    try
    {
      LOG.warn("OpenV4J Try open serial port");
      this.sPort = ProtocolHandler.openPort(this.vo.getCommPortId());
      this.protocolHandler.setStreams(this.sPort.getInputStream(), this.sPort.getOutputStream());
      return true;
    }
    catch (Exception ex) {
      LOG.fatal("OpenV4J Open serial port exception", ex);

      raiseEvent(1, System.currentTimeMillis(), true, getSerialExceptionMessage(ex, this.vo.getCommPortId()));
    }
    return false;
  }

  private void closePort()
  {
    try {
      this.protocolHandler.close();
    }
    catch (InterruptedException ex) {
      LOG.fatal("Close port", ex);
      raiseEvent(1, System.currentTimeMillis(), true, new TranslatableMessage("event.exception2", new Object[] { this.vo.getName(), ex.getMessage(), "HALLO3" }));
    }

    if (this.sPort != null) {
      this.sPort.close();
      this.sPort = null;
    }
  }
}