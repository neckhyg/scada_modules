package com.eazytec.scada.main;

import com.serotonin.m2m2.module.DwrConversionDefinition;

public class RealtimeListDwrConversionDefinition extends DwrConversionDefinition {
    public void addConversions() {
        addConversion(RealtimeList.class);
    }
}
