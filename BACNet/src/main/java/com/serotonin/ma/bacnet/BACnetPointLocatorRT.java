package com.serotonin.ma.bacnet;

import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.obj.ObjectCovSubscription;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.OctetString;
import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;
import org.apache.commons.lang3.StringUtils;

public class BACnetPointLocatorRT extends PointLocatorRT
{
  private static int NEXT_COV_ID = 1;
  private final BACnetPointLocatorVO vo;
  private RemoteDevice remoteDevice;
  private final ObjectIdentifier oid;
  private final PropertyIdentifier pid;
  private boolean cov;
  private final int covId;
  private Address address;
  private OctetString linkService;
  private boolean linkServiceInitialized;

  public BACnetPointLocatorRT(BACnetPointLocatorVO vo)
  {
    this.vo = vo;
    this.oid = new ObjectIdentifier(new ObjectType(vo.getObjectTypeId()), vo.getObjectInstanceNumber());
    this.pid = new PropertyIdentifier(vo.getPropertyIdentifierId());

    this.cov = ((vo.isUseCovSubscription()) && (ObjectCovSubscription.supportedObjectType(this.oid.getObjectType())));

    if (this.cov)
      this.covId = (NEXT_COV_ID++);
    else
      this.covId = -1;
  }

  public Address getAddress(int defaultPort) {
    if (this.address == null)
      this.address = new Address(this.vo.getNetworkNumber(), new OctetString(this.vo.getMac(), defaultPort));
    return this.address;
  }

  public OctetString getLinkService(int port) {
    if (!this.linkServiceInitialized) {
      this.linkServiceInitialized = true;
      if (!StringUtils.isBlank(this.vo.getLink()))
        this.linkService = new OctetString(this.vo.getLink(), port);
    }
    return this.linkService;
  }

  public int getRemoteDeviceInstanceNumber() {
    return this.vo.getRemoteDeviceInstanceNumber();
  }

  public boolean isInitialized() {
    return this.remoteDevice != null;
  }

  public boolean isUseCovSubscription() {
    return this.cov;
  }

  public void cancelCov() {
    this.cov = false;
  }

  public void setRemoteDevice(RemoteDevice remoteDevice) {
    this.remoteDevice = remoteDevice;
  }

  public RemoteDevice getRemoteDevice() {
    return this.remoteDevice;
  }

  public ObjectIdentifier getOid() {
    return this.oid;
  }

  public PropertyIdentifier getPid() {
    return this.pid;
  }

  public int getCovId() {
    return this.covId;
  }

  public boolean isSettable()
  {
    return this.vo.isSettable();
  }

  public boolean isRelinquishable()
  {
    return this.vo.isRelinquishable();
  }

  public int getWritePriority() {
    return this.vo.getWritePriority();
  }
}