package com.serotonin.m2m2.persistent.ds;

import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;

public class PersistentPointLocatorRT extends PointLocatorRT
{
  private final PersistentPointLocatorVO vo;

  public PersistentPointLocatorRT(PersistentPointLocatorVO vo)
  {
    this.vo = vo;
  }

  public boolean isSettable()
  {
    return this.vo.isSettable();
  }

  public PersistentPointLocatorVO getPointLocatorVO() {
    return this.vo;
  }
}