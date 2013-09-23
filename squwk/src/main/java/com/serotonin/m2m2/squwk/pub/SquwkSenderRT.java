package com.serotonin.m2m2.squwk.pub;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.EventManager;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.dataImage.types.DataValue;
import com.serotonin.m2m2.rt.event.type.EventType;
import com.serotonin.m2m2.rt.event.type.PublisherEventType;
import com.serotonin.m2m2.rt.publish.PublishQueue;
import com.serotonin.m2m2.rt.publish.PublishQueueEntry;
import com.serotonin.m2m2.rt.publish.PublishedPointRT;
import com.serotonin.m2m2.rt.publish.PublisherRT;
import com.serotonin.m2m2.rt.publish.SendThread;
import com.serotonin.m2m2.vo.publish.PublisherVO;
import com.serotonin.squwk.client.ServiceRequest;
import com.serotonin.squwk.client.ServiceResultObjectHandler;
import com.serotonin.squwk.client.SquwkClient;
import com.serotonin.squwk.client.SquwkException;
import com.serotonin.squwk.client.request.PointListRequest;
import com.serotonin.squwk.client.request.SampleAppendRequest;
import com.serotonin.squwk.client.vo.DataType;
import com.serotonin.squwk.client.vo.Point;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SquwkSenderRT extends PublisherRT<SquwkPointVO>
{
  private static final Log LOG = LogFactory.getLog(SquwkSenderRT.class);
  private static final int MAX_BATCH = 100;
  public static final int REQUEST_EXCEPTION_EVENT = 11;
  public static final int SERVICE_EXCEPTION_EVENT = 12;
  final EventType requestExceptionEventType = new PublisherEventType(getId(), 11);
  final EventType serviceExceptionEventType = new PublisherEventType(getId(), 12);
  final SquwkSenderVO vo;
  final SquwkClient squwkClient;
  private List<Point> squwkPoints;

  public SquwkSenderRT(SquwkSenderVO vo)
  {
    super(vo);
    this.vo = vo;
    this.squwkClient = new SquwkClient(vo.getAccessKey(), vo.getSecretKey());
  }

  protected PublishQueue<SquwkPointVO> createPublishQueue(PublisherVO<SquwkPointVO> vo)
  {
    return new SquwkPublishQueue(this, vo.getCacheWarningSize(), vo.getCacheDiscardSize());
  }

  public void initialize()
  {
    super.initialize(new SquwkSendThread());
    try
    {
      this.squwkPoints = ((List)this.squwkClient.send(new PointListRequest()));
    }
    catch (SquwkException e) {
      LOG.warn("Request exception", e);
      Common.eventManager.raiseEvent(this.requestExceptionEventType, System.currentTimeMillis(), true, 2, toTranslatableMessage(e), createEventContext());
    }
  }

  protected void pointInitialized(PublishedPointRT<SquwkPointVO> rt)
  {
    super.pointInitialized(rt);
    String guid;
    if (this.squwkClient != null) {
      guid = ((SquwkPointVO)rt.getVo()).getGuid();
      for (Point point : this.squwkPoints)
        if (point.getGuid().equals(guid)) {
          ((SquwkPointVO)rt.getVo()).setDataType(point.getDataType());
          break;
        }
    }
  }

  PublishQueue<SquwkPointVO> getPublishQueue()
  {
    return this.queue;
  }

  private Object coerceDataValue(DataValue mv, DataType dataType)
  {
    if (dataType == null) {
      return mv.getObjectValue();
    }
    switch (dataType.ordinal()) {
    case 1:
      return null;
    case 2:
      return Boolean.valueOf(mv.getBooleanValue());
    case 3:
      return Integer.valueOf(mv.getIntegerValue());
    case 4:
      return Double.valueOf(mv.getDoubleValue());
    }
    return mv.getStringValue();
  }

  private TranslatableMessage toTranslatableMessage(SquwkException e)
  {
    return new TranslatableMessage("common.default", new Object[] { e.getMessage() });
  }

  class ResultHandler extends ServiceResultObjectHandler
  {
    private SquwkException squwkException;

    ResultHandler()
    {
    }

    public <T> void exception(ServiceRequest<T> serviceRequest, SquwkException squwkException)
    {
      if (this.squwkException != null)
        this.squwkException = squwkException;
    }

    public <T> void handleServiceResultObject(ServiceRequest<T> request, T result)
    {
    }

    public SquwkException getSquwkException()
    {
      return this.squwkException;
    }
  }

  class SquwkSendThread extends SendThread
  {
    SquwkSendThread()
    {
      super("SquwkSendThread");
    }

    protected void runImpl()
    {
      while (isRunning()) {
        List entries = SquwkSenderRT.this.getPublishQueue().get(100);

        if (entries != null) {
          if (send(entries)) {
            SquwkSenderRT.this.getPublishQueue().removeAll(entries);
          }
          else
            sleepImpl(2000L);
        }
        else
          waitImpl(10000L);
      }
    }

    private boolean send(List<PublishQueueEntry<SquwkPointVO>> entries)
    {
      List reqs = new ArrayList();

      for (PublishQueueEntry entry : entries) {
        SquwkPointVO vo = (SquwkPointVO)entry.getVo();
        PointValueTime pvt = entry.getPvt();
        SampleAppendRequest req = new SampleAppendRequest(vo.getGuid(), pvt.getTime(), SquwkSenderRT.this.coerceDataValue(pvt.getValue(), vo.getDataType()));

        reqs.add(req);
      }

      SquwkSenderRT.ResultHandler resultHandler = new SquwkSenderRT.ResultHandler();
      try
      {
        SquwkSenderRT.this.squwkClient.sendBatch(reqs, resultHandler);

        Common.eventManager.returnToNormal(SquwkSenderRT.this.requestExceptionEventType, System.currentTimeMillis());

        if (resultHandler.getSquwkException() != null) {
          SquwkSenderRT.LOG.warn("Service exception", resultHandler.getSquwkException());
          Common.eventManager.raiseEvent(SquwkSenderRT.this.serviceExceptionEventType, System.currentTimeMillis(), false, 2, SquwkSenderRT.this.toTranslatableMessage(resultHandler.getSquwkException()), SquwkSenderRT.this.createEventContext());
        }

      }
      catch (SquwkException e)
      {
        SquwkSenderRT.LOG.warn("Request exception", e);
        Common.eventManager.raiseEvent(SquwkSenderRT.this.requestExceptionEventType, System.currentTimeMillis(), true, 2, SquwkSenderRT.this.toTranslatableMessage(e), SquwkSenderRT.this.createEventContext());

        return false;
      }

      return true;
    }
  }
}