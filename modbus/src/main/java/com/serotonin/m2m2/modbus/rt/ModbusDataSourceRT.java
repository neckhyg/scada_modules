package com.serotonin.m2m2.modbus.rt;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.db.dao.DataPointDao;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.modbus.vo.ModbusDataSourceVO;
import com.serotonin.m2m2.modbus.vo.ModbusPointLocatorVO;
import com.serotonin.m2m2.rt.RuntimeManager;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.dataImage.SetPointSource;
import com.serotonin.m2m2.rt.dataSource.DataSourceRT;
import com.serotonin.m2m2.rt.dataSource.PollingDataSource;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.messaging.MessagingExceptionHandler;
import com.serotonin.messaging.TimeoutException;
import com.serotonin.modbus4j.BatchRead;
import com.serotonin.modbus4j.BatchResults;
import com.serotonin.modbus4j.ExceptionResult;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.locator.BaseLocator;
import com.serotonin.modbus4j.msg.ModbusResponse;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class ModbusDataSourceRT extends PollingDataSource
  implements MessagingExceptionHandler
{
  private final Log LOG = LogFactory.getLog(ModbusDataSourceRT.class);
  public static final int POINT_READ_EXCEPTION_EVENT = 1;
  public static final int POINT_WRITE_EXCEPTION_EVENT = 2;
  public static final int DATA_SOURCE_EXCEPTION_EVENT = 3;
  private ModbusMaster modbusMaster;
  private BatchRead<ModbusPointLocatorRT> batchRead;
  private final ModbusDataSourceVO<?> vo;
  private final Map<Integer, DataPointRT> slaveMonitors = new HashMap();

  public ModbusDataSourceRT(ModbusDataSourceVO<?> vo)
  {
    super(vo);
    this.vo = vo;
    setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), vo.isQuantize());
  }

  public void addDataPoint(DataPointRT dataPoint)
  {
    super.addDataPoint(dataPoint);

    ModbusPointLocatorVO locatorVO = (ModbusPointLocatorVO)dataPoint.getVO().getPointLocator();
    if ((!locatorVO.isSlaveMonitor()) && (!locatorVO.isWriteOnly())) {
      dataPoint.setAttribute("UNRELIABLE", Boolean.valueOf(true));
    }

    if (this.vo.isCreateSlaveMonitorPoints()) {
      int slaveId = locatorVO.getSlaveId();

      if (locatorVO.isSlaveMonitor())
      {
        this.slaveMonitors.put(Integer.valueOf(slaveId), dataPoint);
      } else if (!locatorVO.isWriteOnly())
      {
        if (!this.slaveMonitors.containsKey(Integer.valueOf(slaveId)))
        {
          this.slaveMonitors.put(Integer.valueOf(slaveId), null);

          DataPointDao dataPointDao = new DataPointDao();
          boolean found = false;

          List<DataPointVO> points = dataPointDao.getDataPoints(this.vo.getId(), null);
          for (DataPointVO dp : points) {
            ModbusPointLocatorVO loc = (ModbusPointLocatorVO)dp.getPointLocator();
            if ((loc.getSlaveId() == slaveId) && (loc.isSlaveMonitor())) {
              found = true;
              break;
            }
          }

          if (!found)
          {
            DataPointVO dp = new DataPointVO();
            dp.setXid(dataPointDao.generateUniqueXid());
            dp.setName(Common.getMessage("MODBUS.rt.monitorPointName", new Object[] { Integer.valueOf(slaveId) }));
            dp.setDataSourceId(this.vo.getId());
            dp.setEnabled(true);
            dp.setLoggingType(1);
            dp.setEventDetectors(new ArrayList());

            ModbusPointLocatorVO locator = new ModbusPointLocatorVO();
            locator.setSlaveId(slaveId);
            locator.setSlaveMonitor(true);
            dp.setPointLocator(locator);

            Common.runtimeManager.saveDataPoint(dp);
            this.LOG.info("Monitor point added: " + dp.getXid());
          }
        }
      }
    }
  }

  public void removeDataPoint(DataPointRT dataPoint) {
    synchronized (this.pointListChangeLock) {
      super.removeDataPoint(dataPoint);

      ModbusPointLocatorVO locatorVO = (ModbusPointLocatorVO)dataPoint.getVO().getPointLocator();
      if (locatorVO.isSlaveMonitor())
        this.slaveMonitors.put(Integer.valueOf(locatorVO.getSlaveId()), null);
    }
  }

  protected void doPoll(long time)
  {
    if (!this.modbusMaster.isInitialized()) {
      if (this.vo.isCreateSlaveMonitorPoints())
      {
        for (DataPointRT monitor : this.slaveMonitors.values()) {
          if (monitor != null) {
            PointValueTime oldValue = monitor.getPointValue();
            if ((oldValue == null) || (oldValue.getBooleanValue())) {
              monitor.setPointValue(new PointValueTime(false, time), null);
            }
          }
        }
      }
      return;
    }

    if ((this.batchRead == null) || (this.pointListChanged)) {
      this.pointListChanged = false;
      this.batchRead = new BatchRead();
      this.batchRead.setContiguousRequests(this.vo.isContiguousBatches());
      this.batchRead.setErrorsInResults(true);
      this.batchRead.setExceptionsInResults(true);

      for (DataPointRT dataPoint : this.dataPoints) {
        ModbusPointLocatorRT locator = (ModbusPointLocatorRT)dataPoint.getPointLocator();
        if ((!locator.getVO().isSlaveMonitor()) && (!locator.getVO().isWriteOnly())) {
          BaseLocator modbusLocator = createModbusLocator(locator.getVO());
          this.batchRead.addLocator(locator, modbusLocator);
        }
      }
    }

    boolean dataSourceExceptions = false;
    try
    {
      BatchResults results = this.modbusMaster.send(this.batchRead);

      Map<Integer,Boolean> slaveStatuses = new HashMap<Integer,Boolean>();


      for (DataPointRT dataPoint : this.dataPoints) {
        ModbusPointLocatorRT locator = (ModbusPointLocatorRT)dataPoint.getPointLocator();
        if ((locator.getVO().isSlaveMonitor()) || (locator.getVO().isWriteOnly())) {
          continue;
        }
        Object result = results.getValue(locator);

        if ((result instanceof ExceptionResult)) {
          ExceptionResult exceptionResult = (ExceptionResult)result;

          raiseEvent(1, time, true, new TranslatableMessage("event.exception2", new Object[] { dataPoint.getVO().getName(), exceptionResult.getExceptionMessage() }));

          dataPoint.setAttribute("UNRELIABLE", Boolean.valueOf(true));

          slaveStatuses.put(Integer.valueOf(locator.getVO().getSlaveId()), Boolean.valueOf(true));
        }
        else if ((result instanceof ModbusTransportException)) {
          ModbusTransportException e = (ModbusTransportException)result;

          if (!slaveStatuses.containsKey(Integer.valueOf(locator.getVO().getSlaveId()))) {
            slaveStatuses.put(Integer.valueOf(locator.getVO().getSlaveId()), Boolean.valueOf(false));
          }

          raiseEvent(3, time, true, getLocalExceptionMessage(e));
          dataSourceExceptions = true;

          dataPoint.setAttribute("UNRELIABLE", Boolean.valueOf(true));
        }
        else
        {
          returnToNormal(1, time);
          dataPoint.setAttribute("UNRELIABLE", Boolean.valueOf(false));
          updatePointValue(dataPoint, locator, result, time);
          slaveStatuses.put(Integer.valueOf(locator.getVO().getSlaveId()), Boolean.valueOf(true));
        }
      }

      if (this.vo.isCreateSlaveMonitorPoints()) {
        for (Map.Entry status : slaveStatuses.entrySet()) {
          DataPointRT monitor = (DataPointRT)this.slaveMonitors.get(status.getKey());
          if (monitor != null) {
            boolean oldOnline = false;
            boolean newOnline = ((Boolean)status.getValue()).booleanValue();

            PointValueTime oldValue = monitor.getPointValue();
            if (oldValue != null) {
              oldOnline = oldValue.getBooleanValue();
            }
            else {
              oldOnline = !newOnline;
            }
            if (oldOnline != newOnline) {
              monitor.setPointValue(new PointValueTime(newOnline, time), null);
            }
          }
        }
      }
      if (!dataSourceExceptions)
      {
        returnToNormal(3, time);
      }
    }
    catch (ErrorResponseException e) {
      throw new ShouldNeverHappenException(e);
    }
    catch (ModbusTransportException e)
    {
      throw new ShouldNeverHappenException(e);
    }

    if ((!dataSourceExceptions) && (this.vo.isLogIO()))
      if (this.modbusMaster.getIoLog().checkError()) {
        raiseEvent(3, time, true, new TranslatableMessage("dsEdit.modbus.logError.trouble"));
      }
      else
        returnToNormal(3, time);
  }

  protected void initialize(ModbusMaster modbusMaster)
  {
    this.modbusMaster = modbusMaster;
    modbusMaster.setTimeout(this.vo.getTimeout());
    modbusMaster.setRetries(this.vo.getRetries());
    modbusMaster.setMaxReadBitCount(this.vo.getMaxReadBitCount());
    modbusMaster.setMaxReadRegisterCount(this.vo.getMaxReadRegisterCount());
    modbusMaster.setMaxWriteRegisterCount(this.vo.getMaxWriteRegisterCount());
    modbusMaster.setDiscardDataDelay(this.vo.getDiscardDataDelay());
    modbusMaster.setMultipleWritesOnly(this.vo.isMultipleWritesOnly());

    if (this.vo.isLogIO()) {
      File file = getIOLogFile(this.vo.getId());
      try
      {
        PrintWriter log = new PrintWriter(new FileWriter(file, true));
        modbusMaster.setIoLog(log);
        log.println(System.currentTimeMillis() + " data source started");
      }
      catch (Exception e) {
        TranslatableMessage msg = new TranslatableMessage("dsEdit.modbus.logError", new Object[] { file.getPath(), getLocalExceptionMessage(e) });

        raiseEvent(3, System.currentTimeMillis(), true, msg);
        this.LOG.debug("Error while initializing data source", e);
        return;
      }

    }

    modbusMaster.setExceptionHandler(this);
    try
    {
      modbusMaster.init();

      returnToNormal(3, System.currentTimeMillis());
    }
    catch (Exception e) {
      raiseEvent(3, System.currentTimeMillis(), true, getLocalExceptionMessage(e));
      this.LOG.debug("Error while initializing data source", e);
      return;
    }

    super.initialize();
  }

  public void forcePointRead(DataPointRT dataPoint)
  {
    ModbusPointLocatorRT pl = (ModbusPointLocatorRT)dataPoint.getPointLocator();
    if ((pl.getVO().isSlaveMonitor()) || (pl.getVO().isWriteOnly()))
    {
      return;
    }
    BaseLocator ml = createModbusLocator(pl.getVO());
    long time = System.currentTimeMillis();

    synchronized (this.pointListChangeLock) {
      try {
        Object value = this.modbusMaster.getValue(ml);

        returnToNormal(1, time);
        dataPoint.setAttribute("UNRELIABLE", Boolean.valueOf(false));

        updatePointValue(dataPoint, pl, value, time);
      }
      catch (ErrorResponseException e) {
        raiseEvent(1, time, true, new TranslatableMessage("event.exception2", new Object[] { dataPoint.getVO().getName(), e.getMessage() }));

        dataPoint.setAttribute("UNRELIABLE", Boolean.valueOf(true));
      }
      catch (ModbusTransportException e)
      {
        this.LOG.warn("Error during forcePointRead", e);
        dataPoint.setAttribute("UNRELIABLE", Boolean.valueOf(true));
      }
    }
  }

  private void updatePointValue(DataPointRT dataPoint, ModbusPointLocatorRT pl, Object value, long time) {
    if (pl.getVO().getDataTypeId() == 1) {
      dataPoint.updatePointValue(new PointValueTime(((Boolean)value).booleanValue(), time));
    } else if (pl.getVO().getDataTypeId() == 4) {
      dataPoint.updatePointValue(new PointValueTime((String)value, time));
    }
    else {
      double newValue = ((Number)value).doubleValue();
      newValue *= pl.getVO().getMultiplier();
      newValue += pl.getVO().getAdditive();
      dataPoint.updatePointValue(new PointValueTime(newValue, time));
    }
  }

  public void terminate()
  {
    super.terminate();

    this.modbusMaster.destroy();

    if (this.vo.isLogIO())
      this.modbusMaster.getIoLog().close();
  }

  public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source)
  {
    ModbusPointLocatorRT pl = (ModbusPointLocatorRT)dataPoint.getPointLocator();
    BaseLocator ml = createModbusLocator(pl.getVO());
    try
    {
      if (dataPoint.getDataTypeId() == 3) {
        double convertedValue = valueTime.getDoubleValue();
        convertedValue -= pl.getVO().getAdditive();
        convertedValue /= pl.getVO().getMultiplier();
        this.modbusMaster.setValue(ml, Double.valueOf(convertedValue));
      }
      else if (dataPoint.getDataTypeId() == 4) {
        this.modbusMaster.setValue(ml, valueTime.getStringValue());
      } else {
        this.modbusMaster.setValue(ml, Boolean.valueOf(valueTime.getBooleanValue()));
      }dataPoint.setPointValue(valueTime, source);

      returnToNormal(2, valueTime.getTime());
    }
    catch (ModbusTransportException e)
    {
      raiseEvent(2, valueTime.getTime(), true, new TranslatableMessage("event.exception2", new Object[] { dataPoint.getVO().getName(), e.getMessage() }));

      this.LOG.info("Error setting point value", e);
    }
    catch (ErrorResponseException e) {
      raiseEvent(2, valueTime.getTime(), true, new TranslatableMessage("event.exception2", new Object[] { dataPoint.getVO().getName(), e.getErrorResponse().getExceptionMessage() }));

      this.LOG.info("Error setting point value", e);
    }
  }

  public static BaseLocator<?> createModbusLocator(ModbusPointLocatorVO vo) {
    return BaseLocator.createLocator(vo.getSlaveId(), vo.getRange(), vo.getOffset(), vo.getModbusDataType(), vo.getBit(), vo.getRegisterCount(), Charset.forName(vo.getCharset()));
  }

  public static TranslatableMessage localExceptionMessage(Exception e)
  {
    if ((e instanceof ModbusTransportException)) {
      Throwable cause = e.getCause();
      if ((cause instanceof TimeoutException))
        return new TranslatableMessage("MODBUS.rt.noResponse", new Object[] { Integer.valueOf(((ModbusTransportException)e).getSlaveId()) });
      if ((cause instanceof ConnectException)) {
        return new TranslatableMessage("common.default", new Object[] { e.getMessage() });
      }
    }
    return DataSourceRT.getExceptionMessage(e);
  }

  protected TranslatableMessage getLocalExceptionMessage(Exception e) {
    return localExceptionMessage(e);
  }

  public void receivedException(Exception e)
  {
    this.LOG.debug("Modbus exception in " + this.vo.getId() + ": " + this.vo.getName(), e);
    raiseEvent(3, System.currentTimeMillis(), true, new TranslatableMessage("MODBUS.rt.master", new Object[] { e.getMessage() }));
  }

  public static File getIOLogFile(int dataSourceId)
  {
    return new File(Common.getLogsDir(), "modbusIO-" + dataSourceId + ".log");
  }
}