package com.eazytec.scada.main;

import com.serotonin.m2m2.module.UriMappingDefinition;
import com.serotonin.m2m2.web.mvc.UrlHandler;

public class RealtimeMappingDefinition extends UriMappingDefinition {
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    public String getPath() {
        return "/realtime.shtm";
    }

    @Override
    public UrlHandler getHandler() {
        return new RealtimeHandler();
    }

    @Override
    public String getJspPath() {
        return "web/realtime.jsp";
    }
}
