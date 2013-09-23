package com.serotonin.m2m2.openv.dwr;

import net.sf.openv4j.DataPoint;
import net.sf.openv4j.Group;

public class OpenV4JDataPointBean
{
  private final DataPoint p;
  private final String value;

  public OpenV4JDataPointBean(DataPoint p, String value)
  {
    this.p = p;
    this.value = value;
  }

  public OpenV4JDataPointBean(DataPoint p) {
    this.p = p;
    this.value = null;
  }

  public String getGroupName()
  {
    return this.p.getGroup().getName();
  }

  public String getGroupLabel()
  {
    return this.p.getGroup().getLabel();
  }

  public String getName()
  {
    return this.p.getName();
  }

  public String getLabel()
  {
    return this.p.getLabel();
  }

  public String getValue()
  {
    return this.value;
  }
}