package com.serotonin.m2m2.galil.rt;

import com.serotonin.m2m2.galil.vo.TellPositionPointTypeVO;
import com.serotonin.m2m2.i18n.TranslatableException;
import com.serotonin.m2m2.rt.dataImage.types.DataValue;
import com.serotonin.m2m2.rt.dataImage.types.NumericValue;

public class TellPositionPointTypeRT extends PointTypeRT
{
  private final TellPositionPointTypeVO vo;

  public TellPositionPointTypeRT(TellPositionPointTypeVO vo)
  {
    super(vo);
    this.vo = vo;
  }

  public String getPollRequestImpl()
  {
    return "TP" + this.vo.getAxis();
  }

  public DataValue parsePollResponse(String data, String pointName) throws TranslatableException
  {
    double value = parseValue(data, this.vo.getDataTypeId(), pointName).getDoubleValue();

    value = rawToEngineeringUnits(value, this.vo.getScaleRawLow(), this.vo.getScaleRawHigh(), this.vo.getScaleEngLow(), this.vo.getScaleEngHigh());

    if (this.vo.isRoundToInteger()) {
      value = Math.round(value);
    }
    return new NumericValue(value);
  }

  protected String getSetRequestImpl(DataValue value)
  {
    return null;
  }

  public DataValue parseSetResponse(String data)
  {
    return null;
  }
}