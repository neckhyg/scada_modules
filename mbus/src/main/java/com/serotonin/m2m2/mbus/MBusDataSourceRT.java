package com.serotonin.m2m2.mbus;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.dataImage.SetPointSource;
import com.serotonin.m2m2.rt.dataSource.PollingDataSource;
import gnu.io.SerialPort;
import java.io.IOException;
import java.math.BigDecimal;
import net.sf.mbus4j.SerialPortTools;
import net.sf.mbus4j.dataframes.datablocks.BigDecimalDataBlock;
import net.sf.mbus4j.dataframes.datablocks.ByteDataBlock;
import net.sf.mbus4j.dataframes.datablocks.IntegerDataBlock;
import net.sf.mbus4j.dataframes.datablocks.LongDataBlock;
import net.sf.mbus4j.dataframes.datablocks.RealDataBlock;
import net.sf.mbus4j.dataframes.datablocks.ShortDataBlock;
import net.sf.mbus4j.dataframes.datablocks.StringDataBlock;
import net.sf.mbus4j.master.MBusMaster;
import net.sf.mbus4j.master.ValueRequest;
import net.sf.mbus4j.master.ValueRequestPointLocator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MBusDataSourceRT extends PollingDataSource
{
  private static final Log LOG = LogFactory.getLog(MBusDataSourceRT.class);
  public static final int DATA_SOURCE_EXCEPTION_EVENT = 1;
  public static final int POINT_READ_EXCEPTION_EVENT = 2;
  public static final int POINT_WRITE_EXCEPTION_EVENT = 3;
  private final MBusDataSourceVO vo;
  private SerialPort sPort;
  private final MBusMaster master = new MBusMaster();

  public MBusDataSourceRT(MBusDataSourceVO vo) {
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
    ValueRequest request = new ValueRequest();
    for (DataPointRT point : this.dataPoints) {
      MBusPointLocatorRT locator = (MBusPointLocatorRT)point.getPointLocator();
      request.add(locator.createValueRequestPointLocator(point));
    }

    if (openSerialPort())
      try {
        this.master.readValues(request);
        for (ValueRequestPointLocator vr : request) {
          try {
            if (vr.getDb() == null)
            {
              throw new ShouldNeverHappenException("Got null value ");
            }
            if ((vr.getDb() instanceof ByteDataBlock)) {
              ((DataPointRT)vr.getReference()).updatePointValue(new PointValueTime(((ByteDataBlock)vr.getDb()).getValue(), time));
            }
            else if ((vr.getDb() instanceof ShortDataBlock)) {
              ((DataPointRT)vr.getReference()).updatePointValue(new PointValueTime(((ShortDataBlock)vr.getDb()).getValue(), time));
            }
            else if ((vr.getDb() instanceof IntegerDataBlock)) {
              ((DataPointRT)vr.getReference()).updatePointValue(new PointValueTime(((IntegerDataBlock)vr.getDb()).getValue(), time));
            }
            else if ((vr.getDb() instanceof LongDataBlock)) {
              ((DataPointRT)vr.getReference()).updatePointValue(new PointValueTime(((LongDataBlock)vr.getDb()).getValue(), time));
            }
            else if ((vr.getDb() instanceof RealDataBlock)) {
              ((DataPointRT)vr.getReference()).updatePointValue(new PointValueTime(((RealDataBlock)vr.getDb()).getValue(), time));
            }
            else if ((vr.getDb() instanceof BigDecimalDataBlock)) {
              ((DataPointRT)vr.getReference()).updatePointValue(new PointValueTime(((BigDecimalDataBlock)vr.getDb()).getValue().doubleValue(), time));
            }
            else if ((vr.getDb() instanceof StringDataBlock)) {
              ((DataPointRT)vr.getReference()).updatePointValue(new PointValueTime(((StringDataBlock)vr.getDb()).getValue(), time));
            }
            else
            {
              LOG.fatal("Dont know how to save : " + vr.getReference());
              raiseEvent(2, System.currentTimeMillis(), true, new TranslatableMessage("event.exception2", new Object[] { this.vo.getName(), "Dont know how to save : ", "Datapoint" }));
            }

          }
          catch (Exception ex)
          {
            LOG.fatal("Error during saving: " + vr.getReference(), ex);
          }
        }

        returnToNormal(2, time);
        returnToNormal(1, time);
      }
      catch (InterruptedException ex)
      {
        raiseEvent(1, System.currentTimeMillis(), true, getSerialExceptionMessage(ex, this.vo.getCommPortId()));

        LOG.error("cant set value of", ex);
      }
      catch (IOException ex) {
        raiseEvent(1, System.currentTimeMillis(), true, getSerialExceptionMessage(ex, this.vo.getCommPortId()));

        LOG.error("cant set value of", ex);
      }
      finally {
        closePort();
      }
  }

  public synchronized void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source)
  {
  }

  private boolean openSerialPort()
  {
    try
    {
      LOG.warn("MBus Try open serial port");
      this.sPort = SerialPortTools.openPort(this.vo.getCommPortId(), this.vo.getBaudRate());
      this.master.setStreams(this.sPort.getInputStream(), this.sPort.getOutputStream(), this.vo.getBaudRate());
      return true;
    }
    catch (Exception ex) {
      LOG.fatal("MBus Open serial port exception", ex);

      raiseEvent(1, System.currentTimeMillis(), true, getSerialExceptionMessage(ex, this.vo.getCommPortId()));
    }
    return false;
  }

  private void closePort()
  {
    try {
      this.master.close();
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