package com.serotonin.m2m2.globalScripts;

import com.serotonin.db.spring.ExtendedJdbcTemplate;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.db.DatabaseProxy;
import com.serotonin.m2m2.db.DatabaseProxy.DatabaseType;
import com.serotonin.m2m2.module.DatabaseSchemaDefinition;
import com.serotonin.m2m2.module.Module;
import java.util.List;

public class GlobalScriptsSchema extends DatabaseSchemaDefinition
{
  public void newInstallationCheck(ExtendedJdbcTemplate ejt)
  {
    if (!Common.databaseProxy.tableExists(ejt, "globalScripts")) {
      String path = Common.M2M2_HOME + getModule().getDirectoryPath() + "/web/db/createTables-" + Common.databaseProxy.getType().name() + ".sql";

      Common.databaseProxy.runScriptFile(path, null);
    }
  }

  public void addConversionTableNames(List<String> tableNames)
  {
    tableNames.add("globalScripts");
  }

  public String getUpgradePackage()
  {
    return "com.serotonin.m2m2.globalScripts.upgrade";
  }

  public int getDatabaseSchemaVersion()
  {
    return 1;
  }

  public void uninstall()
  {
    String path = Common.M2M2_HOME + getModule().getDirectoryPath() + "/web/db/uninstall.sql";
    Common.databaseProxy.runScriptFile(path, null);
  }
}