package com.serotonin.m2m2.maintenanceEvents;

import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.module.EventManagerListenerDefinition;
import com.serotonin.m2m2.rt.event.type.DataPointEventType;
import com.serotonin.m2m2.rt.event.type.DataSourceEventType;
import com.serotonin.m2m2.rt.event.type.EventType;

public class EMListener extends EventManagerListenerDefinition
{
  public TranslatableMessage autoAckEventWithMessage(EventType eventType)
  {
    if (((eventType instanceof DataSourceEventType)) && (RTMDefinition.instance.isActiveMaintenanceEvent(eventType.getDataSourceId())))
    {
      return new TranslatableMessage("events.ackedByMaintenance");
    }

    if (((eventType instanceof DataPointEventType)) && (RTMDefinition.instance.isActiveMaintenanceEvent(eventType.getDataSourceId())))
    {
      return new TranslatableMessage("events.ackedByMaintenance");
    }
    return null;
  }
}