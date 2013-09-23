package com.serotonin.m2m2.modbus;

import com.serotonin.m2m2.modbus.dwr.ModbusEditDwr;
import com.serotonin.m2m2.modbus.vo.ModbusTcpDataSourceVO;
import com.serotonin.m2m2.module.DataSourceDefinition;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;

public class ModbusTcpDefinition extends DataSourceDefinition {
    @Override
    public String getDataSourceTypeName() {
        return "MODBUS_TCP";
    }

    @Override
    public String getDescriptionKey() {
        return "MODBUS_TCP.dataSource";
    }

    @Override
    protected DataSourceVO<?> createDataSourceVO() {
        return new ModbusTcpDataSourceVO();
    }

    @Override
    public String getEditPagePath() {
        return "web/editModbus.jspf";
    }

    @Override
    public Class<?> getDwrClass() {
        return ModbusEditDwr.class;
    }
}
