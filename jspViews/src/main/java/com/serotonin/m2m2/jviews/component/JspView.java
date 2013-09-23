package com.serotonin.m2m2.jviews.component;

import com.serotonin.m2m2.db.dao.DataPointDao;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.vo.permission.Permissions;
import java.util.ArrayList;
import java.util.List;

public class JspView
{
  private final User authorityUser;
  private final List<JspViewComponent> components = new ArrayList();
  private final List<DataPointVO> pointCache = new ArrayList();

  public JspView(User authorityUser) {
    this.authorityUser = authorityUser;
  }

  public User getAuthorityUser() {
    return this.authorityUser;
  }

  public int addPoint(DataPointVO dataPointVO, boolean raw, String disabledValue, boolean time) {
    JspViewPoint point = new JspViewPoint(this.components.size(), dataPointVO, raw, disabledValue, time);
    this.components.add(point);
    return point.getId();
  }

  public int addChart(long duration, int width, int height, List<JspViewChartPoint> points) {
    JspViewChart chart = new JspViewChart(duration, this.components.size(), width, height, points);
    this.components.add(chart);
    return chart.getId();
  }

  public List<JspViewComponent> getComponents() {
    return this.components;
  }

  public synchronized DataPointVO getPoint(String xid) {
    for (DataPointVO dp : this.pointCache) {
      if (dp.getXid().equals(xid)) {
        return dp;
      }
    }
    DataPointVO dp = new DataPointDao().getDataPoint(xid);
    if (dp != null)
    {
      Permissions.ensureDataPointSetPermission(this.authorityUser, dp);

      this.pointCache.add(dp);
    }
    return dp;
  }
}