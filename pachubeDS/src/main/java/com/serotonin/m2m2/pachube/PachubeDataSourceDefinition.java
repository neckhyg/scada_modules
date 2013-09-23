package com.serotonin.m2m2.pachube;

import com.serotonin.m2m2.module.DataSourceDefinition;
import com.serotonin.m2m2.pachube.ds.PachubeDataSourceDwr;
import com.serotonin.m2m2.pachube.ds.PachubeDataSourceVO;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;

public class PachubeDataSourceDefinition extends DataSourceDefinition
{
  public String getDataSourceTypeName()
  {
    return "PACHUBE";
  }

  public String getDescriptionKey()
  {
    return "dsEdit.pachube";
  }

  protected DataSourceVO<?> createDataSourceVO()
  {
    return new PachubeDataSourceVO();
  }

  public String getEditPagePath()
  {
    return "web/editPachubeDS.jsp";
  }

  public Class<?> getDwrClass()
  {
    return PachubeDataSourceDwr.class;
  }
}