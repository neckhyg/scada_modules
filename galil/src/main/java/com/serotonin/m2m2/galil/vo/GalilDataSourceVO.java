package com.serotonin.m2m2.galil.vo;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.galil.rt.GalilDataSourceRT;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.DataSourceRT;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;
import com.serotonin.m2m2.vo.dataSource.PointLocatorVO;
import com.serotonin.m2m2.vo.event.EventTypeVO;
import com.serotonin.util.SerializationHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class GalilDataSourceVO extends DataSourceVO<GalilDataSourceVO>
{
  private static final ExportCodes EVENT_CODES = new ExportCodes();

  @JsonProperty
  private String host;

  @JsonProperty
  private int port = 2000;

  @JsonProperty
  private int timeout = 1000;

  @JsonProperty
  private int retries = 2;

  private int updatePeriodType = 2;

  @JsonProperty
  private int updatePeriods = 5;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  protected void addEventTypes(List<EventTypeVO> ets)
  {
    ets.add(createEventType(1, new TranslatableMessage("event.ds.dataSource")));

    ets.add(createEventType(2, new TranslatableMessage("event.ds.pointRead")));

    ets.add(createEventType(3, new TranslatableMessage("event.ds.pointWrite")));
  }

  public ExportCodes getEventCodes()
  {
    return EVENT_CODES;
  }

  public PointLocatorVO createPointLocator()
  {
    return new GalilPointLocatorVO();
  }

  public TranslatableMessage getConnectionDescription()
  {
    return new TranslatableMessage("common.default", new Object[] { this.host + ":" + this.port });
  }

  public DataSourceRT createDataSourceRT()
  {
    return new GalilDataSourceRT(this);
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

  public int getTimeout() {
    return this.timeout;
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }

  public int getRetries() {
    return this.retries;
  }

  public void setRetries(int retries) {
    this.retries = retries;
  }

  public int getUpdatePeriods() {
    return this.updatePeriods;
  }

  public void setUpdatePeriods(int updatePeriods) {
    this.updatePeriods = updatePeriods;
  }

  public int getUpdatePeriodType() {
    return this.updatePeriodType;
  }

  public void setUpdatePeriodType(int updatePeriodType) {
    this.updatePeriodType = updatePeriodType;
  }

  public void validate(ProcessResult response)
  {
    super.validate(response);
    if (!Common.TIME_PERIOD_CODES.isValidId(this.updatePeriodType, new int[0]))
      response.addContextualMessage("updatePeriodType", "validate.invalidValue", new Object[0]);
    if (this.updatePeriods <= 0)
      response.addContextualMessage("updatePeriods", "validate.greaterThanZero", new Object[0]);
    if (StringUtils.isBlank(this.host))
      response.addContextualMessage("host", "validate.required", new Object[0]);
    if ((this.port <= 0) || (this.port > 65535))
      response.addContextualMessage("port", "validate.illegalValue", new Object[0]);
    if (this.timeout <= 0)
      response.addContextualMessage("timeout", "validate.greaterThanZero", new Object[0]);
    if (this.retries < 0)
      response.addContextualMessage("retries", "validate.cannotBeNegative", new Object[0]);
  }

  protected void addPropertiesImpl(List<TranslatableMessage> list)
  {
    AuditEventType.addPropertyMessage(list, "dsEdit.galil.host", this.host);
    AuditEventType.addPropertyMessage(list, "dsEdit.galil.port", Integer.valueOf(this.port));
    AuditEventType.addPropertyMessage(list, "dsEdit.galil.timeout", Integer.valueOf(this.timeout));
    AuditEventType.addPropertyMessage(list, "dsEdit.galil.retries", Integer.valueOf(this.retries));
    AuditEventType.addPeriodMessage(list, "dsEdit.updatePeriod", this.updatePeriodType, this.updatePeriods);
  }

  protected void addPropertyChangesImpl(List<TranslatableMessage> list, GalilDataSourceVO from)
  {
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.galil.host", from.host, this.host);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.galil.port", from.port, this.port);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.galil.timeout", from.timeout, this.timeout);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.galil.retries", from.retries, this.retries);
    AuditEventType.maybeAddPeriodChangeMessage(list, "dsEdit.updatePeriod", from.updatePeriodType, from.updatePeriods, this.updatePeriodType, this.updatePeriods);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    SerializationHelper.writeSafeUTF(out, this.host);
    out.writeInt(this.port);
    out.writeInt(this.timeout);
    out.writeInt(this.retries);
    out.writeInt(this.updatePeriodType);
    out.writeInt(this.updatePeriods);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.host = SerializationHelper.readSafeUTF(in);
      this.port = in.readInt();
      this.timeout = in.readInt();
      this.retries = in.readInt();
      this.updatePeriodType = in.readInt();
      this.updatePeriods = in.readInt();
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
    EVENT_CODES.addElement(1, "DATA_SOURCE_EXCEPTION");
    EVENT_CODES.addElement(2, "POINT_READ_EXCEPTION");
    EVENT_CODES.addElement(3, "POINT_WRITE_EXCEPTION");
  }
}