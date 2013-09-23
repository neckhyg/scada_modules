package com.serotonin.ma.ascii;

import com.serotonin.m2m2.module.DataSourceDefinition;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;
import com.serotonin.ma.ascii.serial.SerialDataSourceVO;

public class AsciiSerialDefinition extends DataSourceDefinition
{
  public String getDataSourceTypeName()
  {
    return "ASCII_SERIAL";
  }

  public String getDescriptionKey()
  {
    return "ascii.serial.dataSource";
  }

  public DataSourceVO<?> createDataSourceVO()
  {
    return new SerialDataSourceVO();
  }

  public String getEditPagePath()
  {
    return "web/editAsciiSerial.jsp";
  }

  public Class<?> getDwrClass()
  {
    return AsciiEditDwr.class;
  }
}