package com.serotonin.m2m2.sql.publisher;

import com.serotonin.m2m2.module.PublisherDefinition;
import com.serotonin.m2m2.vo.publish.PublishedPointVO;
import com.serotonin.m2m2.vo.publish.PublisherVO;

public class SqlPublisherDefinition extends PublisherDefinition{
    @Override
    public String getPublisherTypeName() {
        return null;
    }

    @Override
    public String getDescriptionKey() {
        return "publisherEdit.sql";
    }

    @Override
    protected PublisherVO<? extends PublishedPointVO> createPublisherVO() {
        return new SqlSenderVO();
    }

    @Override
    public String getEditPagePath() {
        return "web/editSqlPub.jspf";
    }

    @Override
    public Class<?> getDwrClass() {
        return SqlPublisherDwr.class;
    }
}
