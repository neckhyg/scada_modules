package com.serotonin.m2m2.snmp.vo;

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
import com.serotonin.m2m2.snmp.rt.SnmpPointLocatorRT;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.util.SerializationHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.snmp4j.smi.OID;

public class SnmpPointLocatorVO extends AbstractPointLocatorVO
  implements JsonSerializable
{

  @JsonProperty
  private String oid;
  private int dataTypeId;

  @JsonProperty
  private String binary0Value = "0";

  @JsonProperty
  private int setType;

  @JsonProperty
  private boolean trapOnly;
  private static final long serialVersionUID = -1L;
  private static final int version = 3;

  public TranslatableMessage getConfigurationDescription()
  {
    return new TranslatableMessage("common.default", new Object[] { this.oid });
  }

  public boolean isSettable()
  {
    return this.setType != 0;
  }

  public PointLocatorRT createRuntime()
  {
    return new SnmpPointLocatorRT(this);
  }

  public String getOid()
  {
    return this.oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  public int getDataTypeId()
  {
    return this.dataTypeId;
  }

  public void setDataTypeId(int dataTypeId) {
    this.dataTypeId = dataTypeId;
  }

  public String getBinary0Value() {
    return this.binary0Value;
  }

  public void setBinary0Value(String binary0Value) {
    this.binary0Value = binary0Value;
  }

  public int getSetType() {
    return this.setType;
  }

  public void setSetType(int setType) {
    this.setType = setType;
  }

  public boolean isTrapOnly() {
    return this.trapOnly;
  }

  public void setTrapOnly(boolean trapOnly) {
    this.trapOnly = trapOnly;
  }

  public void validate(ProcessResult response)
  {
    if (StringUtils.isBlank(this.oid)) {
      response.addContextualMessage("oid", "validate.required", new Object[0]);
    } else {
      this.oid = this.oid.trim();
      try {
        new OID(this.oid);
      }
      catch (RuntimeException e) {
        response.addContextualMessage("oid", "validate.parseError", new Object[] { e.getMessage() });
      }
    }

    if (!DataTypes.CODES.isValidId(this.dataTypeId, new int[0]))
      response.addContextualMessage("dataTypeId", "validate.invalidValue", new Object[0]);
  }

  public void addProperties(List<TranslatableMessage> list)
  {
    AuditEventType.addPropertyMessage(list, "dsEdit.snmp.oid", this.oid);
    AuditEventType.addDataTypeMessage(list, "dsEdit.pointDataType", this.dataTypeId);
    AuditEventType.addPropertyMessage(list, "dsEdit.snmp.binary0Value", this.binary0Value);
    AuditEventType.addPropertyMessage(list, "dsEdit.snmp.setType", Integer.valueOf(this.setType));
    AuditEventType.addPropertyMessage(list, "dsEdit.snmp.polling", this.trapOnly);
  }

  public void addPropertyChanges(List<TranslatableMessage> list, Object o)
  {
    SnmpPointLocatorVO from = (SnmpPointLocatorVO)o;
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.oid", from.oid, this.oid);
    AuditEventType.maybeAddDataTypeChangeMessage(list, "dsEdit.pointDataType", from.dataTypeId, this.dataTypeId);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.binary0Value", from.binary0Value, this.binary0Value);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.setType", from.setType, this.setType);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.polling", from.trapOnly, this.trapOnly);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(3);
    SerializationHelper.writeSafeUTF(out, this.oid);
    out.writeInt(this.dataTypeId);
    SerializationHelper.writeSafeUTF(out, this.binary0Value);
    out.writeInt(this.setType);
    out.writeBoolean(this.trapOnly);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.oid = SerializationHelper.readSafeUTF(in);
      this.dataTypeId = in.readInt();
      this.binary0Value = "0";
      this.setType = in.readInt();
      this.trapOnly = false;
    }
    else if (ver == 2) {
      this.oid = SerializationHelper.readSafeUTF(in);
      this.dataTypeId = in.readInt();
      this.binary0Value = "0";
      this.setType = in.readInt();
      this.trapOnly = in.readBoolean();
    }
    else if (ver == 3) {
      this.oid = SerializationHelper.readSafeUTF(in);
      this.dataTypeId = in.readInt();
      this.binary0Value = SerializationHelper.readSafeUTF(in);
      this.setType = in.readInt();
      this.trapOnly = in.readBoolean();
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

  public static abstract interface SetTypes
  {
    public static final int NONE = 0;
    public static final int INTEGER_32 = 1;
    public static final int OCTET_STRING = 2;
    public static final int OID = 3;
    public static final int IP_ADDRESS = 4;
    public static final int COUNTER_32 = 5;
    public static final int GAUGE_32 = 6;
    public static final int TIME_TICKS = 7;
    public static final int OPAQUE = 8;
    public static final int COUNTER_64 = 9;
  }
}