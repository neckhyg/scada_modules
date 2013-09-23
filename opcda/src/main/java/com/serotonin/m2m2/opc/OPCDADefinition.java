package com.serotonin.m2m2.opc;

import com.serotonin.m2m2.module.DataSourceDefinition;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;

public class OPCDADefinition extends DataSourceDefinition {
    public String getDataSourceTypeName() {
        return "OPC DA";
    }

    public String getDescriptionKey() {
        return "dsEdit.opc";
    }

    protected DataSourceVO<?> createDataSourceVO() {
        return new OPCDataSourceVO();
    }

    public String getEditPagePath() {
        return "web/editOpc.jspf";
    }

    public Class<?> getDwrClass() {
        return OPCDAEditDwr.class;
    }
}