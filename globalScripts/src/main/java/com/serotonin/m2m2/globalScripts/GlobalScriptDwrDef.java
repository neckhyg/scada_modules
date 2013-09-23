package com.serotonin.m2m2.globalScripts;

import com.serotonin.m2m2.module.DwrDefinition;
import com.serotonin.m2m2.web.dwr.ModuleDwr;

public class GlobalScriptDwrDef extends DwrDefinition
{
  public Class<? extends ModuleDwr> getDwrClass()
  {
    return GlobalScriptsDwr.class;
  }
}