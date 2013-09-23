package com.serotonin.m2m2.pachube.ds;

import com.serotonin.json.type.JsonArray;
import com.serotonin.json.type.JsonObject;
import com.serotonin.json.type.JsonTypeReader;
import com.serotonin.json.type.JsonValue;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.TranslatableException;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.dataImage.SetPointSource;
import com.serotonin.m2m2.rt.dataImage.types.DataValue;
import com.serotonin.m2m2.rt.dataSource.DataSourceRT;
import com.serotonin.m2m2.rt.dataSource.DataSourceUtils;
import com.serotonin.m2m2.rt.dataSource.PollingDataSource;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.web.http.HttpUtils4;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

public class PachubeDataSourceRT extends PollingDataSource
{
  public static final int DATA_RETRIEVAL_FAILURE_EVENT = 1;
  public static final int PARSE_EXCEPTION_EVENT = 2;
  public static final int POINT_WRITE_EXCEPTION_EVENT = 3;
  public static final String HEADER_API_KEY = "X-ApiKey";
  final Log log = LogFactory.getLog(PachubeDataSourceRT.class);
  final PachubeDataSourceVO vo;
  private final HttpClient httpClient;
  final SimpleDateFormat sdf;

  public PachubeDataSourceRT(PachubeDataSourceVO vo)
  {
    super(vo);
    setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), false);
    this.vo = vo;

    this.httpClient = createHttpClient(vo.getTimeoutSeconds(), vo.getRetries());

    this.sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    this.sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
  }

  public static HttpClient createHttpClient(int timeoutSeconds, int retries) {
    HttpClient httpClient = Common.getHttpClient(timeoutSeconds * 1000);

    return httpClient;
  }

  public void addDataPoint(DataPointRT dataPoint)
  {
    super.addDataPoint(dataPoint);
    dataPoint.setAttribute("UNRELIABLE", Boolean.valueOf(true));
  }

  public void removeDataPoint(DataPointRT dataPoint)
  {
    returnToNormal(2, System.currentTimeMillis());
    super.removeDataPoint(dataPoint);
  }

  public void forcePointRead(DataPointRT dataPoint)
  {
    PachubePointLocatorRT locator = (PachubePointLocatorRT)dataPoint.getPointLocator();
    List point = new ArrayList(1);
    point.add(dataPoint);
    pollFeed(locator.getFeedId(), point, System.currentTimeMillis());
  }

  protected void doPoll(long time)
  {
    Map devicePoints = new HashMap();

    synchronized (this.pointListChangeLock) {
      for (DataPointRT dp : this.dataPoints) {
        PachubePointLocatorRT locator = (PachubePointLocatorRT)dp.getPointLocator();

        List points = (List)devicePoints.get(Integer.valueOf(locator.getFeedId()));
        if (points == null) {
          points = new ArrayList();
          devicePoints.put(Integer.valueOf(locator.getFeedId()), points);
        }

        points.add(dp);
      }
    }

    for (Map.Entry entry : devicePoints.entrySet())
      pollFeed(((Integer)entry.getKey()).intValue(), (List)entry.getValue(), time);
  }

  protected void pollFeed(int feedId, List<DataPointRT> points, long time) {
    Map data;
    try {
      data = getData(this.httpClient, feedId, this.vo.getApiKey());
    }
    catch (Exception e)
    {
      TranslatableMessage lm;
      TranslatableMessage lm;
      if ((e instanceof TranslatableException))
        lm = ((TranslatableException)e).getTranslatableMessage();
      else
        lm = new TranslatableMessage("event.pachube.feed.retrievalError", new Object[] { Integer.valueOf(feedId), e.getMessage() });
      raiseEvent(1, time, true, lm);

      for (DataPointRT point : points) {
        point.setAttribute("UNRELIABLE", Boolean.valueOf(true));
      }
      return;
    }

    returnToNormal(1, time);

    TranslatableMessage parseErrorMessage = null;
    for (DataPointRT dp : points) {
      PachubePointLocatorRT locator = (PachubePointLocatorRT)dp.getPointLocator();
      PachubeValue dataValue = (PachubeValue)data.get(locator.getDataStreamId());

      if (dataValue == null) {
        parseErrorMessage = new TranslatableMessage("event.pachube.dataStreamNotFound", new Object[] { locator.getDataStreamId(), Integer.valueOf(feedId) });

        dp.setAttribute("UNRELIABLE", Boolean.valueOf(true));
      }
      else
      {
        try {
          DataValue value = DataSourceUtils.getValue(dataValue.getValue(), locator.getDataTypeId(), locator.getBinary0Value(), dp.getVO().getTextRenderer(), null, dp.getVO().getName());
          long valueTime;
          long valueTime;
          if (StringUtils.isBlank(dataValue.getTimestamp()))
            valueTime = time;
          else {
            valueTime = this.sdf.parse(dataValue.getTimestamp()).getTime();
          }

          PointValueTime pvt = new PointValueTime(value, valueTime);

          if (!ObjectUtils.equals(dp.getPointValue(), pvt))
            dp.updatePointValue(new PointValueTime(value, valueTime));
          dp.setAttribute("UNRELIABLE", Boolean.valueOf(false));
        }
        catch (TranslatableException e) {
          if (parseErrorMessage == null)
            parseErrorMessage = e.getTranslatableMessage();
          dp.setAttribute("UNRELIABLE", Boolean.valueOf(true));
        }
        catch (ParseException e) {
          if (parseErrorMessage == null) {
            parseErrorMessage = new TranslatableMessage("event.valueParse.timeParsePoint", new Object[] { dataValue.getTimestamp(), dp.getVO().getName() });
          }
          dp.setAttribute("UNRELIABLE", Boolean.valueOf(true));
        }
      }
    }

    if (parseErrorMessage != null)
      raiseEvent(2, time, false, parseErrorMessage);
    else
      returnToNormal(2, time);
  }

  public static Map<String, PachubeValue> getData(HttpClient client, int feedId, String apiKey) throws TranslatableException
  {
    HttpGet request = null;
    try
    {
      request = new HttpGet("http://www.pachube.com/api/feeds/" + feedId + ".json");
      request.addHeader("X-ApiKey", apiKey);
      request.addHeader("User-Agent", "Mango M2M2 Pachube data source");

      HttpResponse response = client.execute(request);
      if (response.getStatusLine().getStatusCode() != 200) {
        throw new TranslatableException(new TranslatableMessage("event.pachube.feed.response", new Object[] { Integer.valueOf(feedId), Integer.valueOf(response.getStatusLine().getStatusCode()) }));
      }

      String json = HttpUtils4.readResponseBody(response);

      Map result = new HashMap();

      JsonTypeReader reader = new JsonTypeReader(json);
      JsonObject content = reader.read().toJsonObject();
      JsonArray streams = content.getJsonArray("datastreams");
      if (streams != null) {
        for (JsonValue element : streams) {
          JsonObject stream = element.toJsonObject();
          String id = stream.getString("id");
          JsonArray values = stream.getJsonArray("values");
          if ((values != null) && (!values.isEmpty())) {
            JsonObject sample = values.get(0).toJsonObject();
            result.put(id, new PachubeValue(sample.getString("value"), sample.getString("recorded_at")));
          }
        }
      }

      ??? = result;
      return ???;
    }
    catch (TranslatableException e)
    {
      throw e;
    }
    catch (Exception e) {
      throw new TranslatableException(DataSourceRT.getExceptionMessage(e));
    }
    finally {
      if (request != null)
        request.reset(); 
    }throw localObject;
  }

  public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source)
  {
    PachubePointLocatorRT pl = (PachubePointLocatorRT)dataPoint.getPointLocator();
    try
    {
      HttpPut request = new HttpPut("http://www.pachube.com/api/feeds/" + pl.getFeedId() + "/datastreams/" + pl.getDataStreamId() + ".csv");

      request.addHeader("X-ApiKey", this.vo.getApiKey());
      request.setEntity(new StringEntity(valueTime.getValue().toString(), ContentType.create("text/csv", "UTF-8")));
      HttpUtils4.execute(this.httpClient, request);
      dataPoint.setPointValue(valueTime, source);

      returnToNormal(3, valueTime.getTime());
    }
    catch (IOException e)
    {
      raiseEvent(3, valueTime.getTime(), true, new TranslatableMessage("event.exception2", new Object[] { dataPoint.getVO().getName(), e.getMessage() }));
    }
  }
}