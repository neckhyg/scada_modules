package com.serotonin.m2m2.onewire;

import com.serotonin.m2m2.module.DataSourceDefinition;
import com.serotonin.m2m2.onewire.vo.OneWireDataSourceVO;
import com.serotonin.m2m2.onewire.vo.OneWireEditDwr;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;

public class OneWireDataSourceDefinition extends DataSourceDefinition
{
  public String getDataSourceTypeName()
  {
    return "1WIRE";
  }

  public String getDescriptionKey()
  {
    return "dsEdit.1wire";
  }

  protected DataSourceVO<?> createDataSourceVO()
  {
    return new OneWireDataSourceVO();
  }

  public String getEditPagePath()
  {
    return "web/editOneWire.jsp";
  }

  public Class<?> getDwrClass()
  {
    return OneWireEditDwr.class;
  }
}