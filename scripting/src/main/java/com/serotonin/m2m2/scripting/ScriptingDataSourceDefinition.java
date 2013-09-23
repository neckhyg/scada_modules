package com.serotonin.m2m2.scripting;

import com.serotonin.m2m2.module.DataSourceDefinition;
import com.serotonin.m2m2.module.Module;
import com.serotonin.m2m2.module.ModuleRegistry;
import com.serotonin.m2m2.module.license.DataSourceTypePointsLimit;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;

public class ScriptingDataSourceDefinition extends DataSourceDefinition
{
  static final String DATA_SOURCE_TYPE_NAME = "SCRIPTING";

  public void preInitialize()
  {
    ModuleRegistry.addLicenseEnforcement(new DataSourceTypePointsLimit(getModule().getName(), "SCRIPTING", 1, null));
  }

  public String getDataSourceTypeName()
  {
    return "SCRIPTING";
  }

  public String getDescriptionKey()
  {
    return "dsEdit.scripting";
  }

  protected DataSourceVO<?> createDataSourceVO()
  {
    return new ScriptDataSourceVO();
  }

  public String getEditPagePath()
  {
    return "web/edit.jsp";
  }

  public Class<?> getDwrClass()
  {
    return ScriptingEditDwr.class;
  }
}