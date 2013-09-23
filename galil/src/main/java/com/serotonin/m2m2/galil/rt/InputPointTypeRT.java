package com.serotonin.m2m2.galil.rt;

import com.serotonin.m2m2.galil.vo.InputPointTypeVO;
import com.serotonin.m2m2.i18n.TranslatableException;
import com.serotonin.m2m2.rt.dataImage.types.DataValue;
import com.serotonin.m2m2.rt.dataImage.types.NumericValue;

public class InputPointTypeRT extends PointTypeRT
{
  private final InputPointTypeVO vo;

  public InputPointTypeRT(InputPointTypeVO vo)
  {
    super(vo);
    this.vo = vo;
  }

  public String getPollRequestImpl()
  {
    if (this.vo.getDataTypeId() == 1)
      return "MG @IN[" + this.vo.getInputId() + "]";
    return "MG @AN[" + this.vo.getInputId() + "]";
  }

  public DataValue parsePollResponse(String data, String pointName) throws TranslatableException
  {
    int dataTypeId = this.vo.getDataTypeId();
    DataValue value = parseValue(data, dataTypeId, pointName);

    if (dataTypeId == 3) {
      value = new NumericValue(rawToEngineeringUnits(value.getDoubleValue(), this.vo.getScaleRawLow(), this.vo.getScaleRawHigh(), this.vo.getScaleEngLow(), this.vo.getScaleEngHigh()));
    }

    return value;
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