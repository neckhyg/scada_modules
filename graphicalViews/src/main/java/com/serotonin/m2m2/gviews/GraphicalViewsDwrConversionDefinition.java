
package com.serotonin.m2m2.gviews;

import com.serotonin.m2m2.gviews.component.CompoundChild;
import com.serotonin.m2m2.gviews.component.ViewComponent;
import com.serotonin.m2m2.module.DwrConversionDefinition;

public class GraphicalViewsDwrConversionDefinition extends DwrConversionDefinition {
    @Override
    public void addConversions() {
        addConversion(GraphicalView.class);
        addConversion(ViewComponentState.class);
        addConversion(ViewComponent.class);
        addConversion(CompoundChild.class);
    }
}
