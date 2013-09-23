package com.serotonin.m2m2.squwk.pub;

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

public class SquwkSenderVO extends PublisherVO<SquwkPointVO>
{
  private static final ExportCodes EVENT_CODES = new ExportCodes();

  @JsonProperty
  private String accessKey;

  @JsonProperty
  private String secretKey;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  protected void getEventTypesImpl(List<EventTypeVO> eventTypes)
  {
    eventTypes.add(new EventTypeVO("PUBLISHER", null, getId(), 11, new TranslatableMessage("event.pb.squwk.request"), 2));

    eventTypes.add(new EventTypeVO("PUBLISHER", null, getId(), 12, new TranslatableMessage("event.pb.squwk.service"), 2));
  }

  public ExportCodes getEventCodes()
  {
    return EVENT_CODES;
  }

  public TranslatableMessage getConfigDescription()
  {
    return new TranslatableMessage("common.noMessage");
  }

  public PublisherRT<SquwkPointVO> createPublisherRT()
  {
    return new SquwkSenderRT(this);
  }

  protected SquwkPointVO createPublishedPointInstance()
  {
    return new SquwkPointVO();
  }

  public String getAccessKey()
  {
    return this.accessKey;
  }

  public void setAccessKey(String accessKey) {
    this.accessKey = accessKey;
  }

  public String getSecretKey() {
    return this.secretKey;
  }

  public void setSecretKey(String secretKey) {
    this.secretKey = secretKey;
  }

  public void validate(ProcessResult response)
  {
    super.validate(response);

    if (StringUtils.isBlank(this.accessKey))
      response.addContextualMessage("accessKey", "validate.required", new Object[0]);
    if (StringUtils.isBlank(this.secretKey)) {
      response.addContextualMessage("secretKey", "validate.required", new Object[0]);
    }
    for (SquwkPointVO point : this.points)
      if (StringUtils.isBlank(point.getGuid())) {
        response.addContextualMessage("points", "validate.squwk.guidRequired", new Object[0]);
        break;
      }
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    SerializationHelper.writeSafeUTF(out, this.accessKey);
    SerializationHelper.writeSafeUTF(out, this.secretKey);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.accessKey = SerializationHelper.readSafeUTF(in);
      this.secretKey = SerializationHelper.readSafeUTF(in);
    }
  }

  static
  {
    PublisherVO.addDefaultEventCodes(EVENT_CODES);
    EVENT_CODES.addElement(11, "REQUEST_EXCEPTION_EVENT");
    EVENT_CODES.addElement(12, "SERVICE_EXCEPTION_EVENT");
  }
}