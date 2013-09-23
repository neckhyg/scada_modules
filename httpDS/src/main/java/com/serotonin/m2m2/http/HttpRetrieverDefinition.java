package com.serotonin.m2m2.http;

import com.serotonin.m2m2.http.dwr.HttpDataSourceDwr;
import com.serotonin.m2m2.http.vo.HttpRetrieverDataSourceVO;
import com.serotonin.m2m2.module.DataSourceDefinition;
import com.serotonin.m2m2.module.Module;
import com.serotonin.m2m2.module.ModuleRegistry;
import com.serotonin.m2m2.module.license.DataSourceTypePointsLimit;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;

public class HttpRetrieverDefinition extends DataSourceDefinition
{
  public void preInitialize()
  {
    ModuleRegistry.addLicenseEnforcement(new DataSourceTypePointsLimit(getModule().getName(), "HTTP_RETRIEVER", 20, null));
  }

  public String getDataSourceTypeName()
  {
    return "HTTP_RETRIEVER";
  }

  public String getDescriptionKey()
  {
    return "dsEdit.httpRetriever";
  }

  protected DataSourceVO<?> createDataSourceVO()
  {
    return new HttpRetrieverDataSourceVO();
  }

  public String getEditPagePath()
  {
    return "web/editHttpRetriever.jsp";
  }

  public Class<?> getDwrClass()
  {
    return HttpDataSourceDwr.class;
  }
}