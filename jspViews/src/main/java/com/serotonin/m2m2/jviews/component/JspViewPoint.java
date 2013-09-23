package com.serotonin.m2m2.jviews.component;

import com.serotonin.m2m2.jviews.JspComponentState;
import com.serotonin.m2m2.rt.RuntimeManager;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.dataImage.types.ImageValue;
import com.serotonin.m2m2.view.text.TextRenderer;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.m2m2.web.dwr.BaseDwr;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class JspViewPoint extends JspViewComponent
{
  private final DataPointVO dataPointVO;
  private final boolean raw;
  private final String disabledValue;
  private final boolean time;

  public JspViewPoint(int id, DataPointVO dataPointVO, boolean raw, String disabledValue, boolean time)
  {
    super(id);
    this.dataPointVO = dataPointVO;
    this.raw = raw;
    if (disabledValue == null)
      this.disabledValue = "";
    else
      this.disabledValue = disabledValue;
    this.time = time;
  }

  protected void createStateImpl(RuntimeManager rtm, HttpServletRequest request, JspComponentState state)
  {
    DataPointRT dataPointRT = rtm.getDataPoint(this.dataPointVO.getId());
    String value;
    if (dataPointRT == null) {
      value = this.disabledValue;
    } else {
      PointValueTime pvt = dataPointRT.getPointValue();
      if ((pvt != null) && ((pvt.getValue() instanceof ImageValue)))
      {
        Map model = new HashMap();
        model.put("point", this.dataPointVO);
        model.put("pointValue", pvt);
        value = BaseDwr.generateContent(request, "imageValueThumbnail.jsp", model);
      }
      else {
        int hint = this.raw ? 1 : 2;
        value = this.dataPointVO.getTextRenderer().getText(pvt, hint);
        if ((pvt != null) && (this.time))
          state.setTime(Long.valueOf(pvt.getTime()));
      }
    }
    state.setValue(value);
  }
}