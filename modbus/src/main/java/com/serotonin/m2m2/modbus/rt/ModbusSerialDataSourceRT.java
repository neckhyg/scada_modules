package com.serotonin.m2m2.modbus.rt;

import com.serotonin.io.serial.SerialParameters;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.modbus.vo.ModbusSerialDataSourceVO;
import com.serotonin.m2m2.modbus.vo.ModbusSerialDataSourceVO.EncodingType;
import com.serotonin.m2m2.rt.dataSource.DataSourceRT;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.provider.InputStreamEPollProvider;
import com.serotonin.provider.Providers;
import gnu.io.NoSuchPortException;

public class ModbusSerialDataSourceRT extends ModbusDataSourceRT
{
  private final ModbusSerialDataSourceVO configuration;

  public ModbusSerialDataSourceRT(ModbusSerialDataSourceVO configuration)
  {
    super(configuration);
    this.configuration = configuration;
  }

  public void initialize()
  {
    SerialParameters params = new SerialParameters();
    params.setCommPortId(this.configuration.getCommPortId());
    params.setPortOwnerName("Mango Modbus Serial Data Source");
    params.setBaudRate(this.configuration.getBaudRate());
    params.setFlowControlIn(this.configuration.getFlowControlIn());
    params.setFlowControlOut(this.configuration.getFlowControlOut());
    params.setDataBits(this.configuration.getDataBits());
    params.setStopBits(this.configuration.getStopBits());
    params.setParity(this.configuration.getParity());
    ModbusMaster modbusMaster;
    if (this.configuration.getEncoding() == ModbusSerialDataSourceVO.EncodingType.ASCII)
      modbusMaster = new ModbusFactory().createAsciiMaster(params, this.configuration.getConcurrency());
    else
      modbusMaster = new ModbusFactory().createRtuMaster(params, this.configuration.getConcurrency());
    modbusMaster.setePoll(((InputStreamEPollProvider) Providers.get(InputStreamEPollProvider.class)).getInputStreamEPoll());

    super.initialize(modbusMaster);
  }

  protected TranslatableMessage getLocalExceptionMessage(Exception e)
  {
    if ((e instanceof ModbusInitException)) {
      Throwable cause = e.getCause();
      if ((cause instanceof NoSuchPortException)) {
        return new TranslatableMessage("event.serial.portOpenError", new Object[] { this.configuration.getCommPortId() });
      }
    }
    return DataSourceRT.getExceptionMessage(e);
  }
}