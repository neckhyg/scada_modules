package com.serotonin.m2m2.scripting;

import com.serotonin.db.pair.IntStringPair;
import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.db.dao.DataPointDao;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableJsonException;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.DataSourceRT;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.rt.script.ScriptError;
import com.serotonin.m2m2.rt.script.ScriptUtils;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.util.VarNames;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;
import com.serotonin.m2m2.vo.event.EventTypeVO;
import com.serotonin.timer.CronTimerTrigger;
import com.serotonin.util.SerializationHelper;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class ScriptDataSourceVO extends DataSourceVO<ScriptDataSourceVO>
{
  private static final ExportCodes EVENT_CODES = new ExportCodes();
  public static final ExportCodes LOG_LEVEL_CODES;
  private List<IntStringPair> context = new ArrayList();

  @JsonProperty
  private String script;

  @JsonProperty
  private String cronPattern;

  @JsonProperty
  private int executionDelaySeconds;
  private int logLevel = 10;
  private static final long serialVersionUID = -1L;
  private static final int version = 2;

  protected void addEventTypes(List<EventTypeVO> ets)
  {
    ets.add(createEventType(1, new TranslatableMessage("event.ds.scriptError")));

    ets.add(createEventType(2, new TranslatableMessage("event.ds.resultType")));

    ets.add(createEventType(3, new TranslatableMessage("event.ds.logError")));
  }

  public ExportCodes getEventCodes()
  {
    return EVENT_CODES;
  }

  public DataSourceRT createDataSourceRT()
  {
    return new ScriptDataSourceRT(this);
  }

  public TranslatableMessage getConnectionDescription()
  {
    if (this.executionDelaySeconds > 0)
      return new TranslatableMessage("common.default", new Object[] { this.cronPattern + " (+" + this.executionDelaySeconds + ")" });
    return new TranslatableMessage("common.default", new Object[] { this.cronPattern });
  }

  public ScriptPointLocatorVO createPointLocator()
  {
    return new ScriptPointLocatorVO();
  }

  public List<IntStringPair> getContext()
  {
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

  public String getCronPattern() {
    return this.cronPattern;
  }

  public void setCronPattern(String cronPattern) {
    this.cronPattern = cronPattern;
  }

  public int getExecutionDelaySeconds() {
    return this.executionDelaySeconds;
  }

  public void setExecutionDelaySeconds(int executionDelaySeconds) {
    this.executionDelaySeconds = executionDelaySeconds;
  }

  public int getLogLevel() {
    return this.logLevel;
  }

  public void setLogLevel(int logLevel) {
    this.logLevel = logLevel;
  }

  public String getLogPath() {
    return ScriptDataSourceRT.getLogFile(getId()).getPath();
  }

  public void validate(ProcessResult response)
  {
    super.validate(response);
    try
    {
      ScriptUtils.compile(this.script);
    }
    catch (ScriptError e) {
      if (e.getColumnNumber() == -1) {
        response.addContextualMessage("script", "globalScript.rhinoException", new Object[] { e.getMessage(), Integer.valueOf(e.getLineNumber()) });
      }
      else
        response.addContextualMessage("script", "globalScript.rhinoExceptionCol", new Object[] { e.getMessage(), Integer.valueOf(e.getLineNumber()), Integer.valueOf(e.getColumnNumber()) });
    }
    catch (RuntimeException e)
    {
      response.addContextualMessage("script", "common.default", new Object[] { e.getMessage() });
    }

    Set varNameSpace = new HashSet();

    for (DataPointVO dpvo : new DataPointDao().getDataPoints(getId(), null)) {
      varNameSpace.add(((ScriptPointLocatorVO)dpvo.getPointLocator()).getVarName());
    }
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

    if (StringUtils.isBlank(this.cronPattern))
      response.addContextualMessage("cronPattern", "validate.required", new Object[0]);
    else {
      try {
        new CronTimerTrigger(this.cronPattern);
      }
      catch (Exception e) {
        response.addContextualMessage("cronPattern", "validate.invalidCron", new Object[] { this.cronPattern });
      }
    }

    if (this.executionDelaySeconds < 0) {
      response.addContextualMessage("executionDelaySeconds", "validate.cannotBeNegative", new Object[0]);
    }
    if (!LOG_LEVEL_CODES.isValidId(this.logLevel, new int[0]))
      response.addContextualMessage("logLevel", "validate.invalidValue", new Object[0]);
  }

  protected void addPropertiesImpl(List<TranslatableMessage> list)
  {
    AuditEventType.addPropertyMessage(list, "dsEdit.script.scriptContext", VarNames.contextToString(this.context));
    AuditEventType.addPropertyMessage(list, "dsEdit.script.script", this.script);
    AuditEventType.addPropertyMessage(list, "dsEdit.script.cron", this.cronPattern);
    AuditEventType.addPropertyMessage(list, "dsEdit.script.delay", Integer.valueOf(this.executionDelaySeconds));
    AuditEventType.addExportCodeMessage(list, "dsEdit.script.logLevel", LOG_LEVEL_CODES, this.logLevel);
  }

  protected void addPropertyChangesImpl(List<TranslatableMessage> list, ScriptDataSourceVO from)
  {
    if (!this.context.equals(from.context)) {
      AuditEventType.addPropertyChangeMessage(list, "dsEdit.script.scriptContext", VarNames.contextToString(from.context), VarNames.contextToString(this.context));
    }
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.script.script", from.script, this.script);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.script.cron", from.cronPattern, this.cronPattern);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.script.delay", from.executionDelaySeconds, this.executionDelaySeconds);

    AuditEventType.maybeAddExportCodeChangeMessage(list, "dsEdit.script.delay", LOG_LEVEL_CODES, from.logLevel, this.logLevel);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(2);
    out.writeObject(this.context);
    SerializationHelper.writeSafeUTF(out, this.script);
    SerializationHelper.writeSafeUTF(out, this.cronPattern);
    out.writeInt(this.executionDelaySeconds);
    out.writeInt(this.logLevel);
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
  {
    int ver = in.readInt();

    if (ver == 1) {
      this.context = ((List)in.readObject());
      this.script = SerializationHelper.readSafeUTF(in);
      this.cronPattern = SerializationHelper.readSafeUTF(in);
      this.executionDelaySeconds = in.readInt();
      this.logLevel = 10;
    }
    else if (ver == 2) {
      this.context = ((List)in.readObject());
      this.script = SerializationHelper.readSafeUTF(in);
      this.cronPattern = SerializationHelper.readSafeUTF(in);
      this.executionDelaySeconds = in.readInt();
      this.logLevel = in.readInt();
    }
  }

  public void jsonWrite(ObjectWriter writer) throws IOException, JsonException
  {
    super.jsonWrite(writer);

    VarNames.jsonWriteVarContext(writer, this.context);
    writer.writeEntry("logLevel", LOG_LEVEL_CODES.getCode(this.logLevel));
  }

  public void jsonRead(JsonReader reader, JsonObject jsonObject) throws JsonException
  {
    super.jsonRead(reader, jsonObject);

    VarNames.jsonReadVarContext(jsonObject, this.context);

    String text = jsonObject.getString("logLevel");
    if (text != null) {
      this.logLevel = LOG_LEVEL_CODES.getId(text, new int[0]);
      if (this.logLevel == -1)
        throw new TranslatableJsonException("emport.error.invalid", new Object[] { "logLevel", text, LOG_LEVEL_CODES.getCodeList(new int[0]) });
    }
  }

  static
  {
    EVENT_CODES.addElement(1, "SCRIPT_ERROR");
    EVENT_CODES.addElement(2, "DATA_TYPE_ERROR");
    EVENT_CODES.addElement(3, "LOG_ERROR");

    LOG_LEVEL_CODES = new ExportCodes();

    LOG_LEVEL_CODES.addElement(10, "NONE", "dsEdit.script.logLevel.none");
    LOG_LEVEL_CODES.addElement(1, "TRACE", "dsEdit.script.logLevel.trace");
    LOG_LEVEL_CODES.addElement(2, "DEBUG", "dsEdit.script.logLevel.debug");
    LOG_LEVEL_CODES.addElement(3, "INFO", "dsEdit.script.logLevel.info");
    LOG_LEVEL_CODES.addElement(4, "WARN", "dsEdit.script.logLevel.warn");
    LOG_LEVEL_CODES.addElement(5, "ERROR", "dsEdit.script.logLevel.error");
    LOG_LEVEL_CODES.addElement(6, "FATAL", "dsEdit.script.logLevel.fatal");
  }

  public static abstract interface LogLevel
  {
    public static final int TRACE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int FATAL = 6;
    public static final int NONE = 10;
  }
}