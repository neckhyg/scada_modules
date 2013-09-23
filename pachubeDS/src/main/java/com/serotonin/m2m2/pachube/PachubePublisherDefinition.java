package com.serotonin.m2m2.pachube;

import com.serotonin.m2m2.module.PublisherDefinition;
import com.serotonin.m2m2.pachube.pub.PachubePublisherDwr;
import com.serotonin.m2m2.pachube.pub.PachubeSenderVO;
import com.serotonin.m2m2.vo.publish.PublishedPointVO;
import com.serotonin.m2m2.vo.publish.PublisherVO;

public class PachubePublisherDefinition extends PublisherDefinition
{
  public String getPublisherTypeName()
  {
    return "PACHUBE";
  }

  public String getDescriptionKey()
  {
    return "publisherEdit.pachube";
  }

  protected PublisherVO<? extends PublishedPointVO> createPublisherVO()
  {
    return new PachubeSenderVO();
  }

  public String getEditPagePath()
  {
    return "web/editPachubePub.jsp";
  }

  public Class<?> getDwrClass()
  {
    return PachubePublisherDwr.class;
  }
}