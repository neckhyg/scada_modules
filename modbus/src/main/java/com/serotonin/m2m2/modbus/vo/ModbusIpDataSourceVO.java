package com.serotonin.m2m2.modbus.vo;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableJsonException;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.modbus.rt.ModbusIpDataSourceRT;
import com.serotonin.m2m2.rt.dataSource.DataSourceRT;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.util.SerializationHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.directwebremoting.annotations.DataTransferObject;

@DataTransferObject(params={@org.directwebremoting.annotations.Param(name="exclude", value="transportType")})
public class ModbusIpDataSourceVO extends ModbusDataSourceVO<ModbusIpDataSourceVO>
{
  private TransportType transportType;

  @JsonProperty
  private String host;

  @JsonProperty
  private int port = 502;

  @JsonProperty
  private boolean encapsulated;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  public TranslatableMessage getConnectionDescription()
  {
    return new TranslatableMessage("common.default", new Object[] { this.host + ":" + this.port });
  }

  public DataSourceRT createDataSourceRT()
  {
    return new ModbusIpDataSourceRT(this);
  }

  public String getHost()
  {
    return this.host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return this.port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public boolean isEncapsulated() {
    return this.encapsulated;
  }

  public void setEncapsulated(boolean encapsulated) {
    this.encapsulated = encapsulated;
  }

  public TransportType getTransportType() {
    return this.transportType;
  }

  public void setTransportType(TransportType transportType) {
    this.transportType = transportType;
  }

  public String getTransportTypeStr() {
    if (this.transportType == null)
      return null;
    return this.transportType.toString();
  }

  public void setTransportTypeStr(String transportType) {
    if (transportType != null)
      this.transportType = TransportType.valueOf(transportType);
  }

  public void validate(ProcessResult response)
  {
    super.validate(response);
    if (this.transportType == null)
      response.addContextualMessage("transportType", "validate.required", new Object[0]);
    if (StringUtils.isBlank(this.host))
      response.addContextualMessage("host", "validate.required", new Object[0]);
    if ((this.port <= 0) || (this.port > 65535))
      response.addContextualMessage("port", "validate.invalidValue", new Object[0]);
  }

  protected void addPropertiesImpl(List<TranslatableMessage> list)
  {
    super.addPropertiesImpl(list);
    AuditEventType.addPropertyMessage(list, "dsEdit.modbusIp.transportType", this.transportType.getKey());
    AuditEventType.addPropertyMessage(list, "dsEdit.modbusIp.host", this.host);
    AuditEventType.addPropertyMessage(list, "dsEdit.modbusIp.port", Integer.valueOf(this.port));
    AuditEventType.addPropertyMessage(list, "dsEdit.modbusIp.encapsulated", this.encapsulated);
  }

  protected void addPropertyChangesImpl(List<TranslatableMessage> list, ModbusIpDataSourceVO from)
  {
    super.addPropertyChangesImpl(list, from);
    if (from.transportType != this.transportType) {
      AuditEventType.addPropertyChangeMessage(list, "dsEdit.modbusIp.transportType", from.transportType.getKey(), this.transportType.getKey());
    }
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbusIp.host", from.host, this.host);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbusIp.port", from.port, this.port);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbusIp.encapsulated", from.encapsulated, this.encapsulated);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    out.writeObject(this.transportType);
    SerializationHelper.writeSafeUTF(out, this.host);
    out.writeInt(this.port);
    out.writeBoolean(this.encapsulated);
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    int ver = in.readInt();

    if (ver == 1) {
      this.transportType = ((TransportType)in.readObject());
      this.host = SerializationHelper.readSafeUTF(in);
      this.port = in.readInt();
      this.encapsulated = in.readBoolean();
    }
  }

  public void jsonWrite(ObjectWriter writer) throws IOException, JsonException
  {
    super.jsonWrite(writer);
    writer.writeEntry("transportType", this.transportType);
  }

  public void jsonRead(JsonReader reader, JsonObject jsonObject) throws JsonException
  {
    super.jsonRead(reader, jsonObject);
    String text = jsonObject.getString("transportType");
    if (text != null) {
      this.transportType = TransportType.valueOfIgnoreCase(text);
      if (this.transportType == null)
        throw new TranslatableJsonException("emport.error.invalid", new Object[] { "transportType", text, TransportType.getTypeList() });
    }
  }

  public static enum TransportType
  {
    TCP("dsEdit.modbusIp.transportType.tcp"), 
    TCP_KEEP_ALIVE("dsEdit.modbusIp.transportType.tcpKA"), 
    UDP("dsEdit.modbusIp.transportType.udp");

    private final String key;

    private TransportType(String key) { this.key = key; }

    public static TransportType valueOfIgnoreCase(String text)
    {
      for (TransportType type : values()) {
        if (type.name().equalsIgnoreCase(text))
          return type;
      }
      return null;
    }

    public static List<String> getTypeList() {
      List result = new ArrayList();
      for (TransportType type : values())
        result.add(type.name());
      return result;
    }

    public String getKey() {
      return this.key;
    }
  }
}