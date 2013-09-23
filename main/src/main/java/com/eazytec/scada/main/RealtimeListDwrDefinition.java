package com.eazytec.scada.main;

import com.serotonin.m2m2.module.DwrDefinition;
import com.serotonin.m2m2.web.dwr.ModuleDwr;

public class RealtimeListDwrDefinition extends DwrDefinition {
    public Class<? extends ModuleDwr> getDwrClass() {
        return RealtimeListDwr.class;
    }
}
