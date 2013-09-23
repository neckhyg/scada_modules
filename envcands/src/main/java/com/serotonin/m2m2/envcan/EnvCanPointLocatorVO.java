package com.serotonin.m2m2.envcan;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonEntity;
import com.serotonin.json.spi.JsonSerializable;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableJsonException;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.vo.dataSource.AbstractPointLocatorVO;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

@JsonEntity
public class EnvCanPointLocatorVO extends AbstractPointLocatorVO
  implements JsonSerializable
{
  public static ExportCodes ATTRIBUTE_CODES = new ExportCodes();

  private int attributeId = 1;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  public boolean isSettable()
  {
    return false;
  }

  public PointLocatorRT createRuntime()
  {
    return new EnvCanPointLocatorRT(this);
  }

  public TranslatableMessage getConfigurationDescription()
  {
    if (ATTRIBUTE_CODES.isValidId(this.attributeId, new int[0]))
      return new TranslatableMessage(ATTRIBUTE_CODES.getKey(this.attributeId));
    return new TranslatableMessage("common.unknown");
  }

  public int getDataTypeId()
  {
    if (this.attributeId == 10)
      return 4;
    return 3;
  }

  public int getAttributeId() {
    return this.attributeId;
  }

  public void setAttributeId(int attributeId) {
    this.attributeId = attributeId;
  }

  public void validate(ProcessResult response)
  {
    if (!ATTRIBUTE_CODES.isValidId(this.attributeId, new int[0]))
      response.addContextualMessage("attributeId", "validate.invalidValue", new Object[0]);
  }

  public void addProperties(List<TranslatableMessage> list)
  {
    AuditEventType.addExportCodeMessage(list, "envcands.attr", ATTRIBUTE_CODES, this.attributeId);
  }

  public void addPropertyChanges(List<TranslatableMessage> list, Object o)
  {
    EnvCanPointLocatorVO from = (EnvCanPointLocatorVO)o;
    AuditEventType.maybeAddExportCodeChangeMessage(list, "envcands.attr", ATTRIBUTE_CODES, from.attributeId, this.attributeId);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    out.writeInt(this.attributeId);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1)
      this.attributeId = in.readInt();
  }

  public void jsonWrite(ObjectWriter writer) throws IOException, JsonException
  {
    writer.writeEntry("attributeId", ATTRIBUTE_CODES.getCode(this.attributeId));
  }

  public void jsonRead(JsonReader reader, JsonObject jsonObject) throws JsonException
  {
    String text = jsonObject.getString("attributeId");
    if (text == null)
      throw new TranslatableJsonException("emport.error.missing", new Object[] { "attributeId", ATTRIBUTE_CODES.getCodeList(new int[0]) });
    this.attributeId = ATTRIBUTE_CODES.getId(text, new int[0]);
    if (!ATTRIBUTE_CODES.isValidId(this.attributeId, new int[0]))
      throw new TranslatableJsonException("emport.error.invalid", new Object[] { "attributeId", text, ATTRIBUTE_CODES.getCodeList(new int[0]) });
  }

  static
  {
    ATTRIBUTE_CODES.addElement(1, "TEMP", "envcands.attr.temp");
    ATTRIBUTE_CODES.addElement(2, "DEW_POINT_TEMP", "envcands.attr.dewPointTemp");
    ATTRIBUTE_CODES.addElement(3, "REL_HUM", "envcands.attr.relHum");
    ATTRIBUTE_CODES.addElement(4, "WIND_DIR", "envcands.attr.windDir");
    ATTRIBUTE_CODES.addElement(5, "WIND_SPEED", "envcands.attr.windSpeed");
    ATTRIBUTE_CODES.addElement(6, "VISIBILITY", "envcands.attr.visibility");
    ATTRIBUTE_CODES.addElement(7, "STN_PRESS", "envcands.attr.stnPress");
    ATTRIBUTE_CODES.addElement(8, "HUMIDEX", "envcands.attr.humidex");
    ATTRIBUTE_CODES.addElement(9, "WIND_CHILL", "envcands.attr.windChill");
    ATTRIBUTE_CODES.addElement(10, "WEATHER", "envcands.attr.weather");
  }

  public static abstract interface Attributes
  {
    public static final int TEMP = 1;
    public static final int DEW_POINT_TEMP = 2;
    public static final int REL_HUM = 3;
    public static final int WIND_DIR = 4;
    public static final int WIND_SPEED = 5;
    public static final int VISIBILITY = 6;
    public static final int STN_PRESS = 7;
    public static final int HUMIDEX = 8;
    public static final int WIND_CHILL = 9;
    public static final int WEATHER = 10;
  }
}