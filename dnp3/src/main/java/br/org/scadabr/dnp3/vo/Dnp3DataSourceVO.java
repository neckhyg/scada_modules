package br.org.scadabr.dnp3.vo;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableJsonException;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;
import com.serotonin.m2m2.vo.dataSource.PointLocatorVO;
import com.serotonin.m2m2.vo.event.EventTypeVO;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public abstract class Dnp3DataSourceVO<T extends Dnp3DataSourceVO<?>> extends DataSourceVO<T>
{
  private static final ExportCodes EVENT_CODES = new ExportCodes();

  @JsonProperty
  private int synchPeriods = 20;

  @JsonProperty
  private int staticPollPeriods = 30;

  private int rbePeriodType = 1;

  @JsonProperty
  private int rbePollPeriods = 1;

  @JsonProperty
  private int timeout = 800;

  @JsonProperty
  private int retries = 2;

  @JsonProperty
  private int sourceAddress = 1;

  @JsonProperty
  private int slaveAddress = 2;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  protected void addEventTypes(List<EventTypeVO> eventTypes)
  {
    eventTypes.add(createEventType(1, new TranslatableMessage("event.ds.pointRead")));

    eventTypes.add(createEventType(2, new TranslatableMessage("event.ds.dataSource")));
  }

  public ExportCodes getEventCodes()
  {
    return EVENT_CODES;
  }

  public PointLocatorVO createPointLocator()
  {
    return new Dnp3PointLocatorVO();
  }

  public int getSynchPeriods()
  {
    return this.synchPeriods;
  }

  public void setSynchPeriods(int synchPeriods) {
    this.synchPeriods = synchPeriods;
  }

  public int getStaticPollPeriods() {
    return this.staticPollPeriods;
  }

  public void setStaticPollPeriods(int staticPollPeriods) {
    this.staticPollPeriods = staticPollPeriods;
  }

  public int getRbePeriodType() {
    return this.rbePeriodType;
  }

  public void setRbePeriodType(int rbePeriodType) {
    this.rbePeriodType = rbePeriodType;
  }

  public int getRbePollPeriods() {
    return this.rbePollPeriods;
  }

  public void setRbePollPeriods(int rbePollPeriods) {
    this.rbePollPeriods = rbePollPeriods;
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

  public int getSourceAddress() {
    return this.sourceAddress;
  }

  public void setSourceAddress(int sourceAddress) {
    this.sourceAddress = sourceAddress;
  }

  public int getSlaveAddress() {
    return this.slaveAddress;
  }

  public void setSlaveAddress(int slaveAddress) {
    this.slaveAddress = slaveAddress;
  }

  public void validate(ProcessResult response)
  {
    super.validate(response);
    if (this.synchPeriods <= 0)
      response.addContextualMessage("synchPeriods", "validate.greaterThanZero", new Object[0]);
    if (this.sourceAddress <= 0)
      response.addContextualMessage("sourceAddress", "validate.greaterThanZero", new Object[0]);
    if (this.slaveAddress <= 0)
      response.addContextualMessage("slaveAddress", "validate.greaterThanZero", new Object[0]);
    if (this.staticPollPeriods <= 0)
      response.addContextualMessage("staticPollPeriods", "validate.greaterThanZero", new Object[0]);
    if (!Common.TIME_PERIOD_CODES.isValidId(this.rbePeriodType, new int[0]))
      response.addContextualMessage("rbePeriodType", "validate.invalidValue", new Object[0]);
    if (this.rbePollPeriods <= 0)
      response.addContextualMessage("rbePollPeriods", "validate.greaterThanZero", new Object[0]);
    if (this.timeout <= 0)
      response.addContextualMessage("timeout", "validate.greaterThanZero", new Object[0]);
    if (this.retries < 0)
      response.addContextualMessage("retries", "validate.cannotBeNegative", new Object[0]);
  }

  protected void addPropertiesImpl(List<TranslatableMessage> list)
  {
    AuditEventType.addPeriodMessage(list, "dsEdit.dnp3.rbePeriod", this.rbePeriodType, this.rbePollPeriods);
    AuditEventType.addPropertyMessage(list, "dsEdit.dnp3.synchPeriod", Integer.valueOf(this.synchPeriods));
    AuditEventType.addPropertyMessage(list, "dsEdit.dnp3.staticPeriod", Integer.valueOf(this.staticPollPeriods));
    AuditEventType.addPropertyMessage(list, "dsEdit.dnp3.sourceAddress", Integer.valueOf(this.sourceAddress));
    AuditEventType.addPropertyMessage(list, "dsEdit.dnp3.slaveAddress", Integer.valueOf(this.slaveAddress));
    AuditEventType.addPropertyMessage(list, "dsEdit.modbus.timeout", Integer.valueOf(this.timeout));
    AuditEventType.addPropertyMessage(list, "dsEdit.modbus.retries", Integer.valueOf(this.retries));
  }

  protected void addPropertyChangesImpl(List<TranslatableMessage> list, T t)
  {
    Dnp3DataSourceVO from = t;
    AuditEventType.maybeAddPeriodChangeMessage(list, "dsEdit.dnp3.rbePeriod", from.rbePeriodType, from.rbePollPeriods, this.rbePeriodType, this.rbePollPeriods);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.dnp3.synchPeriod", from.synchPeriods, this.synchPeriods);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.dnp3.staticPeriod", from.staticPollPeriods, this.staticPollPeriods);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.dnp3.sourceAddress", from.sourceAddress, this.sourceAddress);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.dnp3.slaveAddress", from.slaveAddress, this.slaveAddress);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.dnp3.retries", from.retries, this.retries);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbus.retries", from.retries, this.retries);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    out.writeInt(this.synchPeriods);
    out.writeInt(this.staticPollPeriods);
    out.writeInt(this.rbePeriodType);
    out.writeInt(this.rbePollPeriods);
    out.writeInt(this.timeout);
    out.writeInt(this.retries);
    out.writeInt(this.sourceAddress);
    out.writeInt(this.slaveAddress);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.synchPeriods = in.readInt();

      this.staticPollPeriods = in.readInt();
      this.rbePeriodType = in.readInt();
      this.rbePollPeriods = in.readInt();
      this.timeout = in.readInt();
      this.retries = in.readInt();
      this.sourceAddress = in.readInt();
      this.slaveAddress = in.readInt();
    }
  }

  public void jsonWrite(ObjectWriter writer) throws IOException, JsonException
  {
    super.jsonWrite(writer);
    writePeriodType(writer, this.rbePeriodType, "eventsPeriodType");
  }

  public void jsonRead(JsonReader reader, JsonObject jsonObject) throws JsonException
  {
    super.jsonRead(reader, jsonObject);
    Integer value3 = readPeriodType(jsonObject, "eventsPeriodType");
    if (value3 != null)
      this.rbePeriodType = value3.intValue();
  }

  protected void writePeriodType(ObjectWriter writer, int updatePeriodType, String name)
    throws IOException, JsonException
  {
    writer.writeEntry(name, Common.TIME_PERIOD_CODES.getCode(updatePeriodType));
  }

  protected Integer readPeriodType(JsonObject json, String name) throws JsonException {
    String text = json.getString(name);
    if (text == null) {
      return null;
    }
    int value = Common.TIME_PERIOD_CODES.getId(text, new int[0]);
    if (value == -1) {
      throw new TranslatableJsonException("emport.error.invalid", new Object[] { name, text, Common.TIME_PERIOD_CODES.getCodeList(new int[0]) });
    }

    return Integer.valueOf(value);
  }

  static
  {
    EVENT_CODES.addElement(2, "DATA_SOURCE_EXCEPTION");
    EVENT_CODES.addElement(1, "POINT_READ_EXCEPTION");
  }
}