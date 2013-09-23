package com.serotonin.m2m2.pachube.pub;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.pachube.ds.PachubeDataSourceRT;
import com.serotonin.m2m2.rt.EventManager;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.event.type.EventType;
import com.serotonin.m2m2.rt.event.type.PublisherEventType;
import com.serotonin.m2m2.rt.publish.PublishQueue;
import com.serotonin.m2m2.rt.publish.PublishQueueEntry;
import com.serotonin.m2m2.rt.publish.PublisherRT;
import com.serotonin.m2m2.rt.publish.SendThread;
import com.serotonin.m2m2.vo.publish.PublisherVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

public class PachubeSenderRT extends PublisherRT<PachubePointVO>
{
  static final Log LOG = LogFactory.getLog(PachubeSenderRT.class);
  private static final int MAX_FAILURES = 5;
  public static final int SEND_EXCEPTION_EVENT = 11;
  final EventType sendExceptionEventType = new PublisherEventType(getId(), 11);
  final PachubeSenderVO vo;
  final HttpClient httpClient;

  public PachubeSenderRT(PachubeSenderVO vo)
  {
    super(vo);
    this.vo = vo;
    this.httpClient = PachubeDataSourceRT.createHttpClient(vo.getTimeoutSeconds(), vo.getRetries());
  }

  protected PublishQueue<PachubePointVO> createPublishQueue(PublisherVO<PachubePointVO> vo)
  {
    return new PachubePublishQueue(this, vo.getCacheWarningSize(), vo.getCacheDiscardSize());
  }

  public void initialize()
  {
    super.initialize(new PachubeSendThread());
  }

  PublishQueue<PachubePointVO> getPublishQueue() {
    return this.queue;
  }
  class PachubeSendThread extends SendThread { private int failureCount = 0;
    private TranslatableMessage failureMessage;

    PachubeSendThread() {
      super();
    }

    protected void runImpl()
    {
      while (isRunning()) {
        PublishQueueEntry entry = PachubeSenderRT.this.getPublishQueue().next();

        if (entry != null) {
          if (send(entry)) {
            PachubeSenderRT.this.getPublishQueue().remove(entry);
          }
          else
            sleepImpl(2000L);
        }
        else
          waitImpl(10000L);
      }
    }

    private boolean send(PublishQueueEntry<PachubePointVO> entry)
    {
      PachubePointVO point = (PachubePointVO)entry.getVo();

      HttpPut request = new HttpPut("http://api.cosm.com/v2/feeds/" + point.getFeedId() + "/datastreams/" + point.getDataStreamId() + ".csv");

      request.addHeader("X-ApiKey", PachubeSenderRT.this.vo.getApiKey());
      request.addHeader("User-Agent", "Mango M2M2 Pachube publisher");
      request.setEntity(new StringEntity(entry.getPvt().getValue().toString(), ContentType.create("text/csv", "UTF-8")));

      TranslatableMessage message = null;
      boolean permanentFailure = false;
      try {
        HttpResponse response = PachubeSenderRT.this.httpClient.execute(request);
        if (response.getStatusLine().getStatusCode() != 200) {
          message = new TranslatableMessage("event.publish.invalidResponse", new Object[] { Integer.valueOf(response.getStatusLine().getStatusCode()) });

          permanentFailure = response.getStatusLine().getStatusCode() < 500;
        }
      }
      catch (Exception e) {
        message = new TranslatableMessage("common.default", new Object[] { e.getMessage() });
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
          Common.eventManager.raiseEvent(PachubeSenderRT.this.sendExceptionEventType, System.currentTimeMillis(), true, 2, this.failureMessage, PachubeSenderRT.this.createEventContext());
        }

        return permanentFailure;
      }

      if (this.failureCount > 0) {
        if (this.failureCount > 5) {
          Common.eventManager.returnToNormal(PachubeSenderRT.this.sendExceptionEventType, System.currentTimeMillis());
        }
        this.failureCount = 0;
        this.failureMessage = null;
      }
      return true;
    }
  }
}