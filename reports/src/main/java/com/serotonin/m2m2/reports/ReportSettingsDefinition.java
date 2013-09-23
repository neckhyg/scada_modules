
package com.serotonin.m2m2.reports;

import com.serotonin.m2m2.module.SystemSettingsDefinition;

public class ReportSettingsDefinition extends SystemSettingsDefinition {
    @Override
    public String getDescriptionKey() {
        return "header.reports";
    }

    @Override
    public String getSectionJspPath() {
        return "web/settings.jspf";
    }
}
