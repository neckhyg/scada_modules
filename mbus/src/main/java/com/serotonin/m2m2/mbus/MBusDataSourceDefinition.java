package com.serotonin.m2m2.mbus;

import com.serotonin.m2m2.mbus.dwr.MBusEditDwr;
import com.serotonin.m2m2.module.DataSourceDefinition;
import com.serotonin.m2m2.module.Module;
import com.serotonin.m2m2.module.ModuleRegistry;
import com.serotonin.m2m2.module.license.DataSourceTypePointsLimit;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;

public class MBusDataSourceDefinition extends DataSourceDefinition
{
  public void preInitialize()
  {
    ModuleRegistry.addLicenseEnforcement(new DataSourceTypePointsLimit(getModule().getName(), "MBUS", 20, null));
  }

  public String getDataSourceTypeName()
  {
    return "MBUS";
  }

  public String getDescriptionKey()
  {
    return "dsEdit.mbus";
  }

  protected DataSourceVO<?> createDataSourceVO()
  {
    return new MBusDataSourceVO();
  }

  public String getEditPagePath()
  {
    return "web/editMBus.jsp";
  }

  public Class<?> getDwrClass()
  {
    return MBusEditDwr.class;
  }
}