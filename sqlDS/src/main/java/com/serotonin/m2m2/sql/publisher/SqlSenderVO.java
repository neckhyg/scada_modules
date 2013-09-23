package com.serotonin.m2m2.sql.publisher;

import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.publish.PublisherRT;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.vo.event.EventTypeVO;
import com.serotonin.m2m2.vo.publish.PublisherVO;

import java.util.List;

public class SqlSenderVO extends PublisherVO<SqlPointVO>{
    public TranslatableMessage getConfigDescription() {
        return null;
    }

    protected SqlPointVO createPublishedPointInstance() {
        return null;
    }

    public PublisherRT<SqlPointVO> createPublisherRT() {
        return null;
    }

    protected void getEventTypesImpl(List<EventTypeVO> eventTypes) {
    }

    public ExportCodes getEventCodes() {
        return null;
    }
}
