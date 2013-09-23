
package com.serotonin.m2m2.scheduledEvents;

import com.serotonin.m2m2.module.DwrDefinition;
import com.serotonin.m2m2.web.dwr.ModuleDwr;

public class ScheduledEventDwrDefinition extends DwrDefinition {
    @Override
    public Class<? extends ModuleDwr> getDwrClass() {
        return ScheduledEventsDwr.class;
    }
}
