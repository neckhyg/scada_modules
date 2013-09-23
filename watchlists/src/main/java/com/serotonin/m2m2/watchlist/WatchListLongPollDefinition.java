
package com.serotonin.m2m2.watchlist;

import com.serotonin.m2m2.module.LongPollDefinition;
import com.serotonin.m2m2.web.dwr.longPoll.LongPollHandler;

public class WatchListLongPollDefinition extends LongPollDefinition {
    private WatchListLongPollHandler handler;

    @Override
    public void preInitialize() {
        super.preInitialize();
        WatchListDwr dwr = new WatchListDwr();
        dwr.setModule(getModule());
        handler = new WatchListLongPollHandler(dwr);
    }

    @Override
    public LongPollHandler getHandler() {
        return handler;
    }
}
