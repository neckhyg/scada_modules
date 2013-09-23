package com.serotonin.ma.bacnet.ip;

import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.ma.bacnet.BACnetPointLocatorRT;
import com.serotonin.ma.bacnet.BACnetPointLocatorVO;
import java.util.List;

public class BACnetIPPointLocatorVO extends BACnetPointLocatorVO
{
  public PointLocatorRT createRuntime()
  {
    return new BACnetPointLocatorRT(this);
  }

  public void addProperties(List<TranslatableMessage> list)
  {
    super.addProperties(list);
    AuditEventType.addPropertyMessage(list, "mod.bacnetIp.remoteDeviceLinkIp", getLink());
  }

  public void addPropertyChanges(List<TranslatableMessage> list, Object o)
  {
    super.addPropertyChanges(list, o);
    BACnetIPPointLocatorVO from = (BACnetIPPointLocatorVO)o;
    AuditEventType.maybeAddPropertyChangeMessage(list, "mod.bacnetIp.remoteDeviceLinkIp", from.getLink(), getLink());
  }
}