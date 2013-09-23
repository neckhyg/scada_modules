package com.serotonin.m2m2.modbus.vo;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.spi.JsonSerializable;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableJsonException;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.modbus.rt.ModbusPointLocatorRT;
import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.util.SerializationHelper;
import com.serotonin.validation.StringValidation;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.List;

public class ModbusPointLocatorVO extends AbstractPointLocatorVO
  implements JsonSerializable
{
  private static ExportCodes RANGE_CODES = new ExportCodes();
  private static ExportCodes MODBUS_DATA_TYPE_CODES;
  public static final int WRITE_TYPE_NOT_SETTABLE = 1;
  public static final int WRITE_TYPE_SETTABLE = 2;
  public static final int WRITE_TYPE_WRITE_ONLY = 3;
  public static ExportCodes WRITE_TYPE_TYPE_CODES;
  private int range = 1;
  private int modbusDataType = 1;

  @JsonProperty
  private int slaveId = 1;

  @JsonProperty
  private boolean slaveMonitor;

  @JsonProperty
  private int offset;

  @JsonProperty
  private byte bit;

  @JsonProperty
  private int registerCount;

  @JsonProperty
  private String charset = "ASCII";

  private int writeType = 2;

  @JsonProperty
  private double multiplier = 1.0D;

  @JsonProperty
  private double additive = 0.0D;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  public static TranslatableMessage getRangeMessage(int range)
  {
    return new TranslatableMessage(RANGE_CODES.getKey(range));
  }

  public TranslatableMessage getConfigurationDescription()
  {
    if (this.slaveMonitor)
      return new TranslatableMessage("dsEdit.modbus.dpconn2", new Object[] { Integer.valueOf(this.slaveId) });
    if (((this.range == 3) || (this.range == 4)) && (this.modbusDataType == 1))
    {
      return new TranslatableMessage("dsEdit.modbus.dpconn", new Object[] { Integer.valueOf(this.slaveId), this.offset + "/" + this.bit });
    }return new TranslatableMessage("dsEdit.modbus.dpconn", new Object[] { Integer.valueOf(this.slaveId), Integer.valueOf(this.offset) });
  }

  public int getDataTypeId()
  {
    if (this.slaveMonitor)
      return 1;
    if (this.modbusDataType == 1)
      return 1;
    if (isString())
      return 4;
    return 3;
  }

  public boolean isSettable()
  {
    if (this.slaveMonitor)
      return false;
    return (settableRange()) && ((this.writeType == 2) || (this.writeType == 3));
  }

  public boolean isWriteOnly() {
    if (this.slaveMonitor)
      return false;
    if (!settableRange())
      return false;
    return this.writeType == 3;
  }

  public PointLocatorRT createRuntime()
  {
    return new ModbusPointLocatorRT(this);
  }

  public TranslatableMessage getRangeMessage() {
    return getRangeMessage(this.range);
  }

  public double getAdditive()
  {
    return this.additive;
  }

  public void setAdditive(double additive) {
    this.additive = additive;
  }

  public byte getBit() {
    return this.bit;
  }

  public void setBit(byte bit) {
    this.bit = bit;
  }

  public int getRegisterCount() {
    return this.registerCount;
  }

  public void setRegisterCount(int registerCount) {
    this.registerCount = registerCount;
  }

  public String getCharset() {
    return this.charset;
  }

  public void setCharset(String charset) {
    this.charset = charset;
  }

  public int getWriteType() {
    return this.writeType;
  }

  public void setWriteType(int writeType) {
    this.writeType = writeType;
  }

  public int getRange() {
    return this.range;
  }

  public void setRange(int range) {
    this.range = range;
  }

  public int getModbusDataType() {
    return this.modbusDataType;
  }

  public void setModbusDataType(int modbusDataType) {
    this.modbusDataType = modbusDataType;
  }

  public double getMultiplier() {
    return this.multiplier;
  }

  public void setMultiplier(double multiplier) {
    this.multiplier = multiplier;
  }

  public int getOffset() {
    return this.offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

  public int getSlaveId() {
    return this.slaveId;
  }

  public void setSlaveId(int slaveId) {
    this.slaveId = slaveId;
  }

  public boolean isSlaveMonitor() {
    return this.slaveMonitor;
  }

  public void setSlaveMonitor(boolean slaveMonitor) {
    this.slaveMonitor = slaveMonitor;
  }

  public void validate(ProcessResult response)
  {
    if (!RANGE_CODES.isValidId(this.range, new int[0])) {
      response.addContextualMessage("range", "validate.invalidValue", new Object[0]);
    }
    if (!MODBUS_DATA_TYPE_CODES.isValidId(this.modbusDataType, new int[0])) {
      response.addContextualMessage("modbusDataType", "validate.invalidValue", new Object[0]);
    }
    if (!StringValidation.isBetweenInc(this.slaveId, 1, 240)) {
      response.addContextualMessage("slaveId", "validate.betweenInc", new Object[] { Integer.valueOf(1), Integer.valueOf(240) });
    }
    if (!this.slaveMonitor) {
      int maxEndOffset = 65536 - DataType.getRegisterCount(this.modbusDataType);
      if (!StringValidation.isBetweenInc(this.offset, 0, maxEndOffset)) {
        response.addContextualMessage("offset", "validate.betweenInc", new Object[] { Integer.valueOf(0), Integer.valueOf(maxEndOffset) });
      }
      if (((this.range == 3) || (this.range == 4)) && (this.modbusDataType == 1))
      {
        if (!StringValidation.isBetweenInc(this.bit, 0, 15)) {
          response.addContextualMessage("bit", "validate.betweenInc", new Object[] { Integer.valueOf(0), Integer.valueOf(15) });
        }
      }
      if (isString()) {
        if (this.registerCount < 1)
          response.addContextualMessage("registerCount", "validate.greaterThanZero", new Object[0]);
        try
        {
          Charset.forName(this.charset);
        }
        catch (IllegalCharsetNameException e) {
          response.addContextualMessage("charset", "validate.invalidCharset", new Object[0]);
        }
      }

      if (!WRITE_TYPE_TYPE_CODES.isValidId(this.writeType, new int[0])) {
        response.addContextualMessage("writeType", "validate.invalidValue", new Object[0]);
      }
      if (this.multiplier == 0.0D)
        response.addContextualMessage("multiplier", "validate.not0", new Object[0]);
    }
  }

  private boolean settableRange() {
    return (this.range == 1) || (this.range == 3);
  }

  private boolean isString() {
    return (this.modbusDataType == 18) || (this.modbusDataType == 19);
  }

  public void addProperties(List<TranslatableMessage> list)
  {
    AuditEventType.addPropertyMessage(list, "dsEdit.modbus.slaveId", Integer.valueOf(this.slaveId));
    if (!this.slaveMonitor) {
      AuditEventType.addExportCodeMessage(list, "dsEdit.modbus.registerRange", RANGE_CODES, this.range);
      AuditEventType.addExportCodeMessage(list, "dsEdit.modbus.modbusDataType", MODBUS_DATA_TYPE_CODES, this.modbusDataType);

      AuditEventType.addPropertyMessage(list, "dsEdit.modbus.offset", Integer.valueOf(this.offset));
      AuditEventType.addPropertyMessage(list, "dsEdit.modbus.bit", Byte.valueOf(this.bit));
      AuditEventType.addPropertyMessage(list, "dsEdit.modbus.registerCount", Integer.valueOf(this.registerCount));
      AuditEventType.addPropertyMessage(list, "dsEdit.modbus.charset", this.charset);
      AuditEventType.addPropertyMessage(list, "dsEdit.modbus.writeType", Integer.valueOf(this.writeType));
      AuditEventType.addPropertyMessage(list, "dsEdit.modbus.multiplier", Double.valueOf(this.multiplier));
      AuditEventType.addPropertyMessage(list, "dsEdit.modbus.additive", Double.valueOf(this.additive));
    }
  }

  public void addPropertyChanges(List<TranslatableMessage> list, Object o)
  {
    ModbusPointLocatorVO from = (ModbusPointLocatorVO)o;
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbus.slaveId", from.slaveId, this.slaveId);
    if (!this.slaveMonitor) {
      AuditEventType.maybeAddExportCodeChangeMessage(list, "dsEdit.modbus.registerRange", RANGE_CODES, from.range, this.range);

      AuditEventType.maybeAddExportCodeChangeMessage(list, "dsEdit.modbus.modbusDataType", MODBUS_DATA_TYPE_CODES, from.modbusDataType, this.modbusDataType);

      AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbus.offset", from.offset, this.offset);
      AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbus.bit", from.bit, this.bit);
      AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbus.registerCount", from.registerCount, this.registerCount);

      AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbus.charset", from.charset, this.charset);
      AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbus.writeType", from.writeType, this.writeType);
      AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbus.multiplier", Double.valueOf(from.multiplier), Double.valueOf(this.multiplier));
      AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbus.additive", Double.valueOf(from.additive), Double.valueOf(this.additive));
    }
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    out.writeInt(this.range);
    out.writeInt(this.modbusDataType);
    out.writeInt(this.slaveId);
    out.writeBoolean(this.slaveMonitor);
    out.writeInt(this.offset);
    out.writeByte(this.bit);
    out.writeInt(this.registerCount);
    SerializationHelper.writeSafeUTF(out, this.charset);
    out.writeInt(this.writeType);
    out.writeDouble(this.multiplier);
    out.writeDouble(this.additive);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.range = in.readInt();
      this.modbusDataType = in.readInt();
      this.slaveId = in.readInt();
      this.slaveMonitor = in.readBoolean();
      this.offset = in.readInt();
      this.bit = in.readByte();
      this.registerCount = in.readInt();
      this.charset = SerializationHelper.readSafeUTF(in);
      this.writeType = in.readInt();
      this.multiplier = in.readDouble();
      this.additive = in.readDouble();
    }
  }

  public void jsonWrite(ObjectWriter writer) throws IOException, JsonException
  {
    writer.writeEntry("range", RANGE_CODES.getCode(this.range));
    writer.writeEntry("modbusDataType", MODBUS_DATA_TYPE_CODES.getCode(this.modbusDataType));
    writer.writeEntry("writeType", WRITE_TYPE_TYPE_CODES.getCode(this.writeType));
  }

  public void jsonRead(JsonReader reader, JsonObject jsonObject)
    throws JsonException
  {
    String text = jsonObject.getString("range");
    if (text == null)
      throw new TranslatableJsonException("emport.error.missing", new Object[] { "range", RANGE_CODES.getCodeList(new int[0]) });
    this.range = RANGE_CODES.getId(text, new int[0]);
    if (!RANGE_CODES.isValidId(this.range, new int[0])) {
      throw new TranslatableJsonException("emport.error.invalid", new Object[] { "range", text, RANGE_CODES.getCodeList(new int[0]) });
    }

    text = jsonObject.getString("modbusDataType");
    if (text == null) {
      throw new TranslatableJsonException("emport.error.missing", new Object[] { "modbusDataType", MODBUS_DATA_TYPE_CODES.getCodeList(new int[0]) });
    }
    this.modbusDataType = MODBUS_DATA_TYPE_CODES.getId(text, new int[0]);
    if (!MODBUS_DATA_TYPE_CODES.isValidId(this.modbusDataType, new int[0])) {
      throw new TranslatableJsonException("emport.error.invalid", new Object[] { "modbusDataType", text, MODBUS_DATA_TYPE_CODES.getCodeList(new int[0]) });
    }

    text = jsonObject.getString("writeType");
    if (text != null) {
      this.writeType = WRITE_TYPE_TYPE_CODES.getId(text, new int[0]);
      if (!WRITE_TYPE_TYPE_CODES.isValidId(this.writeType, new int[0]))
        throw new TranslatableJsonException("emport.error.invalid", new Object[] { "writeType", text, WRITE_TYPE_TYPE_CODES.getCodeList(new int[0]) });
    }
  }

  static
  {
    RANGE_CODES.addElement(1, "COIL_STATUS", "dsEdit.modbus.coilStatus");
    RANGE_CODES.addElement(2, "INPUT_STATUS", "dsEdit.modbus.inputStatus");
    RANGE_CODES.addElement(3, "HOLDING_REGISTER", "dsEdit.modbus.holdingRegister");
    RANGE_CODES.addElement(4, "INPUT_REGISTER", "dsEdit.modbus.inputRegister");

    MODBUS_DATA_TYPE_CODES = new ExportCodes();

    MODBUS_DATA_TYPE_CODES.addElement(1, "BINARY", "dsEdit.modbus.modbusDataType.binary");
    MODBUS_DATA_TYPE_CODES.addElement(2, "TWO_BYTE_INT_UNSIGNED", "dsEdit.modbus.modbusDataType.2bUnsigned");

    MODBUS_DATA_TYPE_CODES.addElement(3, "TWO_BYTE_INT_SIGNED", "dsEdit.modbus.modbusDataType.2bSigned");

    MODBUS_DATA_TYPE_CODES.addElement(4, "FOUR_BYTE_INT_UNSIGNED", "dsEdit.modbus.modbusDataType.4bUnsigned");

    MODBUS_DATA_TYPE_CODES.addElement(5, "FOUR_BYTE_INT_SIGNED", "dsEdit.modbus.modbusDataType.4bSigned");

    MODBUS_DATA_TYPE_CODES.addElement(6, "FOUR_BYTE_INT_UNSIGNED_SWAPPED", "dsEdit.modbus.modbusDataType.4bUnsignedSwapped");

    MODBUS_DATA_TYPE_CODES.addElement(7, "FOUR_BYTE_INT_SIGNED_SWAPPED", "dsEdit.modbus.modbusDataType.4bSignedSwapped");

    MODBUS_DATA_TYPE_CODES.addElement(8, "FOUR_BYTE_FLOAT", "dsEdit.modbus.modbusDataType.4bFloat");

    MODBUS_DATA_TYPE_CODES.addElement(9, "FOUR_BYTE_FLOAT_SWAPPED", "dsEdit.modbus.modbusDataType.4bFloatSwapped");

    MODBUS_DATA_TYPE_CODES.addElement(10, "EIGHT_BYTE_INT_UNSIGNED", "dsEdit.modbus.modbusDataType.8bUnsigned");

    MODBUS_DATA_TYPE_CODES.addElement(11, "EIGHT_BYTE_INT_SIGNED", "dsEdit.modbus.modbusDataType.8bSigned");

    MODBUS_DATA_TYPE_CODES.addElement(12, "EIGHT_BYTE_INT_UNSIGNED_SWAPPED", "dsEdit.modbus.modbusDataType.8bUnsignedSwapped");

    MODBUS_DATA_TYPE_CODES.addElement(13, "EIGHT_BYTE_INT_SIGNED_SWAPPED", "dsEdit.modbus.modbusDataType.8bSignedSwapped");

    MODBUS_DATA_TYPE_CODES.addElement(14, "EIGHT_BYTE_FLOAT", "dsEdit.modbus.modbusDataType.8bFloat");

    MODBUS_DATA_TYPE_CODES.addElement(15, "EIGHT_BYTE_FLOAT_SWAPPED", "dsEdit.modbus.modbusDataType.8bFloatSwapped");

    MODBUS_DATA_TYPE_CODES.addElement(16, "TWO_BYTE_BCD", "dsEdit.modbus.modbusDataType.2bBcd");
    MODBUS_DATA_TYPE_CODES.addElement(17, "FOUR_BYTE_BCD", "dsEdit.modbus.modbusDataType.4bBcd");

    MODBUS_DATA_TYPE_CODES.addElement(18, "CHAR", "dsEdit.modbus.modbusDataType.char");
    MODBUS_DATA_TYPE_CODES.addElement(19, "VARCHAR", "dsEdit.modbus.modbusDataType.varchar");

    WRITE_TYPE_TYPE_CODES = new ExportCodes();

    WRITE_TYPE_TYPE_CODES.addElement(1, "NOT_SETTABLE", "dsEdit.modbus.writeType.notSettable");

    WRITE_TYPE_TYPE_CODES.addElement(2, "SETTABLE", "dsEdit.modbus.writeType.settable");
    WRITE_TYPE_TYPE_CODES.addElement(3, "WRITE_ONLY", "dsEdit.modbus.writeType.writeOnly");
  }
}