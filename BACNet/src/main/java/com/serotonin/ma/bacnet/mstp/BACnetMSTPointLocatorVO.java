package com.serotonin.ma.bacnet.mstp;

import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.ma.bacnet.BACnetPointLocatorRT;
import com.serotonin.ma.bacnet.BACnetPointLocatorVO;
import java.util.List;

public class BACnetMSTPointLocatorVO extends BACnetPointLocatorVO
{
  public PointLocatorRT createRuntime()
  {
    return new BACnetPointLocatorRT(this);
  }

  public void addProperties(List<TranslatableMessage> list)
  {
    super.addProperties(list);
    AuditEventType.addPropertyMessage(list, "mod.bacnetMstp.remoteDeviceLinkStation", getLink());
  }

  public void addPropertyChanges(List<TranslatableMessage> list, Object o)
  {
    super.addPropertyChanges(list, o);
    BACnetMSTPointLocatorVO from = (BACnetMSTPointLocatorVO)o;
    AuditEventType.maybeAddPropertyChangeMessage(list, "mod.bacnetMstp.remoteDeviceLinkStation", from.getLink(), getLink());
  }
}