package com.serotonin.m2m2.sql.publisher;

import com.serotonin.m2m2.rt.publish.SendThread;
import com.serotonin.m2m2.util.log.ProcessLog;

public class SqlSenderThread extends SendThread {
    final SqlSenderRT publisher;
    final ProcessLog log;

    public SqlSenderThread(SqlSenderRT publisher) {
        super("SqlSenderRT.SendThread");
        this.publisher = publisher;
        this.log = publisher.log;
    }

    @Override
    protected void runImpl() {
    }
}
