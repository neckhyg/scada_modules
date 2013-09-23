package com.serotonin.m2m2.pachube.ds;

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

public class PachubePointLocatorVO extends AbstractPointLocatorVO
  implements JsonSerializable
{

  @JsonProperty
  private int feedId;

  @JsonProperty
  private String dataStreamId;
  private int dataTypeId;

  @JsonProperty
  private String binary0Value;

  @JsonProperty
  private boolean settable;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  public PointLocatorRT createRuntime()
  {
    return new PachubePointLocatorRT(this);
  }

  public TranslatableMessage getConfigurationDescription()
  {
    return new TranslatableMessage("dsEdit.pachube.dpconn", new Object[] { Integer.valueOf(this.feedId), this.dataStreamId });
  }

  public int getFeedId()
  {
    return this.feedId;
  }

  public void setFeedId(int feedId) {
    this.feedId = feedId;
  }

  public String getDataStreamId() {
    return this.dataStreamId;
  }

  public void setDataStreamId(String dataStreamId) {
    this.dataStreamId = dataStreamId;
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

  public boolean isSettable()
  {
    return this.settable;
  }

  public void setSettable(boolean settable) {
    this.settable = settable;
  }

  public void validate(ProcessResult response)
  {
    if (this.feedId <= 0) {
      response.addContextualMessage("feedId", "validate.invalidValue", new Object[0]);
    }
    if (StringUtils.isBlank(this.dataStreamId)) {
      response.addContextualMessage("dataStreamId", "validate.required", new Object[0]);
    }
    if (!DataTypes.CODES.isValidId(this.dataTypeId, new int[0]))
      response.addContextualMessage("dataTypeId", "validate.invalidValue", new Object[0]);
  }

  public void addProperties(List<TranslatableMessage> list)
  {
    AuditEventType.addPropertyMessage(list, "dsEdit.pachube.feedId", Integer.valueOf(this.feedId));
    AuditEventType.addPropertyMessage(list, "dsEdit.pachube.dataStreamId", this.dataStreamId);
    AuditEventType.addDataTypeMessage(list, "dsEdit.pointDataType", this.dataTypeId);
    AuditEventType.addPropertyMessage(list, "dsEdit.pachube.binaryZeroValue", this.binary0Value);
    AuditEventType.addPropertyMessage(list, "dsEdit.settable", this.settable);
  }

  public void addPropertyChanges(List<TranslatableMessage> list, Object o)
  {
    PachubePointLocatorVO from = (PachubePointLocatorVO)o;
    AuditEventType.maybeAddDataTypeChangeMessage(list, "dsEdit.pointDataType", from.dataTypeId, this.dataTypeId);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.pachube.feedId", from.feedId, this.feedId);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.pachube.dataStreamId", from.dataStreamId, this.dataStreamId);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.pachube.binaryZeroValue", from.binary0Value, this.binary0Value);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.settable", from.settable, this.settable);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    out.writeInt(this.feedId);
    SerializationHelper.writeSafeUTF(out, this.dataStreamId);
    out.writeInt(this.dataTypeId);
    SerializationHelper.writeSafeUTF(out, this.binary0Value);
    out.writeBoolean(this.settable);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.feedId = in.readInt();
      this.dataStreamId = SerializationHelper.readSafeUTF(in);
      this.dataTypeId = in.readInt();
      this.binary0Value = SerializationHelper.readSafeUTF(in);
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