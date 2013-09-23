package com.serotonin.m2m2.wibox;

import com.serotonin.m2m2.module.ServletDefinition;
import javax.servlet.http.HttpServlet;

public class WiBoxDataSourceServletDefinition extends ServletDefinition
{
  public HttpServlet getServlet()
  {
    return new WiBoxDataSourceServlet();
  }

  public String getUriPattern()
  {
    return "/wibox";
  }
}