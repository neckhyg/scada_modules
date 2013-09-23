package com.serotonin.m2m2.http.rt;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.http.vo.HttpRetrieverDataSourceVO;
import com.serotonin.m2m2.i18n.TranslatableException;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.dataImage.SetPointSource;
import com.serotonin.m2m2.rt.dataImage.types.DataValue;
import com.serotonin.m2m2.rt.dataSource.DataSourceRT;
import com.serotonin.m2m2.rt.dataSource.DataSourceUtils;
import com.serotonin.m2m2.rt.dataSource.NoMatchException;
import com.serotonin.m2m2.rt.dataSource.PollingDataSource;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.web.http.HttpUtils4;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;

public class HttpRetrieverDataSourceRT extends PollingDataSource
{
  private static final int READ_LIMIT = 1048576;
  public static final int DATA_RETRIEVAL_FAILURE_EVENT = 1;
  public static final int PARSE_EXCEPTION_EVENT = 2;
  public static final int SET_POINT_FAILURE_EVENT = 3;
  private final HttpRetrieverDataSourceVO vo;

  public HttpRetrieverDataSourceRT(HttpRetrieverDataSourceVO vo)
  {
    super(vo);
    setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), vo.isQuantize());
    this.vo = vo;
  }

  public void removeDataPoint(DataPointRT dataPoint)
  {
    returnToNormal(2, System.currentTimeMillis());
    super.removeDataPoint(dataPoint);
  }

  public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source)
  {
    HttpGet request = null;
    URI uri = null;
    try {
      HttpRetrieverPointLocatorRT pl = (HttpRetrieverPointLocatorRT)dataPoint.getPointLocator();
      URIBuilder urib = new URIBuilder(this.vo.getSetPointUrl());
      urib.addParameter("name", pl.getSetPointName());
      urib.addParameter("value", "" + valueTime.getValue().getObjectValue());
      uri = urib.build();
      request = new HttpGet(uri);

      HttpResponse response = Common.getHttpClient().execute(request);
      if (response.getStatusLine().getStatusCode() != 200) {
        raiseEvent(3, valueTime.getTime(), false, new TranslatableMessage("http.event.setPointBadResponse", new Object[] { Integer.valueOf(response.getStatusLine().getStatusCode()), dataPoint.getVO().getName(), uri }));
      }
      else
      {
        dataPoint.setPointValue(valueTime, source);
      }
    } catch (Exception ex) {
      raiseEvent(3, valueTime.getTime(), false, new TranslatableMessage("http.event.setPointFailure", new Object[] { dataPoint.getVO().getName(), uri, ex.getMessage() }));
    }
    finally
    {
      if (request != null)
        request.reset();
    }
  }

  protected void doPoll(long time)
  {
    List<DataPointRT> readPoints = new ArrayList();
    for (DataPointRT dp : this.dataPoints) {
      HttpRetrieverPointLocatorRT locator = (HttpRetrieverPointLocatorRT)dp.getPointLocator();
      if (locator.getValuePattern() != null) {
        readPoints.add(dp);
      }
    }
    if (readPoints.isEmpty())
    {
      return;
    }String data;
    try {
      data = getData(this.vo.getUrl(), this.vo.getTimeoutSeconds(), this.vo.getRetries());
    }
    catch (Exception e)
    {
      TranslatableMessage lm;
      if ((e instanceof TranslatableException))
        lm = ((TranslatableException)e).getTranslatableMessage();
      else
        lm = new TranslatableMessage("event.httpRetriever.retrievalError", new Object[] { this.vo.getUrl(), e.getMessage() });
      raiseEvent(1, time, true, lm);
      return;
    }

    returnToNormal(1, time);

    TranslatableMessage parseErrorMessage = null;
    for (DataPointRT dp : readPoints) {
      HttpRetrieverPointLocatorRT locator = (HttpRetrieverPointLocatorRT)dp.getPointLocator();
      try
      {
        DataValue value = DataSourceUtils.getValue(locator.getValuePattern(), data, locator.getDataTypeId(), locator.getBinary0Value(), dp.getVO().getTextRenderer(), locator.getValueFormat(), dp.getVO().getName());

        long valueTime = DataSourceUtils.getValueTime(time, locator.getTimePattern(), data, locator.getTimeFormat(), dp.getVO().getName());

        dp.updatePointValue(new PointValueTime(value, valueTime));
      }
      catch (NoMatchException e) {
        if ((!locator.isIgnoreIfMissing()) && 
          (parseErrorMessage == null))
          parseErrorMessage = e.getTranslatableMessage();
      }
      catch (TranslatableException e)
      {
        if (parseErrorMessage == null) {
          parseErrorMessage = e.getTranslatableMessage();
        }
      }
    }
    if (parseErrorMessage != null)
      raiseEvent(2, time, false, parseErrorMessage);
    else
      returnToNormal(2, time);
  }

    public static String getData(String url, int timeoutSeconds, int retries) throws TranslatableException {
        String data;
        while (true) {
            HttpClient client = Common.getHttpClient(timeoutSeconds * 1000);
            HttpGet request = null;
            TranslatableMessage message;
            try {
                request = new HttpGet(url);
                HttpResponse response = client.execute(request);
                if (response.getStatusLine().getStatusCode() == 200) {
                    data = HttpUtils4.readResponseBody(response, 1048576);

                    if (request == null) break;
                    request.reset(); break;
                }
                message = new TranslatableMessage("event.http.response", new Object[] { url, Integer.valueOf(response.getStatusLine().getStatusCode()) });
            }
            catch (Exception e) {
                message = DataSourceRT.getExceptionMessage(e);
            }
            finally {
                if (request != null) {
                    request.reset();
                }
            }
            if (retries <= 0)
                throw new TranslatableException(message);
            retries--;
            try
            {
                Thread.sleep(1000L);
            }
            catch (InterruptedException e)
            {
            }
        }
        return data;
    }
}