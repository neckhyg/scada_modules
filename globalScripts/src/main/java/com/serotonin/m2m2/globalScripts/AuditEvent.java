package com.serotonin.m2m2.globalScripts;

import com.serotonin.m2m2.i18n.Translations;
import com.serotonin.m2m2.module.AuditEventTypeDefinition;
import com.serotonin.m2m2.module.Module;
import com.serotonin.web.taglib.Functions;

public class AuditEvent extends AuditEventTypeDefinition
{
  public static final String TYPE_NAME = "SST_GLOBAL_SCRIPT";

  public String getTypeName()
  {
    return "SST_GLOBAL_SCRIPT";
  }

  public String getDescriptionKey()
  {
    return "event.audit.globalScript";
  }

  public String getEventListLink(int ref1, int ref2, Translations translations)
  {
    String alt = Functions.quotEncode(translations.translate("events.editGlobalScript"));
    StringBuilder sb = new StringBuilder();
    sb.append("<a href='globalScripts.shtm?gsid=");
    sb.append(ref1);
    sb.append("'><img src='");
    sb.append(getModule().getWebPath()).append("/web/script-globe.png");
    sb.append("' alt='").append(alt);
    sb.append("' title='").append(alt);
    sb.append("'/></a>");
    return sb.toString();
  }
}