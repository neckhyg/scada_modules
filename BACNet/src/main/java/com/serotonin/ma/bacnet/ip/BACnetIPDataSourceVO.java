package com.serotonin.ma.bacnet.ip;

import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.DataSourceRT;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.ma.bacnet.BACnetDataSourceVO;
import com.serotonin.util.IpAddressUtils;
import com.serotonin.util.SerializationHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.util.List;

public class BACnetIPDataSourceVO extends BACnetDataSourceVO<BACnetIPDataSourceVO>
{

  @JsonProperty
  private String localBindAddress;

  @JsonProperty
  private String broadcastAddress;

  @JsonProperty
  private int port;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  public DataSourceRT createDataSourceRT()
  {
    return new BACnetIPDataSourceRT(this);
  }

  public BACnetIPPointLocatorVO createPointLocator()
  {
    return new BACnetIPPointLocatorVO();
  }

  public BACnetIPDataSourceVO()
  {
    IpNetwork network = new IpNetwork();
    this.localBindAddress = "0.0.0.0";
    this.broadcastAddress = network.getBroadcastIp();
    this.port = network.getPort();
  }

  public String getLocalBindAddress() {
    return this.localBindAddress;
  }

  public void setLocalBindAddress(String localBindAddress) {
    this.localBindAddress = localBindAddress;
  }

  public String getBroadcastAddress() {
    return this.broadcastAddress;
  }

  public void setBroadcastAddress(String broadcastAddress) {
    this.broadcastAddress = broadcastAddress;
  }

  public int getPort() {
    return this.port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public void validate(ProcessResult response)
  {
    super.validate(response);
    try
    {
      IpAddressUtils.toIpAddress(this.localBindAddress);
    }
    catch (IllegalArgumentException e) {
      response.addContextualMessage("localBindAddress", "common.default", new Object[] { e.getMessage() });
    }
    try
    {
      IpAddressUtils.toIpAddress(this.broadcastAddress);
    }
    catch (IllegalArgumentException e) {
      response.addContextualMessage("broadcastAddress", "common.default", new Object[] { e.getMessage() });
    }
    try
    {
      new InetSocketAddress(this.broadcastAddress, this.port);
    }
    catch (IllegalArgumentException e) {
      if (e.getMessage().startsWith("port"))
        response.addContextualMessage("port", "validate.illegalValue", new Object[0]);
      else
        response.addContextualMessage("broadcastAddress", "validate.illegalValue", new Object[0]);
    }
  }

  public void addPropertiesImpl(List<TranslatableMessage> list)
  {
    AuditEventType.addPropertyMessage(list, "mod.bacnetIp.localBindAddress", this.localBindAddress);
    AuditEventType.addPropertyMessage(list, "mod.bacnetIp.broadcastAddress", this.broadcastAddress);
    AuditEventType.addPropertyMessage(list, "mod.bacnetIp.port", Integer.valueOf(this.port));
  }

  public void addPropertyChangesImpl(List<TranslatableMessage> list, BACnetIPDataSourceVO from)
  {
    AuditEventType.maybeAddPropertyChangeMessage(list, "mod.bacnetIp.localBindAddress", from.localBindAddress, this.localBindAddress);

    AuditEventType.maybeAddPropertyChangeMessage(list, "mod.bacnetIp.broadcastAddress", from.broadcastAddress, this.broadcastAddress);

    AuditEventType.maybeAddPropertyChangeMessage(list, "mod.bacnetIp.port", from.port, this.port);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    SerializationHelper.writeSafeUTF(out, this.localBindAddress);
    SerializationHelper.writeSafeUTF(out, this.broadcastAddress);
    out.writeInt(this.port);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.localBindAddress = SerializationHelper.readSafeUTF(in);
      this.broadcastAddress = SerializationHelper.readSafeUTF(in);
      this.port = in.readInt();
    }
  }
}