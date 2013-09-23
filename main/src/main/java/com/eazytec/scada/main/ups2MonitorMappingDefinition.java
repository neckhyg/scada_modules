package com.eazytec.scada.main;

import com.serotonin.m2m2.module.UriMappingDefinition;
import com.serotonin.m2m2.web.mvc.UrlHandler;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-9-23
 * Time: 上午9:31
 * To change this template use File | Settings | File Templates.
 */
public class ups2MonitorMappingDefinition extends UriMappingDefinition {
    public UriMappingDefinition.Permission getPermission()
    {
        return UriMappingDefinition.Permission.USER;
    }

    public String getPath()
    {
        return "/usp2Monitor.shtm";
    }

    public UrlHandler getHandler()
    {
        return new ups2MonitorViewHandler();
    }

    public String getJspPath()
    {
        return "web/ups2Monitor.jsp";
    }
}
