package com.serotonin.m2m2.globalScripts;

import com.serotonin.m2m2.module.UrlMappingDefinition;
import com.serotonin.m2m2.module.UrlMappingDefinition.Permission;
import com.serotonin.m2m2.web.mvc.UrlHandler;

public class UrlMapping extends UrlMappingDefinition
{
  public String getUrlPath()
  {
    return "/globalScripts.shtm";
  }

  public UrlHandler getHandler()
  {
    return null;
  }

  public String getJspPath()
  {
    return "web/globalScripts.jsp";
  }

  public String getMenuKey()
  {
    return "header.globalScripts";
  }

  public String getMenuImage()
  {
    return "web/script-globe.png";
  }

  public UrlMappingDefinition.Permission getPermission()
  {
    return UrlMappingDefinition.Permission.ADMINISTRATOR;
  }
}