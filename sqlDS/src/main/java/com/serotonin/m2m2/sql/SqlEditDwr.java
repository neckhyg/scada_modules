package com.serotonin.m2m2.sql;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.vo.dataSource.BasicDataSourceVO;
import com.serotonin.m2m2.vo.permission.Permissions;
import com.serotonin.m2m2.web.dwr.DataSourceEditDwr;
import com.serotonin.m2m2.web.dwr.util.DwrPermission;
import java.util.HashMap;
import java.util.Map;

public class SqlEditDwr extends DataSourceEditDwr
{
  @DwrPermission(user=true)
  public ProcessResult saveSqlDataSource(BasicDataSourceVO basic, int updatePeriods, int updatePeriodType, String driverClassname, String connectionUrl, String username, String password, String selectStatement, boolean rowBasedQuery)
  {
    SqlDataSourceVO ds = (SqlDataSourceVO)Common.getUser().getEditDataSource();

    setBasicProps(ds, basic);
    ds.setUpdatePeriods(updatePeriods);
    ds.setUpdatePeriodType(updatePeriodType);
    ds.setDriverClassname(driverClassname);
    ds.setConnectionUrl(connectionUrl);
    ds.setUsername(username);
    ds.setPassword(password);
    ds.setSelectStatement(selectStatement);
    ds.setRowBasedQuery(rowBasedQuery);

    return tryDataSourceSave(ds);
  }
  @DwrPermission(user=true)
  public ProcessResult saveSqlPointLocator(int id, String xid, String name, SqlPointLocatorVO locator) {
    return validatePoint(id, xid, name, locator, null);
  }

  @DwrPermission(user=true)
  public void sqlTestStatement(String driverClassname, String connectionUrl, String username, String password, String selectStatement, boolean rowBasedQuery) {
    User user = Common.getUser();
    Permissions.ensureDataSourcePermission(user);
    user.setTestingUtility(new SqlStatementTester(getTranslations(), driverClassname, connectionUrl, username, password, selectStatement, rowBasedQuery));
  }

  @DwrPermission(user=true)
  public Map<String, Object> sqlTestStatementUpdate() {
    Map result = new HashMap();
    SqlStatementTester statementTester = (SqlStatementTester)Common.getUser().getTestingUtility(SqlStatementTester.class);
    if (statementTester == null)
      return null;
    if (!statementTester.isDone()) {
      return null;
    }
    if (statementTester.getErrorMessage() != null)
      result.put("error", statementTester.getErrorMessage());
    else
      result.put("resultTable", statementTester.getResultTable());
    return result;
  }
}