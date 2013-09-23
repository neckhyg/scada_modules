package com.serotonin.m2m2.pachube.pub;

import com.serotonin.json.spi.JsonProperty;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.publish.PublisherRT;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.vo.event.EventTypeVO;
import com.serotonin.m2m2.vo.publish.PublisherVO;
import com.serotonin.util.SerializationHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class PachubeSenderVO extends PublisherVO<PachubePointVO>
{
  private static final ExportCodes EVENT_CODES = new ExportCodes();

  @JsonProperty
  private String apiKey;

  @JsonProperty
  private int timeoutSeconds = 30;

  @JsonProperty
  private int retries = 2;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  protected void getEventTypesImpl(List<EventTypeVO> eventTypes)
  {
    eventTypes.add(new EventTypeVO("PUBLISHER", null, getId(), 11, new TranslatableMessage("event.pb.httpSend"), 2));
  }

  public ExportCodes getEventCodes()
  {
    return EVENT_CODES;
  }

  public TranslatableMessage getConfigDescription()
  {
    return new TranslatableMessage("common.noMessage");
  }

  public PublisherRT<PachubePointVO> createPublisherRT()
  {
    return new PachubeSenderRT(this);
  }

  protected PachubePointVO createPublishedPointInstance()
  {
    return new PachubePointVO();
  }

  public String getApiKey()
  {
    return this.apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public int getTimeoutSeconds() {
    return this.timeoutSeconds;
  }

  public void setTimeoutSeconds(int timeoutSeconds) {
    this.timeoutSeconds = timeoutSeconds;
  }

  public int getRetries() {
    return this.retries;
  }

  public void setRetries(int retries) {
    this.retries = retries;
  }

  public void validate(ProcessResult response)
  {
    super.validate(response);

    if (StringUtils.isBlank(this.apiKey))
      response.addContextualMessage("apiKey", "validate.required", new Object[0]);
    if (this.timeoutSeconds <= 0)
      response.addContextualMessage("updatePeriods", "validate.greaterThanZero", new Object[0]);
    if (this.retries < 0) {
      response.addContextualMessage("retries", "validate.cannotBeNegative", new Object[0]);
    }
    for (PachubePointVO point : this.points)
      if (StringUtils.isBlank(point.getDataStreamId())) {
        response.addContextualMessage("points", "validate.pachube.dataStreadIdRequired", new Object[0]);
        break;
      }
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    SerializationHelper.writeSafeUTF(out, this.apiKey);
    out.writeInt(this.timeoutSeconds);
    out.writeInt(this.retries);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.apiKey = SerializationHelper.readSafeUTF(in);
      this.timeoutSeconds = in.readInt();
      this.retries = in.readInt();
    }
  }

  static
  {
    PublisherVO.addDefaultEventCodes(EVENT_CODES);
    EVENT_CODES.addElement(11, "SEND_EXCEPTION_EVENT");
  }
}