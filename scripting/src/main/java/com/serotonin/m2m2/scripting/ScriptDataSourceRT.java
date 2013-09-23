package com.serotonin.m2m2.scripting;

import com.serotonin.db.pair.IntStringPair;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.RuntimeManager;
import com.serotonin.m2m2.rt.dataImage.DataPointListener;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.IDataPointValueSource;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.dataImage.SetPointSource;
import com.serotonin.m2m2.rt.dataImage.types.DataValue;
import com.serotonin.m2m2.rt.dataSource.DataSourceRT;
import com.serotonin.m2m2.rt.dataSource.PollingDataSource;
import com.serotonin.m2m2.rt.script.PointValueSetter;
import com.serotonin.m2m2.rt.script.ResultTypeException;
import com.serotonin.m2m2.rt.script.ScriptError;
import com.serotonin.m2m2.rt.script.ScriptUtils;
import com.serotonin.m2m2.rt.script.WrapperContext;
import com.serotonin.m2m2.util.timeout.TimeoutClient;
import com.serotonin.m2m2.util.timeout.TimeoutTask;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.m2m2.vo.dataSource.PointLocatorVO;
import com.serotonin.m2m2.web.taglib.Functions;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import org.apache.commons.io.output.NullWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ScriptDataSourceRT extends PollingDataSource
  implements SetPointSource
{
  private static final Log LOG = LogFactory.getLog(ScriptDataSourceRT.class);
  public static final String LOG_CONTEXT_KEY = "LOG";
  public static final int EVENT_TYPE_SCRIPT_ERROR = 1;
  public static final int EVENT_TYPE_DATA_TYPE_ERROR = 2;
  public static final int EVENT_TYPE_LOG_ERROR = 3;
  final ScriptDataSourceVO vo;
  final ScriptEngine scriptEngine;
  private ScriptLog scriptLog;
  private final CompiledScript compiledScript;
  private final int executionDelay;
  final ReentrantReadWriteLock executionLock = new ReentrantReadWriteLock();
  final SetCallback setCallback = new SetCallback();

  private final List<ExternalPointListener> listeners = new ArrayList();
  private final ExecutionDelayTimeout executionDelayTimeout;
  TimeoutTask executionDelayTask;

  public ScriptDataSourceRT(ScriptDataSourceVO vo)
  {
    super(vo);
    this.vo = vo;
    setCronPattern(vo.getCronPattern());

    this.scriptEngine = ScriptUtils.newEngine();
    ScriptUtils.prepareEngine(this.scriptEngine);

    this.scriptEngine.getContext().setWriter(new PrintWriter(new NullWriter()));
    try
    {
      ScriptUtils.executeGlobalScripts(this.scriptEngine);
      this.compiledScript = ScriptUtils.compile(this.scriptEngine, vo.getScript());
    }
    catch (ScriptError e)
    {
      throw new RuntimeException(e);
    }

    this.executionDelay = (vo.getExecutionDelaySeconds() * 1000);
    this.executionDelayTimeout = new ExecutionDelayTimeout();
  }
  public void initialize() {
    super.initialize();

    File file = getLogFile(this.vo.getId());
    PrintWriter out;
    try { out = new PrintWriter(file);
    } catch (IOException e)
    {
      raiseEvent(3, System.currentTimeMillis(), false, new TranslatableMessage("dsEdit.script.logError.open", new Object[] { file.getPath(), e.getMessage() }));

      out = new PrintWriter(new NullWriter());
    }

    this.scriptLog = new ScriptLog(out, this.vo.getLogLevel());
    this.scriptEngine.put("LOG", this.scriptLog);
    this.scriptLog.info("Data source started");

    for (IntStringPair ivp : this.vo.getContext()) {
      ExternalPointListener l = new ExternalPointListener(ivp.getKey(), ivp.getValue());
      this.listeners.add(l);
      Common.runtimeManager.addDataPointListener(ivp.getKey(), l);

      DataPointRT dprt = Common.runtimeManager.getDataPoint(ivp.getKey());
      if (dprt != null)
        addToContext(ivp.getValue(), dprt, false);
    }
  }

  public void terminate()
  {
    super.terminate();
    try
    {
      this.executionLock.writeLock().lock();

      for (ExternalPointListener l : this.listeners)
        Common.runtimeManager.removeDataPointListener(l.dataPointId, l);
    }
    finally {
      this.executionLock.writeLock().unlock();
    }

    this.scriptLog.info("Data source stopped");
    this.scriptLog.close();

    this.scriptEngine.getBindings(100).clear();
  }

  public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source)
  {
    dataPoint.setPointValue(valueTime, source);
  }

  public void doPoll(long time)
  {
    if (this.executionDelay > 0) {
      if (this.executionDelayTask != null)
      {
        LOG.warn(this.vo.getName() + ": poll at " + Functions.getFullSecondTime(time) + " aborted because a previous poll started at " + Functions.getFullSecondTime(this.executionDelayTimeout.getFireTime()) + " is still running");

        return;
      }

      this.executionDelayTimeout.setFireTime(time);
      this.executionDelayTask = new TimeoutTask(this.executionDelay, this.executionDelayTimeout);
    }
    else {
      execute(time);
    }
  }

  public void addDataPoint(DataPointRT dataPoint) {
    synchronized (this.pointListChangeLock) {
      ScriptPointLocatorRT locator = (ScriptPointLocatorRT)dataPoint.getPointLocator();
      addToContext(locator.vo.getVarName(), dataPoint, true);

      super.addDataPoint(dataPoint);
    }
  }

  public void removeDataPoint(DataPointRT dataPoint)
  {
    synchronized (this.pointListChangeLock) {
      ScriptPointLocatorRT locator = (ScriptPointLocatorRT)dataPoint.getPointLocator();
      removeFromContext(locator.vo.getVarName());

      super.removeDataPoint(dataPoint);
    }
  }

  void execute(long time) {
    WrapperContext wrapperContext = new WrapperContext(time);
    ScriptUtils.wrapperContext(this.scriptEngine, wrapperContext);
    try
    {
      this.executionLock.writeLock().lock();
      ScriptUtils.execute(this.compiledScript);
    }
    catch (ScriptError e)
    {
      raiseEvent(1, System.currentTimeMillis(), false, new TranslatableMessage("globalScript.rhinoExceptionCol", new Object[] { e.getMessage(), Integer.valueOf(e.getLineNumber()), Integer.valueOf(e.getColumnNumber()) }));
    }
    finally
    {
      this.executionLock.writeLock().unlock();
    }

    if (this.scriptLog.trouble()) {
      raiseEvent(3, System.currentTimeMillis(), true, new TranslatableMessage("dsEdit.script.logError.trouble"));
    }
    else
      returnToNormal(3, System.currentTimeMillis());
  }

  void addToContext(String varName, DataPointRT dprt, boolean local)
  {
    boolean useCallback;
    if (local)
      useCallback = true;
    else {
      useCallback = dprt.getVO().getPointLocator().isSettable();
    }
    ScriptUtils.addToContext(this.scriptEngine, varName, dprt, useCallback ? this.setCallback : null);
  }

  void removeFromContext(String varName) {
    ScriptUtils.removeFromContext(this.scriptEngine, varName);
  }

  public String getSetPointSourceType()
  {
    return "SCRIPT";
  }

  public int getSetPointSourceId()
  {
    return this.vo.getId();
  }

  public TranslatableMessage getSetPointSourceMessage()
  {
    return new TranslatableMessage("scripting.annotation");
  }

  public void raiseRecursionFailureEvent()
  {
    throw new RuntimeException("raiseRecursionFailureEvent");
  }

  void raiseDataTypeError(long timestamp, DataPointRT dprt, ResultTypeException e)
  {
    raiseEvent(2, timestamp, false, new TranslatableMessage("event.script.typeError", new Object[] { dprt.getVO().getName(), e.getMessage() }));
  }

  public static File getLogFile(int dataSourceId)
  {
    return new File(Common.M2M2_HOME, "scripting-" + dataSourceId + ".log");
  }

  class SetCallback
    implements PointValueSetter
  {
    SetCallback()
    {
    }

    public void set(IDataPointValueSource point, Object value, long timestamp)
    {
      DataPointRT dprt = (DataPointRT)point;
      try
      {
        DataValue mangoValue = ScriptUtils.coerce(value, dprt.getDataTypeId());
        PointValueTime newValue = new PointValueTime(mangoValue, timestamp);

        if (dprt.getDataSourceId() == ScriptDataSourceRT.this.vo.getId())
        {
          point.updatePointValue(newValue);
        }
        else {
          DataSourceRT dsrt = Common.runtimeManager.getRunningDataSource(dprt.getDataSourceId());
          dsrt.setPointValue(dprt, newValue, ScriptDataSourceRT.this);
        }
      }
      catch (ResultTypeException e)
      {
        ScriptDataSourceRT.this.raiseDataTypeError(timestamp, dprt, e);
      }
    }
  }

  class ExternalPointListener
    implements DataPointListener
  {
    final int dataPointId;
    final String varName;

    public ExternalPointListener(int dataPointId, String varName)
    {
      this.dataPointId = dataPointId;
      this.varName = varName;
    }

    public void pointInitialized()
    {
      try
      {
        ScriptDataSourceRT.this.executionLock.readLock().lock();
        ScriptDataSourceRT.this.addToContext(this.varName, Common.runtimeManager.getDataPoint(this.dataPointId), false);
      }
      finally {
        ScriptDataSourceRT.this.executionLock.readLock().unlock();
      }
    }

    public void pointTerminated()
    {
      try
      {
        ScriptDataSourceRT.this.executionLock.readLock().lock();
        ScriptDataSourceRT.this.removeFromContext(this.varName);
      }
      finally {
        ScriptDataSourceRT.this.executionLock.readLock().unlock();
      }
    }

    public void pointUpdated(PointValueTime newValue)
    {
    }

    public void pointChanged(PointValueTime oldValue, PointValueTime newValue)
    {
    }

    public void pointSet(PointValueTime oldValue, PointValueTime newValue)
    {
    }

    public void pointBackdated(PointValueTime value)
    {
    }
  }

  class ExecutionDelayTimeout
    implements TimeoutClient
  {
    private long fireTime;

    ExecutionDelayTimeout()
    {
    }

    public long getFireTime()
    {
      return this.fireTime;
    }

    public void setFireTime(long fireTime) {
      this.fireTime = fireTime;
    }

    public void scheduleTimeout(long fireTime)
    {
      ScriptDataSourceRT.this.execute(this.fireTime);
      ScriptDataSourceRT.this.executionDelayTask = null;
    }
  }
}