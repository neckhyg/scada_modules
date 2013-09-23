package com.serotonin.m2m2.http;

import com.serotonin.m2m2.http.dwr.HttpPublisherDwr;
import com.serotonin.m2m2.http.vo.HttpSenderVO;
import com.serotonin.m2m2.module.PublisherDefinition;
import com.serotonin.m2m2.vo.publish.PublishedPointVO;
import com.serotonin.m2m2.vo.publish.PublisherVO;

public class HttpSenderDefinition extends PublisherDefinition
{
  public String getPublisherTypeName()
  {
    return "HTTP_SENDER";
  }

  public String getDescriptionKey()
  {
    return "publisherEdit.httpSender";
  }

  protected PublisherVO<? extends PublishedPointVO> createPublisherVO()
  {
    return new HttpSenderVO();
  }

  public String getEditPagePath()
  {
    return "web/editHttpSender.jsp";
  }

  public Class<?> getDwrClass()
  {
    return HttpPublisherDwr.class;
  }
}