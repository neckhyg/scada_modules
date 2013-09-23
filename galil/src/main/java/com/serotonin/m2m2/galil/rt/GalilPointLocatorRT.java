package com.serotonin.m2m2.galil.rt;

import com.serotonin.m2m2.i18n.TranslatableException;
import com.serotonin.m2m2.rt.dataImage.types.DataValue;
import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;

public class GalilPointLocatorRT extends PointLocatorRT
{
  private final PointTypeRT pointType;

  public GalilPointLocatorRT(PointTypeRT pointType)
  {
    this.pointType = pointType;
  }

  public PointTypeRT getPointType() {
    return this.pointType;
  }

  public boolean isSettable()
  {
    return this.pointType.isSettable();
  }

  public GalilRequest getPollRequest() {
    return this.pointType.getPollRequest();
  }

  public DataValue parsePollResponse(String data, String pointName) throws TranslatableException {
    return this.pointType.parsePollResponse(data, pointName);
  }

  public GalilRequest getSetRequest(DataValue value) {
    return this.pointType.getSetRequest(value);
  }

  public DataValue parseSetResponse(String data) throws TranslatableException {
    return this.pointType.parseSetResponse(data);
  }
}