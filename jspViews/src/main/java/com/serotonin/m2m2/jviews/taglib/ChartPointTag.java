package com.serotonin.m2m2.jviews.taglib;

import com.serotonin.InvalidArgumentException;
import com.serotonin.util.ColorUtils;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.commons.lang3.StringUtils;

public class ChartPointTag extends TagSupport
{
  private static final long serialVersionUID = -1L;
  private String xid;
  private String color;

  public void setXid(String xid)
  {
    this.xid = xid;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public int doStartTag() throws JspException
  {
    ChartTag chartTag = (ChartTag)findAncestorWithClass(this, ChartTag.class);
    if (chartTag == null) {
      throw new JspException("chartPoint tags must be used within a chart tag");
    }
    try
    {
      if (!StringUtils.isBlank(this.color))
        ColorUtils.toColor(this.color);
    }
    catch (InvalidArgumentException e) {
      throw new JspException("Invalid color '" + this.color + "'");
    }

    chartTag.addChartPoint(this.xid, this.color);

    return 1;
  }

  public void release()
  {
    super.release();
    this.xid = null;
    this.color = null;
  }
}