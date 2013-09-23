package com.serotonin.m2m2.http.vo;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.http.rt.HttpRetrieverDataSourceRT;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.DataSourceRT;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;
import com.serotonin.m2m2.vo.event.EventTypeVO;
import com.serotonin.util.SerializationHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class HttpRetrieverDataSourceVO extends DataSourceVO<HttpRetrieverDataSourceVO>
{
  private static final ExportCodes EVENT_CODES = new ExportCodes();

  @JsonProperty
  private String url;
  private int updatePeriodType = 2;

  @JsonProperty
  private int updatePeriods = 5;

  @JsonProperty
  private boolean quantize;

  @JsonProperty
  private int timeoutSeconds = 30;

  @JsonProperty
  private int retries = 2;

  @JsonProperty
  private String setPointUrl;
  private static final long serialVersionUID = -1L;
  private static final int version = 3;

  protected void addEventTypes(List<EventTypeVO> ets)
  {
    ets.add(createEventType(1, new TranslatableMessage("event.ds.dataRetrieval")));

    ets.add(createEventType(2, new TranslatableMessage("event.ds.dataParse")));

    ets.add(createEventType(3, new TranslatableMessage("event.ds.setPointFail")));
  }

  public ExportCodes getEventCodes()
  {
    return EVENT_CODES;
  }

  public TranslatableMessage getConnectionDescription()
  {
    return new TranslatableMessage("common.default", new Object[] { com.serotonin.util.StringUtils.truncate(this.url, 30, " ...") });
  }

  public DataSourceRT createDataSourceRT()
  {
    return new HttpRetrieverDataSourceRT(this);
  }

  public HttpRetrieverPointLocatorVO createPointLocator()
  {
    return new HttpRetrieverPointLocatorVO();
  }

  public String getUrl()
  {
    return this.url;
  }

  public void setUrl(String url) {
    this.url = url;
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

  public boolean isQuantize() {
    return this.quantize;
  }

  public void setQuantize(boolean quantize) {
    this.quantize = quantize;
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

  public String getSetPointUrl() {
    return this.setPointUrl;
  }

  public void setSetPointUrl(String setPointUrl) {
    this.setPointUrl = setPointUrl;
  }

  public void validate(ProcessResult response)
  {
    super.validate(response);
    if (org.apache.commons.lang3.StringUtils.isBlank(this.url))
      response.addContextualMessage("url", "validate.required", new Object[0]);
    else {
      try {
        new URL(this.url);
      }
      catch (MalformedURLException e) {
        response.addContextualMessage("url", "validate.invalidValue", new Object[0]);
      }
    }
    if (!Common.TIME_PERIOD_CODES.isValidId(this.updatePeriodType, new int[0]))
      response.addContextualMessage("updatePeriodType", "validate.invalidValue", new Object[0]);
    if (this.updatePeriods <= 0)
      response.addContextualMessage("updatePeriods", "validate.greaterThanZero", new Object[0]);
    if (this.timeoutSeconds <= 0)
      response.addContextualMessage("updatePeriods", "validate.greaterThanZero", new Object[0]);
    if (this.retries < 0) {
      response.addContextualMessage("retries", "validate.cannotBeNegative", new Object[0]);
    }
    if (!org.apache.commons.lang3.StringUtils.isBlank(this.setPointUrl))
      try {
        new URL(this.setPointUrl);
      }
      catch (MalformedURLException e) {
        response.addContextualMessage("setPointUrl", "validate.invalidValue", new Object[0]);
      }
  }

  protected void addPropertiesImpl(List<TranslatableMessage> list)
  {
    AuditEventType.addPeriodMessage(list, "dsEdit.updatePeriod", this.updatePeriodType, this.updatePeriods);
    AuditEventType.addPropertyMessage(list, "dsEdit.quantize", this.quantize);
    AuditEventType.addPropertyMessage(list, "dsEdit.httpRetriever.url", this.url);
    AuditEventType.addPropertyMessage(list, "dsEdit.httpRetriever.timeout", Integer.valueOf(this.timeoutSeconds));
    AuditEventType.addPropertyMessage(list, "dsEdit.httpRetriever.retries", Integer.valueOf(this.retries));
    AuditEventType.addPropertyMessage(list, "http.dsEdit.setPointUrl", this.setPointUrl);
  }

  protected void addPropertyChangesImpl(List<TranslatableMessage> list, HttpRetrieverDataSourceVO from)
  {
    AuditEventType.maybeAddPeriodChangeMessage(list, "dsEdit.updatePeriod", from.updatePeriodType, from.updatePeriods, this.updatePeriodType, this.updatePeriods);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.quantize", from.quantize, this.quantize);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.httpRetriever.url", from.url, this.url);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.httpRetriever.timeout", from.timeoutSeconds, this.timeoutSeconds);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.httpRetriever.retries", from.retries, this.retries);
    AuditEventType.maybeAddPropertyChangeMessage(list, "http.dsEdit.setPointUrl", from.setPointUrl, this.setPointUrl);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(3);
    SerializationHelper.writeSafeUTF(out, this.url);
    out.writeInt(this.updatePeriodType);
    out.writeInt(this.updatePeriods);
    out.writeBoolean(this.quantize);
    out.writeInt(this.timeoutSeconds);
    out.writeInt(this.retries);
    SerializationHelper.writeSafeUTF(out, this.setPointUrl);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.url = SerializationHelper.readSafeUTF(in);
      this.updatePeriodType = in.readInt();
      this.updatePeriods = in.readInt();
      this.quantize = false;
      this.timeoutSeconds = in.readInt();
      this.retries = in.readInt();
      this.setPointUrl = null;
    }
    else if (ver == 2) {
      this.url = SerializationHelper.readSafeUTF(in);
      this.updatePeriodType = in.readInt();
      this.updatePeriods = in.readInt();
      this.quantize = in.readBoolean();
      this.timeoutSeconds = in.readInt();
      this.retries = in.readInt();
      this.setPointUrl = null;
    }
    else if (ver == 3) {
      this.url = SerializationHelper.readSafeUTF(in);
      this.updatePeriodType = in.readInt();
      this.updatePeriods = in.readInt();
      this.quantize = in.readBoolean();
      this.timeoutSeconds = in.readInt();
      this.retries = in.readInt();
      this.setPointUrl = SerializationHelper.readSafeUTF(in);
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
    EVENT_CODES.addElement(1, "DATA_RETRIEVAL_FAILURE");
    EVENT_CODES.addElement(2, "PARSE_EXCEPTION");
    EVENT_CODES.addElement(3, "SET_POINT_FAILURE");
  }
}