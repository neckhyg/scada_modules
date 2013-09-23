package com.serotonin.m2m2.http.vo;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.spi.JsonSerializable;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.DataTypes;
import com.serotonin.m2m2.db.dao.DataSourceDao;
import com.serotonin.m2m2.http.rt.HttpReceiverPointLocatorRT;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.m2m2.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.util.SerializationHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class HttpReceiverPointLocatorVO extends AbstractPointLocatorVO
  implements JsonSerializable
{

  @JsonProperty
  private String parameterName;
  private int dataTypeId;

  @JsonProperty
  private String binary0Value;

  @JsonProperty
  private boolean settable;

  @JsonProperty
  private String setPointName;
  private static final long serialVersionUID = -1L;
  private static final int version = 2;

  public PointLocatorRT createRuntime()
  {
    return new HttpReceiverPointLocatorRT(this);
  }

  public TranslatableMessage getConfigurationDescription()
  {
    return new TranslatableMessage("dsEdit.httpReceiver.dpconn", new Object[] { this.parameterName });
  }

  public int getDataTypeId()
  {
    return this.dataTypeId;
  }

  public void setDataTypeId(int dataTypeId) {
    this.dataTypeId = dataTypeId;
  }

  public String getParameterName() {
    return this.parameterName;
  }

  public void setParameterName(String parameterName) {
    this.parameterName = parameterName;
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

  public String getSetPointName() {
    return this.setPointName;
  }

  public void setSetPointName(String setPointName) {
    this.setPointName = setPointName;
  }

  public void validate(ProcessResult response, DataPointVO dpvo)
  {
    if (StringUtils.isBlank(this.parameterName))
      response.addContextualMessage("parameterName", "validate.required", new Object[0]);
    if (!DataTypes.CODES.isValidId(this.dataTypeId, new int[0])) {
      response.addContextualMessage("dataTypeId", "validate.invalidValue", new Object[0]);
    }
    if (this.settable) {
      if (StringUtils.isBlank(this.setPointName)) {
        response.addContextualMessage("setPointName", "validate.required", new Object[0]);
      }
      HttpReceiverDataSourceVO ds = (HttpReceiverDataSourceVO)new DataSourceDao().getDataSource(dpvo.getDataSourceId());

      if (StringUtils.isBlank(ds.getSetPointUrl()))
        response.addContextualMessage("setPointName", "http.validate.noSetPointUrl", new Object[0]);
    }
  }

  public void validate(ProcessResult response)
  {
    throw new ShouldNeverHappenException("Should not have been called");
  }

  public void addProperties(List<TranslatableMessage> list)
  {
    AuditEventType.addPropertyMessage(list, "dsEdit.httpReceiver.httpParamName", this.parameterName);
    AuditEventType.addDataTypeMessage(list, "dsEdit.pointDataType", this.dataTypeId);
    AuditEventType.addPropertyMessage(list, "dsEdit.httpReceiver.binaryZeroValue", this.binary0Value);
    AuditEventType.addPropertyMessage(list, "dsEdit.settable", this.settable);
    AuditEventType.addPropertyMessage(list, "http.dsEdit.setPointName", this.setPointName);
  }

  public void addPropertyChanges(List<TranslatableMessage> list, Object o)
  {
    HttpReceiverPointLocatorVO from = (HttpReceiverPointLocatorVO)o;
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.httpReceiver.httpParamName", from.parameterName, this.parameterName);

    AuditEventType.maybeAddDataTypeChangeMessage(list, "dsEdit.pointDataType", from.dataTypeId, this.dataTypeId);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.httpReceiver.binaryZeroValue", from.binary0Value, this.binary0Value);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.settable", from.settable, this.settable);
    AuditEventType.maybeAddPropertyChangeMessage(list, "http.dsEdit.setPointName", from.setPointName, this.setPointName);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(2);
    SerializationHelper.writeSafeUTF(out, this.parameterName);
    out.writeInt(this.dataTypeId);
    SerializationHelper.writeSafeUTF(out, this.binary0Value);
    out.writeBoolean(this.settable);
    SerializationHelper.writeSafeUTF(out, this.setPointName);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.parameterName = SerializationHelper.readSafeUTF(in);
      this.dataTypeId = in.readInt();
      this.binary0Value = SerializationHelper.readSafeUTF(in);
      this.settable = false;
      this.setPointName = null;
    }
    else if (ver == 2) {
      this.parameterName = SerializationHelper.readSafeUTF(in);
      this.dataTypeId = in.readInt();
      this.binary0Value = SerializationHelper.readSafeUTF(in);
      this.settable = in.readBoolean();
      this.setPointName = SerializationHelper.readSafeUTF(in);
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