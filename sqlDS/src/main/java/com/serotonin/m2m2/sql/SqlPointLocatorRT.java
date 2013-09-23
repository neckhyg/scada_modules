package com.serotonin.m2m2.sql;

import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;

public class SqlPointLocatorRT extends PointLocatorRT
{
  private final SqlPointLocatorVO vo;

  public SqlPointLocatorRT(SqlPointLocatorVO vo)
  {
    this.vo = vo;
  }

  public boolean isSettable()
  {
    return this.vo.isSettable();
  }

  public SqlPointLocatorVO getVO() {
    return this.vo;
  }
}