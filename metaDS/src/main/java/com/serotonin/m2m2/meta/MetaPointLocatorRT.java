package com.serotonin.m2m2.meta;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.db.pair.IntStringPair;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.RuntimeManager;
import com.serotonin.m2m2.rt.dataImage.DataPointListener;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.IDataPointValueSource;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;
import com.serotonin.m2m2.rt.script.CompiledScriptExecutor;
import com.serotonin.m2m2.rt.script.DataPointStateException;
import com.serotonin.m2m2.rt.script.ResultTypeException;
import com.serotonin.m2m2.util.DateUtils;
import com.serotonin.timer.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.script.CompiledScript;
import javax.script.ScriptException;

public class MetaPointLocatorRT extends PointLocatorRT
  implements DataPointListener
{
  private static final ThreadLocal<List<Integer>> threadLocal = new ThreadLocal();
  private static final int MAX_RECURSION = 10;
  final Boolean LOCK = new Boolean(false);
  final MetaPointLocatorVO vo;
  AbstractTimer timer;
  private MetaDataSourceRT dataSource;
  protected DataPointRT dataPoint;
  protected Map<String, IDataPointValueSource> context;
  boolean initialized;
  TimerTask timerTask;
  CompiledScript compiledScript;

  public MetaPointLocatorRT(MetaPointLocatorVO vo)
  {
    this.vo = vo;
  }

  public boolean isSettable()
  {
    return this.vo.isSettable();
  }

  public MetaPointLocatorVO getPointLocatorVO() {
    return this.vo;
  }

  boolean isContextCreated() {
    return this.context != null;
  }

  public void initialize(AbstractTimer timer, MetaDataSourceRT dataSource, DataPointRT dataPoint)
  {
    this.timer = timer;
    this.dataSource = dataSource;
    this.dataPoint = dataPoint;

    createContext();
    try
    {
      this.compiledScript = CompiledScriptExecutor.compile(this.vo.getScript());
    }
    catch (ScriptException e) {
      e = CompiledScriptExecutor.prettyScriptMessage(e);
      dataSource.raiseScriptError(System.currentTimeMillis(), dataPoint, new TranslatableMessage("common.default", new Object[] { e.getMessage() }));
    }

    for (IntStringPair contextKey : this.vo.getContext())
    {
      if (dataPoint.getId() != contextKey.getKey()) {
        Common.runtimeManager.addDataPointListener(contextKey.getKey(), this);
      }
    }
    this.initialized = true;

    initializeTimerTask();
  }

  protected void initializeTimerTask() {
    int updateEventId = this.vo.getUpdateEvent();
    if (updateEventId != 0)
    {
      this.timerTask = new ScheduledUpdateTimeout(calculateTimeout(this.timer.currentTimeMillis()));
    }
  }

  public void terminate() {
    synchronized (this.LOCK)
    {
      for (IntStringPair contextKey : this.vo.getContext()) {
        Common.runtimeManager.removeDataPointListener(contextKey.getKey(), this);
      }

      if (this.timerTask != null) {
        this.timerTask.cancel();
      }
      this.initialized = false;
    }
  }

  public void pointChanged(PointValueTime oldValue, PointValueTime newValue)
  {
  }

  public void pointSet(PointValueTime oldValue, PointValueTime newValue)
  {
  }

  public void pointUpdated(PointValueTime newValue)
  {
    if (this.vo.getUpdateEvent() == 0)
    {
      List sourceIds;
      if (threadLocal.get() == null)
        sourceIds = new ArrayList();
      else {
        sourceIds = (List)threadLocal.get();
      }
      long time = newValue.getTime();
      if (this.vo.getExecutionDelaySeconds() == 0)
        execute(time, sourceIds);
      else
        synchronized (this.LOCK) {
          if (this.initialized) {
            if (this.timerTask != null)
              this.timerTask.cancel();
            this.timerTask = new ExecutionDelayTimeout(sourceIds);
          }
        }
    }
  }

  public void pointBackdated(PointValueTime value)
  {
  }

  public void pointInitialized()
  {
    createContext();
    this.dataSource.checkForDisabledPoints();
  }

  public void pointTerminated()
  {
    createContext();
    this.dataSource.checkForDisabledPoints();
  }

  long calculateTimeout(long time)
  {
    int updateEventId = this.vo.getUpdateEvent();
    long timeout;
    if (updateEventId == 100) {
      try {
        CronExpression ce = new CronExpression(this.vo.getUpdateCronPattern());
        timeout = ce.getNextValidTimeAfter(new Date(time)).getTime();
      }
      catch (ParseException e) {
        throw new ShouldNeverHappenException(e);
      }
    }
    else
      timeout = DateUtils.next(time, updateEventId);
    return timeout + this.vo.getExecutionDelaySeconds() * 1000;
  }

  void execute(long runtime, List<Integer> sourceIds)
  {
    if (this.context == null) {
      return;
    }

    int count = 0;
    for (Integer id : sourceIds) {
      if (id.intValue() == this.dataPoint.getId()) {
        count++;
      }
    }
    if (count > 10) {
      handleError(runtime, new TranslatableMessage("event.meta.recursionFailure"));
      return;
    }

    sourceIds.add(Integer.valueOf(this.dataPoint.getId()));
    threadLocal.set(sourceIds);
    try {
      try {
        PointValueTime pvt = CompiledScriptExecutor.execute(this.compiledScript, this.context, this.timer.currentTimeMillis(), this.vo.getDataTypeId(), runtime);

        if (pvt.getValue() == null)
          handleError(runtime, new TranslatableMessage("event.meta.nullResult"));
        else
          updatePoint(pvt);
      }
      catch (ScriptException e) {
        handleError(runtime, new TranslatableMessage("common.default", new Object[] { e.getMessage() }));
      }
      catch (ResultTypeException e) {
        handleError(runtime, e.getTranslatableMessage());
      }
    }
    finally {
      threadLocal.remove();
    }
  }

  private void createContext() {
    this.context = null;
    try {
      this.context = CompiledScriptExecutor.convertContext(this.vo.getContext());
    }
    catch (DataPointStateException e)
    {
    }
  }

  protected void updatePoint(PointValueTime pvt) {
    this.dataPoint.updatePointValue(pvt);
  }

  protected void handleError(long runtime, TranslatableMessage message) {
    this.dataSource.raiseScriptError(runtime, this.dataPoint, message);
  }

  class ExecutionDelayTimeout extends TimerTask
  {
    private final long updateTime=0;
    private final List<Integer> sourceIds;

    public ExecutionDelayTimeout(List<Integer> arg3)
    {
      super(new TimerTrigger() {
          @Override
          public long mostRecentExecutionTime() {
              return 0;  //To change body of implemented methods use File | Settings | File Templates.
          }

          @Override
          protected long getFirstExecutionTime() {
              return 0;  //To change body of implemented methods use File | Settings | File Templates.
          }

          @Override
          protected long calculateNextExecutionTime() {
              return 0;  //To change body of implemented methods use File | Settings | File Templates.
          }
      });
//      this.updateTime = updateTime;
      this.sourceIds = arg3;
      MetaPointLocatorRT.this.timer.schedule(this);
    }

    public void run(long fireTime)
    {
      MetaPointLocatorRT.this.execute(this.updateTime, this.sourceIds);
    }
  }

  class ScheduledUpdateTimeout extends TimerTask
  {
    ScheduledUpdateTimeout(long fireTime)
    {
      super(new TimerTrigger() {
          @Override
          public long mostRecentExecutionTime() {
              return 0;  //To change body of implemented methods use File | Settings | File Templates.
          }

          @Override
          protected long getFirstExecutionTime() {
              return 0;  //To change body of implemented methods use File | Settings | File Templates.
          }

          @Override
          protected long calculateNextExecutionTime() {
              return 0;  //To change body of implemented methods use File | Settings | File Templates.
          }
      });
      MetaPointLocatorRT.this.timer.schedule(this);
    }

    public void run(long fireTime)
    {
      MetaPointLocatorRT.this.execute(fireTime - MetaPointLocatorRT.this.vo.getExecutionDelaySeconds() * 1000, new ArrayList());

      synchronized (MetaPointLocatorRT.this.LOCK) {
        if (MetaPointLocatorRT.this.initialized)
          MetaPointLocatorRT.this.timerTask = new ScheduledUpdateTimeout(calculateTimeout(fireTime));
      }
    }
  }
}