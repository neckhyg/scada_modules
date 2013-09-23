package com.serotonin.m2m2.envcan;

import com.serotonin.m2m2.module.DataSourceDefinition;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;

public class EnvCanDataSourceDefinition extends DataSourceDefinition
{
  public String getDataSourceTypeName()
  {
    return "EnvCan";
  }

  public String getDescriptionKey()
  {
    return "envcands.desc";
  }

  protected DataSourceVO<?> createDataSourceVO()
  {
    return new EnvCanDataSourceVO();
  }

  public String getEditPagePath()
  {
    return "web/editEnvCan.jsp";
  }

  public Class<?> getDwrClass()
  {
    return EnvCanEditDwr.class;
  }
}