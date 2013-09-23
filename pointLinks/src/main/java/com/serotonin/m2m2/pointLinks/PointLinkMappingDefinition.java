
package com.serotonin.m2m2.pointLinks;

import com.serotonin.m2m2.module.UrlMappingDefinition;
import com.serotonin.m2m2.web.mvc.UrlHandler;

public class PointLinkMappingDefinition extends UrlMappingDefinition {
    @Override
    public String getUrlPath() {
        return "/point_links.shtm";
    }

    @Override
    public UrlHandler getHandler() {
        return null;
    }

    @Override
    public String getJspPath() {
        return "web/pointLinks.jsp";
    }

    @Override
    public String getMenuKey() {
        return "header.pointLinks";
    }

    @Override
    public String getMenuImage() {
        return "web/pin_32.png";
    }

    @Override
    public Permission getPermission() {
        return Permission.DATA_SOURCE;
    }
}
