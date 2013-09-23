package com.serotonin.m2m2.modbus.rt;

import com.serotonin.m2m2.modbus.vo.ModbusPointLocatorVO;
import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;
import com.serotonin.modbus4j.code.DataType;

public class ModbusPointLocatorRT extends PointLocatorRT
{
  private final ModbusPointLocatorVO vo;

  public ModbusPointLocatorRT(ModbusPointLocatorVO vo)
  {
    this.vo = vo;
  }

  public int getRegisterCount() {
    return DataType.getRegisterCount(this.vo.getModbusDataType());
  }

  public boolean isSettable()
  {
    return this.vo.isSettable();
  }

  public ModbusPointLocatorVO getVO() {
    return this.vo;
  }
}