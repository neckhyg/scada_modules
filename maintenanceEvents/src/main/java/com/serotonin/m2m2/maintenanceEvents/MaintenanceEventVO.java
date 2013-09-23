package com.serotonin.m2m2.maintenanceEvents;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.spi.JsonSerializable;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.db.dao.DataSourceDao;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableJsonException;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.event.AlarmLevels;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.util.ChangeComparable;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;
import com.serotonin.m2m2.vo.event.EventTypeVO;
import com.serotonin.m2m2.web.taglib.Functions;
import com.serotonin.timer.CronTimerTrigger;
import com.serotonin.validation.StringValidation;
import java.io.IOException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

public class MaintenanceEventVO
  implements ChangeComparable<MaintenanceEventVO>, JsonSerializable
{
  public static final String XID_PREFIX = "ME_";
  public static final int TYPE_MANUAL = 1;
  public static final int TYPE_HOURLY = 2;
  public static final int TYPE_DAILY = 3;
  public static final int TYPE_WEEKLY = 4;
  public static final int TYPE_MONTHLY = 5;
  public static final int TYPE_YEARLY = 6;
  public static final int TYPE_ONCE = 7;
  public static final int TYPE_CRON = 8;
  public static ExportCodes TYPE_CODES = new ExportCodes();

  private int id = -1;
  private String xid;
  private int dataSourceId;

  @JsonProperty
  private String alias;
  private int alarmLevel = 0;
  private int scheduleType = 1;

  @JsonProperty
  private boolean disabled = false;

  @JsonProperty
  private int activeYear;

  @JsonProperty
  private int activeMonth;

  @JsonProperty
  private int activeDay;

  @JsonProperty
  private int activeHour;

  @JsonProperty
  private int activeMinute;

  @JsonProperty
  private int activeSecond;

  @JsonProperty
  private String activeCron;

  @JsonProperty
  private int inactiveYear;

  @JsonProperty
  private int inactiveMonth;

  @JsonProperty
  private int inactiveDay;

  @JsonProperty
  private int inactiveHour;

  @JsonProperty
  private int inactiveMinute;

  @JsonProperty
  private int inactiveSecond;

  @JsonProperty
  private String inactiveCron;
  private String dataSourceTypeId;
  private String dataSourceName;
  private String dataSourceXid;
  private static final String[] weekdays;
  private static final String[] months;

  public boolean isNew() { return this.id == -1;
  }

  public int getId()
  {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getXid() {
    return this.xid;
  }

  public void setXid(String xid) {
    this.xid = xid;
  }

  public int getDataSourceId() {
    return this.dataSourceId;
  }

  public void setDataSourceId(int dataSourceId) {
    this.dataSourceId = dataSourceId;
  }

  public String getAlias() {
    return this.alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public int getAlarmLevel() {
    return this.alarmLevel;
  }

  public void setAlarmLevel(int alarmLevel) {
    this.alarmLevel = alarmLevel;
  }

  public int getScheduleType() {
    return this.scheduleType;
  }

  public void setScheduleType(int scheduleType) {
    this.scheduleType = scheduleType;
  }

  public boolean isDisabled() {
    return this.disabled;
  }

  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

  public int getActiveYear() {
    return this.activeYear;
  }

  public void setActiveYear(int activeYear) {
    this.activeYear = activeYear;
  }

  public int getActiveMonth() {
    return this.activeMonth;
  }

  public void setActiveMonth(int activeMonth) {
    this.activeMonth = activeMonth;
  }

  public int getActiveDay() {
    return this.activeDay;
  }

  public void setActiveDay(int activeDay) {
    this.activeDay = activeDay;
  }

  public int getActiveHour() {
    return this.activeHour;
  }

  public void setActiveHour(int activeHour) {
    this.activeHour = activeHour;
  }

  public int getActiveMinute() {
    return this.activeMinute;
  }

  public void setActiveMinute(int activeMinute) {
    this.activeMinute = activeMinute;
  }

  public int getActiveSecond() {
    return this.activeSecond;
  }

  public void setActiveSecond(int activeSecond) {
    this.activeSecond = activeSecond;
  }

  public String getActiveCron() {
    return this.activeCron;
  }

  public void setActiveCron(String activeCron) {
    this.activeCron = activeCron;
  }

  public int getInactiveYear() {
    return this.inactiveYear;
  }

  public void setInactiveYear(int inactiveYear) {
    this.inactiveYear = inactiveYear;
  }

  public int getInactiveMonth() {
    return this.inactiveMonth;
  }

  public void setInactiveMonth(int inactiveMonth) {
    this.inactiveMonth = inactiveMonth;
  }

  public int getInactiveDay() {
    return this.inactiveDay;
  }

  public void setInactiveDay(int inactiveDay) {
    this.inactiveDay = inactiveDay;
  }

  public int getInactiveHour() {
    return this.inactiveHour;
  }

  public void setInactiveHour(int inactiveHour) {
    this.inactiveHour = inactiveHour;
  }

  public int getInactiveMinute() {
    return this.inactiveMinute;
  }

  public void setInactiveMinute(int inactiveMinute) {
    this.inactiveMinute = inactiveMinute;
  }

  public int getInactiveSecond() {
    return this.inactiveSecond;
  }

  public void setInactiveSecond(int inactiveSecond) {
    this.inactiveSecond = inactiveSecond;
  }

  public String getInactiveCron() {
    return this.inactiveCron;
  }

  public void setInactiveCron(String inactiveCron) {
    this.inactiveCron = inactiveCron;
  }

  public String getDataSourceTypeId() {
    return this.dataSourceTypeId;
  }

  public void setDataSourceTypeId(String dataSourceTypeId) {
    this.dataSourceTypeId = dataSourceTypeId;
  }

  public String getDataSourceName() {
    return this.dataSourceName;
  }

  public void setDataSourceName(String dataSourceName) {
    this.dataSourceName = dataSourceName;
  }

  public String getDataSourceXid() {
    return this.dataSourceXid;
  }

  public void setDataSourceXid(String dataSourceXid) {
    this.dataSourceXid = dataSourceXid;
  }

  public EventTypeVO getEventType() {
    return new EventTypeVO("MAINTENANCE", null, this.id, 0, getDescription(), this.alarmLevel);
  }

  public TranslatableMessage getDescription()
  {
    TranslatableMessage message;
    if (!StringUtils.isBlank(this.alias)) {
      message = new TranslatableMessage("common.default", new Object[] { this.alias });
    }
    else
    {
      if (this.scheduleType == 1) {
        message = new TranslatableMessage("maintenanceEvents.schedule.manual", new Object[] { this.dataSourceName });
      }
      else
      {
        if (this.scheduleType == 7) {
          message = new TranslatableMessage("maintenanceEvents.schedule.onceUntil", new Object[] { this.dataSourceName, Functions.getTime(new DateTime(this.activeYear, this.activeMonth, this.activeDay, this.activeHour, this.activeMinute, this.activeSecond, 0).getMillis()), Functions.getTime(new DateTime(this.inactiveYear, this.inactiveMonth, this.inactiveDay, this.inactiveHour, this.inactiveMinute, this.inactiveSecond, 0).getMillis()) });
        }
        else
        {
          if (this.scheduleType == 2) {
            String activeTime = StringUtils.leftPad(Integer.toString(this.activeMinute), 2, '0') + ":" + StringUtils.leftPad(Integer.toString(this.activeSecond), 2, '0');

            message = new TranslatableMessage("maintenanceEvents.schedule.hoursUntil", new Object[] { this.dataSourceName, activeTime, StringUtils.leftPad(Integer.toString(this.inactiveMinute), 2, '0') + ":" + StringUtils.leftPad(Integer.toString(this.inactiveSecond), 2, '0') });
          }
          else
          {
            if (this.scheduleType == 3) {
              message = new TranslatableMessage("maintenanceEvents.schedule.dailyUntil", new Object[] { this.dataSourceName, activeTime(), inactiveTime() });
            }
            else
            {
              if (this.scheduleType == 4) {
                message = new TranslatableMessage("maintenanceEvents.schedule.weeklyUntil", new Object[] { this.dataSourceName, weekday(true), activeTime(), weekday(false), inactiveTime() });
              }
              else
              {
                if (this.scheduleType == 5) {
                  message = new TranslatableMessage("maintenanceEvents.schedule.monthlyUntil", new Object[] { this.dataSourceName, monthday(true), activeTime(), monthday(false), inactiveTime() });
                }
                else
                {
                  if (this.scheduleType == 6) {
                    message = new TranslatableMessage("maintenanceEvents.schedule.yearlyUntil", new Object[] { this.dataSourceName, monthday(true), month(true), activeTime(), monthday(false), month(false), inactiveTime() });
                  }
                  else
                  {
                    if (this.scheduleType == 8) {
                      message = new TranslatableMessage("maintenanceEvents.schedule.cronUntil", new Object[] { this.dataSourceName, this.activeCron, this.inactiveCron });
                    }
                    else
                      throw new ShouldNeverHappenException("Unknown schedule type: " + this.scheduleType);
                  }
                }
              }
            }
          }
        }
      }
    }
    return message;
  }

  private TranslatableMessage getTypeMessage() {
    switch (this.scheduleType) {
    case 1:
      return new TranslatableMessage("maintenanceEvents.type.manual");
    case 2:
      return new TranslatableMessage("maintenanceEvents.type.hour");
    case 3:
      return new TranslatableMessage("maintenanceEvents.type.day");
    case 4:
      return new TranslatableMessage("maintenanceEvents.type.week");
    case 5:
      return new TranslatableMessage("maintenanceEvents.type.month");
    case 6:
      return new TranslatableMessage("maintenanceEvents.type.year");
    case 7:
      return new TranslatableMessage("maintenanceEvents.type.once");
    case 8:
      return new TranslatableMessage("maintenanceEvents.type.cron");
    }
    return null;
  }

  private String activeTime() {
    return StringUtils.leftPad(Integer.toString(this.activeHour), 2, '0') + ":" + StringUtils.leftPad(Integer.toString(this.activeMinute), 2, '0') + ":" + StringUtils.leftPad(Integer.toString(this.activeSecond), 2, '0');
  }

  private String inactiveTime()
  {
    return StringUtils.leftPad(Integer.toString(this.inactiveHour), 2, '0') + ":" + StringUtils.leftPad(Integer.toString(this.inactiveMinute), 2, '0') + ":" + StringUtils.leftPad(Integer.toString(this.inactiveSecond), 2, '0');
  }

  private TranslatableMessage weekday(boolean active)
  {
    int day = this.activeDay;
    if (!active)
      day = this.inactiveDay;
    return new TranslatableMessage(weekdays[day]);
  }

  private TranslatableMessage monthday(boolean active) {
    int day = this.activeDay;

    if (!active) {
      day = this.inactiveDay;
    }
    if (day == -3)
      return new TranslatableMessage("common.day.thirdLast");
    if (day == -2)
      return new TranslatableMessage("common.day.secondLastLast");
    if (day == -1)
      return new TranslatableMessage("common.day.last");
    if ((day != 11) && (day % 10 == 1))
      return new TranslatableMessage("common.counting.st", new Object[] { Integer.toString(day) });
    if ((day != 12) && (day % 10 == 2))
      return new TranslatableMessage("common.counting.nd", new Object[] { Integer.toString(day) });
    if ((day != 13) && (day % 10 == 3))
      return new TranslatableMessage("common.counting.rd", new Object[] { Integer.toString(day) });
    return new TranslatableMessage("common.counting.th", new Object[] { Integer.toString(day) });
  }

  private TranslatableMessage month(boolean active)
  {
    int day = this.activeDay;
    if (!active)
      day = this.inactiveDay;
    return new TranslatableMessage(months[day]);
  }

  public String getTypeKey()
  {
    return "event.audit.maintenanceEvent";
  }

  public void validate(ProcessResult response) {
    if (StringValidation.isLengthGreaterThan(this.alias, 50)) {
      response.addContextualMessage("alias", "maintenanceEvents.validate.aliasTooLong", new Object[0]);
    }
    if (this.dataSourceId <= 0) {
      response.addContextualMessage("dataSourceId", "validate.invalidValue", new Object[0]);
    }

    if (this.scheduleType == 8) {
      try {
        new CronTimerTrigger(this.activeCron);
      }
      catch (Exception e) {
        response.addContextualMessage("activeCron", "maintenanceEvents.validate.activeCron", new Object[] { e.getMessage() });
      }
      try
      {
        new CronTimerTrigger(this.inactiveCron);
      }
      catch (Exception e) {
        response.addContextualMessage("inactiveCron", "maintenanceEvents.validate.inactiveCron", new Object[] { e.getMessage() });
      }

    }

    MaintenanceEventRT rt = new MaintenanceEventRT(this);
    try {
      rt.createTrigger(true);
    }
    catch (RuntimeException e) {
      response.addContextualMessage("activeCron", "maintenanceEvents.validate.activeTrigger", new Object[] { e.getMessage() });
    }
    try
    {
      rt.createTrigger(false);
    }
    catch (RuntimeException e) {
      response.addContextualMessage("inactiveCron", "maintenanceEvents.validate.inactiveTrigger", new Object[] { e.getMessage() });
    }

    if (this.scheduleType == 7) {
      DateTime adt = new DateTime(this.activeYear, this.activeMonth, this.activeDay, this.activeHour, this.activeMinute, this.activeSecond, 0);
      DateTime idt = new DateTime(this.inactiveYear, this.inactiveMonth, this.inactiveDay, this.inactiveHour, this.inactiveMinute, this.inactiveSecond, 0);

      if (idt.getMillis() <= adt.getMillis())
        response.addContextualMessage("scheduleType", "maintenanceEvents.validate.invalidRtn", new Object[0]);
    }
  }

  public void addProperties(List<TranslatableMessage> list)
  {
    AuditEventType.addPropertyMessage(list, "common.xid", this.xid);
    AuditEventType.addPropertyMessage(list, "maintenanceEvents.dataSource", Integer.valueOf(this.dataSourceId));
    AuditEventType.addPropertyMessage(list, "maintenanceEvents.alias", this.alias);
    AuditEventType.addPropertyMessage(list, "common.alarmLevel", AlarmLevels.getAlarmLevelMessage(this.alarmLevel));
    AuditEventType.addPropertyMessage(list, "maintenanceEvents.type", getTypeMessage());
    AuditEventType.addPropertyMessage(list, "common.disabled", this.disabled);
    AuditEventType.addPropertyMessage(list, "common.configuration", getDescription());
  }

  public void addPropertyChanges(List<TranslatableMessage> list, MaintenanceEventVO from)
  {
    AuditEventType.maybeAddPropertyChangeMessage(list, "common.xid", from.xid, this.xid);
    AuditEventType.maybeAddPropertyChangeMessage(list, "maintenanceEvents.dataSource", from.dataSourceId, this.dataSourceId);

    AuditEventType.maybeAddPropertyChangeMessage(list, "maintenanceEvents.alias", from.alias, this.alias);
    AuditEventType.maybeAddAlarmLevelChangeMessage(list, "common.alarmLevel", from.alarmLevel, this.alarmLevel);
    if (from.scheduleType != this.scheduleType) {
      AuditEventType.addPropertyChangeMessage(list, "maintenanceEvents.type", from.getTypeMessage(), getTypeMessage());
    }
    AuditEventType.maybeAddPropertyChangeMessage(list, "common.disabled", from.disabled, this.disabled);
    if ((from.activeYear != this.activeYear) || (from.activeMonth != this.activeMonth) || (from.activeDay != this.activeDay) || (from.activeHour != this.activeHour) || (from.activeMinute != this.activeMinute) || (from.activeSecond != this.activeSecond) || (from.activeCron != this.activeCron) || (from.inactiveYear != this.inactiveYear) || (from.inactiveMonth != this.inactiveMonth) || (from.inactiveDay != this.inactiveDay) || (from.inactiveHour != this.inactiveHour) || (from.inactiveMinute != this.inactiveMinute) || (from.inactiveSecond != this.inactiveSecond) || (from.inactiveCron != this.inactiveCron))
    {
      AuditEventType.maybeAddPropertyChangeMessage(list, "common.configuration", from.getDescription(), getDescription());
    }
  }

  public void jsonWrite(ObjectWriter writer)
    throws IOException, JsonException
  {
    writer.writeEntry("xid", this.xid);
    writer.writeEntry("dataSourceXid", this.dataSourceXid);
    writer.writeEntry("alarmLevel", AlarmLevels.CODES.getCode(this.alarmLevel));
    writer.writeEntry("scheduleType", TYPE_CODES.getCode(this.scheduleType));
  }

  public void jsonRead(JsonReader reader, JsonObject jsonObject) throws JsonException
  {
    String text = jsonObject.getString("dataSourceXid");
    if (text != null) {
      DataSourceVO ds = new DataSourceDao().getDataSource(text);
      if (ds == null)
        throw new TranslatableJsonException("emport.error.maintenanceEvent.invalid", new Object[] { "dataSourceXid", text });
      this.dataSourceId = ds.getId();
    }

    text = jsonObject.getString("alarmLevel");
    if (text != null) {
      this.alarmLevel = AlarmLevels.CODES.getId(text, new int[0]);
      if (!AlarmLevels.CODES.isValidId(this.alarmLevel, new int[0])) {
        throw new TranslatableJsonException("emport.error.maintenanceEvent.invalid", new Object[] { "alarmLevel", text, AlarmLevels.CODES.getCodeList(new int[0]) });
      }
    }

    text = jsonObject.getString("scheduleType");
    if (text != null) {
      this.scheduleType = TYPE_CODES.getId(text, new int[0]);
      if (!TYPE_CODES.isValidId(this.scheduleType, new int[0]))
        throw new TranslatableJsonException("emport.error.maintenanceEvent.invalid", new Object[] { "scheduleType", text, TYPE_CODES.getCodeList(new int[0]) });
    }
  }

  static
  {
    TYPE_CODES.addElement(1, "MANUAL", "maintenanceEvents.type.manual");
    TYPE_CODES.addElement(2, "HOURLY", "maintenanceEvents.type.hour");
    TYPE_CODES.addElement(3, "DAILY", "maintenanceEvents.type.day");
    TYPE_CODES.addElement(4, "WEEKLY", "maintenanceEvents.type.week");
    TYPE_CODES.addElement(5, "MONTHLY", "maintenanceEvents.type.month");
    TYPE_CODES.addElement(6, "YEARLY", "maintenanceEvents.type.year");
    TYPE_CODES.addElement(7, "ONCE", "maintenanceEvents.type.once");
    TYPE_CODES.addElement(8, "CRON", "maintenanceEvents.type.cron");

    weekdays = new String[] { "", "common.day.mon", "common.day.tue", "common.day.wed", "common.day.thu", "common.day.fri", "common.day.sat", "common.day.sun" };

    months = new String[] { "", "common.month.jan", "common.month.feb", "common.month.mar", "common.month.apr", "common.month.may", "common.month.jun", "common.month.jul", "common.month.aug", "common.month.sep", "common.month.oct", "common.month.nov", "common.month.dec" };
  }
}