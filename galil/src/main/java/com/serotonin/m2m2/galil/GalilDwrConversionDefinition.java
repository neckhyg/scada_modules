package com.serotonin.m2m2.galil;

import com.serotonin.m2m2.galil.vo.PointTypeVO;
import com.serotonin.m2m2.module.DwrConversionDefinition;

public class GalilDwrConversionDefinition extends DwrConversionDefinition
{
  public void addConversions()
  {
    addConversion(PointTypeVO.class);
  }
}