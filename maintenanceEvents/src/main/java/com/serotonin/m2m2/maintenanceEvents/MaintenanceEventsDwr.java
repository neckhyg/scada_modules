package com.serotonin.m2m2.maintenanceEvents;

import com.serotonin.db.pair.IntStringPair;
import com.serotonin.m2m2.db.dao.DataSourceDao;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.i18n.Translations;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;
import com.serotonin.m2m2.web.dwr.ModuleDwr;
import com.serotonin.m2m2.web.dwr.util.DwrPermission;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

public class MaintenanceEventsDwr extends ModuleDwr
{
  @DwrPermission(admin=true)
  public ProcessResult getMaintenanceEvents()
  {
    ProcessResult response = new ProcessResult();
    final Translations translations = getTranslations();

    List events = new MaintenanceEventDao().getMaintenanceEvents();
    Collections.sort(events, new Comparator<MaintenanceEventVO>()
    {
      public int compare(MaintenanceEventVO m1, MaintenanceEventVO m2) {
        return m1.getDescription().translate(translations).compareTo(m1.getDescription().translate(translations));
      }
    });
    response.addData("events", events);

    List dataSources = new ArrayList();
    for (DataSourceVO ds : new DataSourceDao().getDataSources())
      dataSources.add(new IntStringPair(ds.getId(), ds.getName()));
    response.addData("dataSources", dataSources);

    return response;
  }
  @DwrPermission(admin=true)
  public ProcessResult getMaintenanceEvent(int id) {
    ProcessResult response = new ProcessResult();

    boolean activated = false;
    MaintenanceEventVO me;
    if (id == -1) {
      DateTime dt = new DateTime();
      me = new MaintenanceEventVO();
      me.setXid(new MaintenanceEventDao().generateUniqueXid());
      me.setActiveYear(dt.getYear());
      me.setInactiveYear(dt.getYear());
      me.setActiveMonth(dt.getMonthOfYear());
      me.setInactiveMonth(dt.getMonthOfYear());
    }
    else {
      me = new MaintenanceEventDao().getMaintenanceEvent(id);

      MaintenanceEventRT rt = RTMDefinition.instance.getRunningMaintenanceEvent(me.getId());
      if (rt != null) {
        activated = rt.isEventActive();
      }
    }
    response.addData("me", me);
    response.addData("activated", Boolean.valueOf(activated));

    return response;
  }

  @DwrPermission(admin=true)
  public ProcessResult saveMaintenanceEvent(int id, String xid, int dataSourceId, String alias, int alarmLevel, int scheduleType, boolean disabled, int activeYear, int activeMonth, int activeDay, int activeHour, int activeMinute, int activeSecond, String activeCron, int inactiveYear, int inactiveMonth, int inactiveDay, int inactiveHour, int inactiveMinute, int inactiveSecond, String inactiveCron)
  {
    MaintenanceEventVO e = new MaintenanceEventVO();
    e.setId(id);
    e.setXid(xid);
    e.setDataSourceId(dataSourceId);
    e.setAlias(alias);
    e.setAlarmLevel(alarmLevel);
    e.setScheduleType(scheduleType);
    e.setDisabled(disabled);
    e.setActiveYear(activeYear);
    e.setActiveMonth(activeMonth);
    e.setActiveDay(activeDay);
    e.setActiveHour(activeHour);
    e.setActiveMinute(activeMinute);
    e.setActiveSecond(activeSecond);
    e.setActiveCron(activeCron);
    e.setInactiveYear(inactiveYear);
    e.setInactiveMonth(inactiveMonth);
    e.setInactiveDay(inactiveDay);
    e.setInactiveHour(inactiveHour);
    e.setInactiveMinute(inactiveMinute);
    e.setInactiveSecond(inactiveSecond);
    e.setInactiveCron(inactiveCron);

    ProcessResult response = new ProcessResult();
    MaintenanceEventDao maintenanceEventDao = new MaintenanceEventDao();

    if (StringUtils.isBlank(xid))
      response.addContextualMessage("xid", "validate.required", new Object[0]);
    else if (!maintenanceEventDao.isXidUnique(xid, id)) {
      response.addContextualMessage("xid", "validate.xidUsed", new Object[0]);
    }
    e.validate(response);

    if (!response.getHasMessages()) {
      RTMDefinition.instance.saveMaintenanceEvent(e);
      response.addData("meId", Integer.valueOf(e.getId()));
    }

    return response;
  }
  @DwrPermission(admin=true)
  public void deleteMaintenanceEvent(int meId) {
    RTMDefinition.instance.deleteMaintenanceEvent(meId);
  }
  @DwrPermission(admin=true)
  public ProcessResult toggleMaintenanceEvent(int id) {
    ProcessResult response = new ProcessResult();

    MaintenanceEventRT rt = RTMDefinition.instance.getRunningMaintenanceEvent(id);
    boolean activated = false;
    if (rt == null)
      response.addMessage(new TranslatableMessage("maintenanceEvents.toggle.disabled"));
    else {
      activated = rt.toggle();
    }
    response.addData("activated", Boolean.valueOf(activated));

    return response;
  }
}