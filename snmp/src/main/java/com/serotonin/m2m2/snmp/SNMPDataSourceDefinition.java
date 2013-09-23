package com.serotonin.m2m2.snmp;

import com.serotonin.m2m2.module.DataSourceDefinition;
import com.serotonin.m2m2.module.Module;
import com.serotonin.m2m2.module.ModuleRegistry;
import com.serotonin.m2m2.module.license.DataSourceTypePointsLimit;
import com.serotonin.m2m2.snmp.vo.SNMPEditDwr;
import com.serotonin.m2m2.snmp.vo.SnmpDataSourceVO;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;

public class SNMPDataSourceDefinition extends DataSourceDefinition
{
  public void preInitialize()
  {
    ModuleRegistry.addLicenseEnforcement(new DataSourceTypePointsLimit(getModule().getName(), "SNMP", 20, null));
  }

  public String getDataSourceTypeName()
  {
    return "SNMP";
  }

  public String getDescriptionKey()
  {
    return "dsEdit.snmp";
  }

  protected DataSourceVO<?> createDataSourceVO()
  {
    return new SnmpDataSourceVO();
  }

  public String getEditPagePath()
  {
    return "web/editSnmp.jsp";
  }

  public Class<?> getDwrClass()
  {
    return SNMPEditDwr.class;
  }
}