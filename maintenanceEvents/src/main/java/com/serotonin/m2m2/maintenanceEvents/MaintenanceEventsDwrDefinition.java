package com.serotonin.m2m2.maintenanceEvents;

import com.serotonin.m2m2.module.DwrDefinition;
import com.serotonin.m2m2.web.dwr.ModuleDwr;

public class MaintenanceEventsDwrDefinition extends DwrDefinition
{
  public Class<? extends ModuleDwr> getDwrClass()
  {
    return MaintenanceEventsDwr.class;
  }
}