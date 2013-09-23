package com.serotonin.m2m2.maintenanceEvents;

import com.serotonin.m2m2.module.DwrConversionDefinition;

public class MaintenanceEventsConversionDefinition extends DwrConversionDefinition
{
  public void addConversions()
  {
    addConversion(MaintenanceEventVO.class);
  }
}