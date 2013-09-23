package com.serotonin.m2m2.mbus;

import com.serotonin.m2m2.module.DwrConversionDefinition;

public class MBusDwrConversionDefinition extends DwrConversionDefinition
{
  public void addConversions()
  {
    addConversion(MBusConnectionType.class, "enum");
  }
}