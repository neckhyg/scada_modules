package com.serotonin.m2m2.envcan;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.TranslatableException;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.dataImage.SetPointSource;
import com.serotonin.m2m2.rt.dataSource.DataSourceRT;
import com.serotonin.m2m2.rt.dataSource.PollingDataSource;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.util.XmlUtilsTS;
import com.serotonin.web.http.HttpUtils4;
import java.util.TimeZone;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class EnvCanDataSourceRT extends PollingDataSource
{
  public static final int DATA_RETRIEVAL_FAILURE_EVENT = 1;
  public static final int PARSE_EXCEPTION_EVENT = 2;
  private final EnvCanDataSourceVO vo;
  private long nextValueTime = -1L;
  private long tzOffset;

  public EnvCanDataSourceRT(EnvCanDataSourceVO vo)
  {
    super(vo);
    this.vo = vo;
    setPollingPeriod(3, 1, false);
  }

  public void removeDataPoint(DataPointRT dataPoint)
  {
    returnToNormal(2, System.currentTimeMillis());
    super.removeDataPoint(dataPoint);
  }

  public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source)
  {
  }

  public void initialize()
  {
    this.tzOffset = TimeZone.getDefault().getRawOffset();
  }

  protected void doPoll(long time)
  {
    if (this.nextValueTime == -1L)
    {
      this.nextValueTime = System.currentTimeMillis();
      for (DataPointRT dp : this.dataPoints) {
        PointValueTime pvt = dp.getPointValue();
        if (pvt == null) {
          this.nextValueTime = 0L;
          break;
        }
        if (this.nextValueTime > pvt.getTime()) {
          this.nextValueTime = pvt.getTime();
        }
      }
      if (this.nextValueTime == 0L)
        this.nextValueTime = new DateTime(2008, 1, 1, 0, 0, 0, 0).getMillis();
      else {
        this.nextValueTime += 3600000L;
      }
    }
    long previousValueTime = this.nextValueTime;
    doPollImpl(time);

    while (this.nextValueTime != previousValueTime)
    {
      DateTime prev = new DateTime(previousValueTime);
      DateTime now = new DateTime(System.currentTimeMillis());
      if ((prev.getYear() >= now.getYear()) && (prev.getMonthOfYear() >= now.getMonthOfYear())) break;
      previousValueTime = this.nextValueTime;
      doPollImpl(time); } 
  }
  private void doPollImpl(long runtime) { DateTime dt = new DateTime(this.nextValueTime);
    StringBuilder url = new StringBuilder();
    url.append("http://climate.weatheroffice.gc.ca/climateData/bulkdata_e.html?StationID=").append(this.vo.getStationId());

    url.append("&Year=").append(dt.getYear());
    url.append("&Month=").append(dt.getMonthOfYear() + 1);
    url.append("&format=xml&timeframe=1");
    String data;
    try { data = getData(url.toString(), 30, 2);
    }
    catch (Exception e)
    {
      TranslatableMessage lm;
      TranslatableMessage lm;
      if ((e instanceof TranslatableException))
        lm = ((TranslatableException)e).getTranslatableMessage();
      else
        lm = new TranslatableMessage("envcands.retrievalError", new Object[] { e.getMessage() });
      raiseEvent(1, runtime, true, lm);
      return;
    }

    returnToNormal(1, runtime);
    try
    {
      Document xml = XmlUtilsTS.parse(data);
      Element climateDataElement = xml.getDocumentElement();
      for (Element stationDataElement : XmlUtilsTS.getChildElements(climateDataElement, "stationdata")) {
        int year = XmlUtilsTS.getIntAttribute(stationDataElement, "year", -1);
        int month = XmlUtilsTS.getIntAttribute(stationDataElement, "month", -1);
        int day = XmlUtilsTS.getIntAttribute(stationDataElement, "day", -1);
        int hour = XmlUtilsTS.getIntAttribute(stationDataElement, "hour", -1);

        time = new DateTime(year, month, day, hour, 0, 0, 0, DateTimeZone.UTC).getMillis();
        time -= this.tzOffset;

        temp = XmlUtilsTS.getChildElementTextAsDouble(stationDataElement, "temp", (0.0D / 0.0D));
        dptemp = XmlUtilsTS.getChildElementTextAsDouble(stationDataElement, "dptemp", (0.0D / 0.0D));
        visibility = XmlUtilsTS.getChildElementTextAsDouble(stationDataElement, "visibility", (0.0D / 0.0D));

        relhum = XmlUtilsTS.getChildElementTextAsDouble(stationDataElement, "relhum", (0.0D / 0.0D));
        winddir = XmlUtilsTS.getChildElementTextAsDouble(stationDataElement, "winddir", (0.0D / 0.0D));
        windspd = XmlUtilsTS.getChildElementTextAsDouble(stationDataElement, "windspd", (0.0D / 0.0D));
        stnpress = XmlUtilsTS.getChildElementTextAsDouble(stationDataElement, "stnpress", (0.0D / 0.0D));
        humidex = XmlUtilsTS.getChildElementTextAsDouble(stationDataElement, "humidex", (0.0D / 0.0D));
        windchill = XmlUtilsTS.getChildElementTextAsDouble(stationDataElement, "windchill", (0.0D / 0.0D));
        weather = XmlUtilsTS.getChildElementText(stationDataElement, "weather");

        if (Double.isNaN(temp))
        {
          continue;
        }

        this.nextValueTime = (time + 3600000L);

        for (DataPointRT dp : this.dataPoints) {
          PointValueTime pvt = dp.getPointValue();
          if ((pvt != null) && (pvt.getTime() >= time))
          {
            continue;
          }
          EnvCanPointLocatorVO plvo = (EnvCanPointLocatorVO)dp.getVO().getPointLocator();
          if ((plvo.getAttributeId() == 1) && (!Double.isNaN(temp)))
            pvt = new PointValueTime(temp, time);
          else if ((plvo.getAttributeId() == 2) && (!Double.isNaN(dptemp)))
          {
            pvt = new PointValueTime(dptemp, time);
          } else if ((plvo.getAttributeId() == 3) && (!Double.isNaN(relhum)))
            pvt = new PointValueTime(relhum, time);
          else if ((plvo.getAttributeId() == 4) && (!Double.isNaN(winddir)))
          {
            pvt = new PointValueTime(winddir, time);
          } else if ((plvo.getAttributeId() == 5) && (!Double.isNaN(windspd)))
          {
            pvt = new PointValueTime(windspd, time);
          } else if ((plvo.getAttributeId() == 6) && (!Double.isNaN(visibility)))
          {
            pvt = new PointValueTime(visibility, time);
          } else if ((plvo.getAttributeId() == 7) && (!Double.isNaN(stnpress)))
          {
            pvt = new PointValueTime(stnpress, time);
          } else if ((plvo.getAttributeId() == 8) && (!Double.isNaN(humidex)))
            pvt = new PointValueTime(humidex, time);
          else if ((plvo.getAttributeId() == 9) && (!Double.isNaN(windchill)))
          {
            pvt = new PointValueTime(windchill, time);
          } else if ((plvo.getAttributeId() == 10) && (weather != null)) {
            pvt = new PointValueTime(weather, time);
          }

          dp.updatePointValue(pvt);
        }
      }
      long time;
      double temp;
      double dptemp;
      double visibility;
      double relhum;
      double winddir;
      double windspd;
      double stnpress;
      double humidex;
      double windchill;
      String weather;
      returnToNormal(2, runtime);
    }
    catch (SAXException e) {
      raiseEvent(2, runtime, true, DataSourceRT.getExceptionMessage(e));
    } } 
  private String getData(String url, int timeoutSeconds, int retries) throws TranslatableException
  {
    while (true)
    {
      HttpClient client = Common.getHttpClient(timeoutSeconds * 1000);
      HttpGet request = null;
      TranslatableMessage message;
      try {
        request = new HttpGet(url);
        HttpResponse response = client.execute(request);
        if (response.getStatusLine().getStatusCode() == 200) {
          String data = HttpUtils4.readResponseBody(response);

          request.reset(); break;
        }
        message = new TranslatableMessage("event.http.response", new Object[] { url, Integer.valueOf(response.getStatusLine().getStatusCode()) });
      }
      catch (Exception e) {
        message = DataSourceRT.getExceptionMessage(e);
      }
      finally {
        request.reset();
      }

      if (retries <= 0)
        throw new TranslatableException(message);
      retries--;
      try
      {
        Thread.sleep(10000L);
      }
      catch (InterruptedException e)
      {
      }
    }
    String data;
    return data;
  }
}