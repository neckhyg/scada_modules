package com.serotonin.m2m2.modbus.dwr;

import com.serotonin.io.StreamUtils;
import com.serotonin.io.serial.SerialParameters;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.modbus.rt.ModbusDataSourceRT;
import com.serotonin.m2m2.modbus.vo.ModbusIpDataSourceVO;
import com.serotonin.m2m2.modbus.vo.ModbusIpDataSourceVO.TransportType;
import com.serotonin.m2m2.modbus.vo.ModbusPointLocatorVO;
import com.serotonin.m2m2.modbus.vo.ModbusSerialDataSourceVO;
import com.serotonin.m2m2.modbus.vo.ModbusSerialDataSourceVO.EncodingType;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.vo.dataSource.BasicDataSourceVO;
import com.serotonin.m2m2.vo.permission.Permissions;
import com.serotonin.m2m2.web.dwr.DataSourceEditDwr;
import com.serotonin.m2m2.web.dwr.util.DwrPermission;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusIdException;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.locator.BaseLocator;
import com.serotonin.modbus4j.msg.ModbusRequest;
import com.serotonin.modbus4j.msg.ModbusResponse;
import com.serotonin.modbus4j.msg.ReadResponse;
import java.nio.charset.IllegalCharsetNameException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.serotonin.provider.InputStreamEPollProvider;
import com.serotonin.provider.Providers;
import org.apache.commons.lang3.StringUtils;

public class ModbusEditDwr extends DataSourceEditDwr
{
  @DwrPermission(user=true)
  public Map<String, Object> modbusScanUpdate()
  {
    Map result = new HashMap();
    ModbusNodeScanListener scan = (ModbusNodeScanListener)Common.getUser().getTestingUtility(ModbusNodeScanListener.class);
    if (scan == null) {
      return null;
    }
    result.put("nodes", scan.getNodesFound());
    result.put("message", scan.getMessage());
    result.put("finished", Boolean.valueOf(scan.isFinished()));

    return result;
  }
  @DwrPermission(user=true)
  public ProcessResult saveModbusPointLocator(int id, String xid, String name, ModbusPointLocatorVO locator) {
    return validatePoint(id, xid, name, locator, null);
  }

  private void testModbusPointLocator(ModbusMaster modbusMaster, ModbusPointLocatorVO locator, boolean serial, ProcessResult response)
  {
    locator.validate(response);
    if (response.getHasMessages())
      return;
    try
    {
      BaseLocator bl = ModbusDataSourceRT.createModbusLocator(locator);
      modbusMaster.init();
      Object result = modbusMaster.getValue(bl);
      response.addData("result", new TranslatableMessage("dsEdit.modbus.locatorTest.result", new Object[] { result }));
    }
    catch (ModbusInitException e) {
      if (serial)
        response.addMessage(new TranslatableMessage("dsEdit.modbus.locatorTestIp.startError", new Object[] { e.getMessage() }));
      else
        response.addMessage(new TranslatableMessage("dsEdit.modbus.locatorTestSerial.startError", new Object[] { e.getMessage() }));
    }
    catch (ErrorResponseException e)
    {
      response.addMessage(new TranslatableMessage("common.default", new Object[] { e.getErrorResponse().getExceptionMessage() }));
    }
    catch (ModbusTransportException e) {
      response.addMessage(ModbusDataSourceRT.localExceptionMessage(e));
    }
    catch (IllegalCharsetNameException e) {
      response.addMessage(new TranslatableMessage("validate.invalidCharset"));
    }
    finally {
      modbusMaster.destroy();
    }
  }

  private void testModbusData(ModbusMaster modbusMaster, int slaveId, int range, int offset, int length, boolean serial, ProcessResult response)
  {
    boolean binary = (range == 1) || (range == 2);
    if (length > modbusMaster.getMaxReadCount(range))
      length = modbusMaster.getMaxReadCount(range);
    if (offset + length > 65536)
      length = 65536 - offset;
    response.addData("length", Integer.valueOf(length));
    try
    {
      ModbusRequest mreq = new ModbusFactory().createReadRequest(slaveId, range, offset, length);

      modbusMaster.init();
      ReadResponse mres = (ReadResponse)modbusMaster.send(mreq);
      if (mres.isException()) {
        response.addMessage(new TranslatableMessage("common.default", new Object[] { mres.getExceptionMessage() }));
      } else {
        List results = new ArrayList();
        if (binary) {
          boolean[] data = mres.getBooleanData();
          for (int i = 0; i < length; i++)
            results.add(Integer.toString(offset + i) + " ==> " + Boolean.toString(data[i]));
        }
        else {
          short[] data = mres.getShortData();
          for (int i = 0; i < length; i++)
            results.add(Integer.toString(offset + i) + " ==> " + StreamUtils.toHex(data[i]));
        }
        response.addData("results", results);
      }
    }
    catch (ModbusIdException e) {
      response.addMessage(ModbusDataSourceRT.localExceptionMessage(e));
    }
    catch (ModbusInitException e) {
      if (serial)
        response.addMessage(new TranslatableMessage("dsEdit.modbus.locatorTestIp.startError", new Object[] { e.getMessage() }));
      else
        response.addMessage(new TranslatableMessage("dsEdit.modbus.locatorTestSerial.startError", new Object[] { e.getMessage() }));
    }
    catch (ModbusTransportException e)
    {
      response.addMessage(ModbusDataSourceRT.localExceptionMessage(e));
    }
    finally {
      modbusMaster.destroy();
    }
  }

  @DwrPermission(user=true)
  public ProcessResult saveModbusSerialDataSource(BasicDataSourceVO basic, int updatePeriods, int updatePeriodType, boolean quantize, int timeout, int retries, boolean multipleWritesOnly, boolean contiguousBatches, boolean createSlaveMonitorPoints, int maxReadBitCount, int maxReadRegisterCount, int maxWriteRegisterCount, boolean logIO, int discardDataDelay, String commPortId, int baudRate, int flowControlIn, int flowControlOut, int dataBits, int stopBits, int parity, String encoding, boolean echo, int concurrency)
  {
    ModbusSerialDataSourceVO ds = (ModbusSerialDataSourceVO)Common.getUser().getEditDataSource();

    setBasicProps(ds, basic);
    ds.setUpdatePeriods(updatePeriods);
    ds.setUpdatePeriodType(updatePeriodType);
    ds.setQuantize(quantize);
    ds.setTimeout(timeout);
    ds.setRetries(retries);
    ds.setMultipleWritesOnly(multipleWritesOnly);
    ds.setContiguousBatches(contiguousBatches);
    ds.setCreateSlaveMonitorPoints(createSlaveMonitorPoints);
    ds.setMaxReadBitCount(maxReadBitCount);
    ds.setMaxReadRegisterCount(maxReadRegisterCount);
    ds.setMaxWriteRegisterCount(maxWriteRegisterCount);
    ds.setLogIO(logIO);
    ds.setDiscardDataDelay(discardDataDelay);
    ds.setCommPortId(commPortId);
    ds.setBaudRate(baudRate);
    ds.setFlowControlIn(flowControlIn);
    ds.setFlowControlOut(flowControlOut);
    ds.setDataBits(dataBits);
    ds.setStopBits(stopBits);
    ds.setParity(parity);
    ds.setEncodingStr(encoding);
    ds.setEcho(echo);
    ds.setConcurrency(concurrency);

    return tryDataSourceSave(ds);
  }
  @DwrPermission(user=true)
  public String modbusSerialScan(int timeout, int retries, String commPortId, int baudRate, int flowControlIn, int flowControlOut, int dataBits, int stopBits, int parity, String encoding, int concurrency) {
    ModbusMaster modbusMaster;
    try {
      modbusMaster = createModbusSerialMaster(timeout, retries, commPortId, baudRate, flowControlIn, flowControlOut, dataBits, stopBits, parity, encoding, concurrency);
    }
    catch (Exception e)
    {
      return translate("dsEdit.modbus.scanError", new Object[0]);
    }
    ModbusNodeScanListener scan = new ModbusNodeScanListener(getTranslations(), modbusMaster, true);
    Common.getUser().setTestingUtility(scan);
    return null;
  }

  @DwrPermission(user=true)
  public ProcessResult testModbusSerialLocator(int timeout, int retries, String commPortId, int baudRate, int flowControlIn, int flowControlOut, int dataBits, int stopBits, int parity, String encoding, int concurrency, ModbusPointLocatorVO locator)
  {
    ProcessResult response = new ProcessResult();
    try
    {
      ModbusMaster modbusMaster = createModbusSerialMaster(timeout, retries, commPortId, baudRate, flowControlIn, flowControlOut, dataBits, stopBits, parity, encoding, concurrency);

      testModbusPointLocator(modbusMaster, locator, true, response);
    }
    catch (Exception e) {
      response.addMessage(new TranslatableMessage("dsEdit.modbus.scanError"));
    }
    return response;
  }

  @DwrPermission(user=true)
  public ProcessResult testModbusSerialData(int timeout, int retries, String commPortId, int baudRate, int flowControlIn, int flowControlOut, int dataBits, int stopBits, int parity, String encoding, int concurrency, int slaveId, int range, int offset, int length)
  {
    ProcessResult response = new ProcessResult();
    try
    {
      ModbusMaster modbusMaster = createModbusSerialMaster(timeout, retries, commPortId, baudRate, flowControlIn, flowControlOut, dataBits, stopBits, parity, encoding, concurrency);

      testModbusData(modbusMaster, slaveId, range, offset, length, true, response);
    }
    catch (Exception e) {
      response.addMessage(new TranslatableMessage("dsEdit.modbus.scanError"));
    }
    return response;
  }

  private ModbusMaster createModbusSerialMaster(int timeout, int retries, String commPortId, int baudRate, int flowControlIn, int flowControlOut, int dataBits, int stopBits, int parity, String encoding, int concurrency)
    throws Exception
  {
    User user = Common.getUser();
    Permissions.ensureDataSourcePermission(user);

    if (StringUtils.isBlank(commPortId)) {
      throw new Exception();
    }
    SerialParameters params = new SerialParameters();
    params.setCommPortId(commPortId);
    params.setPortOwnerName("Mango Modbus Serial Data Source Scan");
    params.setBaudRate(baudRate);
    params.setFlowControlIn(flowControlIn);
    params.setFlowControlOut(flowControlOut);
    params.setDataBits(dataBits);
    params.setStopBits(stopBits);
    params.setParity(parity);

    ModbusSerialDataSourceVO.EncodingType encodingType = ModbusSerialDataSourceVO.EncodingType.valueOf(encoding);
    ModbusMaster modbusMaster;
    if (encodingType == ModbusSerialDataSourceVO.EncodingType.ASCII)
      modbusMaster = new ModbusFactory().createAsciiMaster(params, concurrency);
    else
      modbusMaster = new ModbusFactory().createRtuMaster(params, concurrency);
    modbusMaster.setePoll(((InputStreamEPollProvider) Providers.get(InputStreamEPollProvider.class)).getInputStreamEPoll());
    modbusMaster.setTimeout(timeout);
    modbusMaster.setRetries(retries);

    return modbusMaster;
  }

  @DwrPermission(user=true)
  public ProcessResult saveModbusIpDataSource(BasicDataSourceVO basic, int updatePeriods, int updatePeriodType, boolean quantize, int timeout, int retries, boolean multipleWritesOnly, boolean contiguousBatches, boolean createSlaveMonitorPoints, int maxReadBitCount, int maxReadRegisterCount, int maxWriteRegisterCount, boolean logIO, int discardDataDelay, String transportType, String host, int port, boolean encapsulated)
  {
    ModbusIpDataSourceVO ds = (ModbusIpDataSourceVO)Common.getUser().getEditDataSource();

    setBasicProps(ds, basic);
    ds.setUpdatePeriods(updatePeriods);
    ds.setUpdatePeriodType(updatePeriodType);
    ds.setQuantize(quantize);
    ds.setTimeout(timeout);
    ds.setRetries(retries);
    ds.setMultipleWritesOnly(multipleWritesOnly);
    ds.setContiguousBatches(contiguousBatches);
    ds.setCreateSlaveMonitorPoints(createSlaveMonitorPoints);
    ds.setMaxReadBitCount(maxReadBitCount);
    ds.setMaxReadRegisterCount(maxReadRegisterCount);
    ds.setMaxWriteRegisterCount(maxWriteRegisterCount);
    ds.setLogIO(logIO);
    ds.setDiscardDataDelay(discardDataDelay);
    ds.setTransportTypeStr(transportType);
    ds.setHost(host);
    ds.setPort(port);
    ds.setEncapsulated(encapsulated);

    return tryDataSourceSave(ds);
  }
  @DwrPermission(user=true)
  public String modbusIpScan(int timeout, int retries, String transport, String host, int port, boolean encapsulated) {
    ModbusMaster modbusMaster = createModbusIpMaster(timeout, retries, transport, host, port, encapsulated);
    ModbusNodeScanListener scan = new ModbusNodeScanListener(getTranslations(), modbusMaster, false);
    Common.getUser().setTestingUtility(scan);
    return null;
  }

  @DwrPermission(user=true)
  public ProcessResult testModbusIpLocator(int timeout, int retries, String transport, String host, int port, boolean encapsulated, ModbusPointLocatorVO locator) {
    ProcessResult response = new ProcessResult();
    ModbusMaster modbusMaster = createModbusIpMaster(timeout, retries, transport, host, port, encapsulated);
    testModbusPointLocator(modbusMaster, locator, false, response);
    return response;
  }

  @DwrPermission(user=true)
  public ProcessResult testModbusIpData(int timeout, int retries, String transport, String host, int port, boolean encapsulated, int slaveId, int range, int offset, int length) {
    ProcessResult response = new ProcessResult();
    ModbusMaster modbusMaster = createModbusIpMaster(timeout, retries, transport, host, port, encapsulated);
    testModbusData(modbusMaster, slaveId, range, offset, length, false, response);
    return response;
  }

  private ModbusMaster createModbusIpMaster(int timeout, int retries, String transport, String host, int port, boolean encapsulated)
  {
    User user = Common.getUser();
    Permissions.ensureDataSourcePermission(user);

    IpParameters params = new IpParameters();
    params.setHost(host);
    params.setPort(port);
    params.setEncapsulated(encapsulated);

    ModbusIpDataSourceVO.TransportType transportType = ModbusIpDataSourceVO.TransportType.valueOf(transport);
    ModbusMaster modbusMaster;
    if (transportType == ModbusIpDataSourceVO.TransportType.UDP) {
      modbusMaster = new ModbusFactory().createUdpMaster(params);
    } else {
      modbusMaster = new ModbusFactory().createTcpMaster(params, transportType == ModbusIpDataSourceVO.TransportType.TCP_KEEP_ALIVE);
      modbusMaster.setePoll(((InputStreamEPollProvider)Providers.get(InputStreamEPollProvider.class)).getInputStreamEPoll());
    }
    modbusMaster.setTimeout(timeout);
    modbusMaster.setRetries(retries);

    return modbusMaster;
  }
}