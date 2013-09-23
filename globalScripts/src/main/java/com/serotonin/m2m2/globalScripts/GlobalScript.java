package com.serotonin.m2m2.globalScripts;

import com.serotonin.json.spi.JsonProperty;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.rt.script.ScriptError;
import com.serotonin.m2m2.rt.script.ScriptUtils;
import com.serotonin.m2m2.util.ChangeComparable;
import com.serotonin.validation.StringValidation;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class GlobalScript
  implements ChangeComparable<GlobalScript>
{
  public static final String XID_PREFIX = "GS_";
  private int id = -1;

  @JsonProperty(read=false)
  private String xid;

  @JsonProperty
  private String name;

  @JsonProperty
  private String script;

  public boolean isNew() { return this.id == -1;
  }

  public int getId()
  {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getXid() {
    return this.xid;
  }

  public void setXid(String xid) {
    this.xid = xid;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getScript() {
    return this.script;
  }

  public void setScript(String script) {
    this.script = script;
  }

  public void validate(ProcessResult response) {
    if (StringUtils.isBlank(this.name))
      response.addContextualMessage("name", "validate.required", new Object[0]);
    if (StringValidation.isLengthGreaterThan(this.name, 100))
      response.addContextualMessage("name", "validate.notLongerThan", new Object[] { Integer.valueOf(100) });
    try
    {
      ScriptUtils.execute(this.script);
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
  }

  public String getTypeKey()
  {
    return "event.audit.globalScript";
  }

  public void addProperties(List<TranslatableMessage> list)
  {
    AuditEventType.addPropertyMessage(list, "common.xid", this.xid);
    AuditEventType.addPropertyMessage(list, "globalScript.name", this.name);
    AuditEventType.addPropertyMessage(list, "globalScript.script", this.script);
  }

  public void addPropertyChanges(List<TranslatableMessage> list, GlobalScript from)
  {
    AuditEventType.maybeAddPropertyChangeMessage(list, "common.xid", from.xid, this.xid);
    AuditEventType.maybeAddPropertyChangeMessage(list, "globalScript.name", from.name, this.name);
    AuditEventType.maybeAddPropertyChangeMessage(list, "globalScript.script", from.script, this.script);
  }
}