package com.serotonin.m2m2.jviews.taglib;

import com.serotonin.m2m2.db.dao.UserDao;
import com.serotonin.m2m2.jviews.JspViewsCommon;
import com.serotonin.m2m2.jviews.component.JspView;
import com.serotonin.m2m2.vo.User;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

public class JspViewInitTag extends TagSupport
{
  private static final long serialVersionUID = -1L;
  private String username;

  public void setUsername(String username)
  {
    this.username = username;
  }

  public int doStartTag()
    throws JspException
  {
    User user = new UserDao().getUser(this.username);
    if (user == null)
      throw new JspException("Username '" + this.username + "' not found");
    if (user.isDisabled()) {
      throw new JspException("Username '" + this.username + "' is disabled");
    }
    JspViewsCommon.setJspView((HttpServletRequest)this.pageContext.getRequest(), new JspView(user));

    return 1;
  }

  public void release()
  {
    super.release();
    this.username = null;
  }
}