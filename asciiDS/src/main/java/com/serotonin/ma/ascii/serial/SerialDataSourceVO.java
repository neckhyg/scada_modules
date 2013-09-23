package com.serotonin.ma.ascii.serial;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.type.JsonObject;
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

public class SerialDataSourceVO extends DataSourceVO<SerialDataSourceVO>
{
  private static final ExportCodes EVENT_CODES = new ExportCodes();

  private int updatePeriodType = 1;

  @JsonProperty
  private int updatePeriods = 1;

  @JsonProperty
  private String commPortId;

  @JsonProperty
  private int baudRate = 9600;

  @JsonProperty
  private int flowControlIn = 0;

  @JsonProperty
  private int flowControlOut = 0;

  @JsonProperty
  private int dataBits = 8;

  @JsonProperty
  private int stopBits = 1;

  @JsonProperty
  private int parity = 0;

  @JsonProperty
  private int timeout = 300;

  @JsonProperty
  private int retries = 2;

  @JsonProperty
  private int stopMode = 0;

  @JsonProperty
  private int nChar = 1;

  @JsonProperty
  private int charStopMode = 0;

  @JsonProperty
  private String charX = "";

  @JsonProperty
  private String hexValue = "";

  @JsonProperty
  private int stopTimeout = 1000;

  @JsonProperty
  private String initString = "";

  @JsonProperty
  private int bufferSize = 2;

  @JsonProperty
  private boolean quantize;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  protected void addEventTypes(List<EventTypeVO> ets)
  {
    ets.add(createEventType(1, new TranslatableMessage("event.ds.pointRead")));

    ets.add(createEventType(2, new TranslatableMessage("event.ds.dataSource")));
  }

  public ExportCodes getEventCodes()
  {
    return EVENT_CODES;
  }

  public TranslatableMessage getConnectionDescription()
  {
    return new TranslatableMessage("common.default", new Object[] { this.commPortId });
  }

  public DataSourceRT createDataSourceRT()
  {
    return new SerialDataSourceRT(this);
  }

  public PointLocatorVO createPointLocator()
  {
    return new SerialPointLocatorVO();
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

  public String getCommPortId() {
    return this.commPortId;
  }

  public void setCommPortId(String commPortId) {
    this.commPortId = commPortId;
  }

  public int getBaudRate() {
    return this.baudRate;
  }

  public void setBaudRate(int baudRate) {
    this.baudRate = baudRate;
  }

  public int getFlowControlIn() {
    return this.flowControlIn;
  }

  public void setFlowControlIn(int flowControlIn) {
    this.flowControlIn = flowControlIn;
  }

  public int getFlowControlOut() {
    return this.flowControlOut;
  }

  public void setFlowControlOut(int flowControlOut) {
    this.flowControlOut = flowControlOut;
  }

  public int getDataBits() {
    return this.dataBits;
  }

  public void setDataBits(int dataBits) {
    this.dataBits = dataBits;
  }

  public int getStopBits() {
    return this.stopBits;
  }

  public void setStopBits(int stopBits) {
    this.stopBits = stopBits;
  }

  public int getParity() {
    return this.parity;
  }

  public void setParity(int parity) {
    this.parity = parity;
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

  public int getStopMode() {
    return this.stopMode;
  }

  public void setStopMode(int stopMode) {
    this.stopMode = stopMode;
  }

  public int getnChar() {
    return this.nChar;
  }

  public void setnChar(int nChar) {
    this.nChar = nChar;
  }

  public void setCharStopMode(int charStopMode) {
    this.charStopMode = charStopMode;
  }

  public int getCharStopMode() {
    return this.charStopMode;
  }

  public String getCharX() {
    return this.charX;
  }

  public void setCharX(String charX) {
    this.charX = charX;
  }

  public void setHexValue(String hexValue) {
    this.hexValue = hexValue;
  }

  public String getHexValue() {
    return this.hexValue;
  }

  public void setStopTimeout(int stopTimeout) {
    this.stopTimeout = stopTimeout;
  }

  public int getStopTimeout() {
    return this.stopTimeout;
  }

  public String getInitString() {
    return this.initString;
  }

  public void setInitString(String initString) {
    this.initString = initString;
  }

  public int getBufferSize() {
    return this.bufferSize;
  }

  public void setBufferSize(int bufferSize) {
    this.bufferSize = bufferSize;
  }

  public boolean isQuantize() {
    return this.quantize;
  }

  public void setQuantize(boolean quantize) {
    this.quantize = quantize;
  }

  public void validate(ProcessResult response)
  {
    super.validate(response);
  }

  protected void addPropertiesImpl(List<TranslatableMessage> list)
  {
    AuditEventType.addPeriodMessage(list, "dsEdit.updatePeriod", this.updatePeriodType, this.updatePeriods);
    AuditEventType.addPropertyMessage(list, "ascii.serial.commPortId", this.commPortId);
    AuditEventType.addPropertyMessage(list, "ascii.serial.baud", Integer.valueOf(this.baudRate));
    AuditEventType.addPropertyMessage(list, "ascii.serial.flowControlIn", Integer.valueOf(this.flowControlIn));
    AuditEventType.addPropertyMessage(list, "ascii.serial.flowControlOut", Integer.valueOf(this.flowControlOut));
    AuditEventType.addPropertyMessage(list, "ascii.serial.dataBits", Integer.valueOf(this.dataBits));
    AuditEventType.addPropertyMessage(list, "ascii.serial.stopBits", Integer.valueOf(this.stopBits));
    AuditEventType.addPropertyMessage(list, "ascii.serial.parity", Integer.valueOf(this.parity));
    AuditEventType.addPropertyMessage(list, "ascii.serial.timeout", Integer.valueOf(this.timeout));
    AuditEventType.addPropertyMessage(list, "ascii.serial.retries", Integer.valueOf(this.retries));
    AuditEventType.addPropertyMessage(list, "ascii.serial.stopMode", Integer.valueOf(this.stopMode));
    AuditEventType.addPropertyMessage(list, "ascii.serial.nChar", Integer.valueOf(this.nChar));
    AuditEventType.addPropertyMessage(list, "ascii.serial.charStopMode", Integer.valueOf(this.charStopMode));
    AuditEventType.addPropertyMessage(list, "ascii.serial.charX", this.charX);
    AuditEventType.addPropertyMessage(list, "ascii.serial.hexValue", this.hexValue);
    AuditEventType.addPropertyMessage(list, "ascii.serial.stopTimeout", Integer.valueOf(this.stopTimeout));
    AuditEventType.addPropertyMessage(list, "ascii.serial.initString", this.initString);
    AuditEventType.addPropertyMessage(list, "ascii.serial.bufferSize", Integer.valueOf(this.bufferSize));
    AuditEventType.addPropertyMessage(list, "dsEdit.quantize", this.quantize);
  }

  protected void addPropertyChangesImpl(List<TranslatableMessage> list, SerialDataSourceVO from)
  {
    AuditEventType.maybeAddPeriodChangeMessage(list, "dsEdit.updatePeriod", from.updatePeriodType, from.updatePeriods, this.updatePeriodType, this.updatePeriods);

    AuditEventType.maybeAddPropertyChangeMessage(list, "ascii.serial.commPortId", from.commPortId, this.commPortId);
    AuditEventType.maybeAddPropertyChangeMessage(list, "ascii.serial.baud", from.baudRate, this.baudRate);
    AuditEventType.maybeAddPropertyChangeMessage(list, "ascii.serial.flowControlIn", from.flowControlIn, this.flowControlIn);

    AuditEventType.maybeAddPropertyChangeMessage(list, "ascii.serial.flowControlOut", from.flowControlOut, this.flowControlOut);

    AuditEventType.maybeAddPropertyChangeMessage(list, "ascii.serial.dataBits", from.dataBits, this.dataBits);
    AuditEventType.maybeAddPropertyChangeMessage(list, "ascii.serial.stopBits", from.stopBits, this.stopBits);
    AuditEventType.maybeAddPropertyChangeMessage(list, "ascii.serial.parity", from.parity, this.parity);
    AuditEventType.maybeAddPropertyChangeMessage(list, "ascii.serial.timeout", from.timeout, this.timeout);
    AuditEventType.maybeAddPropertyChangeMessage(list, "ascii.serial.retries", from.retries, this.retries);
    AuditEventType.maybeAddPropertyChangeMessage(list, "ascii.serial.stopMode", from.stopMode, this.stopMode);
    AuditEventType.maybeAddPropertyChangeMessage(list, "ascii.serial.nChar", from.nChar, this.nChar);
    AuditEventType.maybeAddPropertyChangeMessage(list, "ascii.serial.charStopMode", from.charStopMode, this.charStopMode);

    AuditEventType.maybeAddPropertyChangeMessage(list, "ascii.serial.charX", from.charX, this.charX);
    AuditEventType.maybeAddPropertyChangeMessage(list, "ascii.serial.hexValue", from.hexValue, this.hexValue);
    AuditEventType.maybeAddPropertyChangeMessage(list, "ascii.serial.stopTimeout", from.stopTimeout, this.stopTimeout);
    AuditEventType.maybeAddPropertyChangeMessage(list, "ascii.serial.initString", from.initString, this.initString);
    AuditEventType.maybeAddPropertyChangeMessage(list, "ascii.serial.bufferSize", from.bufferSize, this.bufferSize);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.quantize", from.quantize, this.quantize);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    out.writeInt(this.updatePeriodType);
    out.writeInt(this.updatePeriods);
    SerializationHelper.writeSafeUTF(out, this.commPortId);
    out.writeInt(this.baudRate);
    out.writeInt(this.flowControlIn);
    out.writeInt(this.flowControlOut);
    out.writeInt(this.dataBits);
    out.writeInt(this.stopBits);
    out.writeInt(this.parity);
    out.writeInt(this.timeout);
    out.writeInt(this.retries);
    out.writeInt(this.stopMode);
    out.writeInt(this.nChar);
    out.writeInt(this.charStopMode);
    SerializationHelper.writeSafeUTF(out, this.charX);
    SerializationHelper.writeSafeUTF(out, this.hexValue);
    out.writeInt(this.stopTimeout);
    SerializationHelper.writeSafeUTF(out, this.initString);
    out.writeInt(this.bufferSize);
    out.writeBoolean(this.quantize);
  }

  private void readObject(ObjectInputStream in) throws IOException
  {
    int ver = in.readInt();
    if (ver == 1) {
      this.updatePeriodType = in.readInt();
      this.updatePeriods = in.readInt();
      this.commPortId = SerializationHelper.readSafeUTF(in);
      this.baudRate = in.readInt();
      this.flowControlIn = in.readInt();
      this.flowControlOut = in.readInt();
      this.dataBits = in.readInt();
      this.stopBits = in.readInt();
      this.parity = in.readInt();
      this.timeout = in.readInt();
      this.retries = in.readInt();
      this.stopMode = in.readInt();
      this.nChar = in.readInt();
      this.charStopMode = in.readInt();
      this.charX = SerializationHelper.readSafeUTF(in);
      this.hexValue = SerializationHelper.readSafeUTF(in);
      this.stopTimeout = in.readInt();
      this.initString = SerializationHelper.readSafeUTF(in);
      this.bufferSize = in.readInt();
      this.quantize = in.readBoolean();
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
    EVENT_CODES.addElement(2, "DATA_SOURCE_EXCEPTION");
    EVENT_CODES.addElement(1, "POINT_READ_EXCEPTION");
  }
}