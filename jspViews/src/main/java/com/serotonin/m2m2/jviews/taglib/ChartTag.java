package com.serotonin.m2m2.jviews.taglib;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.jviews.component.JspView;
import com.serotonin.m2m2.jviews.component.JspViewChartPoint;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.vo.DataPointVO;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class ChartTag extends ViewTagSupport
{
  private static final long serialVersionUID = -1L;
  private int duration;
  private String durationType;
  private int width;
  private int height;
  private List<JspViewChartPoint> points;
  private JspView view;

  public void setDuration(int duration)
  {
    this.duration = duration;
  }

  public void setDurationType(String durationType) {
    this.durationType = durationType;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public int doStartTag() throws JspException
  {
    this.points = new ArrayList();

    this.view = getJspView();

    return 1;
  }

  void addChartPoint(String xid, String color) throws JspException {
    DataPointVO dataPointVO = getDataPointVO(this.view, xid);
    this.points.add(new JspViewChartPoint(dataPointVO, color));
  }

  public int doEndTag() throws JspException
  {
    int periodType = Common.TIME_PERIOD_CODES.getId(this.durationType.toUpperCase(), new int[0]);
    if (periodType == -1)
      throw new JspException("Invalid durationType. Must be one of " + Common.TIME_PERIOD_CODES.getCodeList(new int[0]));
    long millis = Common.getMillis(periodType, this.duration);

    int id = this.view.addChart(millis, this.width, this.height, this.points);

    this.pageContext.setAttribute("componentId", Integer.valueOf(id));

    return 6;
  }

  public void release()
  {
    super.release();
    this.duration = 0;
    this.durationType = null;
    this.width = 0;
    this.height = 0;
    this.view = null;
    this.points = null;
  }
}