package com.serotonin.m2m2.jviews.component;

import com.serotonin.m2m2.jviews.JspComponentState;
import com.serotonin.m2m2.rt.RuntimeManager;
import javax.servlet.http.HttpServletRequest;

public abstract class JspViewComponent
{
  private final int id;

  public JspViewComponent(int id)
  {
    this.id = id;
  }

  public int getId() {
    return this.id;
  }

  public JspComponentState createState(RuntimeManager rtm, HttpServletRequest request) {
    JspComponentState state = new JspComponentState();
    state.setId(this.id);
    createStateImpl(rtm, request, state);
    return state;
  }

  protected abstract void createStateImpl(RuntimeManager paramRuntimeManager, HttpServletRequest paramHttpServletRequest, JspComponentState paramJspComponentState);
}