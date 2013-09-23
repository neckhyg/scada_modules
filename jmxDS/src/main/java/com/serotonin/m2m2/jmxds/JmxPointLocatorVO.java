package com.serotonin.m2m2.jmxds;

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

public class JmxPointLocatorVO extends AbstractPointLocatorVO
  implements JsonSerializable
{

  @JsonProperty
  private String objectName;

  @JsonProperty
  private String attributeName;

  @JsonProperty
  private String compositeItemName;
  private int dataTypeId;

  @JsonProperty
  private boolean settable;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  public boolean isSettable()
  {
    return this.settable;
  }

  public PointLocatorRT createRuntime()
  {
    return new JmxPointLocatorRT(this);
  }

  public TranslatableMessage getConfigurationDescription()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(this.objectName).append(" > ").append(this.attributeName);
    if (!StringUtils.isBlank(this.compositeItemName))
      sb.append(" > ").append(this.compositeItemName);
    return new TranslatableMessage("common.default", new Object[] { sb.toString() });
  }

  public String getObjectName() {
    return this.objectName;
  }

  public void setObjectName(String objectName) {
    this.objectName = objectName;
  }

  public String getAttributeName() {
    return this.attributeName;
  }

  public void setAttributeName(String attributeName) {
    this.attributeName = attributeName;
  }

  public String getCompositeItemName() {
    return this.compositeItemName;
  }

  public void setCompositeItemName(String compositeItemName) {
    this.compositeItemName = compositeItemName;
  }

  public int getDataTypeId()
  {
    return this.dataTypeId;
  }

  public void setDataTypeId(int dataTypeId) {
    this.dataTypeId = dataTypeId;
  }

  public void setSettable(boolean settable) {
    this.settable = settable;
  }

  public void validate(ProcessResult response)
  {
    if (StringUtils.isBlank(this.objectName))
      response.addContextualMessage("objectName", "validate.required", new Object[0]);
    if (StringUtils.isBlank(this.attributeName))
      response.addContextualMessage("attributeName", "validate.required", new Object[0]);
    if (!DataTypes.CODES.isValidId(this.dataTypeId, new int[0]))
      response.addContextualMessage("dataTypeId", "validate.invalidValue", new Object[0]);
  }

  public void addProperties(List<TranslatableMessage> list)
  {
    AuditEventType.addPropertyMessage(list, "dsEdit.jmx.objectName", this.objectName);
    AuditEventType.addPropertyMessage(list, "dsEdit.jmx.attributeName", this.attributeName);
    AuditEventType.addPropertyMessage(list, "dsEdit.jmx.compositeItemName", this.compositeItemName);
    AuditEventType.addDataTypeMessage(list, "dsEdit.pointDataType", this.dataTypeId);
    AuditEventType.addPropertyMessage(list, "dsEdit.settable", this.settable);
  }

  public void addPropertyChanges(List<TranslatableMessage> list, Object o)
  {
    JmxPointLocatorVO from = (JmxPointLocatorVO)o;
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.jmx.objectName", from.objectName, this.objectName);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.jmx.attributeName", from.attributeName, this.attributeName);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.jmx.compositeItemName", from.compositeItemName, this.compositeItemName);

    AuditEventType.maybeAddDataTypeChangeMessage(list, "dsEdit.pointDataType", from.dataTypeId, this.dataTypeId);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.settable", from.settable, this.settable);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    SerializationHelper.writeSafeUTF(out, this.objectName);
    SerializationHelper.writeSafeUTF(out, this.attributeName);
    SerializationHelper.writeSafeUTF(out, this.compositeItemName);
    out.writeInt(this.dataTypeId);
    out.writeBoolean(this.settable);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.objectName = SerializationHelper.readSafeUTF(in);
      this.attributeName = SerializationHelper.readSafeUTF(in);
      this.compositeItemName = SerializationHelper.readSafeUTF(in);
      this.dataTypeId = in.readInt();
      this.settable = in.readBoolean();
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