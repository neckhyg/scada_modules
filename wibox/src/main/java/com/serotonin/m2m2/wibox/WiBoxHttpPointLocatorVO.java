package com.serotonin.m2m2.wibox;

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

public class WiBoxHttpPointLocatorVO extends AbstractPointLocatorVO
  implements JsonSerializable
{

  @JsonProperty
  private String moteId;

  @JsonProperty
  private String dataKey;
  private int dataTypeId;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  public boolean isSettable()
  {
    return false;
  }

  public PointLocatorRT createRuntime()
  {
    return new WiBoxHttpPointLocatorRT(this);
  }

  public TranslatableMessage getConfigurationDescription()
  {
    return new TranslatableMessage("dsEdit.wiboxHttp.dpconn", new Object[] { this.moteId, this.dataKey });
  }

  public String getMoteId()
  {
    return this.moteId;
  }

  public void setMoteId(String moteId) {
    this.moteId = moteId;
  }

  public String getDataKey() {
    return this.dataKey;
  }

  public void setDataKey(String dataKey) {
    this.dataKey = dataKey;
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
    if (StringUtils.isBlank(this.moteId))
      response.addContextualMessage("moteId", "validate.required", new Object[0]);
    if (StringUtils.isBlank(this.dataKey))
      response.addContextualMessage("parameterName", "validate.required", new Object[0]);
    if (!DataTypes.CODES.isValidId(this.dataTypeId, new int[0]))
      response.addContextualMessage("dataTypeId", "validate.invalidValue", new Object[0]);
  }

  public void addProperties(List<TranslatableMessage> list)
  {
    AuditEventType.addPropertyMessage(list, "dsEdit.wiboxHttp.moteId", this.moteId);
    AuditEventType.addPropertyMessage(list, "dsEdit.wiboxHttp.dataKey", this.dataKey);
    AuditEventType.addDataTypeMessage(list, "dsEdit.pointDataType", this.dataTypeId);
  }

  public void addPropertyChanges(List<TranslatableMessage> list, Object o)
  {
    WiBoxHttpPointLocatorVO from = (WiBoxHttpPointLocatorVO)o;
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.wiboxHttp.moteId", from.moteId, this.moteId);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.wiboxHttp.dataKey", from.dataKey, this.dataKey);
    AuditEventType.maybeAddDataTypeChangeMessage(list, "dsEdit.pointDataType", from.dataTypeId, this.dataTypeId);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    SerializationHelper.writeSafeUTF(out, this.moteId);
    SerializationHelper.writeSafeUTF(out, this.dataKey);
    out.writeInt(this.dataTypeId);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.moteId = SerializationHelper.readSafeUTF(in);
      this.dataKey = SerializationHelper.readSafeUTF(in);
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