package com.serotonin.m2m2.jviews;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.jviews.component.JspView;
import com.serotonin.m2m2.jviews.component.JspViewComponent;
import com.serotonin.m2m2.module.LongPollDefinition;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.web.dwr.longPoll.LongPollData;
import com.serotonin.m2m2.web.dwr.longPoll.LongPollHandler;
import com.serotonin.m2m2.web.dwr.longPoll.LongPollRequest;
import com.serotonin.m2m2.web.dwr.longPoll.LongPollState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

public class JspViewLongPollDefinition extends LongPollDefinition
  implements LongPollHandler
{
  public LongPollHandler getHandler()
  {
    return this;
  }

  public void handleLongPoll(LongPollData data, Map<String, Object> response, User user)
  {
    if (data.getRequest().hasHandler("jspView")) {
      LongPollState state = data.getState();
      List jspViewStates = JspViewsCommon.getJspViewListStates(data);

      synchronized (state) {
        List<JspComponentState> newStates = getViewPointData();
        List differentStates = new ArrayList();

        for (JspComponentState newState : newStates) {
          JspComponentState oldState = getState(newState.getId(), jspViewStates);
          if (oldState == null) {
            differentStates.add(newState);
          } else {
            JspComponentState copy = newState.clone();
            copy.removeEqualValue(oldState);
            if (!copy.isEmpty()) {
              differentStates.add(copy);
            }
          }
        }
        if (!differentStates.isEmpty()) {
          response.put("jspViewStates", differentStates);
          JspViewsCommon.setJspViewListStates(data, newStates);
        }
      }
    }
  }

  private List<JspComponentState> getViewPointData() {
    HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();

    JspView view = JspViewsCommon.getJspView(request);
    if (view == null) {
      return Collections.emptyList();
    }
    List states = new ArrayList();

    for (JspViewComponent comp : view.getComponents()) {
      states.add(comp.createState(Common.runtimeManager, request));
    }
    return states;
  }

  private JspComponentState getState(int id, List<JspComponentState> states) {
    for (JspComponentState state : states) {
      if (state.getId() == id)
        return state;
    }
    return null;
  }
}