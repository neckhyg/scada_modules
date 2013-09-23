package com.serotonin.m2m2.wibox;

import com.serotonin.m2m2.module.DataSourceDefinition;
import com.serotonin.m2m2.module.Module;
import com.serotonin.m2m2.module.ModuleRegistry;
import com.serotonin.m2m2.module.license.DataSourceTypePointsLimit;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;

public class WiBoxDataSourceDefinition extends DataSourceDefinition
{
  static final String DATA_SOURCE_TYPE_NAME = "WIBOX";

  public void preInitialize()
  {
    ModuleRegistry.addLicenseEnforcement(new DataSourceTypePointsLimit(getModule().getName(), "WIBOX", 10, null));
  }

  public String getDataSourceTypeName()
  {
    return "WIBOX";
  }

  public String getDescriptionKey()
  {
    return "dsEdit.wiboxHttp";
  }

  protected DataSourceVO<?> createDataSourceVO()
  {
    return new WiBoxHttpDataSourceVO();
  }

  public String getEditPagePath()
  {
    return "web/edit.jsp";
  }

  public Class<?> getDwrClass()
  {
    return WiBoxEditDwr.class;
  }
}