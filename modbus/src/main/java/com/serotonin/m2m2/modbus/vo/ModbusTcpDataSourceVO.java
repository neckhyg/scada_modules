package com.serotonin.m2m2.modbus.vo;

import com.serotonin.json.spi.JsonProperty;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.modbus.rt.ModbusTcpDataSourceRT;
import com.serotonin.m2m2.rt.dataSource.DataSourceRT;

public class ModbusTcpDataSourceVO extends ModbusDataSourceVO<ModbusTcpDataSourceVO>{

    @JsonProperty
    private int port = 502;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override

    public TranslatableMessage getConnectionDescription() {
        return new TranslatableMessage("common.default", new Object[] { this.port });
    }

    @Override
    public DataSourceRT createDataSourceRT() {
        return new ModbusTcpDataSourceRT(this);
    }
}
