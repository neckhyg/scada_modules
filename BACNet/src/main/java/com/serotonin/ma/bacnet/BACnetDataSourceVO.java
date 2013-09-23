package com.serotonin.ma.bacnet;

import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;
import com.serotonin.m2m2.vo.event.EventTypeVO;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public abstract class BACnetDataSourceVO<T extends BACnetDataSourceVO<?>> extends DataSourceVO<T>
{
  private static final ExportCodes EVENT_CODES = new ExportCodes();

  protected int updatePeriodType = 2;

  @JsonProperty
  protected int updatePeriods = 5;

  @JsonProperty
  protected int deviceId;

  @JsonProperty
  boolean strict;

  @JsonProperty
  int timeout;

  @JsonProperty
  int segTimeout;

  @JsonProperty
  int segWindow;

  @JsonProperty
  int retries;

  @JsonProperty
  int covSubscriptionTimeoutMinutes = 60;

  @JsonProperty
  int maxReadMultipleReferencesSegmented = 200;

  @JsonProperty
  int maxReadMultipleReferencesNonsegmented = 20;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  protected void addEventTypes(List<EventTypeVO> ets)
  {
    ets.add(createEventType(1, new TranslatableMessage("event.ds.initialization")));

    ets.add(createEventType(2, new TranslatableMessage("event.ds.message")));
    ets.add(createEventType(3, new TranslatableMessage("event.ds.device")));
  }

  public ExportCodes getEventCodes()
  {
    return EVENT_CODES;
  }

  public TranslatableMessage getConnectionDescription()
  {
    return new TranslatableMessage("mod.bacnet.dsconn", new Object[] { Integer.valueOf(this.deviceId) });
  }

  public BACnetDataSourceVO()
  {
    this.strict = false;
    this.timeout = 6000;
    this.segTimeout = 5000;
    this.segWindow = 5;
    this.retries = 2;
  }

  public int getUpdatePeriodType() {
    return this.updatePeriodType;
  }

  public void setUpdatePeriodType(int updatePeriodType) {
    this.updatePeriodType = updatePeriodType;
  }

  public int getUpdatePeriods() {
    return this.updatePeriods;
  }

  public void setUpdatePeriods(int updatePeriods) {
    this.updatePeriods = updatePeriods;
  }

  public int getDeviceId() {
    return this.deviceId;
  }

  public void setDeviceId(int deviceId) {
    this.deviceId = deviceId;
  }

  public boolean isStrict() {
    return this.strict;
  }

  public void setStrict(boolean strict) {
    this.strict = strict;
  }

  public int getCovSubscriptionTimeoutMinutes() {
    return this.covSubscriptionTimeoutMinutes;
  }

  public void setCovSubscriptionTimeoutMinutes(int covSubscriptionTimeoutMinutes) {
    this.covSubscriptionTimeoutMinutes = covSubscriptionTimeoutMinutes;
  }

  public int getTimeout() {
    return this.timeout;
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }

  public int getSegTimeout() {
    return this.segTimeout;
  }

  public void setSegTimeout(int segTimeout) {
    this.segTimeout = segTimeout;
  }

  public int getSegWindow() {
    return this.segWindow;
  }

  public void setSegWindow(int segWindow) {
    this.segWindow = segWindow;
  }

  public int getRetries() {
    return this.retries;
  }

  public void setRetries(int retries) {
    this.retries = retries;
  }

  public int getMaxReadMultipleReferencesSegmented() {
    return this.maxReadMultipleReferencesSegmented;
  }

  public void setMaxReadMultipleReferencesSegmented(int maxReadMultipleReferencesSegmented) {
    this.maxReadMultipleReferencesSegmented = maxReadMultipleReferencesSegmented;
  }

  public int getMaxReadMultipleReferencesNonsegmented() {
    return this.maxReadMultipleReferencesNonsegmented;
  }

  public void setMaxReadMultipleReferencesNonsegmented(int maxReadMultipleReferencesNonsegmented) {
    this.maxReadMultipleReferencesNonsegmented = maxReadMultipleReferencesNonsegmented;
  }

  public void validate(ProcessResult response)
  {
    super.validate(response);

    if (!Common.TIME_PERIOD_CODES.isValidId(this.updatePeriodType, new int[0])) {
      response.addContextualMessage("updatePeriodType", "validate.invalidValue", new Object[0]);
    }
    if (this.updatePeriods <= 0)
      response.addContextualMessage("updatePeriods", "validate.cannotBeNegative", new Object[0]);
    try
    {
      new ObjectIdentifier(ObjectType.device, this.deviceId);
    }
    catch (IllegalArgumentException e) {
      response.addContextualMessage("deviceId", "validate.illegalValue", new Object[0]);
    }

    if (this.timeout < 0)
      response.addContextualMessage("timeout", "validate.cannotBeNegative", new Object[0]);
    if (this.segTimeout < 0)
      response.addContextualMessage("segTimeout", "validate.cannotBeNegative", new Object[0]);
    if (this.segWindow < 1)
      response.addContextualMessage("segWindow", "validate.greaterThanZero", new Object[0]);
    if (this.retries < 0)
      response.addContextualMessage("retries", "validate.cannotBeNegative", new Object[0]);
    if (this.covSubscriptionTimeoutMinutes < 1)
      response.addContextualMessage("covSubscriptionTimeoutMinutes", "validate.greaterThanZero", new Object[0]);
    if (this.maxReadMultipleReferencesSegmented < 1)
      response.addContextualMessage("maxReadMultipleReferencesSegmented", "validate.greaterThanZero", new Object[0]);
    if (this.maxReadMultipleReferencesNonsegmented < 1)
      response.addContextualMessage("maxReadMultipleReferencesNonsegmented", "validate.greaterThanZero", new Object[0]);
  }

  public void addPropertiesImpl(List<TranslatableMessage> list)
  {
    AuditEventType.addPeriodMessage(list, "dsEdit.updatePeriod", this.updatePeriodType, this.updatePeriods);
    AuditEventType.addPropertyMessage(list, "mod.bacnet.deviceId", Integer.valueOf(this.deviceId));
    AuditEventType.addPropertyMessage(list, "mod.bacnet.strict", this.strict);
    AuditEventType.addPropertyMessage(list, "mod.bacnet.timeout", Integer.valueOf(this.timeout));
    AuditEventType.addPropertyMessage(list, "mod.bacnet.segmentTimeout", Integer.valueOf(this.segTimeout));
    AuditEventType.addPropertyMessage(list, "mod.bacnet.segmentWindow", Integer.valueOf(this.segWindow));
    AuditEventType.addPropertyMessage(list, "mod.bacnet.retries", Integer.valueOf(this.retries));
    AuditEventType.addPropertyMessage(list, "mod.bacnet.covLease", Integer.valueOf(this.covSubscriptionTimeoutMinutes));
    AuditEventType.addPropertyMessage(list, "mod.bacnet.maxReadMultSeg", Integer.valueOf(this.maxReadMultipleReferencesSegmented));
    AuditEventType.addPropertyMessage(list, "mod.bacnet.maxReadMultNonseg", Integer.valueOf(this.maxReadMultipleReferencesNonsegmented));
  }

  public void addPropertyChangesImpl(List<TranslatableMessage> list, T from)
  {
    AuditEventType.maybeAddPeriodChangeMessage(list, "dsEdit.updatePeriod", from.updatePeriodType, from.updatePeriods, this.updatePeriodType, this.updatePeriods);

    AuditEventType.maybeAddPropertyChangeMessage(list, "mod.bacnet.deviceId", from.deviceId, this.deviceId);
    AuditEventType.maybeAddPropertyChangeMessage(list, "mod.bacnet.strict", from.strict, this.strict);
    AuditEventType.maybeAddPropertyChangeMessage(list, "mod.bacnet.timeout", from.timeout, this.timeout);
    AuditEventType.maybeAddPropertyChangeMessage(list, "mod.bacnet.segmentTimeout", from.segTimeout, this.segTimeout);
    AuditEventType.maybeAddPropertyChangeMessage(list, "mod.bacnet.segmentWindow", from.segWindow, this.segWindow);
    AuditEventType.maybeAddPropertyChangeMessage(list, "mod.bacnet.retries", from.retries, this.retries);
    AuditEventType.maybeAddPropertyChangeMessage(list, "mod.bacnet.covLease", from.covSubscriptionTimeoutMinutes, this.covSubscriptionTimeoutMinutes);

    AuditEventType.maybeAddPropertyChangeMessage(list, "mod.bacnet.maxReadMultSeg", from.maxReadMultipleReferencesSegmented, this.maxReadMultipleReferencesSegmented);

    AuditEventType.maybeAddPropertyChangeMessage(list, "mod.bacnet.maxReadMultNonseg", from.maxReadMultipleReferencesNonsegmented, this.maxReadMultipleReferencesNonsegmented);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    out.writeInt(this.updatePeriodType);
    out.writeInt(this.updatePeriods);
    out.writeInt(this.deviceId);
    out.writeBoolean(this.strict);
    out.writeInt(this.timeout);
    out.writeInt(this.segTimeout);
    out.writeInt(this.segWindow);
    out.writeInt(this.retries);
    out.writeInt(this.covSubscriptionTimeoutMinutes);
    out.writeInt(this.maxReadMultipleReferencesSegmented);
    out.writeInt(this.maxReadMultipleReferencesNonsegmented);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.updatePeriodType = in.readInt();
      this.updatePeriods = in.readInt();
      this.deviceId = in.readInt();
      this.strict = in.readBoolean();
      this.timeout = in.readInt();
      this.segTimeout = in.readInt();
      this.segWindow = in.readInt();
      this.retries = in.readInt();
      this.covSubscriptionTimeoutMinutes = in.readInt();
      this.maxReadMultipleReferencesSegmented = in.readInt();
      this.maxReadMultipleReferencesNonsegmented = in.readInt();
    }
  }

  public void jsonWrite(ObjectWriter writer) throws IOException, JsonException
  {
    super.jsonWrite(writer);
    writeUpdatePeriodType(writer, this.updatePeriodType);
  }

  public void jsonRead(JsonReader reader, JsonObject jsonObject) throws JsonException
  {
    super.jsonRead(reader, jsonObject);
    Integer value = readUpdatePeriodType(jsonObject);
    if (value != null)
      this.updatePeriodType = value.intValue();
  }

  static
  {
    EVENT_CODES.addElement(1, "INITIALIZATION_EXCEPTION");
    EVENT_CODES.addElement(2, "MESSAGE_EXCEPTION");
    EVENT_CODES.addElement(3, "DEVICE_EXCEPTION");
  }
}