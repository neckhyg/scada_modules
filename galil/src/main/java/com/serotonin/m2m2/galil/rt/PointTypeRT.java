package com.serotonin.m2m2.galil.rt;

import com.serotonin.m2m2.galil.vo.PointTypeVO;
import com.serotonin.m2m2.i18n.TranslatableException;
import com.serotonin.m2m2.rt.dataImage.types.DataValue;
import com.serotonin.m2m2.rt.dataSource.DataSourceUtils;
import java.text.DecimalFormat;

public abstract class PointTypeRT
{
  private static final DecimalFormat numericFormat = new DecimalFormat("#.#");
  private final PointTypeVO vo;

  public PointTypeRT(PointTypeVO vo)
  {
    this.vo = vo;
  }

  public boolean isSettable() {
    return this.vo.isSettable();
  }

  public final GalilRequest getPollRequest() {
    String data = getPollRequestImpl();
    if (data == null)
      return null;
    return new GalilRequest(data);
  }
  protected abstract String getPollRequestImpl();

  public abstract DataValue parsePollResponse(String paramString1, String paramString2) throws TranslatableException;

  public final GalilRequest getSetRequest(DataValue value) { String data = getSetRequestImpl(value);
    if (data == null)
      return null;
    return new GalilRequest(data); } 
  protected abstract String getSetRequestImpl(DataValue paramDataValue);

  public abstract DataValue parseSetResponse(String paramString) throws TranslatableException;

  protected DataValue parseValue(String data, int dataTypeId, String pointName) throws TranslatableException {
    return DataSourceUtils.getValue(data, dataTypeId, "0.0000", null, numericFormat, pointName);
  }

  protected double rawToEngineeringUnits(double raw, double rawLow, double rawHigh, double engLow, double engHigh) {
    double numerator = (engHigh - engLow) * raw + rawHigh * engLow - rawLow * engHigh;
    return numerator / (rawHigh - rawLow);
  }

  protected double engineeringUnitsToRaw(double eng, double rawLow, double rawHigh, double engLow, double engHigh) {
    double numerator = (rawHigh - rawLow) * eng - rawHigh * engLow + rawLow * engHigh;
    return numerator / (engHigh - engLow);
  }
}