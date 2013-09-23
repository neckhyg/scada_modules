package com.serotonin.m2m2.jviews;

import com.serotonin.m2m2.module.DwrDefinition;
import com.serotonin.m2m2.web.dwr.ModuleDwr;

public class JspViewDwrDefinition extends DwrDefinition
{
  public Class<? extends ModuleDwr> getDwrClass()
  {
    return JspViewDwr.class;
  }
}