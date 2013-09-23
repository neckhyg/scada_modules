package com.serotonin.m2m2.jmxds;

import com.serotonin.m2m2.module.DataSourceDefinition;
import com.serotonin.m2m2.module.Module;
import com.serotonin.m2m2.module.ModuleRegistry;
import com.serotonin.m2m2.module.license.DataSourceTypePointsLimit;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;

public class JmxDataSourceDefinition extends DataSourceDefinition
{
  public void preInitialize()
  {
    ModuleRegistry.addLicenseEnforcement(new DataSourceTypePointsLimit(getModule().getName(), "JMX", 4, null));
  }

  public String getDataSourceTypeName()
  {
    return "JMX";
  }

  public String getDescriptionKey()
  {
    return "dsEdit.jmx";
  }

  protected DataSourceVO<?> createDataSourceVO()
  {
    return new JmxDataSourceVO();
  }

  public String getEditPagePath()
  {
    return "web/editJmx.jsp";
  }

  public Class<?> getDwrClass()
  {
    return JmxEditDwr.class;
  }
}