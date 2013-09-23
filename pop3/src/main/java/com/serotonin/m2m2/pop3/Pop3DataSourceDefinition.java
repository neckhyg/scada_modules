package com.serotonin.m2m2.pop3;

import com.serotonin.m2m2.module.DataSourceDefinition;
import com.serotonin.m2m2.module.ModuleRegistry;
import com.serotonin.m2m2.module.license.DataSourceTypePointsLimit;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;

public class Pop3DataSourceDefinition extends DataSourceDefinition {
    public void preInitialize() {
        ModuleRegistry.addLicenseEnforcement(new DataSourceTypePointsLimit(getModule().getName(), "POP3", 20, null));
    }

    public String getDataSourceTypeName() {
        return "POP3";
    }

    public String getDescriptionKey() {
        return "dsEdit.pop3";
    }

    protected DataSourceVO<?> createDataSourceVO() {
        return new Pop3DataSourceVO();
    }

    public String getEditPagePath() {
        return "web/editPop3.jsp";
    }

    public Class<?> getDwrClass() {
        return Pop3EditDwr.class;
    }
}