package com.serotonin.m2m2.wibox;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.m2m2.DataTypes;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.dataImage.types.AlphanumericValue;
import com.serotonin.m2m2.rt.dataImage.types.BinaryValue;
import com.serotonin.m2m2.rt.dataImage.types.DataValue;
import com.serotonin.m2m2.rt.dataImage.types.NumericValue;
import com.serotonin.m2m2.rt.dataSource.EventDataSource;
import com.serotonin.m2m2.wibox.request.DataRequest;
import com.serotonin.m2m2.wibox.request.WiBoxRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WiBoxHttpDataSourceRT extends EventDataSource
  implements WiBoxMulticastListener
{
  private static final Log LOG = LogFactory.getLog(WiBoxHttpDataSourceRT.class);
  public static final int PARSE_EXCEPTION_EVENT = 1;
  public static final int DATA_CONVERSION_ERROR_EVENT = 2;
  private final WiBoxHttpDataSourceVO vo;

  public WiBoxHttpDataSourceRT(WiBoxHttpDataSourceVO vo)
  {
    super(vo);
    this.vo = vo;
  }

  public String getPassword()
  {
    return this.vo.getPassword();
  }

  public void wiBoxRequest(WiBoxRequest req)
  {
    if (!(req instanceof DataRequest)) {
      req.setHandled(true);
      return;
    }

    DataRequest dataRequest = (DataRequest)req;

    TranslatableMessage errorMessage = null;
    try
    {
      synchronized (this.pointListChangeLock) {
        for (DataPointRT dp : this.dataPoints) {
          WiBoxHttpPointLocatorVO locator = ((WiBoxHttpPointLocatorRT)dp.getPointLocator()).getPointLocatorVO();

          if (dataRequest.getMoteId().equals(locator.getMoteId()))
          {
            Object o = dataRequest.getConverted(locator.getDataKey());

            if (o != null) {
              long time = dataRequest.getUTC();

              DataValue value = null;
              if (locator.getDataTypeId() == 1) {
                if ((o instanceof Boolean))
                  value = new BinaryValue(((Boolean)o).booleanValue());
                else if ((o instanceof String))
                  value = new BinaryValue("true".equals(o));
                else
                  errorMessage = createErrorMessage(errorMessage, locator, o);
              }
              else if (locator.getDataTypeId() == 3) {
                if ((o instanceof Number))
                  value = new NumericValue(((Number)o).doubleValue());
                else
                  errorMessage = createErrorMessage(errorMessage, locator, o);
              }
              else if (locator.getDataTypeId() == 4)
                value = new AlphanumericValue(o.toString());
              else {
                throw new ShouldNeverHappenException("Data type: " + locator.getDataTypeId());
              }
              if (value != null) {
                dp.updatePointValue(new PointValueTime(value, time));
              }
            }
          }
        }
      }
      returnToNormal(1, System.currentTimeMillis());
      req.setHandled(true);
    }
    catch (Exception e) {
      LOG.warn("WiBox API threw an exception while parsing data request", e);
      raiseEvent(1, System.currentTimeMillis(), true, new TranslatableMessage("dsEdit.wiboxHttp.parseException", new Object[] { e.getMessage() }));
    }

    if (errorMessage != null)
      raiseEvent(2, System.currentTimeMillis(), false, errorMessage);
  }

  private TranslatableMessage createErrorMessage(TranslatableMessage current, WiBoxHttpPointLocatorVO locator, Object o)
  {
    if (current != null)
      return current;
    return new TranslatableMessage("dsEdit.wiboxHttp.dataTypeException", new Object[] { locator.getDataKey(), o.getClass(), DataTypes.getDataTypeMessage(locator.getDataTypeId()) });
  }

  public void initialize()
  {
    WiBoxDataSourceServlet.multicaster.addListener(this);
    super.initialize();
  }

  public void terminate()
  {
    super.terminate();
    WiBoxDataSourceServlet.multicaster.removeListener(this);
  }
}