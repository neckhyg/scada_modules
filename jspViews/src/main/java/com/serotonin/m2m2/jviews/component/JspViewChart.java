package com.serotonin.m2m2.jviews.component;

import com.serotonin.m2m2.jviews.JspComponentState;
import com.serotonin.m2m2.rt.RuntimeManager;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.vo.DataPointVO;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public class JspViewChart extends JspViewComponent
{
  private final long duration;
  private final int width;
  private final int height;
  private final List<JspViewChartPoint> points;

  public JspViewChart(long duration, int id, int width, int height, List<JspViewChartPoint> points)
  {
    super(id);
    this.duration = duration;
    this.width = width;
    this.height = height;
    this.points = points;
  }

  protected void createStateImpl(RuntimeManager rtm, HttpServletRequest request, JspComponentState state)
  {
    long maxTs = 0L;
    for (JspViewChartPoint point : this.points) {
      DataPointRT dataPointRT = rtm.getDataPoint(point.getDataPointVO().getId());
      if (dataPointRT != null) {
        PointValueTime pvt = dataPointRT.getPointValue();
        if ((pvt != null) && (maxTs < pvt.getTime())) {
          maxTs = pvt.getTime();
        }
      }
    }
    StringBuilder htmlData = new StringBuilder();
    htmlData.append("chart/");
    htmlData.append(maxTs);
    htmlData.append('_');
    htmlData.append(this.duration);

    for (JspViewChartPoint point : this.points) {
      htmlData.append('_');
      htmlData.append(point.getDataPointVO().getId());
      if (!StringUtils.isBlank(point.getColor())) {
        htmlData.append('|').append(point.getColor().replaceAll("#", "0x"));
      }
    }
    htmlData.append(".png");

    htmlData.append("?w=");
    htmlData.append(this.width);
    htmlData.append("&h=");
    htmlData.append(this.height);

    state.setValue(htmlData.toString());
  }
}