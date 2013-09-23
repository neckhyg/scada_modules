package com.serotonin.m2m2.openv.dwr;

import com.serotonin.m2m2.module.DwrConversionDefinition;
import net.sf.openv4j.DataPoint;
import net.sf.openv4j.Devices;
import net.sf.openv4j.Group;
import net.sf.openv4j.Protocol;

public class OpenV4JDwrConversionDefinition extends DwrConversionDefinition
{
  public void addConversions()
  {
    addConversion(OpenV4JDataPointBean.class);
    addConversion(Protocol.class, "enum");
    addConversion(Devices.class, "enum");
    addConversion(Group.class, "enum");
    addConversion(DataPoint.class, "enum");
  }
}