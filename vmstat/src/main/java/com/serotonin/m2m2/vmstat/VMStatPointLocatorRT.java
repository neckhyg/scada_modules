package com.serotonin.m2m2.vmstat;

import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;

public class VMStatPointLocatorRT extends PointLocatorRT
{
  private final VMStatPointLocatorVO vo;

  public VMStatPointLocatorRT(VMStatPointLocatorVO vo)
  {
    this.vo = vo;
  }

  public boolean isSettable()
  {
    return this.vo.isSettable();
  }

  public VMStatPointLocatorVO getPointLocatorVO() {
    return this.vo;
  }
}