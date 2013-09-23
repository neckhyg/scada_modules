package com.serotonin.m2m2.sql;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.DataSourceRT;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;
import com.serotonin.m2m2.vo.event.EventTypeVO;
import com.serotonin.util.SerializationHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class SqlDataSourceVO extends DataSourceVO<SqlDataSourceVO>
{
  private static final ExportCodes EVENT_CODES = new ExportCodes();

  @JsonProperty
  private String driverClassname;

  @JsonProperty
  private String connectionUrl;

  @JsonProperty
  private String username;

  @JsonProperty
  private String password;

  @JsonProperty
  private String selectStatement;
  private int updatePeriodType = 2;

  @JsonProperty
  private int updatePeriods = 5;

  @JsonProperty
  private boolean rowBasedQuery = false;
  private static final long serialVersionUID = -1L;
  private static final int version = 2;

  protected void addEventTypes(List<EventTypeVO> ets)
  {
    ets.add(createEventType(1, new TranslatableMessage("event.ds.dataSource")));

    ets.add(createEventType(2, new TranslatableMessage("event.ds.statement")));
  }

  public ExportCodes getEventCodes()
  {
    return EVENT_CODES;
  }

  public TranslatableMessage getConnectionDescription()
  {
    return new TranslatableMessage("common.default", new Object[] { this.connectionUrl });
  }

  public DataSourceRT createDataSourceRT()
  {
    return new SqlDataSourceRT(this);
  }

  public SqlPointLocatorVO createPointLocator()
  {
    return new SqlPointLocatorVO();
  }

  public String getDriverClassname()
  {
    return this.driverClassname;
  }

  public void setDriverClassname(String driverClassname) {
    this.driverClassname = driverClassname;
  }

  public String getConnectionUrl() {
    return this.connectionUrl;
  }

  public void setConnectionUrl(String connectionUrl) {
    this.connectionUrl = connectionUrl;
  }

  public int getUpdatePeriods() {
    return this.updatePeriods;
  }

  public void setUpdatePeriods(int updatePeriods) {
    this.updatePeriods = updatePeriods;
  }

  public int getUpdatePeriodType() {
    return this.updatePeriodType;
  }

  public void setUpdatePeriodType(int updatePeriodType) {
    this.updatePeriodType = updatePeriodType;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getSelectStatement() {
    return this.selectStatement;
  }

  public void setSelectStatement(String selectStatement) {
    this.selectStatement = selectStatement;
  }

  public boolean isRowBasedQuery() {
    return this.rowBasedQuery;
  }

  public void setRowBasedQuery(boolean rowBasedQuery) {
    this.rowBasedQuery = rowBasedQuery;
  }

  public void validate(ProcessResult response)
  {
    super.validate(response);
    if (!Common.TIME_PERIOD_CODES.isValidId(this.updatePeriodType, new int[0]))
      response.addContextualMessage("updatePeriodType", "validate.invalidValue", new Object[0]);
    if (this.updatePeriods <= 0)
      response.addContextualMessage("updatePeriods", "validate.greaterThanZero", new Object[0]);
    if (StringUtils.isBlank(this.driverClassname))
      response.addContextualMessage("driverClassname", "validate.required", new Object[0]);
    if (StringUtils.isBlank(this.connectionUrl))
      response.addContextualMessage("connectionUrl", "validate.required", new Object[0]);
  }

  protected void addPropertiesImpl(List<TranslatableMessage> list)
  {
    AuditEventType.addPeriodMessage(list, "dsEdit.updatePeriod", this.updatePeriodType, this.updatePeriods);
    AuditEventType.addPropertyMessage(list, "dsEdit.sql.driverClassName", this.driverClassname);
    AuditEventType.addPropertyMessage(list, "dsEdit.sql.connectionString", this.connectionUrl);
    AuditEventType.addPropertyMessage(list, "dsEdit.sql.username", this.username);
    AuditEventType.addPropertyMessage(list, "dsEdit.sql.password", this.password);
    AuditEventType.addPropertyMessage(list, "dsEdit.sql.select", this.selectStatement);
    AuditEventType.addPropertyMessage(list, "dsEdit.sql.rowQuery", this.rowBasedQuery);
  }

  protected void addPropertyChangesImpl(List<TranslatableMessage> list, SqlDataSourceVO from)
  {
    AuditEventType.maybeAddPeriodChangeMessage(list, "dsEdit.updatePeriod", from.updatePeriodType, from.updatePeriods, this.updatePeriodType, this.updatePeriods);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.sql.driverClassName", from.driverClassname, this.driverClassname);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.sql.connectionString", from.connectionUrl, this.connectionUrl);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.sql.username", from.username, this.username);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.sql.password", from.password, this.password);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.sql.select", from.selectStatement, this.selectStatement);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.sql.rowQuery", from.rowBasedQuery, this.rowBasedQuery);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(2);
    SerializationHelper.writeSafeUTF(out, this.driverClassname);
    SerializationHelper.writeSafeUTF(out, this.connectionUrl);
    SerializationHelper.writeSafeUTF(out, this.username);
    SerializationHelper.writeSafeUTF(out, this.password);
    SerializationHelper.writeSafeUTF(out, this.selectStatement);
    out.writeInt(this.updatePeriodType);
    out.writeInt(this.updatePeriods);
    out.writeBoolean(this.rowBasedQuery);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.driverClassname = SerializationHelper.readSafeUTF(in);
      this.connectionUrl = SerializationHelper.readSafeUTF(in);
      this.username = SerializationHelper.readSafeUTF(in);
      this.password = SerializationHelper.readSafeUTF(in);
      this.selectStatement = SerializationHelper.readSafeUTF(in);
      this.updatePeriodType = in.readInt();
      this.updatePeriods = in.readInt();
      this.rowBasedQuery = false;
    }
    else if (ver == 2) {
      this.driverClassname = SerializationHelper.readSafeUTF(in);
      this.connectionUrl = SerializationHelper.readSafeUTF(in);
      this.username = SerializationHelper.readSafeUTF(in);
      this.password = SerializationHelper.readSafeUTF(in);
      this.selectStatement = SerializationHelper.readSafeUTF(in);
      this.updatePeriodType = in.readInt();
      this.updatePeriods = in.readInt();
      this.rowBasedQuery = in.readBoolean();
    }
  }

  public void jsonWrite(ObjectWriter writer) throws IOException, JsonException
  {
    super.jsonWrite(writer);
    writeUpdatePeriodType(writer, this.updatePeriodType);
  }

  public void jsonRead(JsonReader reader, JsonObject jsonObject) throws JsonException
  {
    super.jsonRead(reader, jsonObject);
    Integer value = readUpdatePeriodType(jsonObject);
    if (value != null)
      this.updatePeriodType = value.intValue();
  }

  static
  {
    EVENT_CODES.addElement(1, "DATA_SOURCE_EXCEPTION");
    EVENT_CODES.addElement(2, "STATEMENT_EXCEPTION");
  }
}