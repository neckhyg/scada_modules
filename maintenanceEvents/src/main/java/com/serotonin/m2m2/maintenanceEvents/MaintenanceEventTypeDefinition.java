package com.serotonin.m2m2.maintenanceEvents;

import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.i18n.Translations;
import com.serotonin.m2m2.module.EventTypeDefinition;
import com.serotonin.m2m2.module.Module;
import com.serotonin.m2m2.rt.event.type.EventType;
import com.serotonin.m2m2.vo.event.EventTypeVO;
import com.serotonin.web.taglib.Functions;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceEventTypeDefinition extends EventTypeDefinition
{
  public String getTypeName()
  {
    return "MAINTENANCE";
  }

  public Class<? extends EventType> getEventTypeClass()
  {
    return MaintenanceEventType.class;
  }

  public EventType createEventType(String subtype, int ref1, int ref2)
  {
    return new MaintenanceEventType(ref1);
  }

  public boolean getHandlersRequireAdmin()
  {
    return true;
  }

  public List<EventTypeVO> getEventTypeVOs()
  {
    List vos = new ArrayList();

    for (MaintenanceEventVO me : new MaintenanceEventDao().getMaintenanceEvents()) {
      vos.add(me.getEventType());
    }
    return vos;
  }

  public String getIconPath()
  {
    return getModule().getWebPath() + "/web/hammer.png";
  }

  public String getDescriptionKey()
  {
    return "maintenanceEvents.mes";
  }

  public String getEventListLink(String subtype, int ref1, int ref2, Translations translations)
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

  public TranslatableMessage getSourceDisabledMessage()
  {
    return new TranslatableMessage("event.rtn.maintDisabled");
  }
}