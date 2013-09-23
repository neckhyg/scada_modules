package com.serotonin.m2m2.log4jreset;

import com.serotonin.m2m2.module.SystemSettingsDefinition;

public class Log4JResetSettingsDefinition extends SystemSettingsDefinition
{
  public String getDescriptionKey()
  {
    return "log4JReset.settings.header";
  }

  public String getSectionJspPath()
  {
    return "web/settings.jspf";
  }
}