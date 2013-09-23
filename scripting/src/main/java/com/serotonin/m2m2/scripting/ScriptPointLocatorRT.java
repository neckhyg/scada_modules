package com.serotonin.m2m2.scripting;

import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;

public class ScriptPointLocatorRT extends PointLocatorRT
{
  final ScriptPointLocatorVO vo;

  public ScriptPointLocatorRT(ScriptPointLocatorVO vo)
  {
    this.vo = vo;
  }

  public boolean isSettable()
  {
    return this.vo.isSettable();
  }
}