package com.serotonin.m2m2.http.rt;

import com.serotonin.m2m2.http.vo.HttpReceiverPointLocatorVO;
import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;

public class HttpReceiverPointLocatorRT extends PointLocatorRT
{
  private final HttpReceiverPointLocatorVO vo;

  public HttpReceiverPointLocatorRT(HttpReceiverPointLocatorVO vo)
  {
    this.vo = vo;
  }

  public boolean isSettable()
  {
    return this.vo.isSettable();
  }

  public HttpReceiverPointLocatorVO getPointLocatorVO() {
    return this.vo;
  }
}