package com.serotonin.m2m2.pachube.ds;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.Common;
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
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class PachubeDataSourceVO extends DataSourceVO<PachubeDataSourceVO>
{
  private static final ExportCodes EVENT_CODES = new ExportCodes();

  @JsonProperty
  private String apiKey;
  private int updatePeriodType = 2;

  @JsonProperty
  private int updatePeriods = 5;

  @JsonProperty
  private int timeoutSeconds = 30;

  @JsonProperty
  private int retries = 2;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  protected void addEventTypes(List<EventTypeVO> ets)
  {
    ets.add(createEventType(1, new TranslatableMessage("event.ds.dataRetrieval")));

    ets.add(createEventType(2, new TranslatableMessage("event.ds.dataParse")));

    ets.add(createEventType(3, new TranslatableMessage("event.ds.pointWrite")));
  }

  public ExportCodes getEventCodes()
  {
    return EVENT_CODES;
  }

  public TranslatableMessage getConnectionDescription()
  {
    return new TranslatableMessage("common.noMessage");
  }

  public DataSourceRT createDataSourceRT()
  {
    return new PachubeDataSourceRT(this);
  }

  public PachubePointLocatorVO createPointLocator()
  {
    return new PachubePointLocatorVO();
  }

  public String getApiKey()
  {
    return this.apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
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
    if (!Common.TIME_PERIOD_CODES.isValidId(this.updatePeriodType, new int[0]))
      response.addContextualMessage("updatePeriodType", "validate.invalidValue", new Object[0]);
    if (this.updatePeriods <= 0)
      response.addContextualMessage("updatePeriods", "validate.greaterThanZero", new Object[0]);
    if (this.timeoutSeconds <= 0)
      response.addContextualMessage("timeoutSeconds", "validate.greaterThanZero", new Object[0]);
    if (this.retries < 0)
      response.addContextualMessage("retries", "validate.cannotBeNegative", new Object[0]);
  }

  protected void addPropertiesImpl(List<TranslatableMessage> list)
  {
    AuditEventType.addPropertyMessage(list, "dsEdit.pachube.apiKey", this.apiKey);
    AuditEventType.addPeriodMessage(list, "dsEdit.updatePeriod", this.updatePeriodType, this.updatePeriods);
    AuditEventType.addPropertyMessage(list, "dsEdit.pachube.timeout", Integer.valueOf(this.timeoutSeconds));
    AuditEventType.addPropertyMessage(list, "dsEdit.pachube.retries", Integer.valueOf(this.retries));
  }

  protected void addPropertyChangesImpl(List<TranslatableMessage> list, PachubeDataSourceVO from)
  {
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.pachube.apiKey", from.apiKey, this.apiKey);
    AuditEventType.maybeAddPeriodChangeMessage(list, "dsEdit.updatePeriod", from.updatePeriodType, from.updatePeriods, this.updatePeriodType, this.updatePeriods);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.pachube.timeout", from.timeoutSeconds, this.timeoutSeconds);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.pachube.retries", from.retries, this.retries);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    SerializationHelper.writeSafeUTF(out, this.apiKey);
    out.writeInt(this.updatePeriodType);
    out.writeInt(this.updatePeriods);
    out.writeInt(this.timeoutSeconds);
    out.writeInt(this.retries);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.apiKey = SerializationHelper.readSafeUTF(in);
      this.updatePeriodType = in.readInt();
      this.updatePeriods = in.readInt();
      this.timeoutSeconds = in.readInt();
      this.retries = in.readInt();
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
    EVENT_CODES.addElement(3, "POINT_WRITE_EXCEPTION");
  }
}