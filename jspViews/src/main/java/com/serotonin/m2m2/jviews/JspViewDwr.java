package com.serotonin.m2m2.jviews;

import com.serotonin.m2m2.jviews.component.JspView;
import com.serotonin.m2m2.web.dwr.ModuleDwr;
import com.serotonin.m2m2.web.dwr.longPoll.LongPollData;
import com.serotonin.m2m2.web.dwr.util.DwrPermission;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

public class JspViewDwr extends ModuleDwr
{
  @DwrPermission(anonymous=true)
  public void setJspViewPoint(int pollSessionId, String xid, String valueStr)
  {
    JspView view = JspViewsCommon.getJspView(WebContextFactory.get().getHttpServletRequest());
    setPointImpl(view.getPoint(xid), valueStr, view.getAuthorityUser());
    notifyLongPollImpl(getLongPollData(pollSessionId, false).getRequest());
  }
}