package com.serotonin.m2m2.persistent.pub;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableJsonException;
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

public class PersistentSenderVO extends PublisherVO<PersistentPointVO>
{
  private static final ExportCodes EVENT_CODES = new ExportCodes();
  public static final int SYNC_TYPE_NONE = 0;
  public static final int SYNC_TYPE_DAILY = 1;
  public static final int SYNC_TYPE_WEEKLY = 2;
  public static final int SYNC_TYPE_MONTHLY = 3;
  private static ExportCodes SYNC_TYPE_CODES;

  @JsonProperty
  private String host;

  @JsonProperty
  private int port;

  @JsonProperty
  private String authorizationKey;

  @JsonProperty
  private String xidPrefix;
  private int syncType = 1;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  protected void getEventTypesImpl(List<EventTypeVO> eventTypes)
  {
    eventTypes.add(new EventTypeVO("PUBLISHER", null, getId(), 11, new TranslatableMessage("event.pb.persistent.connectionFailed"), 2));

    eventTypes.add(new EventTypeVO("PUBLISHER", null, getId(), 12, new TranslatableMessage("event.pb.persistent.protocolFailure"), 2));

    eventTypes.add(new EventTypeVO("PUBLISHER", null, getId(), 13, new TranslatableMessage("event.pb.persistent.connectionAborted"), 2));

    eventTypes.add(new EventTypeVO("PUBLISHER", null, getId(), 14, new TranslatableMessage("event.pb.persistent.connectionLost"), 2));

    eventTypes.add(new EventTypeVO("PUBLISHER", null, getId(), 15, new TranslatableMessage("event.pb.persistent.syncCompleted"), 0));
  }

  public ExportCodes getEventCodes()
  {
    return EVENT_CODES;
  }

  public TranslatableMessage getConfigDescription()
  {
    return new TranslatableMessage("common.default", new Object[] { this.host });
  }

  public PublisherRT<PersistentPointVO> createPublisherRT()
  {
    return new PersistentSenderRT(this);
  }

  protected PersistentPointVO createPublishedPointInstance()
  {
    return new PersistentPointVO();
  }

  public String getHost()
  {
    return this.host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return this.port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getAuthorizationKey() {
    return this.authorizationKey;
  }

  public void setAuthorizationKey(String authorizationKey) {
    this.authorizationKey = authorizationKey;
  }

  public String getXidPrefix() {
    return this.xidPrefix;
  }

  public void setXidPrefix(String xidPrefix) {
    this.xidPrefix = xidPrefix;
  }

  public int getSyncType() {
    return this.syncType;
  }

  public void setSyncType(int syncType) {
    this.syncType = syncType;
  }

  public void validate(ProcessResult response)
  {
    super.validate(response);

    if (StringUtils.isBlank(this.host))
      response.addContextualMessage("host", "validate.required", new Object[0]);
    if ((this.port <= 0) || (this.port >= 65536)) {
      response.addContextualMessage("port", "validate.illegalValue", new Object[0]);
    }
    if (!SYNC_TYPE_CODES.isValidId(this.syncType, new int[0]))
      response.addContextualMessage("syncType", "validate.invalidValue", new Object[0]);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    SerializationHelper.writeSafeUTF(out, this.host);
    out.writeInt(this.port);
    SerializationHelper.writeSafeUTF(out, this.authorizationKey);
    SerializationHelper.writeSafeUTF(out, this.xidPrefix);
    out.writeInt(this.syncType);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.host = SerializationHelper.readSafeUTF(in);
      this.port = in.readInt();
      this.authorizationKey = SerializationHelper.readSafeUTF(in);
      this.xidPrefix = SerializationHelper.readSafeUTF(in);
      this.syncType = in.readInt();
    }
  }

  public void jsonWrite(ObjectWriter writer) throws IOException, JsonException
  {
    super.jsonWrite(writer);
    writer.writeEntry("syncType", SYNC_TYPE_CODES.getCode(this.syncType));
  }

  public void jsonRead(JsonReader reader, JsonObject jsonObject) throws JsonException
  {
    super.jsonRead(reader, jsonObject);

    String text = jsonObject.getString("syncType");
    if (text != null) {
      this.syncType = SYNC_TYPE_CODES.getId(text, new int[0]);
      if (this.syncType == -1)
        throw new TranslatableJsonException("emport.error.invalid", new Object[] { "syncType", text, SYNC_TYPE_CODES.getCodeList(new int[0]) });
    }
  }

  static
  {
    PublisherVO.addDefaultEventCodes(EVENT_CODES);
    EVENT_CODES.addElement(11, "CONNECTION_FAILED_EVENT");
    EVENT_CODES.addElement(12, "PROTOCOL_FAILURE_EVENT");
    EVENT_CODES.addElement(13, "CONNECTION_ABORTED_EVENT");
    EVENT_CODES.addElement(14, "CONNECTION_LOST_EVENT");
    EVENT_CODES.addElement(15, "SYNC_COMPLETION_EVENT");

    SYNC_TYPE_CODES = new ExportCodes();

    SYNC_TYPE_CODES.addElement(0, "SYNC_TYPE_NONE", "publisherEdit.persistent.sync.none");
    SYNC_TYPE_CODES.addElement(1, "SYNC_TYPE_DAILY", "publisherEdit.persistent.sync.daily");
    SYNC_TYPE_CODES.addElement(2, "SYNC_TYPE_WEEKLY", "publisherEdit.persistent.sync.weekly");
    SYNC_TYPE_CODES.addElement(3, "SYNC_TYPE_MONTHLY", "publisherEdit.persistent.sync.monthly");
  }
}