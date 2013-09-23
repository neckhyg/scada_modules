package com.serotonin.m2m2.galil.rt;

import com.serotonin.m2m2.galil.vo.CommandPointTypeVO;
import com.serotonin.m2m2.rt.dataImage.types.AlphanumericValue;
import com.serotonin.m2m2.rt.dataImage.types.DataValue;

public class CommandPointTypeRT extends PointTypeRT
{
  public CommandPointTypeRT(CommandPointTypeVO vo)
  {
    super(vo);
  }

  public String getPollRequestImpl()
  {
    return null;
  }

  public DataValue parsePollResponse(String data, String pointName)
  {
    return null;
  }

  protected String getSetRequestImpl(DataValue value)
  {
    return value.getStringValue();
  }

  public DataValue parseSetResponse(String data)
  {
    return new AlphanumericValue(data);
  }
}