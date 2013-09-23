package com.serotonin.ma.bacnet.mstp;

import com.serotonin.bacnet4j.npdu.Network;
import com.serotonin.ma.bacnet.BACnetDataSourceRT;

public class BACnetMSTPDataSourceRT extends BACnetDataSourceRT
{
  private final BACnetMSTPDataSourceVO vo;

  public BACnetMSTPDataSourceRT(BACnetMSTPDataSourceVO vo)
  {
    super(vo);
    this.vo = vo;
  }

  protected Network createNetwork() throws Exception
  {
    return BACnetMSTPDefinition.createNetwork(this.vo.getCommPortId(), this.vo.getBaudRate(), this.vo.getThisStation(), this.vo.getRetryCount());
  }

  protected int getDefaultPort()
  {
    return 0;
  }
}