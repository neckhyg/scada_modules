
package com.serotonin.m2m2.watchlist;

import com.serotonin.m2m2.module.DwrConversionDefinition;

public class WatchListDwrConversionDefinition extends DwrConversionDefinition {
    @Override
    public void addConversions() {
        addConversion(WatchListState.class);
    }
}
