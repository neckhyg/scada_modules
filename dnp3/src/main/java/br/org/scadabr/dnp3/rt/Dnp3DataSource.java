package br.org.scadabr.dnp3.rt;

import br.org.scadabr.app.DNP3Master;
import br.org.scadabr.app.DNPElementVO;
import br.org.scadabr.dnp3.vo.Dnp3DataSourceVO;
import br.org.scadabr.dnp3.vo.Dnp3PointLocatorVO;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.dataImage.SetPointSource;
import com.serotonin.m2m2.rt.dataImage.types.DataValue;
import com.serotonin.m2m2.rt.dataSource.PollingDataSource;
import com.serotonin.m2m2.vo.DataPointVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Dnp3DataSource extends PollingDataSource
{
  private final Log LOG = LogFactory.getLog(Dnp3DataSource.class);
  public static final int POINT_READ_EXCEPTION_EVENT = 1;
  public static final int DATA_SOURCE_EXCEPTION_EVENT = 2;
  private DNP3Master dnp3Master;
  private final Dnp3DataSourceVO<?> vo;

  public Dnp3DataSource(Dnp3DataSourceVO<?> vo)
  {
    super(vo);
    this.vo = vo;
    setPollingPeriod(1, vo.getRbePollPeriods(), false);
  }

  protected void doPoll(long time)
  {
    for (DataPointRT dataPoint : this.dataPoints) {
      try {
        if (this.dnp3Master.getElement(dataPoint.getId()) == null) {
          Dnp3PointLocatorVO pointLocator = (Dnp3PointLocatorVO)dataPoint.getVO().getPointLocator();
          try {
            this.dnp3Master.addElement(dataPoint.getId(), pointLocator.getDnp3DataType(), ((Dnp3PointLocatorVO)dataPoint.getVO().getPointLocator()).getIndex());
          }
          catch (Exception e)
          {
            raiseEvent(2, time, true, new TranslatableMessage("event.exception2", new Object[] { this.vo.getName(), e.getMessage() }));
          }
        }
      }
      catch (Exception e)
      {
        raiseEvent(2, time, true, new TranslatableMessage("event.exception2", new Object[] { this.vo.getName(), e.getMessage() }));
      }
    }

    try
    {
      this.dnp3Master.doPoll();
      returnToNormal(2, time);
    }
    catch (Exception e) {
      raiseEvent(2, time, true, new TranslatableMessage("event.exception2", new Object[] { this.vo.getName(), e.getMessage() }));
    }

    for (DataPointRT dataPoint : this.dataPoints) {
      DNPElementVO dnpElementScada = new DNPElementVO();
      try {
        dnpElementScada = this.dnp3Master.getElement(dataPoint.getId());
      }
      catch (Exception e) {
        raiseEvent(2, time, true, new TranslatableMessage("event.exception2", new Object[] { this.vo.getName(), e.getMessage() }));
      }

      if (dnpElementScada != null) {
        DataValue value = null;
        if (dataPoint.getDataTypeId() == 1) {
          value = DataValue.stringToValue(dnpElementScada.getValue().toString(), 1);
        }
        else if (dataPoint.getDataTypeId() == 4) {
          value = DataValue.stringToValue(dnpElementScada.getValue().toString(), 4);
        }
        else
        {
          value = DataValue.stringToValue(dnpElementScada.getValue().toString(), 3);
        }

        dataPoint.updatePointValue(new PointValueTime(value, time));
      }
    }
  }

  protected void initialize(DNP3Master dnp3Master) {
    this.dnp3Master = dnp3Master;
    dnp3Master.setAddress(this.vo.getSourceAddress());
    dnp3Master.setSlaveAddress(this.vo.getSlaveAddress());
    dnp3Master.setTimeout(this.vo.getTimeout());
    dnp3Master.setRetries(this.vo.getRetries());
    dnp3Master.setStaticPollMultiplier(this.vo.getStaticPollPeriods());
    try
    {
      dnp3Master.init();
      returnToNormal(2, System.currentTimeMillis());
    }
    catch (Exception e) {
      raiseEvent(2, System.currentTimeMillis(), true, new TranslatableMessage("event.exception2", new Object[] { this.vo.getName(), e.getMessage() }));

      this.LOG.debug("Error while initializing data source", e);
      return;
    }

    super.initialize();
  }

  public void terminate()
  {
    super.terminate();
    try {
      this.dnp3Master.terminate();
    }
    catch (Exception e) {
      this.LOG.error("Termination error", e);
    }
  }

  public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source)
  {
    Dnp3PointLocatorVO pointLocator = (Dnp3PointLocatorVO)dataPoint.getVO().getPointLocator();

    int dataType = pointLocator.getDnp3DataType();
    int index = pointLocator.getIndex();
    try
    {
      if (dataType == 16) {
        this.dnp3Master.controlCommand(valueTime.getValue().toString(), dataType, index, pointLocator.getControlCommand(), pointLocator.getTimeOn(), pointLocator.getTimeOff());
      }
      else
      {
        this.dnp3Master.sendAnalogCommand(index, valueTime.getIntegerValue());
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}