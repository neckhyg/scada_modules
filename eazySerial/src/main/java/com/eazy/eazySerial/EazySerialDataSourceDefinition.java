package com.eazy.eazySerial;

import com.serotonin.m2m2.module.DataSourceDefinition;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;

public class EazySerialDataSourceDefinition extends DataSourceDefinition
{
  public String getDataSourceTypeName()
  {
    return "EAZYSERIAL";
  }

  public String getDescriptionKey()
  {
    return "dsEdit.eazySerial";
  }

  protected DataSourceVO<?> createDataSourceVO()
  {
    return new EazySerialDataSourceVO();
  }

  public String getEditPagePath()
  {
    return "web/editEazySerial.jsp";
  }

  public Class<?> getDwrClass()
  {
    return EazySerialEditDwr.class;
  }
}