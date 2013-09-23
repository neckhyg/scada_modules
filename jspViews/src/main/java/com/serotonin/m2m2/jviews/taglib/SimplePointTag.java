package com.serotonin.m2m2.jviews.taglib;

import com.serotonin.m2m2.jviews.component.JspView;
import com.serotonin.m2m2.vo.DataPointVO;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class SimplePointTag extends ViewTagSupport
{
  private static final long serialVersionUID = -1L;
  private String xid;
  private boolean raw;
  private String disabledValue;
  private boolean time;

  public void setXid(String xid)
  {
    this.xid = xid;
  }

  public void setRaw(boolean raw) {
    this.raw = raw;
  }

  public void setDisabledValue(String disabledValue) {
    this.disabledValue = disabledValue;
  }

  public void setTime(boolean time) {
    this.time = time;
  }

  public int doStartTag()
    throws JspException
  {
    JspView view = getJspView();

    DataPointVO dataPointVO = getDataPointVO(view, this.xid);

    int id = view.addPoint(dataPointVO, this.raw, this.disabledValue, this.time);

    this.pageContext.setAttribute("componentId", Integer.valueOf(id));

    return 1;
  }

  public void release()
  {
    super.release();
    this.xid = null;
    this.raw = false;
    this.disabledValue = null;
    this.time = false;
  }
}