package com.serotonin.ma.ascii.file;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.spi.JsonSerializable;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;
import com.serotonin.m2m2.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.util.SerializationHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class FilePointLocatorVO extends AbstractPointLocatorVO
  implements JsonSerializable
{
  private int dataTypeId = 1;

  @JsonProperty
  private String valueRegex = "";

  @JsonProperty
  private boolean customTimestamp;

  @JsonProperty
  private String timestampFormat = "";

  @JsonProperty
  private String timestampRegex = "";
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  public PointLocatorRT createRuntime()
  {
    return new FilePointLocatorRT(this);
  }

  public TranslatableMessage getConfigurationDescription()
  {
    return null;
  }

  public boolean isSettable()
  {
    return false;
  }

  public int getDataTypeId()
  {
    return this.dataTypeId;
  }

  public void setDataTypeId(int dataTypeId) {
    this.dataTypeId = dataTypeId;
  }

  public String getValueRegex() {
    return this.valueRegex;
  }

  public void setValueRegex(String valueRegex) {
    this.valueRegex = valueRegex;
  }

  public void setCustomTimestamp(boolean customTimestamp) {
    this.customTimestamp = customTimestamp;
  }

  public boolean isCustomTimestamp() {
    return this.customTimestamp;
  }

  public void setTimestampFormat(String timestampFormat) {
    this.timestampFormat = timestampFormat;
  }

  public String getTimestampFormat() {
    return this.timestampFormat;
  }

  public String getTimestampRegex() {
    return this.timestampRegex;
  }

  public void setTimestampRegex(String timestampRegex) {
    this.timestampRegex = timestampRegex;
  }

  public void validate(ProcessResult response)
  {
    if (StringUtils.isBlank(this.valueRegex))
      response.addContextualMessage("valueRegex", "validate.required", new Object[0]);
    if (this.customTimestamp) {
      if (StringUtils.isBlank(this.timestampFormat))
        response.addContextualMessage("timestampFormat", "validate.required", new Object[0]);
      if (StringUtils.isBlank(this.timestampRegex))
        response.addContextualMessage("timestampRegex", "validate.required", new Object[0]);
    }
  }

  public void addProperties(List<TranslatableMessage> list)
  {
  }

  public void addPropertyChanges(List<TranslatableMessage> list, Object o)
  {
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    out.writeInt(this.dataTypeId);
    SerializationHelper.writeSafeUTF(out, this.valueRegex);
    out.writeBoolean(this.customTimestamp);
    SerializationHelper.writeSafeUTF(out, this.timestampFormat);
    SerializationHelper.writeSafeUTF(out, this.timestampRegex);
  }

  private void readObject(ObjectInputStream in) throws IOException
  {
    int ver = in.readInt();
    if (ver == 1) {
      this.dataTypeId = in.readInt();
      this.valueRegex = SerializationHelper.readSafeUTF(in);
      this.customTimestamp = in.readBoolean();
      this.timestampFormat = SerializationHelper.readSafeUTF(in);
      this.timestampRegex = SerializationHelper.readSafeUTF(in);
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