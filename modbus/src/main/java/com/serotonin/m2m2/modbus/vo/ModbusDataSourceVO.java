package com.serotonin.m2m2.modbus.vo;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.modbus.rt.ModbusDataSourceRT;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;
import com.serotonin.m2m2.vo.dataSource.PointLocatorVO;
import com.serotonin.m2m2.vo.event.EventTypeVO;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public abstract class ModbusDataSourceVO<T extends ModbusDataSourceVO<?>> extends DataSourceVO<T>
{
  private static final ExportCodes EVENT_CODES = new ExportCodes();

  private int updatePeriodType = 2;

  @JsonProperty
  private int updatePeriods = 5;

  @JsonProperty
  private boolean quantize;

  @JsonProperty
  private int timeout = 500;

  @JsonProperty
  private int retries = 2;

  @JsonProperty
  private boolean multipleWritesOnly;

  @JsonProperty
  private boolean contiguousBatches;

  @JsonProperty
  private boolean createSlaveMonitorPoints;

  @JsonProperty
  private int maxReadBitCount = 2000;

  @JsonProperty
  private int maxReadRegisterCount = 125;

  @JsonProperty
  private int maxWriteRegisterCount = 120;

  @JsonProperty
  private int discardDataDelay;

  @JsonProperty
  private boolean logIO;
  private static final long serialVersionUID = -1L;
  private static final int version = 4;

  protected void addEventTypes(List<EventTypeVO> ets)
  {
    ets.add(createEventType(3, new TranslatableMessage("event.ds.dataSource")));

    ets.add(createEventType(1, new TranslatableMessage("event.ds.pointRead")));

    ets.add(createEventType(2, new TranslatableMessage("event.ds.pointWrite")));
  }

  public ExportCodes getEventCodes()
  {
    return EVENT_CODES;
  }

  public PointLocatorVO createPointLocator()
  {
    return new ModbusPointLocatorVO();
  }

  public int getUpdatePeriods()
  {
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

  public boolean isQuantize() {
    return this.quantize;
  }

  public void setQuantize(boolean quantize) {
    this.quantize = quantize;
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

  public boolean isMultipleWritesOnly() {
    return this.multipleWritesOnly;
  }

  public void setMultipleWritesOnly(boolean multipleWritesOnly) {
    this.multipleWritesOnly = multipleWritesOnly;
  }

  public boolean isContiguousBatches() {
    return this.contiguousBatches;
  }

  public void setContiguousBatches(boolean contiguousBatches) {
    this.contiguousBatches = contiguousBatches;
  }

  public boolean isCreateSlaveMonitorPoints() {
    return this.createSlaveMonitorPoints;
  }

  public void setCreateSlaveMonitorPoints(boolean createSlaveMonitorPoints) {
    this.createSlaveMonitorPoints = createSlaveMonitorPoints;
  }

  public int getMaxReadBitCount() {
    return this.maxReadBitCount;
  }

  public void setMaxReadBitCount(int maxReadBitCount) {
    this.maxReadBitCount = maxReadBitCount;
  }

  public int getMaxReadRegisterCount() {
    return this.maxReadRegisterCount;
  }

  public void setMaxReadRegisterCount(int maxReadRegisterCount) {
    this.maxReadRegisterCount = maxReadRegisterCount;
  }

  public int getMaxWriteRegisterCount() {
    return this.maxWriteRegisterCount;
  }

  public void setMaxWriteRegisterCount(int maxWriteRegisterCount) {
    this.maxWriteRegisterCount = maxWriteRegisterCount;
  }

  public int getDiscardDataDelay() {
    return this.discardDataDelay;
  }

  public void setDiscardDataDelay(int discardDataDelay) {
    this.discardDataDelay = discardDataDelay;
  }

  public boolean isLogIO() {
    return this.logIO;
  }

  public void setLogIO(boolean logIO) {
    this.logIO = logIO;
  }

  public String getIoLogPath() {
    return ModbusDataSourceRT.getIOLogFile(getId()).getPath();
  }

  public void validate(ProcessResult response)
  {
    super.validate(response);
    if (!Common.TIME_PERIOD_CODES.isValidId(this.updatePeriodType, new int[0]))
      response.addContextualMessage("updatePeriodType", "validate.invalidValue", new Object[0]);
    if (this.updatePeriods <= 0)
      response.addContextualMessage("updatePeriods", "validate.greaterThanZero", new Object[0]);
    if (this.timeout <= 0)
      response.addContextualMessage("timeout", "validate.greaterThanZero", new Object[0]);
    if (this.retries < 0)
      response.addContextualMessage("retries", "validate.cannotBeNegative", new Object[0]);
    if (this.maxReadBitCount < 1)
      response.addContextualMessage("maxReadBitCount", "validate.greaterThanZero", new Object[0]);
    if (this.maxReadRegisterCount < 1)
      response.addContextualMessage("maxReadRegisterCount", "validate.greaterThanZero", new Object[0]);
    if (this.maxWriteRegisterCount < 1)
      response.addContextualMessage("maxWriteRegisterCount", "validate.greaterThanZero", new Object[0]);
    if (this.discardDataDelay < 0)
      response.addContextualMessage("discardDataDelay", "validate.cannotBeNegative", new Object[0]);
  }

  protected void addPropertiesImpl(List<TranslatableMessage> list)
  {
    AuditEventType.addPeriodMessage(list, "dsEdit.updatePeriod", this.updatePeriodType, this.updatePeriods);
    AuditEventType.addPropertyMessage(list, "dsEdit.quantize", this.quantize);
    AuditEventType.addPropertyMessage(list, "dsEdit.modbus.timeout", Integer.valueOf(this.timeout));
    AuditEventType.addPropertyMessage(list, "dsEdit.modbus.retries", Integer.valueOf(this.retries));
    AuditEventType.addPropertyMessage(list, "dsEdit.modbus.multipleWritesOnly", this.multipleWritesOnly);
    AuditEventType.addPropertyMessage(list, "dsEdit.modbus.contiguousBatches", this.contiguousBatches);
    AuditEventType.addPropertyMessage(list, "dsEdit.modbus.createSlaveMonitorPoints", this.createSlaveMonitorPoints);
    AuditEventType.addPropertyMessage(list, "dsEdit.modbus.maxReadBitCount", Integer.valueOf(this.maxReadBitCount));
    AuditEventType.addPropertyMessage(list, "dsEdit.modbus.maxReadRegisterCount", Integer.valueOf(this.maxReadRegisterCount));
    AuditEventType.addPropertyMessage(list, "dsEdit.modbus.maxWriteRegisterCount", Integer.valueOf(this.maxWriteRegisterCount));
    AuditEventType.addPropertyMessage(list, "dsEdit.modbus.discardDataDelay", Integer.valueOf(this.discardDataDelay));
    AuditEventType.addPropertyMessage(list, "dsEdit.modbus.logIO", this.logIO);
  }

  protected void addPropertyChangesImpl(List<TranslatableMessage> list, T t)
  {
    ModbusDataSourceVO from = t;
    AuditEventType.maybeAddPeriodChangeMessage(list, "dsEdit.updatePeriod", from.updatePeriodType, from.updatePeriods, this.updatePeriodType, this.updatePeriods);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.quantize", from.quantize, this.quantize);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbus.timeout", from.timeout, this.timeout);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbus.retries", from.retries, this.retries);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbus.multipleWritesOnly", from.multipleWritesOnly, this.multipleWritesOnly);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbus.contiguousBatches", from.contiguousBatches, this.contiguousBatches);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbus.createSlaveMonitorPoints", from.createSlaveMonitorPoints, this.createSlaveMonitorPoints);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbus.maxReadBitCount", from.maxReadBitCount, this.maxReadBitCount);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbus.maxReadRegisterCount", from.maxReadRegisterCount, this.maxReadRegisterCount);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbus.maxWriteRegisterCount", from.maxWriteRegisterCount, this.maxWriteRegisterCount);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbus.discardDataDelay", from.discardDataDelay, this.discardDataDelay);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbus.logIO", from.logIO, this.logIO);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(4);
    out.writeInt(this.updatePeriodType);
    out.writeInt(this.updatePeriods);
    out.writeBoolean(this.quantize);
    out.writeInt(this.timeout);
    out.writeInt(this.retries);
    out.writeBoolean(this.multipleWritesOnly);
    out.writeBoolean(this.contiguousBatches);
    out.writeBoolean(this.createSlaveMonitorPoints);
    out.writeInt(this.maxReadBitCount);
    out.writeInt(this.maxReadRegisterCount);
    out.writeInt(this.maxWriteRegisterCount);
    out.writeInt(this.discardDataDelay);
    out.writeBoolean(this.logIO);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.updatePeriodType = in.readInt();
      this.updatePeriods = in.readInt();
      this.quantize = in.readBoolean();
      this.timeout = in.readInt();
      this.retries = in.readInt();
      this.multipleWritesOnly = true;
      this.contiguousBatches = in.readBoolean();
      this.createSlaveMonitorPoints = in.readBoolean();
      this.maxReadBitCount = in.readInt();
      this.maxReadRegisterCount = in.readInt();
      this.maxWriteRegisterCount = in.readInt();
      this.discardDataDelay = 0;
      this.logIO = false;
    }
    else if (ver == 2) {
      this.updatePeriodType = in.readInt();
      this.updatePeriods = in.readInt();
      this.quantize = in.readBoolean();
      this.timeout = in.readInt();
      this.retries = in.readInt();
      this.multipleWritesOnly = true;
      this.contiguousBatches = in.readBoolean();
      this.createSlaveMonitorPoints = in.readBoolean();
      this.maxReadBitCount = in.readInt();
      this.maxReadRegisterCount = in.readInt();
      this.maxWriteRegisterCount = in.readInt();
      this.discardDataDelay = in.readInt();
      this.logIO = false;
    }
    else if (ver == 3) {
      this.updatePeriodType = in.readInt();
      this.updatePeriods = in.readInt();
      this.quantize = in.readBoolean();
      this.timeout = in.readInt();
      this.retries = in.readInt();
      this.multipleWritesOnly = in.readBoolean();
      this.contiguousBatches = in.readBoolean();
      this.createSlaveMonitorPoints = in.readBoolean();
      this.maxReadBitCount = in.readInt();
      this.maxReadRegisterCount = in.readInt();
      this.maxWriteRegisterCount = in.readInt();
      this.discardDataDelay = in.readInt();
      this.logIO = false;
    }
    else if (ver == 4) {
      this.updatePeriodType = in.readInt();
      this.updatePeriods = in.readInt();
      this.quantize = in.readBoolean();
      this.timeout = in.readInt();
      this.retries = in.readInt();
      this.multipleWritesOnly = in.readBoolean();
      this.contiguousBatches = in.readBoolean();
      this.createSlaveMonitorPoints = in.readBoolean();
      this.maxReadBitCount = in.readInt();
      this.maxReadRegisterCount = in.readInt();
      this.maxWriteRegisterCount = in.readInt();
      this.discardDataDelay = in.readInt();
      this.logIO = in.readBoolean();
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
    EVENT_CODES.addElement(3, "DATA_SOURCE_EXCEPTION");
    EVENT_CODES.addElement(1, "POINT_READ_EXCEPTION");
    EVENT_CODES.addElement(2, "POINT_WRITE_EXCEPTION");
  }
}