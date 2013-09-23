package com.serotonin.m2m2.modbus.rt;

import com.serotonin.m2m2.modbus.vo.ModbusIpDataSourceVO;
import com.serotonin.m2m2.modbus.vo.ModbusIpDataSourceVO.TransportType;
import com.serotonin.m2m2.modbus.vo.ModbusTcpDataSourceVO;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.ip.IpParameters;
//import com.serotonin.provider.InputStreamEPollProvider;
import com.serotonin.provider.Providers;

public class ModbusTcpDataSourceRT extends ModbusDataSourceRT
{
  private final ModbusTcpDataSourceVO configuration;

  public ModbusTcpDataSourceRT(ModbusTcpDataSourceVO configuration)
  {
    super(configuration);
    this.configuration = configuration;
  }

  public void initialize()
  {
//    IpParameters params = new IpParameters();
//    params.setHost(this.configuration.getHost());
//    params.setPort(this.configuration.getPort());
//    params.setEncapsulated(this.configuration.isEncapsulated());
//    ModbusMaster modbusMaster;
//    if (this.configuration.getTransportType() == TransportType.UDP) {
//      modbusMaster = new ModbusFactory().createUdpMaster(params);
//    } else {
//      modbusMaster = new ModbusFactory().createTcpMaster(params, this.configuration.getTransportType() == TransportType.TCP_KEEP_ALIVE);
//
//        modbusMaster.setePoll(((InputStreamEPollProvider) Providers.get(InputStreamEPollProvider.class)).getInputStreamEPoll());
//    }
//
//    super.initialize(modbusMaster);
  }
}