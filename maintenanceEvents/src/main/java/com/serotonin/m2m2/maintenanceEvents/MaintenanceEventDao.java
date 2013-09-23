package com.serotonin.m2m2.maintenanceEvents;

import com.serotonin.db.spring.ExtendedJdbcTemplate;
import com.serotonin.m2m2.db.dao.BaseDao;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class MaintenanceEventDao extends BaseDao
{
  private static final String MAINTENANCE_EVENT_SELECT = "select m.id, m.xid, m.dataSourceId, m.alias, m.alarmLevel,   m.scheduleType, m.disabled, m.activeYear, m.activeMonth, m.activeDay, m.activeHour, m.activeMinute,   m.activeSecond, m.activeCron, m.inactiveYear, m.inactiveMonth, m.inactiveDay, m.inactiveHour,   m.inactiveMinute, m.inactiveSecond, m.inactiveCron, d.dataSourceType, d.name, d.xid from maintenanceEvents m join dataSources d on m.dataSourceId=d.id ";

  public String generateUniqueXid()
  {
    return generateUniqueXid("ME_", "maintenanceEvents");
  }

  public boolean isXidUnique(String xid, int excludeId) {
    return isXidUnique(xid, excludeId, "maintenanceEvents");
  }

  public List<MaintenanceEventVO> getMaintenanceEvents() {
    return query("select m.id, m.xid, m.dataSourceId, m.alias, m.alarmLevel,   m.scheduleType, m.disabled, m.activeYear, m.activeMonth, m.activeDay, m.activeHour, m.activeMinute,   m.activeSecond, m.activeCron, m.inactiveYear, m.inactiveMonth, m.inactiveDay, m.inactiveHour,   m.inactiveMinute, m.inactiveSecond, m.inactiveCron, d.dataSourceType, d.name, d.xid from maintenanceEvents m join dataSources d on m.dataSourceId=d.id ", new MaintenanceEventRowMapper());
  }

  public MaintenanceEventVO getMaintenanceEvent(int id) {
    MaintenanceEventVO me = (MaintenanceEventVO)queryForObject("select m.id, m.xid, m.dataSourceId, m.alias, m.alarmLevel,   m.scheduleType, m.disabled, m.activeYear, m.activeMonth, m.activeDay, m.activeHour, m.activeMinute,   m.activeSecond, m.activeCron, m.inactiveYear, m.inactiveMonth, m.inactiveDay, m.inactiveHour,   m.inactiveMinute, m.inactiveSecond, m.inactiveCron, d.dataSourceType, d.name, d.xid from maintenanceEvents m join dataSources d on m.dataSourceId=d.id where m.id=?", new Object[] { Integer.valueOf(id) }, new MaintenanceEventRowMapper());

    return me;
  }

  public MaintenanceEventVO getMaintenanceEvent(String xid) {
    return (MaintenanceEventVO)queryForObject("select m.id, m.xid, m.dataSourceId, m.alias, m.alarmLevel,   m.scheduleType, m.disabled, m.activeYear, m.activeMonth, m.activeDay, m.activeHour, m.activeMinute,   m.activeSecond, m.activeCron, m.inactiveYear, m.inactiveMonth, m.inactiveDay, m.inactiveHour,   m.inactiveMinute, m.inactiveSecond, m.inactiveCron, d.dataSourceType, d.name, d.xid from maintenanceEvents m join dataSources d on m.dataSourceId=d.id where m.xid=?", new Object[] { xid }, new MaintenanceEventRowMapper(), null);
  }

  public void saveMaintenanceEvent(MaintenanceEventVO me)
  {
    if (me.getId() == -1)
      insertMaintenanceEvent(me);
    else
      updateMaintenanceEvent(me);
  }

  private void insertMaintenanceEvent(MaintenanceEventVO me) {
    me.setId(doInsert("insert into maintenanceEvents (  xid, dataSourceId, alias, alarmLevel, scheduleType, disabled,   activeYear, activeMonth, activeDay, activeHour, activeMinute, activeSecond, activeCron,   inactiveYear, inactiveMonth, inactiveDay, inactiveHour, inactiveMinute, inactiveSecond, inactiveCron ) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[] { me.getXid(), Integer.valueOf(me.getDataSourceId()), me.getAlias(), Integer.valueOf(me.getAlarmLevel()), Integer.valueOf(me.getScheduleType()), boolToChar(me.isDisabled()), Integer.valueOf(me.getActiveYear()), Integer.valueOf(me.getActiveMonth()), Integer.valueOf(me.getActiveDay()), Integer.valueOf(me.getActiveHour()), Integer.valueOf(me.getActiveMinute()), Integer.valueOf(me.getActiveSecond()), me.getActiveCron(), Integer.valueOf(me.getInactiveYear()), Integer.valueOf(me.getInactiveMonth()), Integer.valueOf(me.getInactiveDay()), Integer.valueOf(me.getInactiveHour()), Integer.valueOf(me.getInactiveMinute()), Integer.valueOf(me.getInactiveSecond()), me.getInactiveCron() }));

    AuditEventType.raiseAddedEvent("MAINTENANCE_EVENT", me);
  }

  private void updateMaintenanceEvent(MaintenanceEventVO me) {
    MaintenanceEventVO old = getMaintenanceEvent(me.getId());
    this.ejt.update("update maintenanceEvents set   xid=?, dataSourceId=?, alias=?, alarmLevel=?, scheduleType=?, disabled=?,   activeYear=?, activeMonth=?, activeDay=?, activeHour=?, activeMinute=?, activeSecond=?, activeCron=?,   inactiveYear=?, inactiveMonth=?, inactiveDay=?, inactiveHour=?, inactiveMinute=?, inactiveSecond=?,   inactiveCron=? where id=?", new Object[] { me.getXid(), Integer.valueOf(me.getDataSourceId()), me.getAlias(), Integer.valueOf(me.getAlarmLevel()), Integer.valueOf(me.getScheduleType()), boolToChar(me.isDisabled()), Integer.valueOf(me.getActiveYear()), Integer.valueOf(me.getActiveMonth()), Integer.valueOf(me.getActiveDay()), Integer.valueOf(me.getActiveHour()), Integer.valueOf(me.getActiveMinute()), Integer.valueOf(me.getActiveSecond()), me.getActiveCron(), Integer.valueOf(me.getInactiveYear()), Integer.valueOf(me.getInactiveMonth()), Integer.valueOf(me.getInactiveDay()), Integer.valueOf(me.getInactiveHour()), Integer.valueOf(me.getInactiveMinute()), Integer.valueOf(me.getInactiveSecond()), me.getInactiveCron(), Integer.valueOf(me.getId()) });

    AuditEventType.raiseChangedEvent("MAINTENANCE_EVENT", old, me);
  }

  public void deleteMaintenanceEventsForDataSource(int dataSourceId) {
    List<Integer> ids = queryForList("select id from maintenanceEvents where dataSourceId=?", new Object[] { Integer.valueOf(dataSourceId) }, Integer.class);

    for (Integer id : ids)
      deleteMaintenanceEvent(id.intValue());
  }

  public void deleteMaintenanceEvent(final int maintenanceEventId) {
    MaintenanceEventVO me = getMaintenanceEvent(maintenanceEventId);
    final ExtendedJdbcTemplate ejt2 = this.ejt;
    if (me != null) {
      getTransactionTemplate().execute(new TransactionCallbackWithoutResult()
      {
        protected void doInTransactionWithoutResult(TransactionStatus status) {
          ejt2.update("delete from eventHandlers where eventTypeName=? and eventTypeRef1=?", new Object[] { "MAINTENANCE", Integer.valueOf(maintenanceEventId) });

          ejt2.update("delete from maintenanceEvents where id=?", new Object[] { Integer.valueOf(maintenanceEventId) });
        }
      });
      AuditEventType.raiseDeletedEvent("MAINTENANCE_EVENT", me);
    }
  }

  class MaintenanceEventRowMapper
    implements RowMapper<MaintenanceEventVO>
  {
    MaintenanceEventRowMapper()
    {
    }

    public MaintenanceEventVO mapRow(ResultSet rs, int rowNum)
      throws SQLException
    {
      MaintenanceEventVO me = new MaintenanceEventVO();
      int i = 0;
      i++; me.setId(rs.getInt(i));
      i++; me.setXid(rs.getString(i));
      i++; me.setDataSourceId(rs.getInt(i));
      i++; me.setAlias(rs.getString(i));
      i++; me.setAlarmLevel(rs.getInt(i));
      i++; me.setScheduleType(rs.getInt(i));
      i++; me.setDisabled(BaseDao.charToBool(rs.getString(i)));
      i++; me.setActiveYear(rs.getInt(i));
      i++; me.setActiveMonth(rs.getInt(i));
      i++; me.setActiveDay(rs.getInt(i));
      i++; me.setActiveHour(rs.getInt(i));
      i++; me.setActiveMinute(rs.getInt(i));
      i++; me.setActiveSecond(rs.getInt(i));
      i++; me.setActiveCron(rs.getString(i));
      i++; me.setInactiveYear(rs.getInt(i));
      i++; me.setInactiveMonth(rs.getInt(i));
      i++; me.setInactiveDay(rs.getInt(i));
      i++; me.setInactiveHour(rs.getInt(i));
      i++; me.setInactiveMinute(rs.getInt(i));
      i++; me.setInactiveSecond(rs.getInt(i));
      i++; me.setInactiveCron(rs.getString(i));
      i++; me.setDataSourceTypeId(rs.getString(i));
      i++; me.setDataSourceName(rs.getString(i));
      i++; me.setDataSourceXid(rs.getString(i));
      return me;
    }
  }
}