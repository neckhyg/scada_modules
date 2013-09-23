package com.serotonin.m2m2.pachube.ds;

import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;

public class PachubePointLocatorRT extends PointLocatorRT
{
  private final int feedId;
  private final String dataStreamId;
  private final int dataTypeId;
  private final String binary0Value;
  private final boolean settable;

  public PachubePointLocatorRT(PachubePointLocatorVO vo)
  {
    this.feedId = vo.getFeedId();
    this.dataStreamId = vo.getDataStreamId();
    this.dataTypeId = vo.getDataTypeId();
    this.binary0Value = vo.getBinary0Value();
    this.settable = vo.isSettable();
  }

  public int getFeedId() {
    return this.feedId;
  }

  public String getDataStreamId() {
    return this.dataStreamId;
  }

  public boolean isSettable()
  {
    return this.settable;
  }

  public int getDataTypeId() {
    return this.dataTypeId;
  }

  public String getBinary0Value() {
    return this.binary0Value;
  }
}