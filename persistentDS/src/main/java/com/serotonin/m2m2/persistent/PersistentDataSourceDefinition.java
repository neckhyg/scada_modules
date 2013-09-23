package com.serotonin.m2m2.persistent;

import com.serotonin.m2m2.module.DataSourceDefinition;
import com.serotonin.m2m2.persistent.ds.PersistentDataSourceVO;
import com.serotonin.m2m2.persistent.dwr.PersistentDataSourceDwr;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;

public class PersistentDataSourceDefinition extends DataSourceDefinition
{
  public String getDataSourceTypeName()
  {
    return "PERSISTENT";
  }

  public String getDescriptionKey()
  {
    return "dsEdit.persistent";
  }

  public DataSourceVO<?> createDataSourceVO()
  {
    return new PersistentDataSourceVO();
  }

  public String getEditPagePath()
  {
    return "web/editPersistentDS.jspf";
  }

  public Class<?> getDwrClass()
  {
    return PersistentDataSourceDwr.class;
  }
}