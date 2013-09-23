package com.serotonin.m2m2.maintenanceEvents;

import com.serotonin.m2m2.module.RuntimeManagerDefinition;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.util.Assert;

public class RTMDefinition extends RuntimeManagerDefinition
{
  public static RTMDefinition instance;
  private final List<MaintenanceEventRT> maintenanceEvents = new CopyOnWriteArrayList();

  public RTMDefinition() {
    instance = this;
  }

  public int getInitializationPriority()
  {
    return 11;
  }

  public void initialize(boolean safe)
  {
    MaintenanceEventDao maintenanceEventDao = new MaintenanceEventDao();
    for (MaintenanceEventVO vo : maintenanceEventDao.getMaintenanceEvents())
      if (!vo.isDisabled())
        if (safe) {
          vo.setDisabled(true);
          maintenanceEventDao.saveMaintenanceEvent(vo);
        }
        else {
          startMaintenanceEvent(vo);
        }
  }

  public void terminate()
  {
    while (!this.maintenanceEvents.isEmpty())
      stopMaintenanceEvent(((MaintenanceEventRT)this.maintenanceEvents.get(0)).getVo().getId());
  }

  public MaintenanceEventRT getRunningMaintenanceEvent(int id)
  {
    for (MaintenanceEventRT rt : this.maintenanceEvents) {
      if (rt.getVo().getId() == id)
        return rt;
    }
    return null;
  }

  public boolean isActiveMaintenanceEvent(int dataSourceId) {
    for (MaintenanceEventRT rt : this.maintenanceEvents) {
      if ((rt.getVo().getDataSourceId() == dataSourceId) && (rt.isEventActive()))
        return true;
    }
    return false;
  }

  public boolean isMaintenanceEventRunning(int id) {
    return getRunningMaintenanceEvent(id) != null;
  }

  public void deleteMaintenanceEvent(int id) {
    stopMaintenanceEvent(id);
    new MaintenanceEventDao().deleteMaintenanceEvent(id);
  }

  public void saveMaintenanceEvent(MaintenanceEventVO vo)
  {
    stopMaintenanceEvent(vo.getId());

    new MaintenanceEventDao().saveMaintenanceEvent(vo);

    if (!vo.isDisabled())
      startMaintenanceEvent(vo);
  }

  private void startMaintenanceEvent(MaintenanceEventVO vo) {
    synchronized (this.maintenanceEvents)
    {
      if (isMaintenanceEventRunning(vo.getId())) {
        return;
      }

      Assert.isTrue(!vo.isDisabled());

      MaintenanceEventRT rt = new MaintenanceEventRT(vo);
      rt.initialize();

      this.maintenanceEvents.add(rt);
    }
  }

  private void stopMaintenanceEvent(int id) {
    synchronized (this.maintenanceEvents) {
      MaintenanceEventRT rt = getRunningMaintenanceEvent(id);
      if (rt == null) {
        return;
      }
      this.maintenanceEvents.remove(rt);
      rt.terminate();
    }
  }
}