package com.serotonin.m2m2.sql;

import com.serotonin.m2m2.i18n.Translations;
import com.serotonin.m2m2.web.dwr.beans.TestingUtility;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqlStatementTester extends Thread
  implements TestingUtility
{
  private static final int MAX_ROWS = 50;
  private final Translations translations;
  private final String driverClassname;
  private final String connectionUrl;
  private final String username;
  private final String password;
  private final String selectStatement;
  private final boolean rowBasedQuery;
  private boolean done;
  private String errorMessage;
  private final List<List<String>> resultTable = new ArrayList();

  public SqlStatementTester(Translations translations, String driverClassname, String connectionUrl, String username, String password, String selectStatement, boolean rowBasedQuery)
  {
    this.translations = translations;
    this.driverClassname = driverClassname;
    this.connectionUrl = connectionUrl;
    this.username = username;
    this.password = password;
    this.selectStatement = selectStatement;
    this.rowBasedQuery = rowBasedQuery;
    start();
  }

  public void run()
  {
    Connection conn = null;
    try {
      DriverManager.registerDriver((Driver)Class.forName(this.driverClassname).newInstance());
      DriverManager.setLoginTimeout(5000);
      conn = DriverManager.getConnection(this.connectionUrl, this.username, this.password);
      PreparedStatement stmt = conn.prepareStatement(this.selectStatement);
      ResultSet rs = stmt.executeQuery();

      if (this.rowBasedQuery)
        getRowData(rs);
      else {
        getColumnData(rs);
      }
      rs.close();
    }
    catch (Exception e) {
      this.errorMessage = (e.getClass() + ": " + e.getMessage());
    }
    finally {
      try {
        if (conn != null)
          conn.close();
      }
      catch (SQLException e)
      {
      }
    }
    this.done = true;
  }

  public boolean isDone() {
    return this.done;
  }

  public String getErrorMessage() {
    return this.errorMessage;
  }

  public List<List<String>> getResultTable() {
    return this.resultTable;
  }

  public void cancel()
  {
  }

  private void getRowData(ResultSet rs) throws SQLException
  {
    ResultSetMetaData meta = rs.getMetaData();
    int columns = meta.getColumnCount();

    List row = new ArrayList();
    for (int i = 1; i <= columns; i++)
      row.add(meta.getColumnLabel(i) + " (" + meta.getColumnTypeName(i) + ")");
    this.resultTable.add(row);

    while (rs.next()) {
      row = new ArrayList();

      for (int i = 1; i <= columns; i++) {
        row.add(rs.getString(i));
      }
      this.resultTable.add(row);
      if (this.resultTable.size() <= 50)
        continue;
    }
  }

  private void getColumnData(ResultSet rs) throws SQLException
  {
    ResultSetMetaData meta = rs.getMetaData();
    int columns = meta.getColumnCount();

    boolean data = rs.next();

    List row = new ArrayList();
    row.add(this.translations.translate("dsEdit.sql.tester.columnName"));
    row.add(this.translations.translate("dsEdit.sql.tester.columnType"));
    row.add(this.translations.translate("dsEdit.sql.tester.value"));
    this.resultTable.add(row);

    for (int i = 1; i <= columns; i++) {
      row = new ArrayList();
      row.add(meta.getColumnLabel(i));
      row.add(meta.getColumnTypeName(i));
      String value;
      if (data)
        value = rs.getString(i);
      else {
        value = this.translations.translate("common.noData");
      }
      row.add(value);
      this.resultTable.add(row);
    }
  }
}