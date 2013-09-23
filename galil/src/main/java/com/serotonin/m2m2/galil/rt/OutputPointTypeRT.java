package com.serotonin.m2m2.galil.rt;

import com.serotonin.m2m2.galil.vo.OutputPointTypeVO;
import com.serotonin.m2m2.i18n.TranslatableException;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataImage.types.BinaryValue;
import com.serotonin.m2m2.rt.dataImage.types.DataValue;

public class OutputPointTypeRT extends PointTypeRT
{
  private final OutputPointTypeVO vo;

  public OutputPointTypeRT(OutputPointTypeVO vo)
  {
    super(vo);
    this.vo = vo;
  }

  public String getPollRequestImpl()
  {
    return "MG @OUT[" + this.vo.getOutputId() + "]";
  }

  public DataValue parsePollResponse(String data, String pointName) throws TranslatableException
  {
    return super.parseValue(data, this.vo.getDataTypeId(), pointName);
  }

  protected String getSetRequestImpl(DataValue value)
  {
    boolean b = ((BinaryValue)value).getBooleanValue();
    if (b)
      return "SB " + this.vo.getOutputId();
    return "CB " + this.vo.getOutputId();
  }

  public DataValue parseSetResponse(String data) throws TranslatableException
  {
    if (!"".equals(data))
      throw new TranslatableException(new TranslatableMessage("event.galil.unexpected", new Object[] { data }));
    return null;
  }
}