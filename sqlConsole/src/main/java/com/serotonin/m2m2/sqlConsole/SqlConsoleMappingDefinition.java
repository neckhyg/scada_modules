package com.serotonin.m2m2.sqlConsole;

import com.serotonin.m2m2.module.UrlMappingDefinition;
import com.serotonin.m2m2.module.UrlMappingDefinition.Permission;
import com.serotonin.m2m2.web.mvc.UrlHandler;

public class SqlConsoleMappingDefinition extends UrlMappingDefinition
{
  public String getUrlPath()
  {
    return "/sqlConsole.shtm";
  }

  public UrlHandler getHandler()
  {
    return new SqlController();
  }

  public String getJspPath()
  {
    return "web/sql.jsp";
  }

  public String getMenuKey()
  {
    return "header.sql";
  }

  public String getMenuImage()
  {
    return "web/sql.png";
  }

  public UrlMappingDefinition.Permission getPermission()
  {
    return UrlMappingDefinition.Permission.ADMINISTRATOR;
  }
}