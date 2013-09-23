package com.serotonin.m2m2.jviews.component;

import com.serotonin.m2m2.vo.DataPointVO;

public class JspViewChartPoint
{
  private final DataPointVO dataPointVO;
  private final String color;

  public JspViewChartPoint(DataPointVO dataPointVO, String color)
  {
    this.dataPointVO = dataPointVO;
    this.color = color;
  }

  public DataPointVO getDataPointVO() {
    return this.dataPointVO;
  }

  public String getColor() {
    return this.color;
  }
}