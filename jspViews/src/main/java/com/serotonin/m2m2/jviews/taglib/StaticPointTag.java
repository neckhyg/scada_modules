package com.serotonin.m2m2.jviews.taglib;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.jviews.component.JspView;
import com.serotonin.m2m2.rt.RuntimeManager;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.dataImage.types.ImageValue;
import com.serotonin.m2m2.view.text.TextRenderer;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.m2m2.web.dwr.BaseDwr;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

public class StaticPointTag extends ViewTagSupport
{
  private static final long serialVersionUID = -1L;
  private String xid;
  private boolean raw;
  private String disabledValue;

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

  public int doStartTag()
    throws JspException
  {
    JspView view = getJspView();

    DataPointVO dataPointVO = getDataPointVO(view, this.xid);

    JspWriter out = this.pageContext.getOut();
    HttpServletRequest request = (HttpServletRequest)this.pageContext.getRequest();

    DataPointRT dataPointRT = Common.runtimeManager.getDataPoint(dataPointVO.getId());
    if (dataPointRT == null) {
      write(out, this.disabledValue);
    } else {
      PointValueTime pvt = dataPointRT.getPointValue();

      if ((pvt != null) && ((pvt.getValue() instanceof ImageValue)))
      {
        Map model = new HashMap();
        model.put("point", dataPointVO);
        model.put("pointValue", pvt);
        write(out, BaseDwr.generateContent(request, "imageValueThumbnail.jsp", model));
      }
      else {
        int hint = this.raw ? 1 : 2;
        write(out, dataPointVO.getTextRenderer().getText(pvt, hint));
      }
    }

    return 1;
  }

  private void write(JspWriter out, String content) throws JspException {
    try {
      out.append(content);
    }
    catch (IOException e) {
      throw new JspException(e);
    }
  }

  public void release()
  {
    super.release();
    this.xid = null;
    this.raw = false;
    this.disabledValue = null;
  }
}