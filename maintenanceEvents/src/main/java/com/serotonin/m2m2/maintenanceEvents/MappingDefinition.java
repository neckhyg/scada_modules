package com.serotonin.m2m2.maintenanceEvents;

import com.serotonin.m2m2.module.UrlMappingDefinition;
import com.serotonin.m2m2.module.UrlMappingDefinition.Permission;
import com.serotonin.m2m2.web.mvc.UrlHandler;

public class MappingDefinition extends UrlMappingDefinition
{
  public String getUrlPath()
  {
    return "/maintenance_events.shtm";
  }

  public UrlHandler getHandler()
  {
    return null;
  }

  public String getJspPath()
  {
    return "web/maintenanceEvents.jsp";
  }

  public String getMenuKey()
  {
    return "header.maintenanceEvents";
  }

  public String getMenuImage()
  {
    return "web/hammer_32.png";
  }

  public UrlMappingDefinition.Permission getPermission()
  {
    return UrlMappingDefinition.Permission.ADMINISTRATOR;
  }
}