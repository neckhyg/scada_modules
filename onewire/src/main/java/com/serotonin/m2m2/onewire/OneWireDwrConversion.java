package com.serotonin.m2m2.onewire;

import com.serotonin.m2m2.module.DwrConversionDefinition;
import com.serotonin.m2m2.onewire.common.OneWireContainerAttribute;
import com.serotonin.m2m2.onewire.common.OneWireContainerInfo;

public class OneWireDwrConversion extends DwrConversionDefinition
{
  public void addConversions()
  {
    addConversion(OneWireContainerInfo.class);
    addConversion(OneWireContainerAttribute.class);
  }
}