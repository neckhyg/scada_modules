package com.serotonin.m2m2.bacnet;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.DataSourceRT;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;
import com.serotonin.m2m2.vo.event.EventTypeVO;
import com.serotonin.util.SerializationHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class BACnetIPDataSourceVO extends DataSourceVO<BACnetIPDataSourceVO>
{
  private int updatePeriodType = 2;

  @JsonProperty
  private int updatePeriods = 5;

  @JsonProperty
  private int deviceId;

  @JsonProperty
  private String broadcastAddress;

  @JsonProperty
  private int port;

  @JsonProperty
  private int timeout;

  @JsonProperty
  private int segTimeout;

  @JsonProperty
  private int segWindow;

  @JsonProperty
  private int retries;

  @JsonProperty
  private int covSubscriptionTimeoutMinutes = 60;

  @JsonProperty
  private int maxReadMultipleReferencesSegmented = 200;

  @JsonProperty
  private int maxReadMultipleReferencesNonsegmented = 20;
  private static final long serialVersionUID = -1L;
  private static final int version = 2;

  protected void addEventTypes(List<EventTypeVO> ets)
  {
  }

  public ExportCodes getEventCodes()
  {
    return null;
  }

  public TranslatableMessage getConnectionDescription()
  {
    return null;
  }

  public DataSourceRT createDataSourceRT()
  {
    return null;
  }

  public BACnetIPPointLocatorVO createPointLocator()
  {
    return new BACnetIPPointLocatorVO();
  }

  public int getUpdatePeriodType()
  {
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

  public String getBroadcastAddress() {
    return this.broadcastAddress;
  }

  public void setBroadcastAddress(String broadcastAddress) {
    this.broadcastAddress = broadcastAddress;
  }

  public int getPort() {
    return this.port;
  }

  public void setPort(int port) {
    this.port = port;
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
  }

  public void addPropertiesImpl(List<TranslatableMessage> list)
  {
  }

  public void addPropertyChangesImpl(List<TranslatableMessage> list, BACnetIPDataSourceVO from)
  {
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(2);
    out.writeInt(this.updatePeriodType);
    out.writeInt(this.updatePeriods);
    out.writeInt(this.deviceId);
    SerializationHelper.writeSafeUTF(out, this.broadcastAddress);
    out.writeInt(this.port);
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
      this.broadcastAddress = SerializationHelper.readSafeUTF(in);
      this.port = in.readInt();
      this.timeout = in.readInt();
      this.segTimeout = in.readInt();
      this.segWindow = in.readInt();
      this.retries = in.readInt();
      this.covSubscriptionTimeoutMinutes = in.readInt();
      this.maxReadMultipleReferencesSegmented = 200;
      this.maxReadMultipleReferencesNonsegmented = 20;
    }
    else if (ver == 2) {
      this.updatePeriodType = in.readInt();
      this.updatePeriods = in.readInt();
      this.deviceId = in.readInt();
      this.broadcastAddress = SerializationHelper.readSafeUTF(in);
      this.port = in.readInt();
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
  }

  public void jsonRead(JsonReader reader, JsonObject jsonObject) throws JsonException
  {
    super.jsonRead(reader, jsonObject);
  }
}