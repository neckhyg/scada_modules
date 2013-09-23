package com.serotonin.m2m2.bacnet;

import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.spi.JsonSerializable;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;
import com.serotonin.m2m2.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.util.SerializationHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class BACnetIPPointLocatorVO extends AbstractPointLocatorVO
  implements JsonSerializable
{

  @JsonProperty
  private String remoteDeviceIp;

  @JsonProperty
  private int remoteDevicePort;

  @JsonProperty
  private int networkNumber;

  @JsonProperty
  private String networkAddress;

  @JsonProperty
  private int remoteDeviceInstanceNumber;
  private int objectTypeId;

  @JsonProperty
  private int objectInstanceNumber;
  private int propertyIdentifierId = PropertyIdentifier.presentValue.intValue();

  @JsonProperty
  private boolean useCovSubscription;

  @JsonProperty
  private boolean settable;

  @JsonProperty
  private int writePriority = 16;
  private int dataTypeId;
  private static final long serialVersionUID = -1L;
  private static final int version = 4;

  public PointLocatorRT createRuntime()
  {
    return null;
  }

  public TranslatableMessage getConfigurationDescription()
  {
    return null;
  }

  public int getDataTypeId()
  {
    return this.dataTypeId;
  }

  public void setDataTypeId(int dataTypeId) {
    this.dataTypeId = dataTypeId;
  }

  public String getRemoteDeviceIp() {
    return this.remoteDeviceIp;
  }

  public void setRemoteDeviceIp(String remoteDeviceIp) {
    this.remoteDeviceIp = remoteDeviceIp;
  }

  public int getRemoteDevicePort() {
    return this.remoteDevicePort;
  }

  public void setRemoteDevicePort(int remoteDevicePort) {
    this.remoteDevicePort = remoteDevicePort;
  }

  public int getNetworkNumber() {
    return this.networkNumber;
  }

  public void setNetworkNumber(int networkNumber) {
    this.networkNumber = networkNumber;
  }

  public String getNetworkAddress() {
    return this.networkAddress;
  }

  public void setNetworkAddress(String networkAddress) {
    this.networkAddress = networkAddress;
  }

  public int getRemoteDeviceInstanceNumber() {
    return this.remoteDeviceInstanceNumber;
  }

  public void setRemoteDeviceInstanceNumber(int remoteDeviceInstanceNumber) {
    this.remoteDeviceInstanceNumber = remoteDeviceInstanceNumber;
  }

  public int getObjectTypeId() {
    return this.objectTypeId;
  }

  public void setObjectTypeId(int objectTypeId) {
    this.objectTypeId = objectTypeId;
  }

  public int getObjectInstanceNumber() {
    return this.objectInstanceNumber;
  }

  public void setObjectInstanceNumber(int objectInstanceNumber) {
    this.objectInstanceNumber = objectInstanceNumber;
  }

  public int getPropertyIdentifierId() {
    return this.propertyIdentifierId;
  }

  public void setPropertyIdentifierId(int propertyIdentifierId) {
    this.propertyIdentifierId = propertyIdentifierId;
  }

  public boolean isUseCovSubscription() {
    return this.useCovSubscription;
  }

  public void setUseCovSubscription(boolean useCovSubscription) {
    this.useCovSubscription = useCovSubscription;
  }

  public void setSettable(boolean settable) {
    this.settable = settable;
  }

  public boolean isSettable()
  {
    return this.settable;
  }

  public int getWritePriority() {
    return this.writePriority;
  }

  public void setWritePriority(int writePriority) {
    this.writePriority = writePriority;
  }

  public boolean isRelinquishable()
  {
    return false;
  }

  public void validate(ProcessResult response)
  {
  }

  public void addProperties(List<TranslatableMessage> list)
  {
  }

  public void addPropertyChanges(List<TranslatableMessage> list, Object o)
  {
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(4);
    SerializationHelper.writeSafeUTF(out, this.remoteDeviceIp);
    out.writeInt(this.remoteDevicePort);
    out.writeInt(this.networkNumber);
    SerializationHelper.writeSafeUTF(out, this.networkAddress);
    out.writeInt(this.remoteDeviceInstanceNumber);
    out.writeInt(this.objectTypeId);
    out.writeInt(this.objectInstanceNumber);
    out.writeInt(this.propertyIdentifierId);
    out.writeBoolean(this.useCovSubscription);
    out.writeBoolean(this.settable);
    out.writeInt(this.dataTypeId);
    out.writeInt(this.writePriority);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.remoteDeviceIp = SerializationHelper.readSafeUTF(in);
      this.remoteDevicePort = in.readInt();
      this.networkNumber = 0;
      this.networkAddress = null;
      this.remoteDeviceInstanceNumber = 0;
      this.objectTypeId = in.readInt();
      this.objectInstanceNumber = in.readInt();
      this.propertyIdentifierId = in.readInt();
      this.useCovSubscription = in.readBoolean();
      this.settable = in.readBoolean();
      this.dataTypeId = in.readInt();
      this.writePriority = 16;
    }
    else if (ver == 2) {
      this.remoteDeviceIp = SerializationHelper.readSafeUTF(in);
      this.remoteDevicePort = in.readInt();
      this.networkNumber = 0;
      this.networkAddress = null;
      this.remoteDeviceInstanceNumber = 0;
      this.objectTypeId = in.readInt();
      this.objectInstanceNumber = in.readInt();
      this.propertyIdentifierId = in.readInt();
      this.useCovSubscription = in.readBoolean();
      this.settable = in.readBoolean();
      this.dataTypeId = in.readInt();
      this.writePriority = in.readInt();
    }
    else if (ver == 3) {
      this.remoteDeviceIp = SerializationHelper.readSafeUTF(in);
      this.remoteDevicePort = in.readInt();
      this.networkNumber = 0;
      this.networkAddress = null;
      this.remoteDeviceInstanceNumber = in.readInt();
      this.objectTypeId = in.readInt();
      this.objectInstanceNumber = in.readInt();
      this.propertyIdentifierId = in.readInt();
      this.useCovSubscription = in.readBoolean();
      this.settable = in.readBoolean();
      this.dataTypeId = in.readInt();
      this.writePriority = in.readInt();
    }
    else if (ver == 4) {
      this.remoteDeviceIp = SerializationHelper.readSafeUTF(in);
      this.remoteDevicePort = in.readInt();
      this.networkNumber = in.readInt();
      this.networkAddress = SerializationHelper.readSafeUTF(in);
      this.remoteDeviceInstanceNumber = in.readInt();
      this.objectTypeId = in.readInt();
      this.objectInstanceNumber = in.readInt();
      this.propertyIdentifierId = in.readInt();
      this.useCovSubscription = in.readBoolean();
      this.settable = in.readBoolean();
      this.dataTypeId = in.readInt();
      this.writePriority = in.readInt();
    }
  }

  public void jsonWrite(ObjectWriter writer)
    throws IOException, JsonException
  {
  }

  public void jsonRead(JsonReader reader, JsonObject jsonObject)
    throws JsonException
  {
  }
}