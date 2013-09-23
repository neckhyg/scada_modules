package com.serotonin.m2m2.onewire.rt;

import com.dalsemi.onewire.utils.Address;
import com.serotonin.m2m2.onewire.vo.OneWirePointLocatorVO;
import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;

public class OneWirePointLocatorRT extends PointLocatorRT
{
  private final OneWirePointLocatorVO vo;
  private final Long address;

  public OneWirePointLocatorRT(OneWirePointLocatorVO vo)
  {
    this.vo = vo;
    this.address = Long.valueOf(Address.toLong(vo.getAddress()));
  }

  public boolean isSettable()
  {
    return this.vo.isSettable();
  }

  public Long getAddress() {
    return this.address;
  }

  public OneWirePointLocatorVO getVo() {
    return this.vo;
  }
}