package com.serotonin.m2m2.globalScripts;

import com.serotonin.m2m2.module.DwrConversionDefinition;

public class DwrConversion extends DwrConversionDefinition
{
  public void addConversions()
  {
    addConversion(GlobalScript.class);
  }
}