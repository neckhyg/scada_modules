package com.serotonin.m2m2.modbus;

import com.serotonin.m2m2.modbus.dwr.ModbusEditDwr;
import com.serotonin.m2m2.modbus.vo.ModbusIpDataSourceVO;
import com.serotonin.m2m2.module.DataSourceDefinition;
import com.serotonin.m2m2.module.Module;
import com.serotonin.m2m2.module.ModuleRegistry;
import com.serotonin.m2m2.module.license.DataSourceTypePointsLimit;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;

public class ModbusIpDefinition extends DataSourceDefinition
{
  public void preInitialize()
  {
//    ModuleRegistry.addLicenseEnforcement(new DataSourceTypePointsLimit(getModule().getName(), "MODBUS_IPL", 20, null));
  }

  public String getDataSourceTypeName()
  {
    return "MODBUS_IP";
  }

  public String getDescriptionKey()
  {
    return "MODBUS_IP.dataSource";
  }

  public DataSourceVO<?> createDataSourceVO()
  {
    return new ModbusIpDataSourceVO();
  }

  public String getEditPagePath()
  {
    return "web/editModbus.jspf";
  }

  public Class<?> getDwrClass()
  {
    return ModbusEditDwr.class;
  }
}