package com.serotonin.m2m2.meta;

import com.serotonin.db.pair.IntStringPair;
import com.serotonin.m2m2.db.dao.DataPointDao;
import com.serotonin.m2m2.db.dao.PointValueDao;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.HistoricalDataPoint;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.script.CompiledScriptExecutor;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.m2m2.vo.dataSource.PointLocatorVO;
import com.serotonin.timer.SimulationTimer;
import com.serotonin.timer.TimerTask;
import java.util.HashMap;
import java.util.Map;
import javax.script.ScriptException;

public class HistoricalMetaPointLocatorRT extends MetaPointLocatorRT
{
  private long updates;

  public HistoricalMetaPointLocatorRT(MetaPointLocatorVO vo)
  {
    super(vo);
  }

  public void initialize(SimulationTimer timer, DataPointRT dataPoint) throws ScriptException {
    this.timer = timer;
    this.dataPoint = dataPoint;
    this.initialized = true;
    initializeTimerTask();

    this.compiledScript = CompiledScriptExecutor.compile(this.vo.getScript());

    this.context = new HashMap();
    DataPointDao dataPointDao = new DataPointDao();
    PointValueDao pointValueDao = new PointValueDao();
    for (IntStringPair contextEntry : this.vo.getContext()) {
      DataPointVO cvo = dataPointDao.getDataPoint(contextEntry.getKey());
      HistoricalDataPoint point = new HistoricalDataPoint(cvo.getId(), cvo.getPointLocator().getDataTypeId(), timer, pointValueDao);

      this.context.put(contextEntry.getValue(), point);
    }
  }

  public void terminate()
  {
    synchronized (this.LOCK)
    {
      if (this.timerTask != null)
        this.timerTask.cancel();
    }
  }

  public long getUpdates() {
    return this.updates;
  }

  protected void updatePoint(PointValueTime pvt)
  {
    super.updatePoint(pvt);
    this.updates += 1L;
  }

  protected void handleError(long runtime, TranslatableMessage message)
  {
    throw new MetaPointExecutionException(message);
  }
}