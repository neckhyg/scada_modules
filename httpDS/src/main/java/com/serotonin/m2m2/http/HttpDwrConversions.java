package com.serotonin.m2m2.http;

import com.serotonin.m2m2.http.common.HttpReceiverPointSample;
import com.serotonin.m2m2.module.DwrConversionDefinition;

public class HttpDwrConversions extends DwrConversionDefinition
{
  public void addConversions()
  {
    addConversion(HttpReceiverPointSample.class);
  }
}