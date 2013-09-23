package com.serotonin.m2m2.maintenanceEvents;

import com.serotonin.m2m2.i18n.Translations;
import com.serotonin.m2m2.module.AuditEventTypeDefinition;
import com.serotonin.m2m2.module.Module;
import com.serotonin.web.taglib.Functions;

public class AuditEvent extends AuditEventTypeDefinition
{
  public static final String TYPE_NAME = "MAINTENANCE_EVENT";

  public String getTypeName()
  {
    return "MAINTENANCE_EVENT";
  }

  public String getDescriptionKey()
  {
    return "event.audit.maintenanceEvent";
  }

  public String getEventListLink(int ref1, int ref2, Translations translations)
  {
    String alt = Functions.quotEncode(translations.translate("events.editMaintenanceEvent"));
    StringBuilder sb = new StringBuilder();
    sb.append("<a href='maintenance_events.shtm?meid=");
    sb.append(ref1);
    sb.append("'><img src='");
    sb.append(getModule().getWebPath()).append("/web/hammer.png");
    sb.append("' alt='").append(alt);
    sb.append("' title='").append(alt);
    sb.append("'/></a>");
    return sb.toString();
  }
}