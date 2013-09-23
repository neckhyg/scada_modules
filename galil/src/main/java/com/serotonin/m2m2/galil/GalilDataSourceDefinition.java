package com.serotonin.m2m2.galil;

import com.serotonin.m2m2.galil.vo.GalilDataSourceVO;
import com.serotonin.m2m2.module.DataSourceDefinition;
import com.serotonin.m2m2.module.Module;
import com.serotonin.m2m2.module.ModuleRegistry;
import com.serotonin.m2m2.module.license.DataSourceTypePointsLimit;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;

public class GalilDataSourceDefinition extends DataSourceDefinition
{
  public void preInitialize()
  {
    ModuleRegistry.addLicenseEnforcement(new DataSourceTypePointsLimit(getModule().getName(), "GALIL", 20, null));
  }

  public String getDataSourceTypeName()
  {
    return "GALIL";
  }

  public String getDescriptionKey()
  {
    return "dsEdit.galil";
  }

  protected DataSourceVO<?> createDataSourceVO()
  {
    return new GalilDataSourceVO();
  }

  public String getEditPagePath()
  {
    return "web/editGalil.jsp";
  }

  public Class<?> getDwrClass()
  {
    return GalilEditDwr.class;
  }
}