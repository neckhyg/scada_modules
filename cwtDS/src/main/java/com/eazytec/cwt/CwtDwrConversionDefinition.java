package com.eazytec.cwt;

import com.serotonin.m2m2.module.DwrConversionDefinition;

public class CwtDwrConversionDefinition extends DwrConversionDefinition {
    public void addConversions() {
        addConversion(CwtData.class);
    }
}
