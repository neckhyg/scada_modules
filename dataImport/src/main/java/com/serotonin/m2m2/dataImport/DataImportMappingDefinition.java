package com.serotonin.m2m2.dataImport;

import com.serotonin.m2m2.module.UrlMappingDefinition;
import com.serotonin.m2m2.web.mvc.UrlHandler;

public class DataImportMappingDefinition extends UrlMappingDefinition {
    public String getUrlPath() {
        return "/dataImport.shtm";
    }

    public UrlHandler getHandler() {
        return new DataImportController();
    }

    public String getJspPath() {
        return "web/dataImport.jsp";
    }

    public String getMenuKey() {
        return "dataImport.header";
    }

    public String getMenuImage() {
        return "web/csv_32.png";
    }

    public UrlMappingDefinition.Permission getPermission() {
        return UrlMappingDefinition.Permission.ADMINISTRATOR;
    }
}