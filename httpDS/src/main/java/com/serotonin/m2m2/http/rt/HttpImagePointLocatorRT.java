package com.serotonin.m2m2.http.rt;

import com.serotonin.m2m2.http.vo.HttpImagePointLocatorVO;
import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;

public class HttpImagePointLocatorRT extends PointLocatorRT
{
  private final HttpImagePointLocatorVO vo;

  public HttpImagePointLocatorRT(HttpImagePointLocatorVO vo)
  {
    this.vo = vo;
  }

  public boolean isSettable()
  {
    return this.vo.isSettable();
  }

  public HttpImagePointLocatorVO getVo() {
    return this.vo;
  }
}