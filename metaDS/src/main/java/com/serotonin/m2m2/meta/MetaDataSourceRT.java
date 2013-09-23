package com.serotonin.m2m2.meta;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.dataImage.SetPointSource;
import com.serotonin.m2m2.rt.dataSource.DataSourceRT;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MetaDataSourceRT extends DataSourceRT {
    public static final int EVENT_TYPE_CONTEXT_POINT_DISABLED = 1;
    public static final int EVENT_TYPE_SCRIPT_ERROR = 2;
    public static final int EVENT_TYPE_RESULT_TYPE_ERROR = 3;
    private final List<DataPointRT> points = new CopyOnWriteArrayList();
    private boolean contextPointDisabledEventActive;

    public MetaDataSourceRT(MetaDataSourceVO vo) {
        super(vo);
    }

    public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source) {
        dataPoint.setPointValue(valueTime, source);
    }

    public void addDataPoint(DataPointRT dataPoint) {
        synchronized (this.pointListChangeLock) {
            remove(dataPoint);

            MetaPointLocatorRT locator = (MetaPointLocatorRT) dataPoint.getPointLocator();
            this.points.add(dataPoint);
            locator.initialize(Common.timer, this, dataPoint);
            checkForDisabledPoints();
        }
    }

    public void removeDataPoint(DataPointRT dataPoint) {
        synchronized (this.pointListChangeLock) {
            remove(dataPoint);
            checkForDisabledPoints();
        }
    }

    private void remove(DataPointRT dataPoint) {
        MetaPointLocatorRT locator = (MetaPointLocatorRT) dataPoint.getPointLocator();
        locator.terminate();
        this.points.remove(dataPoint);
    }

    synchronized void checkForDisabledPoints() {
        DataPointRT problemPoint = null;

        for (DataPointRT dp : this.points) {
            MetaPointLocatorRT locator = (MetaPointLocatorRT) dp.getPointLocator();
            if (!locator.isContextCreated()) {
                problemPoint = dp;
                break;
            }
        }

        if (this.contextPointDisabledEventActive != (problemPoint != null)) {
            this.contextPointDisabledEventActive = (problemPoint != null);
            if (this.contextPointDisabledEventActive) {
                raiseEvent(1, System.currentTimeMillis(), true, new TranslatableMessage("event.meta.pointUnavailable", new Object[]{problemPoint.getVO().getName()}));
            } else {
                returnToNormal(1, System.currentTimeMillis());
            }
        }
    }

    public void raiseScriptError(long runtime, DataPointRT dataPoint, TranslatableMessage message) {
        raiseEvent(2, runtime, false, new TranslatableMessage("event.meta.scriptError", new Object[]{dataPoint.getVO().getName(), message}));
    }

    public void raiseResultTypeError(long runtime, DataPointRT dataPoint, TranslatableMessage message) {
        raiseEvent(3, runtime, false, new TranslatableMessage("event.meta.typeError", new Object[]{dataPoint.getVO().getName(), message}));
    }
}