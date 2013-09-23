package com.serotonin.m2m2.snmp.vo;

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
import com.serotonin.m2m2.snmp.rt.SnmpDataSourceRT;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;
import com.serotonin.m2m2.vo.event.EventTypeVO;
import com.serotonin.util.SerializationHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class SnmpDataSourceVO extends DataSourceVO<SnmpDataSourceVO>
{
  private static final ExportCodes EVENT_CODES = new ExportCodes();

  @JsonProperty
  private String host;

  @JsonProperty
  private int port = 161;

  @JsonProperty
  private int snmpVersion;

  @JsonProperty
  private String community;

  @JsonProperty
  private String engineId;

  @JsonProperty
  private String contextEngineId;

  @JsonProperty
  private String contextName;

  @JsonProperty
  private String securityName;

  @JsonProperty
  private String authProtocol;

  @JsonProperty
  private String authPassphrase;

  @JsonProperty
  private String privProtocol;

  @JsonProperty
  private String privPassphrase;

  @JsonProperty
  private int retries = 2;

  @JsonProperty
  private int timeout = 1000;

  private int updatePeriodType = 2;

  @JsonProperty
  private int updatePeriods = 5;

  @JsonProperty
  private int trapPort = 162;

  @JsonProperty
  private String localAddress;
  private static final long serialVersionUID = -1L;
  private static final int version = 2;

  protected void addEventTypes(List<EventTypeVO> ets)
  {
    ets.add(createEventType(1, new TranslatableMessage("event.ds.dataSource")));

    ets.add(createEventType(2, new TranslatableMessage("event.ds.pdu")));
  }

  public ExportCodes getEventCodes()
  {
    return EVENT_CODES;
  }

  public TranslatableMessage getConnectionDescription()
  {
    return new TranslatableMessage("common.default", new Object[] { this.host });
  }

  public DataSourceRT createDataSourceRT()
  {
    return new SnmpDataSourceRT(this);
  }

  public SnmpPointLocatorVO createPointLocator()
  {
    return new SnmpPointLocatorVO();
  }

  public String getAuthPassphrase()
  {
    return this.authPassphrase;
  }

  public void setAuthPassphrase(String authPassphrase) {
    this.authPassphrase = authPassphrase;
  }

  public String getAuthProtocol() {
    return this.authProtocol;
  }

  public void setAuthProtocol(String authProtocol) {
    this.authProtocol = authProtocol;
  }

  public String getCommunity() {
    return this.community;
  }

  public void setCommunity(String community) {
    this.community = community;
  }

  public String getContextEngineId() {
    return this.contextEngineId;
  }

  public void setContextEngineId(String contextEngineId) {
    this.contextEngineId = contextEngineId;
  }

  public String getContextName() {
    return this.contextName;
  }

  public void setContextName(String contextName) {
    this.contextName = contextName;
  }

  public String getEngineId() {
    return this.engineId;
  }

  public void setEngineId(String engineId) {
    this.engineId = engineId;
  }

  public String getHost() {
    return this.host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getPrivPassphrase() {
    return this.privPassphrase;
  }

  public void setPrivPassphrase(String privPassphrase) {
    this.privPassphrase = privPassphrase;
  }

  public String getPrivProtocol() {
    return this.privProtocol;
  }

  public void setPrivProtocol(String privProtocol) {
    this.privProtocol = privProtocol;
  }

  public String getSecurityName() {
    return this.securityName;
  }

  public void setSecurityName(String securityName) {
    this.securityName = securityName;
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

  public int getSnmpVersion() {
    return this.snmpVersion;
  }

  public void setSnmpVersion(int snmpVersion) {
    this.snmpVersion = snmpVersion;
  }

  public int getPort() {
    return this.port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public int getRetries() {
    return this.retries;
  }

  public void setRetries(int retries) {
    this.retries = retries;
  }

  public int getTimeout() {
    return this.timeout;
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }

  public int getTrapPort() {
    return this.trapPort;
  }

  public void setTrapPort(int trapPort) {
    this.trapPort = trapPort;
  }

  public String getLocalAddress() {
    return this.localAddress;
  }

  public void setLocalAddress(String localAddress) {
    this.localAddress = localAddress;
  }

  public void validate(ProcessResult response)
  {
    super.validate(response);
    if (!Common.TIME_PERIOD_CODES.isValidId(this.updatePeriodType, new int[0]))
      response.addContextualMessage("updatePeriodType", "validate.invalidValue", new Object[0]);
    if (this.updatePeriods <= 0) {
      response.addContextualMessage("updatePeriods", "validate.greaterThanZero", new Object[0]);
    }
    if ((this.port <= 0) || (this.port > 65535)) {
      response.addContextualMessage("port", "validate.invalidValue", new Object[0]);
    }
    if ((this.trapPort <= 0) || (this.trapPort > 65535)) {
      response.addContextualMessage("trapPort", "validate.invalidValue", new Object[0]);
    }
    if (StringUtils.isBlank(this.host))
      response.addContextualMessage("host", "validate.required", new Object[0]);
    try
    {
      InetAddress.getByName(this.host);
    }
    catch (UnknownHostException e) {
      response.addContextualMessage("host", "validate.invalidValue", new Object[0]);
    }

    if ((this.snmpVersion != 0) && (this.snmpVersion != 1) && (this.snmpVersion != 3))
    {
      response.addContextualMessage("snmpVersion", "validate.invalidValue", new Object[0]);
    }if (this.timeout <= 0)
      response.addContextualMessage("timeout", "validate.greaterThanZero", new Object[0]);
    if (this.retries < 0)
      response.addContextualMessage("retries", "validate.cannotBeNegative", new Object[0]);
  }

  protected void addPropertiesImpl(List<TranslatableMessage> list)
  {
    AuditEventType.addPeriodMessage(list, "dsEdit.updatePeriod", this.updatePeriodType, this.updatePeriods);
    AuditEventType.addPropertyMessage(list, "dsEdit.snmp.host", this.host);
    AuditEventType.addPropertyMessage(list, "dsEdit.snmp.port", Integer.valueOf(this.port));
    AuditEventType.addPropertyMessage(list, "dsEdit.snmp.version", Integer.valueOf(this.snmpVersion));
    AuditEventType.addPropertyMessage(list, "dsEdit.snmp.community", this.community);
    AuditEventType.addPropertyMessage(list, "dsEdit.snmp.securityName", this.securityName);
    AuditEventType.addPropertyMessage(list, "dsEdit.snmp.authProtocol", this.authProtocol);
    AuditEventType.addPropertyMessage(list, "dsEdit.snmp.authPassphrase", this.authPassphrase);
    AuditEventType.addPropertyMessage(list, "dsEdit.snmp.privProtocol", this.privProtocol);
    AuditEventType.addPropertyMessage(list, "dsEdit.snmp.privPassphrase", this.privPassphrase);
    AuditEventType.addPropertyMessage(list, "dsEdit.snmp.engineId", this.engineId);
    AuditEventType.addPropertyMessage(list, "dsEdit.snmp.contextEngine", this.contextEngineId);
    AuditEventType.addPropertyMessage(list, "dsEdit.snmp.contextName", this.contextName);
    AuditEventType.addPropertyMessage(list, "dsEdit.snmp.retries", Integer.valueOf(this.retries));
    AuditEventType.addPropertyMessage(list, "dsEdit.snmp.timeout", Integer.valueOf(this.timeout));
    AuditEventType.addPropertyMessage(list, "dsEdit.snmp.trapPort", Integer.valueOf(this.trapPort));
    AuditEventType.addPropertyMessage(list, "dsEdit.snmp.localAddress", this.localAddress);
  }

  protected void addPropertyChangesImpl(List<TranslatableMessage> list, SnmpDataSourceVO from)
  {
    AuditEventType.maybeAddPeriodChangeMessage(list, "dsEdit.updatePeriod", from.updatePeriodType, from.updatePeriods, this.updatePeriodType, this.updatePeriods);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.host", from.host, this.host);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.port", from.port, this.port);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.version", from.snmpVersion, this.snmpVersion);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.community", from.community, this.community);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.securityName", from.securityName, this.securityName);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.authProtocol", from.authProtocol, this.authProtocol);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.authPassphrase", from.authPassphrase, this.authPassphrase);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.privProtocol", from.privProtocol, this.privProtocol);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.privPassphrase", from.privPassphrase, this.privPassphrase);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.engineId", from.engineId, this.engineId);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.contextEngine", from.contextEngineId, this.contextEngineId);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.contextName", from.contextName, this.contextName);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.retries", from.retries, this.retries);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.timeout", from.timeout, this.timeout);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.trapPort", from.trapPort, this.trapPort);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.localAddress", from.localAddress, this.localAddress);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(2);
    SerializationHelper.writeSafeUTF(out, this.host);
    out.writeInt(this.port);
    out.writeInt(this.snmpVersion);
    SerializationHelper.writeSafeUTF(out, this.community);
    SerializationHelper.writeSafeUTF(out, this.engineId);
    SerializationHelper.writeSafeUTF(out, this.contextEngineId);
    SerializationHelper.writeSafeUTF(out, this.contextName);
    SerializationHelper.writeSafeUTF(out, this.securityName);
    SerializationHelper.writeSafeUTF(out, this.authProtocol);
    SerializationHelper.writeSafeUTF(out, this.authPassphrase);
    SerializationHelper.writeSafeUTF(out, this.privProtocol);
    SerializationHelper.writeSafeUTF(out, this.privPassphrase);
    out.writeInt(this.retries);
    out.writeInt(this.timeout);
    out.writeInt(this.updatePeriodType);
    out.writeInt(this.updatePeriods);
    out.writeInt(this.trapPort);
    SerializationHelper.writeSafeUTF(out, this.localAddress);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.host = SerializationHelper.readSafeUTF(in);
      this.port = in.readInt();
      this.snmpVersion = in.readInt();
      this.community = SerializationHelper.readSafeUTF(in);
      this.engineId = SerializationHelper.readSafeUTF(in);
      this.contextEngineId = SerializationHelper.readSafeUTF(in);
      this.contextName = SerializationHelper.readSafeUTF(in);
      this.securityName = SerializationHelper.readSafeUTF(in);
      this.authProtocol = SerializationHelper.readSafeUTF(in);
      this.authPassphrase = SerializationHelper.readSafeUTF(in);
      this.privProtocol = SerializationHelper.readSafeUTF(in);
      this.privPassphrase = SerializationHelper.readSafeUTF(in);
      this.retries = in.readInt();
      this.timeout = in.readInt();
      this.updatePeriodType = in.readInt();
      this.updatePeriods = in.readInt();
      this.trapPort = in.readInt();
      this.localAddress = "";
    }
    else if (ver == 2) {
      this.host = SerializationHelper.readSafeUTF(in);
      this.port = in.readInt();
      this.snmpVersion = in.readInt();
      this.community = SerializationHelper.readSafeUTF(in);
      this.engineId = SerializationHelper.readSafeUTF(in);
      this.contextEngineId = SerializationHelper.readSafeUTF(in);
      this.contextName = SerializationHelper.readSafeUTF(in);
      this.securityName = SerializationHelper.readSafeUTF(in);
      this.authProtocol = SerializationHelper.readSafeUTF(in);
      this.authPassphrase = SerializationHelper.readSafeUTF(in);
      this.privProtocol = SerializationHelper.readSafeUTF(in);
      this.privPassphrase = SerializationHelper.readSafeUTF(in);
      this.retries = in.readInt();
      this.timeout = in.readInt();
      this.updatePeriodType = in.readInt();
      this.updatePeriods = in.readInt();
      this.trapPort = in.readInt();
      this.localAddress = SerializationHelper.readSafeUTF(in);
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
    EVENT_CODES.addElement(2, "PDU_EXCEPTION");
  }

  public static abstract interface PrivProtocols
  {
    public static final String NONE = "";
    public static final String DES = "DES";
    public static final String AES128 = "AES128";
    public static final String AES192 = "AES192";
    public static final String AES256 = "AES256";
  }

  public static abstract interface AuthProtocols
  {
    public static final String NONE = "";
    public static final String MD5 = "MD5";
    public static final String SHA = "SHA";
  }
}