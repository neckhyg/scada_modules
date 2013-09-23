
package com.serotonin.m2m2.internal;

import com.serotonin.m2m2.module.DataSourceDefinition;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;

public class InternalDataSourceDefinition extends DataSourceDefinition {
    @Override
    public String getDataSourceTypeName() {
        return "INTERNAL";
    }

    @Override
    public String getDescriptionKey() {
        return "dsEdit.internal";
    }

    @Override
    protected DataSourceVO<?> createDataSourceVO() {
        return new InternalDataSourceVO();
    }

    @Override
    public String getEditPagePath() {
        return "web/editInternal.jsp";
    }

    @Override
    public Class<?> getDwrClass() {
        return InternalEditDwr.class;
    }
}
