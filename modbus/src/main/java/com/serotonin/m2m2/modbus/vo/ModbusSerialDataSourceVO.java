package com.serotonin.m2m2.modbus.vo;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableJsonException;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.modbus.rt.ModbusSerialDataSourceRT;
import com.serotonin.m2m2.rt.dataSource.DataSourceRT;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.util.SerializationHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class ModbusSerialDataSourceVO extends ModbusDataSourceVO<ModbusSerialDataSourceVO>
{
  private static ExportCodes CONCURRENCY_CODES = new ExportCodes();

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
  private EncodingType encoding;

  @JsonProperty
  private boolean echo = false;

  private int concurrency = 3;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  public TranslatableMessage getConnectionDescription()
  {
    return new TranslatableMessage("common.default", new Object[] { this.commPortId });
  }

  public DataSourceRT createDataSourceRT()
  {
    return new ModbusSerialDataSourceRT(this);
  }

  public String getEncodingStr()
  {
    if (this.encoding == null)
      return null;
    return this.encoding.toString();
  }

  public void setEncodingStr(String encoding) {
    if (encoding != null)
      this.encoding = EncodingType.valueOf(encoding);
  }

  public int getBaudRate() {
    return this.baudRate;
  }

  public void setBaudRate(int baudRate) {
    this.baudRate = baudRate;
  }

  public String getCommPortId() {
    return this.commPortId;
  }

  public void setCommPortId(String commPortId) {
    this.commPortId = commPortId;
  }

  public int getDataBits() {
    return this.dataBits;
  }

  public void setDataBits(int dataBits) {
    this.dataBits = dataBits;
  }

  public boolean isEcho() {
    return this.echo;
  }

  public void setEcho(boolean echo) {
    this.echo = echo;
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

  public int getParity() {
    return this.parity;
  }

  public void setParity(int parity) {
    this.parity = parity;
  }

  public int getStopBits() {
    return this.stopBits;
  }

  public void setStopBits(int stopBits) {
    this.stopBits = stopBits;
  }

  public EncodingType getEncoding() {
    return this.encoding;
  }

  public void setEncoding(EncodingType encoding) {
    this.encoding = encoding;
  }

  public int getConcurrency() {
    return this.concurrency;
  }

  public void setConcurrency(int concurrency) {
    this.concurrency = concurrency;
  }

  public void validate(ProcessResult response)
  {
    super.validate(response);
    if (StringUtils.isBlank(this.commPortId))
      response.addContextualMessage("commPortId", "validate.required", new Object[0]);
    if (this.baudRate <= 0)
      response.addContextualMessage("baudRate", "validate.invalidValue", new Object[0]);
    if ((this.flowControlIn != 0) && (this.flowControlIn != 1) && (this.flowControlIn != 4))
      response.addContextualMessage("flowControlIn", "validate.invalidValue", new Object[0]);
    if ((this.flowControlOut != 0) && (this.flowControlOut != 2) && (this.flowControlOut != 8))
      response.addContextualMessage("flowControlOut", "validate.invalidValue", new Object[0]);
    if ((this.dataBits < 5) || (this.dataBits > 8))
      response.addContextualMessage("dataBits", "validate.invalidValue", new Object[0]);
    if ((this.stopBits < 1) || (this.stopBits > 3))
      response.addContextualMessage("stopBits", "validate.invalidValue", new Object[0]);
    if ((this.parity < 0) || (this.parity > 4))
      response.addContextualMessage("parityBits", "validate.invalidValue", new Object[0]);
    if (this.encoding == null) {
      response.addContextualMessage("encodingBits", "validate.required", new Object[0]);
    }
    if (!CONCURRENCY_CODES.isValidId(this.concurrency, new int[0]))
      response.addContextualMessage("concurrency", "validate.invalidValue", new Object[0]);
  }

  protected void addPropertiesImpl(List<TranslatableMessage> list)
  {
    super.addPropertiesImpl(list);
    AuditEventType.addPropertyMessage(list, "dsEdit.modbusSerial.port", this.commPortId);
    AuditEventType.addPropertyMessage(list, "dsEdit.modbusSerial.baud", Integer.valueOf(this.baudRate));
    AuditEventType.addPropertyMessage(list, "dsEdit.modbusSerial.flowControlIn", Integer.valueOf(this.flowControlIn));
    AuditEventType.addPropertyMessage(list, "dsEdit.modbusSerial.flowControlOut", Integer.valueOf(this.flowControlOut));
    AuditEventType.addPropertyMessage(list, "dsEdit.modbusSerial.dataBits", Integer.valueOf(this.dataBits));
    AuditEventType.addPropertyMessage(list, "dsEdit.modbusSerial.stopBits", Integer.valueOf(this.stopBits));
    AuditEventType.addPropertyMessage(list, "dsEdit.modbusSerial.parity", Integer.valueOf(this.parity));
    AuditEventType.addPropertyMessage(list, "dsEdit.modbusSerial.encoding", this.encoding);
    AuditEventType.addPropertyMessage(list, "dsEdit.modbusSerial.echo", this.echo);
    AuditEventType.addExportCodeMessage(list, "dsEdit.modbusSerial.concurrency", CONCURRENCY_CODES, this.concurrency);
  }

  protected void addPropertyChangesImpl(List<TranslatableMessage> list, ModbusSerialDataSourceVO from)
  {
    super.addPropertyChangesImpl(list, from);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbusSerial.port", from.commPortId, this.commPortId);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbusSerial.baud", from.baudRate, this.baudRate);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbusSerial.flowControlIn", from.flowControlIn, this.flowControlIn);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbusSerial.flowControlOut", from.flowControlOut, this.flowControlOut);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbusSerial.dataBits", from.dataBits, this.dataBits);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbusSerial.stopBits", from.stopBits, this.stopBits);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbusSerial.parity", from.parity, this.parity);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbusSerial.encoding", from.encoding, this.encoding);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbusSerial.echo", from.echo, this.echo);
    AuditEventType.maybeAddExportCodeChangeMessage(list, "dsEdit.modbusSerial.concurrency", CONCURRENCY_CODES, from.concurrency, this.concurrency);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    SerializationHelper.writeSafeUTF(out, this.commPortId);
    out.writeInt(this.baudRate);
    out.writeInt(this.flowControlIn);
    out.writeInt(this.flowControlOut);
    out.writeInt(this.dataBits);
    out.writeInt(this.stopBits);
    out.writeInt(this.parity);
    out.writeObject(this.encoding);
    out.writeBoolean(this.echo);
    out.writeInt(this.concurrency);
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    int ver = in.readInt();

    if (ver == 1) {
      this.commPortId = SerializationHelper.readSafeUTF(in);
      this.baudRate = in.readInt();
      this.flowControlIn = in.readInt();
      this.flowControlOut = in.readInt();
      this.dataBits = in.readInt();
      this.stopBits = in.readInt();
      this.parity = in.readInt();
      this.encoding = ((EncodingType)in.readObject());
      this.echo = in.readBoolean();
      this.concurrency = in.readInt();
    }
  }

  public void jsonWrite(ObjectWriter writer) throws IOException, JsonException
  {
    super.jsonWrite(writer);
    writer.writeEntry("concurrency", CONCURRENCY_CODES.getCode(this.concurrency));
  }

  public void jsonRead(JsonReader reader, JsonObject jsonObject) throws JsonException
  {
    super.jsonRead(reader, jsonObject);

    String text = jsonObject.getString("concurrency");
    if (text == null)
      throw new TranslatableJsonException("emport.error.missing", new Object[] { "concurrency", CONCURRENCY_CODES.getCodeList(new int[0]) });
    this.concurrency = CONCURRENCY_CODES.getId(text, new int[0]);
    if (!CONCURRENCY_CODES.isValidId(this.concurrency, new int[0]))
      throw new TranslatableJsonException("emport.error.invalid", new Object[] { "concurrency", text, CONCURRENCY_CODES.getCodeList(new int[0]) });
  }

  static
  {
    CONCURRENCY_CODES.addElement(1, "SYNC_TRANSPORT", "dsEdit.modbusSerial.concurrency.transport");

    CONCURRENCY_CODES.addElement(2, "SYNC_SLAVE", "dsEdit.modbusSerial.concurrency.slave");
    CONCURRENCY_CODES.addElement(3, "SYNC_FUNCTION", "dsEdit.modbusSerial.concurrency.function");
  }

  public static enum EncodingType
  {
    RTU("dsEdit.modbusSerial.encoding.rtu"), 
    ASCII("dsEdit.modbusSerial.encoding.ascii");

    private final String nameKey;

    private EncodingType(String nameKey) { this.nameKey = nameKey; }

    public String getNameKey()
    {
      return this.nameKey;
    }
  }
}