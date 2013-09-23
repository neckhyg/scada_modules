
package com.serotonin.m2m2.pointLinks;

import com.serotonin.m2m2.module.DwrConversionDefinition;

public class PointLinkDwrConversionDefinition extends DwrConversionDefinition {
    @Override
    public void addConversions() {
        addConversion(PointLinkVO.class);
    }
}
