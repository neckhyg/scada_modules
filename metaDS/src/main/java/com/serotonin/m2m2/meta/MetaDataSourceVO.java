package com.serotonin.m2m2.meta;

import com.serotonin.json.spi.JsonEntity;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.DataSourceRT;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;
import com.serotonin.m2m2.vo.event.EventTypeVO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

@JsonEntity
public class MetaDataSourceVO extends DataSourceVO<MetaDataSourceVO> {
    private static final ExportCodes EVENT_CODES = new ExportCodes();
    private static final long serialVersionUID = -1L;
    private static final int version = 1;

    public DataSourceRT createDataSourceRT() {
        return new MetaDataSourceRT(this);
    }

    protected void addEventTypes(List<EventTypeVO> ets) {
        ets.add(createEventType(1, new TranslatableMessage("event.ds.contextPoint")));

        ets.add(createEventType(2, new TranslatableMessage("event.ds.scriptError")));

        ets.add(createEventType(3, new TranslatableMessage("event.ds.resultType")));
    }

    public ExportCodes getEventCodes() {
        return EVENT_CODES;
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public TranslatableMessage getConnectionDescription() {
        return new TranslatableMessage("common.noMessage");
    }

    public MetaPointLocatorVO createPointLocator() {
        return new MetaPointLocatorVO();
    }

    protected void addPropertiesImpl(List<TranslatableMessage> list) {
    }

    protected void addPropertyChangesImpl(List<TranslatableMessage> list, MetaDataSourceVO from) {
    }

    private void writeObject(ObjectOutputStream out)
            throws IOException {
        out.writeInt(1);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        if (ver == 1) ;
    }

    static {
        EVENT_CODES.addElement(1, "CONTEXT_POINT_DISABLED");
        EVENT_CODES.addElement(2, "SCRIPT_ERROR");
        EVENT_CODES.addElement(3, "RESULT_TYPE_ERROR");
    }
}