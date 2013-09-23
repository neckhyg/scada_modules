package com.serotonin.m2m2.onewire.vo;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.spi.JsonSerializable;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableJsonException;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.onewire.rt.OneWirePointLocatorRT;
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

public class OneWirePointLocatorVO extends AbstractPointLocatorVO
  implements JsonSerializable
{
  private static final ExportCodes ATTRIBUTE_CODES = new ExportCodes();

  @JsonProperty
  private String address;
  private int attributeId;

  @JsonProperty
  private int index;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  public static String getAttributeDescription(int attributeId) { if (attributeId == 1)
      return "Temperature";
    if (attributeId == 2)
      return "Humidity";
    if (attributeId == 3)
      return "AD voltage";
    if (attributeId == 4)
      return "Latch state";
    if (attributeId == 5)
      return "Wiper position";
    if (attributeId == 6)
      return "Counter";
    return "Unknown"; }

  public static int getAttributeDataType(int attributeId)
  {
    if (attributeId == 4)
      return 1;
    if (attributeId == 5)
      return 2;
    return 3;
  }

  public String getAttributeDescription()
  {
    return getAttributeDescription(this.attributeId);
  }

  public int getDataTypeId()
  {
    return getAttributeDataType(this.attributeId);
  }

  public String getAttributeIndexDescription() {
    String s = getAttributeDescription();
    if ((this.attributeId == 3) || (this.attributeId == 4) || (this.attributeId == 5) || (this.attributeId == 6))
    {
      s = s + " " + this.index;
    }return s;
  }

  public boolean isSettable()
  {
    return (this.attributeId == 4) || (this.attributeId == 5);
  }

  public PointLocatorRT createRuntime()
  {
    return new OneWirePointLocatorRT(this);
  }

  public TranslatableMessage getConfigurationDescription()
  {
    return new TranslatableMessage("dsEdit.1wire.dpconn", new Object[] { this.address, getAttributeIndexDescription() });
  }

  public int getAttributeId() {
    return this.attributeId;
  }

  public void setAttributeId(int attributeId) {
    this.attributeId = attributeId;
  }

  public String getAddress() {
    return this.address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public int getIndex() {
    return this.index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public void validate(ProcessResult response)
  {
    if (StringUtils.isBlank(this.address))
      response.addContextualMessage("address", "validate.required", new Object[0]);
    if (!ATTRIBUTE_CODES.isValidId(this.attributeId, new int[0]))
      response.addContextualMessage("attributeId", "validate.invalidValue", new Object[0]);
  }

  public void addProperties(List<TranslatableMessage> list)
  {
    AuditEventType.addPropertyMessage(list, "dsEdit.1wire.address", this.address);
    AuditEventType.addExportCodeMessage(list, "dsEdit.1wire.attribute", ATTRIBUTE_CODES, this.attributeId);
    AuditEventType.addPropertyMessage(list, "dsEdit.1wire.index", Integer.valueOf(this.index));
  }

  public void addPropertyChanges(List<TranslatableMessage> list, Object o)
  {
    OneWirePointLocatorVO from = (OneWirePointLocatorVO)o;
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.1wire.address", from.address, this.address);
    AuditEventType.maybeAddExportCodeChangeMessage(list, "dsEdit.1wire.attribute", ATTRIBUTE_CODES, from.attributeId, this.attributeId);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.1wire.index", from.index, this.index);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    SerializationHelper.writeSafeUTF(out, this.address);
    out.writeInt(this.attributeId);
    out.writeInt(this.index);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.address = SerializationHelper.readSafeUTF(in);
      this.attributeId = in.readInt();
      this.index = in.readInt();
    }
  }

  public void jsonWrite(ObjectWriter writer) throws IOException, JsonException
  {
    writer.writeEntry("attributeType", ATTRIBUTE_CODES.getCode(this.attributeId));
  }

  public void jsonRead(JsonReader reader, JsonObject jsonObject) throws JsonException
  {
    String text = jsonObject.getString("attributeType");
    if (text == null)
      throw new TranslatableJsonException("emport.error.missing", new Object[] { "attributeType", ATTRIBUTE_CODES.getCodeList(new int[0]) });
    int id = ATTRIBUTE_CODES.getId(text, new int[0]);
    if (id == -1) {
      throw new TranslatableJsonException("emport.error.invalid", new Object[] { "attributeType", text, ATTRIBUTE_CODES.getCodeList(new int[0]) });
    }
    this.attributeId = id;
  }

  static
  {
    ATTRIBUTE_CODES.addElement(1, "TEMPURATURE", "dsEdit.1wire.attr.temperature");
    ATTRIBUTE_CODES.addElement(2, "HUMIDITY", "dsEdit.1wire.attr.humidity");
    ATTRIBUTE_CODES.addElement(3, "AD_VOLTAGE", "dsEdit.1wire.attr.adVoltage");
    ATTRIBUTE_CODES.addElement(4, "LATCH_STATE", "dsEdit.1wire.attr.latchState");
    ATTRIBUTE_CODES.addElement(5, "WIPER_POSITION", "dsEdit.1wire.attr.wiperPosition");
    ATTRIBUTE_CODES.addElement(6, "COUNTER", "dsEdit.1wire.attr.counter");
  }

  public static abstract interface AttributeTypes
  {
    public static final int TEMPURATURE = 1;
    public static final int HUMIDITY = 2;
    public static final int AD_VOLTAGE = 3;
    public static final int LATCH_STATE = 4;
    public static final int WIPER_POSITION = 5;
    public static final int COUNTER = 6;
  }
}