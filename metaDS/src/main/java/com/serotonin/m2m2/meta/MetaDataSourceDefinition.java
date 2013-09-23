package com.serotonin.m2m2.meta;

import com.serotonin.m2m2.module.DataSourceDefinition;
import com.serotonin.m2m2.module.ModuleRegistry;
import com.serotonin.m2m2.module.license.DataSourceTypePointsLimit;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;

public class MetaDataSourceDefinition extends DataSourceDefinition {
    public void preInitialize() {
        ModuleRegistry.addLicenseEnforcement(new DataSourceTypePointsLimit(getModule().getName(), "META", 5, null));
    }

    public String getDataSourceTypeName() {
        return "META";
    }

    public String getDescriptionKey() {
        return "dsEdit.meta";
    }

    protected DataSourceVO<?> createDataSourceVO() {
        return new MetaDataSourceVO();
    }

    public String getEditPagePath() {
        return "web/editMeta.jsp";
    }

    public Class<?> getDwrClass() {
        return MetaEditDwr.class;
    }

    public DataSourceDefinition.StartPriority getStartPriority() {
        return DataSourceDefinition.StartPriority.LAST;
    }
}