package com.serotonin.m2m2.galil.vo;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.DataTypes;
import com.serotonin.m2m2.galil.rt.PointTypeRT;
import com.serotonin.m2m2.galil.rt.VariablePointTypeRT;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableJsonException;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.util.SerializationHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class VariablePointTypeVO extends PointTypeVO
{

  @JsonProperty
  private String variableName = "";

  private int dataTypeId = 3;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  public PointTypeRT createRuntime()
  {
    return new VariablePointTypeRT(this);
  }

  public int typeId()
  {
    return 5;
  }

  public int getDataTypeId()
  {
    return this.dataTypeId;
  }

  public TranslatableMessage getDescription()
  {
    return new TranslatableMessage("dsEdit.galil.pointType.variable");
  }

  public boolean isSettable()
  {
    return true;
  }

  public void validate(ProcessResult response)
  {
    if (!DataTypes.CODES.isValidId(this.dataTypeId, new int[] { 5 }))
      response.addContextualMessage("dataTypeId", "validate.invalidValue", new Object[0]);
    if (StringUtils.isBlank(this.variableName))
      response.addContextualMessage("variablePointType.variableName", "validate.required", new Object[0]);
  }

  public String getVariableName() {
    return this.variableName;
  }

  public void setVariableName(String variableName) {
    this.variableName = variableName;
  }

  public void setDataTypeId(int dataTypeId) {
    this.dataTypeId = dataTypeId;
  }

  public void addProperties(List<TranslatableMessage> list)
  {
    AuditEventType.addPropertyMessage(list, "dsEdit.galil.varName", this.variableName);
    AuditEventType.addDataTypeMessage(list, "dsEdit.pointDataType", this.dataTypeId);
  }

  public void addPropertyChanges(List<TranslatableMessage> list, Object o)
  {
    VariablePointTypeVO from = (VariablePointTypeVO)o;
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.galil.varName", from.variableName, this.variableName);
    AuditEventType.maybeAddDataTypeChangeMessage(list, "dsEdit.pointDataType", from.dataTypeId, this.dataTypeId);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    SerializationHelper.writeSafeUTF(out, this.variableName);
    out.writeInt(this.dataTypeId);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.variableName = SerializationHelper.readSafeUTF(in);
      this.dataTypeId = in.readInt();
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
      if (!DataTypes.CODES.isValidId(this.dataTypeId, new int[] { 5 }))
        throw new TranslatableJsonException("emport.error.invalid", new Object[] { "dataType", text, DataTypes.CODES.getCodeList(new int[] { 5 }) });
    }
  }
}