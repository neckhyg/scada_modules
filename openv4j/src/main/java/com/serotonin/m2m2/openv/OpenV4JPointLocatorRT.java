package com.serotonin.m2m2.openv;

import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;
import net.sf.openv4j.DataPoint;

public class OpenV4JPointLocatorRT extends PointLocatorRT
{
  private final OpenV4JPointLocatorVO vo;
  private final DataPoint dataPoint;

  public OpenV4JPointLocatorRT(OpenV4JPointLocatorVO vo)
  {
    this.vo = vo;
    this.dataPoint = DataPoint.valueOf(vo.getDataPointName());
  }

  public boolean isSettable()
  {
    return this.vo.isSettable();
  }

  public OpenV4JPointLocatorVO getVo()
  {
    return this.vo;
  }

  public DataPoint getDataPoint()
  {
    return this.dataPoint;
  }
}