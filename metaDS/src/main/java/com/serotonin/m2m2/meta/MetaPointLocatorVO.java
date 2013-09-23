package com.serotonin.m2m2.meta;

import com.serotonin.db.IntValuePair;
import com.serotonin.db.pair.IntStringPair;
import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.spi.JsonSerializable;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.DataTypes;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableJsonException;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.util.VarNames;
import com.serotonin.m2m2.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.timer.CronTimerTrigger;
import com.serotonin.util.SerializationHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class MetaPointLocatorVO extends AbstractPointLocatorVO
  implements JsonSerializable
{
  public static final int UPDATE_EVENT_CONTEXT_UPDATE = 0;
  public static final int UPDATE_EVENT_CRON = 100;
  public static ExportCodes UPDATE_EVENT_CODES = new ExportCodes();

  private List<IntStringPair> context = new ArrayList();

  @JsonProperty
  private String script;
  private int dataTypeId;

  @JsonProperty
  private boolean settable;
  private int updateEvent = 0;

  @JsonProperty
  private String updateCronPattern;

  @JsonProperty
  private int executionDelaySeconds;
  private static final long serialVersionUID = -1L;
  private static final int version = 5;

  public PointLocatorRT createRuntime() { return new MetaPointLocatorRT(this);
  }

  public TranslatableMessage getConfigurationDescription()
  {
    return new TranslatableMessage("common.default", new Object[] { "'" + StringUtils.abbreviate(this.script, 40) + "'" });
  }

  public List<IntStringPair> getContext() {
    return this.context;
  }

  public void setContext(List<IntStringPair> context) {
    this.context = context;
  }

  public String getScript() {
    return this.script;
  }

  public void setScript(String script) {
    this.script = script;
  }

  public int getExecutionDelaySeconds() {
    return this.executionDelaySeconds;
  }

  public void setExecutionDelaySeconds(int executionDelaySeconds) {
    this.executionDelaySeconds = executionDelaySeconds;
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

  public int getUpdateEvent() {
    return this.updateEvent;
  }

  public void setUpdateEvent(int updateEvent) {
    this.updateEvent = updateEvent;
  }

  public String getUpdateCronPattern() {
    return this.updateCronPattern;
  }

  public void setUpdateCronPattern(String updateCronPattern) {
    this.updateCronPattern = updateCronPattern;
  }

  public void validate(ProcessResult response)
  {
    if (StringUtils.isBlank(this.script)) {
      response.addContextualMessage("script", "validate.required", new Object[0]);
    }
    List varNameSpace = new ArrayList();
    for (IntStringPair point : this.context) {
      String varName = point.getValue();
      if (StringUtils.isBlank(varName)) {
        response.addContextualMessage("context", "validate.allVarNames", new Object[0]);
        break;
      }

      if (!VarNames.validateVarName(varName)) {
        response.addContextualMessage("context", "validate.invalidVarName", new Object[] { varName });
        break;
      }

      if (varNameSpace.contains(varName)) {
        response.addContextualMessage("context", "validate.duplicateVarName", new Object[] { varName });
        break;
      }

      varNameSpace.add(varName);
    }

    if (!DataTypes.CODES.isValidId(this.dataTypeId, new int[0])) {
      response.addContextualMessage("dataTypeId", "validate.invalidValue", new Object[0]);
    }
    if (this.updateEvent == 100) {
      try {
        new CronTimerTrigger(this.updateCronPattern);
      }
      catch (Exception e) {
        response.addContextualMessage("updateCronPattern", "validate.invalidCron", new Object[] { this.updateCronPattern });
      }
    }
    else if ((this.updateEvent != 0) && (!Common.TIME_PERIOD_CODES.isValidId(this.updateEvent, new int[0]))) {
      response.addContextualMessage("updateEvent", "validate.invalidValue", new Object[0]);
    }
    if (this.executionDelaySeconds < 0)
      response.addContextualMessage("executionDelaySeconds", "validate.cannotBeNegative", new Object[0]);
  }

  public void addProperties(List<TranslatableMessage> list)
  {
    AuditEventType.addDataTypeMessage(list, "dsEdit.pointDataType", this.dataTypeId);
    AuditEventType.addPropertyMessage(list, "dsEdit.settable", this.settable);
    AuditEventType.addPropertyMessage(list, "dsEdit.meta.scriptContext", VarNames.contextToString(this.context));
    AuditEventType.addPropertyMessage(list, "dsEdit.meta.script", this.script);
    AuditEventType.addExportCodeMessage(list, "dsEdit.meta.event", UPDATE_EVENT_CODES, this.updateEvent);
    if (this.updateEvent == 100)
      AuditEventType.addPropertyMessage(list, "dsEdit.meta.event.cron", this.updateCronPattern);
    AuditEventType.addPropertyMessage(list, "dsEdit.meta.delay", Integer.valueOf(this.executionDelaySeconds));
  }

  public void addPropertyChanges(List<TranslatableMessage> list, Object o)
  {
    MetaPointLocatorVO from = (MetaPointLocatorVO)o;
    AuditEventType.maybeAddDataTypeChangeMessage(list, "dsEdit.pointDataType", from.dataTypeId, this.dataTypeId);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.settable", from.settable, this.settable);
    if (!this.context.equals(from.context)) {
      AuditEventType.addPropertyChangeMessage(list, "dsEdit.meta.scriptContext", VarNames.contextToString(from.context), VarNames.contextToString(this.context));
    }
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.meta.script", from.script, this.script);
    AuditEventType.maybeAddExportCodeChangeMessage(list, "dsEdit.meta.event", UPDATE_EVENT_CODES, from.updateEvent, this.updateEvent);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.meta.event.cron", from.updateCronPattern, this.updateCronPattern);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.meta.delay", from.executionDelaySeconds, this.executionDelaySeconds);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(5);
    out.writeObject(this.context);
    SerializationHelper.writeSafeUTF(out, this.script);
    out.writeInt(this.dataTypeId);
    out.writeBoolean(this.settable);
    out.writeInt(this.updateEvent);
    SerializationHelper.writeSafeUTF(out, this.updateCronPattern);
    out.writeInt(this.executionDelaySeconds);
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
  {
    int ver = in.readInt();

    if (ver == 1) {
      this.context = new ArrayList();
      Map ctxMap = (Map)in.readObject();
      for (Map.Entry point : (Set<Entry<?,?> >)ctxMap.entrySet()) {
        this.context.add(new IntStringPair(((Integer)point.getKey()).intValue(), (String)point.getValue()));
      }
      this.script = SerializationHelper.readSafeUTF(in);
      this.dataTypeId = in.readInt();
      this.settable = false;
      this.updateEvent = in.readInt();
      this.updateCronPattern = "";
      this.executionDelaySeconds = in.readInt();
    }
    else if (ver == 2) {
      this.context = convertContext(in);
      this.script = SerializationHelper.readSafeUTF(in);
      this.dataTypeId = in.readInt();
      this.settable = false;
      this.updateEvent = in.readInt();
      this.updateCronPattern = "";
      this.executionDelaySeconds = in.readInt();
    }
    else if (ver == 3) {
      this.context = convertContext(in);
      this.script = SerializationHelper.readSafeUTF(in);
      this.dataTypeId = in.readInt();
      this.settable = false;
      this.updateEvent = in.readInt();
      this.updateCronPattern = SerializationHelper.readSafeUTF(in);
      this.executionDelaySeconds = in.readInt();
    }
    else if (ver == 4) {
      this.context = convertContext(in);
      this.script = SerializationHelper.readSafeUTF(in);
      this.dataTypeId = in.readInt();
      this.settable = in.readBoolean();
      this.updateEvent = in.readInt();
      this.updateCronPattern = SerializationHelper.readSafeUTF(in);
      this.executionDelaySeconds = in.readInt();
    }
    else if (ver == 5) {
      this.context = ((List)in.readObject());
      this.script = SerializationHelper.readSafeUTF(in);
      this.dataTypeId = in.readInt();
      this.settable = in.readBoolean();
      this.updateEvent = in.readInt();
      this.updateCronPattern = SerializationHelper.readSafeUTF(in);
      this.executionDelaySeconds = in.readInt();
    }
  }

  private List<IntStringPair> convertContext(ObjectInputStream in) throws IOException, ClassNotFoundException
  {
    List<IntValuePair> old = (List)in.readObject();
    List ctx = new ArrayList();
    for (IntValuePair ivp : old)
      ctx.add(new IntStringPair(ivp.getKey(), ivp.getValue()));
    return ctx;
  }

  public void jsonWrite(ObjectWriter writer) throws IOException, JsonException
  {
    writeDataType(writer);

    writer.writeEntry("updateEvent", UPDATE_EVENT_CODES.getCode(this.updateEvent));
    VarNames.jsonWriteVarContext(writer, this.context);
  }

  public void jsonRead(JsonReader reader, JsonObject jsonObject) throws JsonException
  {
    Integer value = readDataType(jsonObject, new int[] { 5 });
    if (value != null) {
      this.dataTypeId = value.intValue();
    }
    String text = jsonObject.getString("updateEvent");
    if (text != null) {
      this.updateEvent = UPDATE_EVENT_CODES.getId(text, new int[0]);
      if (this.updateEvent == -1) {
        throw new TranslatableJsonException("emport.error.invalid", new Object[] { "updateEvent", text, UPDATE_EVENT_CODES.getCodeList(new int[0]) });
      }
    }

    VarNames.jsonReadVarContext(jsonObject, this.context);
  }

  static
  {
    UPDATE_EVENT_CODES.addElement(0, "CONTEXT_UPDATE", "dsEdit.meta.event.context");
    UPDATE_EVENT_CODES.addElement(2, "MINUTES", "dsEdit.meta.event.minute");
    UPDATE_EVENT_CODES.addElement(3, "HOURS", "dsEdit.meta.event.hour");
    UPDATE_EVENT_CODES.addElement(4, "DAYS", "dsEdit.meta.event.day");
    UPDATE_EVENT_CODES.addElement(5, "WEEKS", "dsEdit.meta.event.week");
    UPDATE_EVENT_CODES.addElement(6, "MONTHS", "dsEdit.meta.event.month");
    UPDATE_EVENT_CODES.addElement(7, "YEARS", "dsEdit.meta.event.year");
    UPDATE_EVENT_CODES.addElement(100, "CRON", "dsEdit.meta.event.cron");
  }
}