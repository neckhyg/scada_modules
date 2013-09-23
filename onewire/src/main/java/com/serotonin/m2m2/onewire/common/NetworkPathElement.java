package com.serotonin.m2m2.onewire.common;

import com.dalsemi.onewire.container.SwitchContainer;

public class NetworkPathElement
{
  private final SwitchContainer switchContainer;
  private final Long address;
  private final int channel;

  public NetworkPathElement(SwitchContainer switchContainer, Long address, int channelNumber)
  {
    this.switchContainer = switchContainer;
    this.address = address;
    this.channel = channelNumber;
  }

  public SwitchContainer getContainer() {
    return this.switchContainer;
  }

  public int getChannel() {
    return this.channel;
  }

  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    result = 31 * result + (this.address == null ? 0 : this.address.hashCode());
    result = 31 * result + this.channel;
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
    NetworkPathElement other = (NetworkPathElement)obj;
    if (this.address == null) {
      if (other.address != null)
        return false;
    }
    else if (!this.address.equals(other.address)) {
      return false;
    }
    return this.channel == other.channel;
  }
}