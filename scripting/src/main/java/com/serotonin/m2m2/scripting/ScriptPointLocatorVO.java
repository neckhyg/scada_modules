package com.serotonin.m2m2.scripting;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.db.pair.IntStringPair;
import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.spi.JsonSerializable;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.DataTypes;
import com.serotonin.m2m2.db.dao.DataPointDao;
import com.serotonin.m2m2.db.dao.DataSourceDao;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.util.VarNames;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.m2m2.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.util.SerializationHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class ScriptPointLocatorVO extends AbstractPointLocatorVO
  implements JsonSerializable
{

  @JsonProperty
  private String varName;
  private int dataTypeId;

  @JsonProperty
  private boolean settable;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  public PointLocatorRT createRuntime()
  {
    return new ScriptPointLocatorRT(this);
  }

  public TranslatableMessage getConfigurationDescription()
  {
    return new TranslatableMessage("common.default", new Object[] { this.varName });
  }

  public String getVarName() {
    return this.varName;
  }

  public void setVarName(String varName) {
    this.varName = varName;
  }

  public int getDataTypeId()
  {
    return this.dataTypeId;
  }

  public void setDataTypeId(int dataTypeId) {
    this.dataTypeId = dataTypeId;
  }

  public boolean isSettable()
  {
    return this.settable;
  }

  public void setSettable(boolean settable) {
    this.settable = settable;
  }

  public void validate(ProcessResult response, DataPointVO dpvo)
  {
    if (StringUtils.isBlank(this.varName)) {
      response.addContextualMessage("varName", "validate.required", new Object[0]);
    } else if (!VarNames.validateVarName(this.varName)) {
      response.addContextualMessage("varName", "validate.invalidValue", new Object[] { this.varName });
    }
    else {
      ScriptDataSourceVO ds = (ScriptDataSourceVO)new DataSourceDao().getDataSource(dpvo.getDataSourceId());
      for (IntStringPair ivp : ds.getContext()) {
        if (this.varName.equals(ivp.getValue())) {
          response.addContextualMessage("varName", "validate.duplicateVarName", new Object[] { this.varName });
        }
      }
      for (DataPointVO dp : new DataPointDao().getDataPoints(ds.getId(), null))
      {
        if (dp.getId() != dpvo.getId()) {
          ScriptPointLocatorVO that = (ScriptPointLocatorVO)dp.getPointLocator();
          if (this.varName.equals(that.getVarName())) {
            response.addContextualMessage("varName", "validate.duplicateVarName", new Object[] { this.varName });
          }
        }
      }
    }
    if (!DataTypes.CODES.isValidId(this.dataTypeId, new int[0]))
      response.addContextualMessage("dataTypeId", "validate.invalidValue", new Object[0]);
  }

  public void validate(ProcessResult response)
  {
    throw new ShouldNeverHappenException("Should not have been called");
  }

  public void addProperties(List<TranslatableMessage> list)
  {
    AuditEventType.addPropertyMessage(list, "dsEdit.script.varName", this.varName);
    AuditEventType.addDataTypeMessage(list, "dsEdit.pointDataType", this.dataTypeId);
    AuditEventType.addPropertyMessage(list, "dsEdit.settable", this.settable);
  }

  public void addPropertyChanges(List<TranslatableMessage> list, Object o)
  {
    ScriptPointLocatorVO from = (ScriptPointLocatorVO)o;
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.script.varName", from.varName, this.varName);
    AuditEventType.maybeAddDataTypeChangeMessage(list, "dsEdit.pointDataType", from.dataTypeId, this.dataTypeId);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.settable", from.settable, this.settable);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    SerializationHelper.writeSafeUTF(out, this.varName);
    out.writeInt(this.dataTypeId);
    out.writeBoolean(this.settable);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.varName = SerializationHelper.readSafeUTF(in);
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