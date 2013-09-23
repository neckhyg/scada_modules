package com.serotonin.m2m2.jviews;

import com.serotonin.m2m2.module.DwrConversionDefinition;

public class JspViewsDwrConversionDefinition extends DwrConversionDefinition
{
  public void addConversions()
  {
    addConversion(JspComponentState.class);
  }
}