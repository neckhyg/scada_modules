package com.serotonin.m2m2.persistent;

import com.serotonin.m2m2.module.Module;
import com.serotonin.m2m2.module.ModuleRegistry;
import com.serotonin.m2m2.module.PublisherDefinition;
import com.serotonin.m2m2.module.license.PublisherTypePointsLimit;
import com.serotonin.m2m2.persistent.dwr.PersistentPublisherDwr;
import com.serotonin.m2m2.persistent.pub.PersistentSenderVO;
import com.serotonin.m2m2.vo.publish.PublishedPointVO;
import com.serotonin.m2m2.vo.publish.PublisherVO;

public class PersistentPublisherDefinition extends PublisherDefinition
{
  public void preInitialize()
  {
//    ModuleRegistry.addLicenseEnforcement(new PublisherTypePointsLimit(getModule().getName(), "PERSISTENT", 5, null));
  }

  public String getPublisherTypeName()
  {
    return "PERSISTENT";
  }

  public String getDescriptionKey()
  {
    return "publisherEdit.persistent";
  }

  protected PublisherVO<? extends PublishedPointVO> createPublisherVO()
  {
    return new PersistentSenderVO();
  }

  public String getEditPagePath()
  {
    return "web/editPersistentPub.jspf";
  }

  public Class<?> getDwrClass()
  {
    return PersistentPublisherDwr.class;
  }
}