package com.serotonin.ma.ascii.serial;

import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;

public class SerialPointLocatorRT extends PointLocatorRT
{
  private final SerialPointLocatorVO vo;

  public SerialPointLocatorRT(SerialPointLocatorVO vo)
  {
    this.vo = vo;
  }

  public boolean isSettable()
  {
    return this.vo.isSettable();
  }

  public SerialPointLocatorVO getVo() {
    return this.vo;
  }
}