package com.serotonin.m2m2.modbus;

import com.serotonin.m2m2.modbus.vo.ModbusIpDataSourceVO;
import com.serotonin.m2m2.module.DwrConversionDefinition;

public class ModbusDwrConversionDefinition extends DwrConversionDefinition
{
  public void addConversions()
  {
    addConversionWithExclusions(ModbusIpDataSourceVO.class, "transportType");
  }
}