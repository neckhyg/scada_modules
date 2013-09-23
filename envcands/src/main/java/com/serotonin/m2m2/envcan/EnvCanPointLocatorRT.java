package com.serotonin.m2m2.envcan;

import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;

public class EnvCanPointLocatorRT extends PointLocatorRT
{
  private final EnvCanPointLocatorVO vo;

  public EnvCanPointLocatorRT(EnvCanPointLocatorVO vo)
  {
    this.vo = vo;
  }

  public boolean isSettable()
  {
    return this.vo.isSettable();
  }

  public EnvCanPointLocatorVO getPointLocatorVO() {
    return this.vo;
  }
}