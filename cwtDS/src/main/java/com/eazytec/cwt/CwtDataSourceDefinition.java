package com.eazytec.cwt;

import com.serotonin.m2m2.module.DataSourceDefinition;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;

public class CwtDataSourceDefinition extends DataSourceDefinition {
    public String getDataSourceTypeName() {
        return "CWT";
    }

    public String getDescriptionKey() {
        return "dsEdit.cwt";
    }

    protected DataSourceVO<?> createDataSourceVO() {
        return new CwtDataSourceVO();
    }

    public String getEditPagePath() {
        return "web/editCwtDS.jspf";
    }

    public Class<?> getDwrClass() {
        return CwtEditDwr.class;
    }
}
