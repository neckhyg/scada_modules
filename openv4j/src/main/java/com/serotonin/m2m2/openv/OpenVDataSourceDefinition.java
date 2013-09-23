package com.serotonin.m2m2.openv;

import com.serotonin.m2m2.module.DataSourceDefinition;
import com.serotonin.m2m2.openv.dwr.OpenVEditDwr;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;

public class OpenVDataSourceDefinition extends DataSourceDefinition
{
  public String getDataSourceTypeName()
  {
    return "OPEN_V";
  }

  public String getDescriptionKey()
  {
    return "dsEdit.openv4j";
  }

  protected DataSourceVO<?> createDataSourceVO()
  {
    return new OpenV4JDataSourceVO();
  }

  public String getEditPagePath()
  {
    return "web/editOpenV4J.jsp";
  }

  public Class<?> getDwrClass()
  {
    return OpenVEditDwr.class;
  }
}