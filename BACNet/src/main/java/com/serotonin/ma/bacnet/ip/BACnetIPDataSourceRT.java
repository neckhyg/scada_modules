package com.serotonin.ma.bacnet.ip;

import com.serotonin.bacnet4j.npdu.Network;
import com.serotonin.ma.bacnet.BACnetDataSourceRT;

public class BACnetIPDataSourceRT extends BACnetDataSourceRT
{
  private final BACnetIPDataSourceVO vo;

  public BACnetIPDataSourceRT(BACnetIPDataSourceVO vo)
  {
    super(vo);
    this.vo = vo;
  }

  protected Network createNetwork() throws Exception
  {
    return BACnetIPDefinition.createNetwork(this.vo.getBroadcastAddress(), this.vo.getPort(), this.vo.getLocalBindAddress());
  }

  protected int getDefaultPort()
  {
    return this.vo.getPort();
  }
}