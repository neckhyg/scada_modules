package com.serotonin.m2m2.sql.publisher;

import com.serotonin.m2m2.rt.publish.PublisherRT;
import com.serotonin.m2m2.util.log.ProcessLog;
import com.serotonin.m2m2.vo.publish.PublisherVO;

public class SqlSenderRT extends PublisherRT<SqlPointVO>{
    final SqlSenderVO vo;
    final ProcessLog log;

    public SqlSenderRT(SqlSenderVO vo) {
        super(vo);
        this.vo = vo;
        this.log = new ProcessLog("SqlSenderRT-" + vo.getId(), ProcessLog.LogLevel.INFO);
    }

    @Override
    public void initialize() {
    }
}
