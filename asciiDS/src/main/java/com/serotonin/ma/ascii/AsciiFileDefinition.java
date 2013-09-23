package com.serotonin.ma.ascii;

import com.serotonin.m2m2.module.DataSourceDefinition;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;
import com.serotonin.ma.ascii.file.FileDataSourceVO;

public class AsciiFileDefinition extends DataSourceDefinition
{
  public String getDataSourceTypeName()
  {
    return "ASCII_FILE";
  }

  public String getDescriptionKey()
  {
    return "ascii.file.dataSource";
  }

  public DataSourceVO<?> createDataSourceVO()
  {
    return new FileDataSourceVO();
  }

  public String getEditPagePath()
  {
    return "web/editAsciiFile.jsp";
  }

  public Class<?> getDwrClass()
  {
    return AsciiEditDwr.class;
  }
}