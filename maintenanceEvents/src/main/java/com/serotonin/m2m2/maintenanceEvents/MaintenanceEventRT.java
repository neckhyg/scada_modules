package com.serotonin.m2m2.maintenanceEvents;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.util.timeout.ModelTimeoutClient;
import com.serotonin.m2m2.util.timeout.ModelTimeoutTask;
import com.serotonin.timer.CronTimerTrigger;
import com.serotonin.timer.OneTimeTrigger;
import com.serotonin.timer.TimerTask;
import com.serotonin.timer.TimerTrigger;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.util.Date;

public class MaintenanceEventRT implements ModelTimeoutClient<Boolean> {
    private static final String[] weekdays = {"", "MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
    private final MaintenanceEventVO vo;
    private MaintenanceEventType eventType;
    private boolean eventActive;
    private TimerTask activeTask;
    private TimerTask inactiveTask;

    public MaintenanceEventRT(MaintenanceEventVO vo) {
        this.vo = vo;
    }

    public MaintenanceEventVO getVo() {
        return this.vo;
    }

    private void raiseEvent(long time) {
        if (!this.eventActive) {
            Common.eventManager.raiseEvent(this.eventType, time, true, this.vo.getAlarmLevel(), getMessage(), null);
            this.eventActive = true;
        }
    }

    private void returnToNormal(long time) {
        if (this.eventActive) {
            Common.eventManager.returnToNormal(this.eventType, time);
            this.eventActive = false;
        }
    }

    public TranslatableMessage getMessage() {
        return new TranslatableMessage("event.maintenance.active", new Object[]{this.vo.getDescription()});
    }

    public boolean isEventActive() {
        return this.eventActive;
    }

    public boolean toggle() {
        scheduleTimeout(Boolean.valueOf(!this.eventActive), System.currentTimeMillis());
        return this.eventActive;
    }

    public synchronized void scheduleTimeout(Boolean active, long fireTime) {
        if (active.booleanValue())
            raiseEvent(fireTime);
        else
            returnToNormal(fireTime);
    }

    public void initialize() {
        this.eventType = new MaintenanceEventType(this.vo.getId());

        if (this.vo.getScheduleType() != 1) {
            TimerTrigger activeTrigger = createTrigger(true);
            this.activeTask = new ModelTimeoutTask(activeTrigger, this, Boolean.valueOf(true));

            TimerTrigger inactiveTrigger = createTrigger(false);
            this.inactiveTask = new ModelTimeoutTask(inactiveTrigger, this, Boolean.valueOf(false));

            if (this.vo.getScheduleType() != 7) {
                if (inactiveTrigger.getNextExecutionTime() < activeTrigger.getNextExecutionTime())
                    raiseEvent(System.currentTimeMillis());
            }
        }
    }

    public void terminate() {
        if (this.activeTask != null)
            this.activeTask.cancel();
        if (this.inactiveTask != null) {
            this.inactiveTask.cancel();
        }
        if (this.eventActive)
            Common.eventManager.returnToNormal(this.eventType, System.currentTimeMillis(), 4);
    }

    public void joinTermination() {
    }

    public TimerTrigger createTrigger(boolean activeTrigger) {
        if (this.vo.getScheduleType() == 1) {
            return null;
        }
        if (this.vo.getScheduleType() == 8) {
            try {
                if (activeTrigger)
                    return new CronTimerTrigger(this.vo.getActiveCron());
                return new CronTimerTrigger(this.vo.getInactiveCron());
            } catch (ParseException e) {
                throw new ShouldNeverHappenException(e);
            }
        }

        if (this.vo.getScheduleType() == 7) {
            DateTime dt;
            if (activeTrigger) {
                dt = new DateTime(this.vo.getActiveYear(), this.vo.getActiveMonth(), this.vo.getActiveDay(), this.vo.getActiveHour(), this.vo.getActiveMinute(), this.vo.getActiveSecond(), 0);
            } else {
                dt = new DateTime(this.vo.getInactiveYear(), this.vo.getInactiveMonth(), this.vo.getInactiveDay(), this.vo.getInactiveHour(), this.vo.getInactiveMinute(), this.vo.getInactiveSecond(), 0);
            }
            return new OneTimeTrigger(new Date(dt.getMillis()));
        }

        int month = this.vo.getActiveMonth();
        int day = this.vo.getActiveDay();
        int hour = this.vo.getActiveHour();
        int minute = this.vo.getActiveMinute();
        int second = this.vo.getActiveSecond();
        if (!activeTrigger) {
            month = this.vo.getInactiveMonth();
            day = this.vo.getInactiveDay();
            hour = this.vo.getInactiveHour();
            minute = this.vo.getInactiveMinute();
            second = this.vo.getInactiveSecond();
        }

        StringBuilder expression = new StringBuilder();
        expression.append(second).append(' ');
        expression.append(minute).append(' ');
        if (this.vo.getScheduleType() == 2) {
            expression.append("* * * ?");
        } else {
            expression.append(hour).append(' ');
            if (this.vo.getScheduleType() == 3) {
                expression.append("* * ?");
            } else if (this.vo.getScheduleType() == 4) {
                expression.append("? * ").append(weekdays[day]);
            } else {
                if (day > 0)
                    expression.append(day);
                else if (day == -1)
                    expression.append('L');
                else {
                    expression.append(-day).append('L');
                }
                if (this.vo.getScheduleType() == 5)
                    expression.append(" * ?");
                else
                    expression.append(' ').append(month).append(" ?");
            }
        }
        CronTimerTrigger cronTrigger;
        try {
            cronTrigger = new CronTimerTrigger(expression.toString());
        } catch (ParseException e) {
            throw new ShouldNeverHappenException(e);
        }
        return cronTrigger;
    }
}