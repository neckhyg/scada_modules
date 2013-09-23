package com.serotonin.m2m2.http;

import com.serotonin.m2m2.http.dwr.HttpDataSourceDwr;
import com.serotonin.m2m2.http.vo.HttpReceiverDataSourceVO;
import com.serotonin.m2m2.module.DataSourceDefinition;
import com.serotonin.m2m2.module.Module;
import com.serotonin.m2m2.module.ModuleRegistry;
import com.serotonin.m2m2.module.license.DataSourceTypePointsLimit;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;

public class HttpReceiverDefinition extends DataSourceDefinition
{
  public void preInitialize()
  {
    ModuleRegistry.addLicenseEnforcement(new DataSourceTypePointsLimit(getModule().getName(), "HTTP_RECEIVER", 20, null));
  }

  public String getDataSourceTypeName()
  {
    return "HTTP_RECEIVER";
  }

  public String getDescriptionKey()
  {
    return "dsEdit.httpReceiver";
  }

  protected DataSourceVO<?> createDataSourceVO()
  {
    return new HttpReceiverDataSourceVO();
  }

  public String getEditPagePath()
  {
    return "web/editHttpReceiver.jsp";
  }

  public Class<?> getDwrClass()
  {
    return HttpDataSourceDwr.class;
  }
}