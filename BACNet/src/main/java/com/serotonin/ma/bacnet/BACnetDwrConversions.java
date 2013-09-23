package com.serotonin.ma.bacnet;

import com.serotonin.m2m2.module.DwrConversionDefinition;

public class BACnetDwrConversions extends DwrConversionDefinition
{
  public void addConversions()
  {
    addConversion(BACnetDeviceBean.class);
    addConversion(BACnetObjectBean.class);
  }
}