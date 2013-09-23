package com.serotonin.m2m2.galil.vo;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.DataTypes;
import com.serotonin.m2m2.galil.rt.InputPointTypeRT;
import com.serotonin.m2m2.galil.rt.PointTypeRT;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableJsonException;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.util.ExportCodes;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class InputPointTypeVO extends PointTypeVO
{
  private static final int[] EXCLUDE_DATA_TYPES = { 4, 5, 2 };

  private int dataTypeId = 1;

  @JsonProperty
  private int inputId = 1;

  @JsonProperty
  private double scaleRawLow = 0.0D;

  @JsonProperty
  private double scaleRawHigh = 1.0D;

  @JsonProperty
  private double scaleEngLow = 0.0D;

  @JsonProperty
  private double scaleEngHigh = 1.0D;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  public int typeId() { return 2;
  }

  public PointTypeRT createRuntime()
  {
    return new InputPointTypeRT(this);
  }

  public int getDataTypeId()
  {
    return this.dataTypeId;
  }

  public TranslatableMessage getDescription()
  {
    return new TranslatableMessage("dsEdit.galil.pointType.input");
  }

  public boolean isSettable()
  {
    return false;
  }

  public void validate(ProcessResult response)
  {
    if (!DataTypes.CODES.isValidId(this.dataTypeId, EXCLUDE_DATA_TYPES)) {
      response.addContextualMessage("dataTypeId", "validate.invalidValue", new Object[0]);
    }
    if (this.dataTypeId == 1) {
      if ((this.inputId < 1) || (this.inputId > 96))
        response.addContextualMessage("inputPointType.inputId", "validate.between", new Object[] { Integer.valueOf(1), Integer.valueOf(96) });
    }
    else {
      if ((this.inputId < 1) || (this.inputId > 8))
        response.addContextualMessage("inputPointType.inputId", "validate.between", new Object[] { Integer.valueOf(1), Integer.valueOf(8) });
      if (this.scaleRawHigh <= this.scaleRawLow)
        response.addContextualMessage("inputPointType.scaleRawHighId", "validate.greaterThanRawLow", new Object[0]);
      if (this.scaleEngHigh <= this.scaleEngLow)
        response.addContextualMessage("inputPointType.scaleEngHighId", "validate.greaterThanEngLow", new Object[0]);
    }
  }

  public int getInputId() {
    return this.inputId;
  }

  public void setInputId(int inputId) {
    this.inputId = inputId;
  }

  public double getScaleRawLow() {
    return this.scaleRawLow;
  }

  public void setScaleRawLow(double scaleRawLow) {
    this.scaleRawLow = scaleRawLow;
  }

  public double getScaleRawHigh() {
    return this.scaleRawHigh;
  }

  public void setScaleRawHigh(double scaleRawHigh) {
    this.scaleRawHigh = scaleRawHigh;
  }

  public double getScaleEngLow() {
    return this.scaleEngLow;
  }

  public void setScaleEngLow(double scaleEngLow) {
    this.scaleEngLow = scaleEngLow;
  }

  public double getScaleEngHigh() {
    return this.scaleEngHigh;
  }

  public void setScaleEngHigh(double scaleEngHigh) {
    this.scaleEngHigh = scaleEngHigh;
  }

  public void setDataTypeId(int dataTypeId) {
    this.dataTypeId = dataTypeId;
  }

  public void addProperties(List<TranslatableMessage> list)
  {
    AuditEventType.addDataTypeMessage(list, "dsEdit.pointDataType", this.dataTypeId);
    AuditEventType.addPropertyMessage(list, "dsEdit.galil.inputNumber", Integer.valueOf(this.inputId));
    AuditEventType.addPropertyMessage(list, "dsEdit.galil.scaleLow", Double.valueOf(this.scaleRawLow));
    AuditEventType.addPropertyMessage(list, "dsEdit.galil.scaleHigh", Double.valueOf(this.scaleRawHigh));
    AuditEventType.addPropertyMessage(list, "dsEdit.galil.engLow", Double.valueOf(this.scaleEngLow));
    AuditEventType.addPropertyMessage(list, "dsEdit.galil.engHigh", Double.valueOf(this.scaleEngHigh));
  }

  public void addPropertyChanges(List<TranslatableMessage> list, Object o)
  {
    InputPointTypeVO from = (InputPointTypeVO)o;
    AuditEventType.maybeAddDataTypeChangeMessage(list, "dsEdit.pointDataType", from.dataTypeId, this.dataTypeId);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.galil.inputNumber", from.inputId, this.inputId);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.galil.scaleLow", Double.valueOf(from.scaleRawLow), Double.valueOf(this.scaleRawLow));
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.galil.scaleHigh", Double.valueOf(from.scaleRawHigh), Double.valueOf(this.scaleRawHigh));
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.galil.engLow", Double.valueOf(from.scaleEngLow), Double.valueOf(this.scaleEngLow));
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.galil.engHigh", Double.valueOf(from.scaleEngHigh), Double.valueOf(this.scaleEngHigh));
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    out.writeInt(this.dataTypeId);
    out.writeInt(this.inputId);
    out.writeDouble(this.scaleRawLow);
    out.writeDouble(this.scaleRawHigh);
    out.writeDouble(this.scaleEngLow);
    out.writeDouble(this.scaleEngHigh);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.dataTypeId = in.readInt();
      this.inputId = in.readInt();
      this.scaleRawLow = in.readDouble();
      this.scaleRawHigh = in.readDouble();
      this.scaleEngLow = in.readDouble();
      this.scaleEngHigh = in.readDouble();
    }
  }

  public void jsonWrite(ObjectWriter writer) throws IOException, JsonException
  {
    super.jsonWrite(writer);
    writer.writeEntry("dataType", DataTypes.CODES.getCode(this.dataTypeId));
  }

  public void jsonRead(JsonReader reader, JsonObject jsonObject) throws JsonException
  {
    super.jsonRead(reader, jsonObject);

    String text = jsonObject.getString("dataType");
    if (text != null) {
      this.dataTypeId = DataTypes.CODES.getId(text, new int[0]);
      if (!DataTypes.CODES.isValidId(this.dataTypeId, EXCLUDE_DATA_TYPES))
        throw new TranslatableJsonException("emport.error.invalid", new Object[] { "dataType", text, DataTypes.CODES.getCodeList(EXCLUDE_DATA_TYPES) });
    }
  }
}