package com.serotonin.m2m2.opc;

import com.serotonin.m2m2.module.DwrConversionDefinition;

public class OPCDADwrConversionDefinition extends DwrConversionDefinition {
    public void addConversions() {
        addConversion(OPCItem.class);
    }
}