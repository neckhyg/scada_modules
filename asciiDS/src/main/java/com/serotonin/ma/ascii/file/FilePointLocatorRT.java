package com.serotonin.ma.ascii.file;

import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;

public class FilePointLocatorRT extends PointLocatorRT
{
  private final FilePointLocatorVO vo;

  public FilePointLocatorRT(FilePointLocatorVO vo)
  {
    this.vo = vo;
  }

  public boolean isSettable()
  {
    return this.vo.isSettable();
  }

  public FilePointLocatorVO getVo() {
    return this.vo;
  }
}