package com.serotonin.m2m2.sql;

import com.serotonin.m2m2.module.DataSourceDefinition;
import com.serotonin.m2m2.module.Module;
import com.serotonin.m2m2.module.ModuleRegistry;
import com.serotonin.m2m2.module.license.DataSourceTypePointsLimit;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;

public class SqlDataSourceDefinition extends DataSourceDefinition
{
  public void preInitialize()
  {
    ModuleRegistry.addLicenseEnforcement(new DataSourceTypePointsLimit(getModule().getName(), "SQL", 10, null));
  }

  public String getDataSourceTypeName()
  {
    return "SQL";
  }

  public String getDescriptionKey()
  {
    return "dsEdit.sql";
  }

  protected DataSourceVO<?> createDataSourceVO()
  {
    return new SqlDataSourceVO();
  }

  public String getEditPagePath()
  {
    return "web/editSql.jsp";
  }

  public Class<?> getDwrClass()
  {
    return SqlEditDwr.class;
  }
}