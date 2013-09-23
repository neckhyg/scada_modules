package com.serotonin.ma.bacnet;

import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.primitive.OctetString;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.util.StringUtils;

public class BACnetDeviceBean
  implements Comparable<BACnetDeviceBean>
{
  private int instanceNumber;
  private int networkNumber;
  private String mac;
  private String link;

  public BACnetDeviceBean()
  {
  }

  public BACnetDeviceBean(RemoteDevice d)
  {
    if (d != null) {
      this.instanceNumber = d.getInstanceNumber();
      this.networkNumber = d.getAddress().getNetworkNumber().intValue();
      this.mac = d.getAddress().getDescription();
      if (d.getLinkService() != null)
        this.link = d.getLinkService().getDescription();
    }
  }

  public int getInstanceNumber()
  {
    return this.instanceNumber;
  }

  public void setInstanceNumber(int instanceNumber) {
    this.instanceNumber = instanceNumber;
  }

  public int getNetworkNumber() {
    return this.networkNumber;
  }

  public void setNetworkNumber(int networkNumber) {
    this.networkNumber = networkNumber;
  }

  public String getMac() {
    return this.mac;
  }

  public void setMac(String mac) {
    this.mac = mac;
  }

  public String getLink() {
    return this.link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public int compareTo(BACnetDeviceBean that)
  {
    int i = this.instanceNumber - that.instanceNumber;
    if (i == 0) {
      i = StringUtils.compareStrings(this.mac, that.mac, true);
      if (i == 0)
        i = this.networkNumber - that.networkNumber;
    }
    return i;
  }

  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    result = 31 * result + this.instanceNumber;
    result = 31 * result + (this.link == null ? 0 : this.link.hashCode());
    result = 31 * result + (this.mac == null ? 0 : this.mac.hashCode());
    result = 31 * result + this.networkNumber;
    return result;
  }

  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BACnetDeviceBean other = (BACnetDeviceBean)obj;
    if (this.instanceNumber != other.instanceNumber)
      return false;
    if (this.link == null) {
      if (other.link != null)
        return false;
    }
    else if (!this.link.equals(other.link))
      return false;
    if (this.mac == null) {
      if (other.mac != null)
        return false;
    }
    else if (!this.mac.equals(other.mac)) {
      return false;
    }
    return this.networkNumber == other.networkNumber;
  }
}