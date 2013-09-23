package com.serotonin.m2m2.wibox;

import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;

public class WiBoxHttpPointLocatorRT extends PointLocatorRT
{
  private final WiBoxHttpPointLocatorVO vo;

  public WiBoxHttpPointLocatorRT(WiBoxHttpPointLocatorVO vo)
  {
    this.vo = vo;
  }

  public boolean isSettable()
  {
    return this.vo.isSettable();
  }

  public WiBoxHttpPointLocatorVO getPointLocatorVO() {
    return this.vo;
  }
}