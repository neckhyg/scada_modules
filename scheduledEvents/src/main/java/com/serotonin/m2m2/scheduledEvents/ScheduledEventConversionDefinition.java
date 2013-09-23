
package com.serotonin.m2m2.scheduledEvents;

import com.serotonin.m2m2.module.DwrConversionDefinition;

public class ScheduledEventConversionDefinition extends DwrConversionDefinition {
    @Override
    public void addConversions() {
        addConversion(ScheduledEventVO.class);
    }
}
