package com.serotonin.m2m2.vmstat;

import com.serotonin.m2m2.module.DataSourceDefinition;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;

public class VMStatDataSourceDefinition extends DataSourceDefinition
{
  public String getDataSourceTypeName()
  {
    return "VMSTAT";
  }

  public String getDescriptionKey()
  {
    return "dsEdit.vmstat";
  }

  protected DataSourceVO<?> createDataSourceVO()
  {
    return new VMStatDataSourceVO();
  }

  public String getEditPagePath()
  {
    return "web/editVMStat.jsp";
  }

  public Class<?> getDwrClass()
  {
    return VMStatEditDwr.class;
  }
}