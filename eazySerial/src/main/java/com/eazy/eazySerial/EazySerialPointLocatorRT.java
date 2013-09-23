package com.eazy.eazySerial;

import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;

public class EazySerialPointLocatorRT extends PointLocatorRT
{
  private final EazySerialPointLocatorVO vo;

  public EazySerialPointLocatorRT(EazySerialPointLocatorVO vo)
  {
    this.vo = vo;
  }

  public boolean isSettable()
  {
    return this.vo.isSettable();
  }

  public EazySerialPointLocatorVO getPointLocatorVO() {
    return this.vo;
  }
}