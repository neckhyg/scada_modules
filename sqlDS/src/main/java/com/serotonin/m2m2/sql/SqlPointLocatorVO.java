package com.serotonin.m2m2.sql;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.spi.JsonSerializable;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.DataTypes;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.util.SerializationHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class SqlPointLocatorVO extends AbstractPointLocatorVO
  implements JsonSerializable
{

  @JsonProperty
  private String fieldName;

  @JsonProperty
  private String timeOverrideName;
  private int dataTypeId;

  @JsonProperty
  private String updateStatement;
  private static final long serialVersionUID = -1L;
  private static final int version = 2;

  public TranslatableMessage getConfigurationDescription()
  {
    return new TranslatableMessage("common.default", new Object[] { this.fieldName });
  }

  public boolean isSettable()
  {
    return !StringUtils.isBlank(this.updateStatement);
  }

  public PointLocatorRT createRuntime()
  {
    return new SqlPointLocatorRT(this);
  }

  public String getFieldName()
  {
    return this.fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public String getTimeOverrideName() {
    return this.timeOverrideName;
  }

  public void setTimeOverrideName(String timeOverrideName) {
    this.timeOverrideName = timeOverrideName;
  }

  public String getUpdateStatement() {
    return this.updateStatement;
  }

  public void setUpdateStatement(String updateStatement) {
    this.updateStatement = updateStatement;
  }

  public int getDataTypeId()
  {
    return this.dataTypeId;
  }

  public void setDataTypeId(int dataTypeId) {
    this.dataTypeId = dataTypeId;
  }

  public void validate(ProcessResult response)
  {
    if (!DataTypes.CODES.isValidId(this.dataTypeId, new int[0]))
      response.addContextualMessage("dataTypeId", "validate.invalidValue", new Object[0]);
    if ((StringUtils.isBlank(this.fieldName)) && (StringUtils.isBlank(this.updateStatement)))
      response.addContextualMessage("fieldName", "validate.fieldName", new Object[0]);
  }

  public void addProperties(List<TranslatableMessage> list)
  {
    AuditEventType.addDataTypeMessage(list, "dsEdit.pointDataType", this.dataTypeId);
    AuditEventType.addPropertyMessage(list, "dsEdit.sql.rowId", this.fieldName);
    AuditEventType.addPropertyMessage(list, "dsEdit.sql.timeColumn", this.timeOverrideName);
    AuditEventType.addPropertyMessage(list, "dsEdit.sql.update", this.updateStatement);
  }

  public void addPropertyChanges(List<TranslatableMessage> list, Object o)
  {
    SqlPointLocatorVO from = (SqlPointLocatorVO)o;
    AuditEventType.maybeAddDataTypeChangeMessage(list, "dsEdit.pointDataType", from.dataTypeId, this.dataTypeId);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.sql.rowId", from.fieldName, this.fieldName);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.sql.timeColumn", from.timeOverrideName, this.timeOverrideName);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.sql.update", from.updateStatement, this.updateStatement);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(2);
    SerializationHelper.writeSafeUTF(out, this.fieldName);
    SerializationHelper.writeSafeUTF(out, this.timeOverrideName);
    SerializationHelper.writeSafeUTF(out, this.updateStatement);
    out.writeInt(this.dataTypeId);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.fieldName = SerializationHelper.readSafeUTF(in);
      this.timeOverrideName = "";
      this.updateStatement = SerializationHelper.readSafeUTF(in);
      this.dataTypeId = in.readInt();
    }
    else if (ver == 2) {
      this.fieldName = SerializationHelper.readSafeUTF(in);
      this.timeOverrideName = SerializationHelper.readSafeUTF(in);
      this.updateStatement = SerializationHelper.readSafeUTF(in);
      this.dataTypeId = in.readInt();
    }
  }

  public void jsonWrite(ObjectWriter writer) throws IOException, JsonException
  {
    writeDataType(writer);
  }

  public void jsonRead(JsonReader reader, JsonObject jsonObject) throws JsonException
  {
    Integer value = readDataType(jsonObject, new int[] { 5 });
    if (value != null)
      this.dataTypeId = value.intValue();
  }
}