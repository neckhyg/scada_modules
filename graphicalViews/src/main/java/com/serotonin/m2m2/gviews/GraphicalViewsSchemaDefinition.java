
package com.serotonin.m2m2.gviews;

import java.io.File;
import java.util.List;

import com.serotonin.db.spring.ExtendedJdbcTemplate;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.gviews.component.ViewComponent;
import com.serotonin.m2m2.gviews.edit.ImageUploadServletDefinition;
import com.serotonin.m2m2.module.DatabaseSchemaDefinition;
import com.serotonin.util.DirectoryUtils;

public class GraphicalViewsSchemaDefinition extends DatabaseSchemaDefinition {
    static {
        Common.JSON_CONTEXT.addResolver(new ViewComponent.Resolver(), ViewComponent.class);
    }

    @Override
    public void newInstallationCheck(ExtendedJdbcTemplate ejt) {
        if (!Common.databaseProxy.tableExists(ejt, "graphicalViews")) {
            String path = Common.M2M2_HOME + getModule().getDirectoryPath() + "/web/db/createTables-"
                    + Common.databaseProxy.getType().name() + ".sql";
            Common.databaseProxy.runScriptFile(path, null);
        }
    }

    @Override
    public void addConversionTableNames(List<String> tableNames) {
        tableNames.add("graphicalViews");
        tableNames.add("graphicalViewUsers");
    }

    @Override
    public String getUpgradePackage() {
        return "com.serotonin.m2m2.gviews.upgrade";
    }

    @Override
    public void uninstall() {
        // Remove the database tables.
        String path = Common.M2M2_HOME + getModule().getDirectoryPath() + "/web/db/uninstall.sql";
        Common.databaseProxy.runScriptFile(path, null);

        // Remove the background image upload directory.
        DirectoryUtils.deleteDirectory(new File(ImageUploadServletDefinition.UPLOAD_DIR));
    }

    @Override
    public int getDatabaseSchemaVersion() {
        return 1;
    }
}
