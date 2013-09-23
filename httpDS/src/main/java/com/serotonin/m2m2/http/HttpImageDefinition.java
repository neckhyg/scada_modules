package com.serotonin.m2m2.http;

import com.serotonin.m2m2.http.dwr.HttpDataSourceDwr;
import com.serotonin.m2m2.http.vo.HttpImageDataSourceVO;
import com.serotonin.m2m2.module.DataSourceDefinition;
import com.serotonin.m2m2.module.Module;
import com.serotonin.m2m2.module.ModuleRegistry;
import com.serotonin.m2m2.module.license.DataSourceTypePointsLimit;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;

public class HttpImageDefinition extends DataSourceDefinition
{
  public void preInitialize()
  {
    ModuleRegistry.addLicenseEnforcement(new DataSourceTypePointsLimit(getModule().getName(), "HTTP_IMAGE", 20, null));
  }

  public String getDataSourceTypeName()
  {
    return "HTTP_IMAGE";
  }

  public String getDescriptionKey()
  {
    return "dsEdit.httpImage";
  }

  protected DataSourceVO<?> createDataSourceVO()
  {
    return new HttpImageDataSourceVO();
  }

  public String getEditPagePath()
  {
    return "web/editHttpImage.jsp";
  }

  public Class<?> getDwrClass()
  {
    return HttpDataSourceDwr.class;
  }
}