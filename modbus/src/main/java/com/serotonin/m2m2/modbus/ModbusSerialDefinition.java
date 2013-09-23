package com.serotonin.m2m2.modbus;

import com.serotonin.m2m2.modbus.dwr.ModbusEditDwr;
import com.serotonin.m2m2.modbus.vo.ModbusSerialDataSourceVO;
import com.serotonin.m2m2.module.DataSourceDefinition;
import com.serotonin.m2m2.module.Module;
import com.serotonin.m2m2.module.ModuleRegistry;
import com.serotonin.m2m2.module.license.DataSourceTypePointsLimit;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;

public class ModbusSerialDefinition extends DataSourceDefinition
{
  public void preInitialize()
  {
//    ModuleRegistry.addLicenseEnforcement(new DataSourceTypePointsLimit(getModule().getName(), "MODBUS_SERIAL", 20, null));
  }

  public String getDataSourceTypeName()
  {
    return "MODBUS_SERIAL";
  }

  public String getDescriptionKey()
  {
    return "MODBUS_SERIAL.dataSource";
  }

  public DataSourceVO<?> createDataSourceVO()
  {
    return new ModbusSerialDataSourceVO();
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