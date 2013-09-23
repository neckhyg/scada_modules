package com.serotonin.m2m2.http;

import com.serotonin.m2m2.http.common.HttpDataSourceServlet;
import com.serotonin.m2m2.module.ServletDefinition;
import javax.servlet.http.HttpServlet;

public class HttpDataSourceServletDefinition extends ServletDefinition
{
  public HttpServlet getServlet()
  {
    return new HttpDataSourceServlet();
  }

  public String getUriPattern()
  {
    return "/httpds";
  }
}