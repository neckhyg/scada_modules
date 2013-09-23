package com.serotonin.m2m2.jviews;

import com.serotonin.m2m2.jviews.component.JspView;
import com.serotonin.m2m2.web.dwr.longPoll.LongPollData;
import com.serotonin.m2m2.web.dwr.longPoll.LongPollState;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class JspViewsCommon
{
  private static final String VIEW_KEY = JspViewsCommon.class + ".view";
  private static final String STATES_KEY = JspViewsCommon.class + ".states";

  public static List<JspComponentState> getJspViewListStates(LongPollData data)
  {
    List states = (List)data.getState().getAttribute(STATES_KEY);
    if (states == null) {
      synchronized (data) {
        states = (List)data.getState().getAttribute(STATES_KEY);
        if (states == null) {
          states = new ArrayList();
          data.getState().setAttribute(STATES_KEY, states);
        }
      }
    }
    return states;
  }

  public static void setJspViewListStates(LongPollData data, List<JspComponentState> states) {
    data.getState().setAttribute(STATES_KEY, states);
  }

  public static JspView getJspView(HttpServletRequest request) {
    return (JspView)request.getSession().getAttribute(VIEW_KEY);
  }

  public static void setJspView(HttpServletRequest request, JspView view) {
    request.getSession().setAttribute(VIEW_KEY, view);
  }
}