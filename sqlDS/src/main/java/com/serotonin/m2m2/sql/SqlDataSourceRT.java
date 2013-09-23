package com.serotonin.m2m2.sql;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.io.StreamUtils;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.dataImage.SetPointSource;
import com.serotonin.m2m2.rt.dataImage.types.AlphanumericValue;
import com.serotonin.m2m2.rt.dataImage.types.BinaryValue;
import com.serotonin.m2m2.rt.dataImage.types.DataValue;
import com.serotonin.m2m2.rt.dataImage.types.ImageValue;
import com.serotonin.m2m2.rt.dataImage.types.MultistateValue;
import com.serotonin.m2m2.rt.dataImage.types.NumericValue;
import com.serotonin.m2m2.rt.dataSource.DataSourceRT;
import com.serotonin.m2m2.rt.dataSource.PollingDataSource;
import com.serotonin.m2m2.vo.DataPointVO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SqlDataSourceRT extends PollingDataSource
{
  public static final int DATA_SOURCE_EXCEPTION_EVENT = 1;
  public static final int STATEMENT_EXCEPTION_EVENT = 2;
  private final Log log = LogFactory.getLog(SqlDataSourceRT.class);
  private final SqlDataSourceVO vo;
  private Connection conn;

  public SqlDataSourceRT(SqlDataSourceVO vo)
  {
    super(vo);
    setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), false);
    this.vo = vo;
  }

  public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source)
  {
    if (this.conn == null) {
      return;
    }
    SqlPointLocatorVO locatorVO = ((SqlPointLocatorRT)dataPoint.getPointLocator()).getVO();

    PreparedStatement stmt = null;
    try {
      stmt = this.conn.prepareStatement(locatorVO.getUpdateStatement());

      if (locatorVO.getDataTypeId() == 4) {
        stmt.setString(1, valueTime.getStringValue());
      } else if (locatorVO.getDataTypeId() == 1) {
        stmt.setBoolean(1, valueTime.getBooleanValue());
      } else if (locatorVO.getDataTypeId() == 2) {
        stmt.setInt(1, valueTime.getIntegerValue());
      } else if (locatorVO.getDataTypeId() == 3) {
        stmt.setDouble(1, valueTime.getDoubleValue());
      } else if (locatorVO.getDataTypeId() == 5) {
        byte[] data = ((ImageValue)valueTime.getValue()).getImageData();
        stmt.setBlob(1, new ByteArrayInputStream(data), data.length);
      }
      else {
        throw new ShouldNeverHappenException("What's this?: " + locatorVO.getDataTypeId());
      }
      int rows = stmt.executeUpdate();
      if (rows == 0) {
        raiseEvent(2, valueTime.getTime(), false, new TranslatableMessage("event.sql.noRowsUpdated", new Object[] { dataPoint.getVO().getName() }));
      }
      else
      {
        dataPoint.setPointValue(valueTime, source);
      }
    } catch (IOException e) {
      raiseEvent(2, valueTime.getTime(), false, new TranslatableMessage("event.sql.setError", new Object[] { dataPoint.getVO().getName(), getExceptionMessage(e) }));
    }
    catch (SQLException e)
    {
      raiseEvent(2, valueTime.getTime(), false, new TranslatableMessage("event.sql.setError", new Object[] { dataPoint.getVO().getName(), getExceptionMessage(e) }));
    }
    finally
    {
      try {
        if (stmt != null)
          stmt.close();
      }
      catch (SQLException e) {
        throw new ShouldNeverHappenException(e);
      }
    }
  }

  protected void doPoll(long time)
  {
    if (this.conn == null) {
      return;
    }

    if (StringUtils.isBlank(this.vo.getSelectStatement())) {
      return;
    }
    PreparedStatement stmt = null;
    try
    {
      stmt = this.conn.prepareStatement(this.vo.getSelectStatement());
      if (this.vo.isRowBasedQuery())
        doRowPollImpl(time, stmt);
      else
        doColumnPollImpl(time, stmt);
    }
    catch (Exception e) {
      raiseEvent(2, time, true, getExceptionMessage(e));
    }
    finally {
      try {
        if (stmt != null)
          stmt.close();
      }
      catch (SQLException e)
      {
      }
    }
  }

  private void doColumnPollImpl(long time, PreparedStatement stmt) throws SQLException {
    ResultSet rs = stmt.executeQuery();
    ResultSetMetaData meta = rs.getMetaData();

    if (rs.next())
    {
      for (DataPointRT dp : this.dataPoints) {
        SqlPointLocatorRT locatorRT = (SqlPointLocatorRT)dp.getPointLocator();
        SqlPointLocatorVO locatorVO = locatorRT.getVO();

        String fieldName = locatorVO.getFieldName();
        if (!StringUtils.isBlank(fieldName)) {
          DataValue value;
          try {
            value = getValue(locatorVO, rs, fieldName, time);
          }
          catch (IOException e) {
            continue;
          } catch (SQLException e) {
              continue;
          }

          long pointTime = time;
          String timeOverride = locatorVO.getTimeOverrideName();
          if (!StringUtils.isBlank(timeOverride))
          {
            int column = -1;
            for (int i = 1; i <= meta.getColumnCount(); i++) {
              if (timeOverride.equalsIgnoreCase(meta.getColumnLabel(i))) {
                column = i;
                break;
              }
            }

            if (column == -1)
            {
              raiseEvent(2, time, true, new TranslatableMessage("event.sql.timeNotFound", new Object[] { timeOverride }));

              continue;
            }

            pointTime = getTimeOverride(meta, column, rs, time);
            if (pointTime == -1L) {
              continue;
            }
          }
          dp.updatePointValue(new PointValueTime(value, pointTime));
        }
      }
    }
    else {
      raiseEvent(2, time, true, new TranslatableMessage("event.sql.noData"));
    }
    rs.close();
  }

  private void doRowPollImpl(long time, PreparedStatement stmt) throws SQLException {
    ResultSet rs = stmt.executeQuery();
    ResultSetMetaData meta = rs.getMetaData();

    while (rs.next())
    {
      String rowId = rs.getString(1);

      boolean found = false;
      for (DataPointRT dp : this.dataPoints) {
        SqlPointLocatorRT locatorRT = (SqlPointLocatorRT)dp.getPointLocator();
        SqlPointLocatorVO locatorVO = locatorRT.getVO();
        String fieldName = locatorVO.getFieldName();

        if ((!StringUtils.isBlank(fieldName)) && (fieldName.equalsIgnoreCase(rowId))) {
          found = true;
          DataValue value;
          try {
            value = getValue(locatorVO, rs, meta.getColumnLabel(2), time);
          }
          catch (IOException e) {
            continue;
          } catch (SQLException e) {
              continue;
          }

          long pointTime = time;
          if (meta.getColumnCount() > 2) {
            pointTime = getTimeOverride(meta, 3, rs, time);
            if (pointTime == -1L) {
              continue;
            }
          }
          dp.updatePointValue(new PointValueTime(value, pointTime));
        }
      }

      if (!found) {
        raiseEvent(2, time, true, new TranslatableMessage("event.sql.noDataPoint", new Object[] { rowId }));
      }
    }

    rs.close();
  }

  private DataValue getValue(SqlPointLocatorVO locatorVO, ResultSet rs, String fieldName, long time) throws SQLException, IOException
  {
    try {
      int dataType = locatorVO.getDataTypeId();
      if (dataType == 4)
        return new AlphanumericValue(rs.getString(fieldName));
      if (dataType == 1)
        return new BinaryValue(rs.getBoolean(fieldName));
      if (dataType == 2)
        return new MultistateValue(rs.getInt(fieldName));
      if (dataType == 3)
        return new NumericValue(rs.getDouble(fieldName));
      if (dataType == 5) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        StreamUtils.transfer(rs.getBlob(fieldName).getBinaryStream(), out);
        return new ImageValue(out.toByteArray(), 1);
      }

      throw new ShouldNeverHappenException("What's this?: " + locatorVO.getDataTypeId());
    }
    catch (SQLException e)
    {
      raiseEvent(2, time, true, getExceptionMessage(e));
        throw e;
    }
  }

  private long getTimeOverride(ResultSetMetaData meta, int column, ResultSet rs, long time) throws SQLException
  {
    switch (meta.getColumnType(column)) {
    case -5:
    case -1:
      return rs.getLong(column);
    case 91:
      return rs.getDate(column).getTime();
    case 92:
      return rs.getTime(column).getTime();
    case 93:
      return rs.getTimestamp(column).getTime();
    }

    raiseEvent(2, time, true, new TranslatableMessage("event.sql.dataTypeNotSupported", new Object[] { meta.getColumnTypeName(column), Integer.valueOf(meta.getColumnType(column)) }));

    return -1L;
  }

  public void initialize()
  {
    try
    {
      DriverManager.registerDriver((Driver)Class.forName(this.vo.getDriverClassname()).newInstance());
      this.conn = DriverManager.getConnection(this.vo.getConnectionUrl(), this.vo.getUsername(), this.vo.getPassword());

      this.conn.getMetaData();

      returnToNormal(1, System.currentTimeMillis());
    }
    catch (Exception e) {
      raiseEvent(1, System.currentTimeMillis(), true, DataSourceRT.getExceptionMessage(e));

      this.log.info("Error while initializing data source", e);
      return;
    }

    super.initialize();
  }

  public void terminate()
  {
    super.terminate();
    try
    {
      if (this.conn != null)
        this.conn.close();
    }
    catch (SQLException e) {
      throw new ShouldNeverHappenException(e);
    }
  }
}