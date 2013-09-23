package com.serotonin.m2m2.http.rt;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.cache.ThreadSafeCache;
import com.serotonin.db.pair.StringStringPair;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.DataTypes;
import com.serotonin.m2m2.http.common.HttpDataSourceServlet;
import com.serotonin.m2m2.http.vo.HttpPointVO;
import com.serotonin.m2m2.http.vo.HttpSenderVO;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.EventManager;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.event.type.EventType;
import com.serotonin.m2m2.rt.event.type.PublisherEventType;
import com.serotonin.m2m2.rt.publish.PublishQueue;
import com.serotonin.m2m2.rt.publish.PublishQueueEntry;
import com.serotonin.m2m2.rt.publish.PublisherRT;
import com.serotonin.m2m2.rt.publish.SendThread;
import com.serotonin.web.http.HttpUtils4;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class HttpSenderRT extends PublisherRT<HttpPointVO>
{
  public static final String USER_AGENT = "Mango M2M2 HTTP Sender publisher";
  private static final int MAX_FAILURES = 5;
  public static final int SEND_EXCEPTION_EVENT = 11;
  public static final int RESULT_WARNINGS_EVENT = 12;
  final EventType sendExceptionEventType = new PublisherEventType(getId(), 11);
  final EventType resultWarningsEventType = new PublisherEventType(getId(), 12);
  final HttpSenderVO vo;

  public HttpSenderRT(HttpSenderVO vo)
  {
    super(vo);
    this.vo = vo;
  }

  public void initialize()
  {
    super.initialize(new HttpSendThread());
  }

  PublishQueue<HttpPointVO> getPublishQueue() {
    return this.queue;
  }

  List<NameValuePair> createNVPs(List<StringStringPair> staticParameters, List<PublishQueueEntry<HttpPointVO>> list)
  {
    List nvps = new ArrayList();

    for (StringStringPair kvp : staticParameters) {
      nvps.add(new BasicNameValuePair(kvp.getKey(), kvp.getValue()));
    }
    for (PublishQueueEntry e : list) {
      HttpPointVO pvo = (HttpPointVO)e.getVo();
      PointValueTime pvt = e.getPvt();

      String value = DataTypes.valueToString(pvt.getValue());

      if (pvo.isIncludeTimestamp()) {
        value = value + "@";

        switch (this.vo.getDateFormat()) {
        case 1:
          value = value + ((SimpleDateFormat)HttpDataSourceServlet.BASIC_SDF_CACHE.getObject()).format(new Date(pvt.getTime()));
          break;
        case 2:
          value = value + ISODateTimeFormat.dateTime().print(pvt.getTime());
          break;
        case 3:
          value = value + Long.toString(pvt.getTime());
          break;
        default:
          throw new ShouldNeverHappenException("Unknown date format type: " + this.vo.getDateFormat());
        }
      }
      nvps.add(new BasicNameValuePair(pvo.getParameterName(), value));
    }

    return nvps;
  }

  class HttpSendThread extends SendThread
  {
    private int failureCount = 0;
    private TranslatableMessage failureMessage;


    HttpSendThread()
    {
      super(null);
    }

    protected void runImpl()
    {
      int max;
      if (HttpSenderRT.this.vo.isUsePost())
        max = 100;
      else {
        max = 10;
      }
      while (isRunning()) {
        List<PublishQueueEntry<HttpPointVO>> list = HttpSenderRT.this.getPublishQueue().get(max);

        if (list != null) {
          if (send(list)) {
            for (PublishQueueEntry e : list) {
              HttpSenderRT.this.getPublishQueue().remove(e);
            }
          }
          else
            sleepImpl(5000L);
        }
        else
          waitImpl(10000L);
      }
    }

    private boolean send(List<PublishQueueEntry<HttpPointVO>> list)
    {
      List<NameValuePair> params = HttpSenderRT.this.createNVPs(HttpSenderRT.this.vo.getStaticParameters(), list);
      HttpRequestBase request;
      if (HttpSenderRT.this.vo.isUsePost()) {
        HttpPost post = new HttpPost(HttpSenderRT.this.vo.getUrl());
        try {
          post.setEntity(new UrlEncodedFormEntity(params));
        }
        catch (UnsupportedEncodingException e) {
          throw new ShouldNeverHappenException(e);
        }
        request = post;
      }
      else {
        try {
          URIBuilder urib = new URIBuilder(HttpSenderRT.this.vo.getUrl());
          for (NameValuePair nvp : params)
            urib.addParameter(nvp.getName(), nvp.getValue());
          request = new HttpGet(urib.build());
        }
        catch (URISyntaxException e) {
          throw new ShouldNeverHappenException(e);
        }

      }

      request.addHeader("User-Agent", "Mango M2M2 HTTP Sender publisher");

      for (StringStringPair kvp : HttpSenderRT.this.vo.getStaticHeaders()) {
        request.addHeader(kvp.getKey(), kvp.getValue());
      }

      TranslatableMessage message = null;
      try {
        HttpResponse response = Common.getHttpClient().execute(request);
        if (response.getStatusLine().getStatusCode() == 200) {
          if (HttpSenderRT.this.vo.isRaiseResultWarning()) {
            String result = HttpUtils4.readResponseBody(response, 1024);
            if (!StringUtils.isBlank(result)) {
              Common.eventManager.raiseEvent(HttpSenderRT.this.resultWarningsEventType, System.currentTimeMillis(), false, 1, new TranslatableMessage("common.default", new Object[] { result }), HttpSenderRT.this.createEventContext());
            }
          }
        }
        else
        {
          message = new TranslatableMessage("event.publish.invalidResponse", new Object[] { Integer.valueOf(response.getStatusLine().getStatusCode()) });
        }
      }
      catch (Exception ex) {
        message = new TranslatableMessage("common.default", new Object[] { ex.getMessage() });
      }
      finally {
        request.reset();
      }

      if (message != null) {
        this.failureCount += 1;
        if (this.failureMessage == null) {
          this.failureMessage = message;
        }
        if (this.failureCount == 6) {
          Common.eventManager.raiseEvent(HttpSenderRT.this.sendExceptionEventType, System.currentTimeMillis(), true, 2, this.failureMessage, HttpSenderRT.this.createEventContext());
        }

        return false;
      }

      if (this.failureCount > 0) {
        if (this.failureCount > 5) {
          Common.eventManager.returnToNormal(HttpSenderRT.this.sendExceptionEventType, System.currentTimeMillis());
        }
        this.failureCount = 0;
        this.failureMessage = null;
      }
      return true;
    }
  }
}