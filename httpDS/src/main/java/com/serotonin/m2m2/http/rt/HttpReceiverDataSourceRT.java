package com.serotonin.m2m2.http.rt;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.http.common.HttpDataSourceServlet;
import com.serotonin.m2m2.http.common.HttpMulticastListener;
import com.serotonin.m2m2.http.common.HttpReceiverData;
import com.serotonin.m2m2.http.common.HttpReceiverMulticaster;
import com.serotonin.m2m2.http.common.HttpReceiverPointSample;
import com.serotonin.m2m2.http.vo.HttpReceiverDataSourceVO;
import com.serotonin.m2m2.http.vo.HttpReceiverPointLocatorVO;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.dataImage.SetPointSource;
import com.serotonin.m2m2.rt.dataImage.types.BinaryValue;
import com.serotonin.m2m2.rt.dataImage.types.DataValue;
import com.serotonin.m2m2.rt.dataSource.EventDataSource;
import com.serotonin.m2m2.vo.DataPointVO;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;

public class HttpReceiverDataSourceRT extends EventDataSource
  implements HttpMulticastListener
{
  private final Log log = LogFactory.getLog(HttpReceiverDataSourceRT.class);
  public static final int SET_POINT_FAILURE_EVENT = 1;
  private final HttpReceiverDataSourceVO vo;

  public HttpReceiverDataSourceRT(HttpReceiverDataSourceVO vo)
  {
    super(vo);
    this.vo = vo;
  }

  public void initialize()
  {
    HttpDataSourceServlet.httpReceiverMulticaster.addListener(this);
    super.initialize();
  }

  public void terminate()
  {
    super.terminate();
    HttpDataSourceServlet.httpReceiverMulticaster.removeListener(this);
  }

  public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source)
  {
    HttpGet request = null;
    URI uri = null;
    try {
      HttpReceiverPointLocatorRT pl = (HttpReceiverPointLocatorRT)dataPoint.getPointLocator();
      URIBuilder urib = new URIBuilder(this.vo.getSetPointUrl());
      urib.addParameter("name", pl.getPointLocatorVO().getSetPointName());
      urib.addParameter("value", "" + valueTime.getValue().getObjectValue());
      uri = urib.build();
      request = new HttpGet(uri);

      HttpResponse response = Common.getHttpClient().execute(request);
      if (response.getStatusLine().getStatusCode() != 200) {
        raiseEvent(1, valueTime.getTime(), false, new TranslatableMessage("http.event.setPointBadResponse", new Object[] { Integer.valueOf(response.getStatusLine().getStatusCode()), dataPoint.getVO().getName(), uri }));
      }
      else
      {
        dataPoint.setPointValue(valueTime, source);
      }
    } catch (Exception ex) {
      raiseEvent(1, valueTime.getTime(), false, new TranslatableMessage("http.event.setPointFailure", new Object[] { dataPoint.getVO().getName(), uri, ex.getMessage() }));
    }
    finally
    {
      if (request != null)
        request.reset();
    }
  }

  public String[] getDeviceIdWhiteList()
  {
    return this.vo.getDeviceIdWhiteList();
  }

  public String[] getIpWhiteList()
  {
    return this.vo.getIpWhiteList();
  }

  public void ipWhiteListError(String message)
  {
    this.log.warn("Error checking white list: " + message);
  }

  public void data(HttpReceiverData data)
  {
    Iterator i$;
    DataPointRT dp;
    HttpReceiverPointLocatorVO locator;
    String paramName;
    synchronized (this.pointListChangeLock) {
      for (i$ = this.dataPoints.iterator(); i$.hasNext(); ) { dp = (DataPointRT)i$.next();
        locator = ((HttpReceiverPointLocatorRT)dp.getPointLocator()).getPointLocatorVO();

        paramName = locator.getParameterName();

        for (HttpReceiverPointSample sample : data.getData())
          if (sample.getKey().equals(paramName))
          {
            data.consume(paramName);

            String valueStr = sample.getValue();
            long time = sample.getTime();
            if (time == 0L)
              time = data.getTime();
            DataValue value;
            if ((locator.getDataTypeId() == 1) && (!StringUtils.isBlank(locator.getBinary0Value())))
            {
              if (valueStr.equalsIgnoreCase(locator.getBinary0Value()))
                value = BinaryValue.ZERO;
              else
                value = BinaryValue.ONE;
            }
            else {
              value = DataValue.stringToValue(valueStr, locator.getDataTypeId());
            }
            dp.updatePointValue(new PointValueTime(value, time));
          }
      }
    }
  }
}