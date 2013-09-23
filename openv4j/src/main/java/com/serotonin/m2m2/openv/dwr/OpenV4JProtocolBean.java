package com.serotonin.m2m2.openv.dwr;

import net.sf.openv4j.Devices;
import net.sf.openv4j.Protocol;

public class OpenV4JProtocolBean
{
  final Protocol p;

  public static OpenV4JProtocolBean[] fromDevice(Devices device)
  {
    OpenV4JProtocolBean[] result = new OpenV4JProtocolBean[device.getProtocols().length];
    for (int i = 0; i < result.length; i++) {
      result[i] = new OpenV4JProtocolBean(device.getProtocols()[i]);
    }
    return result;
  }

  public OpenV4JProtocolBean(Protocol p)
  {
    this.p = p;
  }

  public String getName() {
    return this.p.getName();
  }

  public String getLabel() {
    return this.p.getLabel();
  }
}