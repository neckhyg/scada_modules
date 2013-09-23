package com.serotonin.ma.bacnet.ip;

import com.serotonin.bacnet4j.npdu.Network;
import com.serotonin.bacnet4j.type.primitive.OctetString;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.vo.dataSource.BasicDataSourceVO;
import com.serotonin.m2m2.web.dwr.util.DwrPermission;
import com.serotonin.ma.bacnet.BACnetEditDwr;
import org.apache.commons.lang3.StringUtils;

public class BACnetIPEditDwr extends BACnetEditDwr
{
  @DwrPermission(user=true)
  public ProcessResult saveBACnetIpDataSource(BasicDataSourceVO basic, int updatePeriods, int updatePeriodType, int deviceId, String localBindAddress, String broadcastAddress, int port, boolean strict, int timeout, int segTimeout, int segWindow, int retries, int covSubscriptionTimeoutMinutes, int maxReadMultipleReferencesSegmented, int maxReadMultipleReferencesNonsegmented)
  {
    BACnetIPDataSourceVO ds = (BACnetIPDataSourceVO)Common.getUser().getEditDataSource();

    ds.setLocalBindAddress(localBindAddress);
    ds.setBroadcastAddress(broadcastAddress);
    ds.setPort(port);

    return saveBACnetDataSource(ds, basic, updatePeriods, updatePeriodType, deviceId, strict, timeout, segTimeout, segWindow, retries, covSubscriptionTimeoutMinutes, maxReadMultipleReferencesSegmented, maxReadMultipleReferencesNonsegmented);
  }

  @DwrPermission(user=true)
  public ProcessResult saveBACnetIPPointLocator(int id, String xid, String name, BACnetIPPointLocatorVO locator)
  {
    return validatePoint(id, xid, name, locator, null);
  }

  protected ProcessResult saveBacnetPoint(DataPointVO dp)
  {
    BACnetIPPointLocatorVO l = (BACnetIPPointLocatorVO)dp.getPointLocator();
    return validatePoint(dp.getId(), dp.getXid(), dp.getName(), l, null, false);
  }

  @DwrPermission(user=true)
  public void sendBACnetIpWhoIs(int deviceId, String localBindAddress, String broadcastIp, int port, int timeout, int segTimeout, int segWindow, int retries, int maxReadMultipleReferencesSegmented, int maxReadMultipleReferencesNonsegmented)
  {
    Network network = BACnetIPDefinition.createNetwork(broadcastIp, port, localBindAddress);
    sendBACnetWhoIs(deviceId, network, timeout, segTimeout, segWindow, retries, maxReadMultipleReferencesSegmented, maxReadMultipleReferencesNonsegmented);
  }

  @DwrPermission(user=true)
  public ProcessResult sendIpObjectListRequest(int deviceId, String localBindAddress, String broadcastIp, int port, int timeout, int segTimeout, int segWindow, int retries, int maxReadMultipleReferencesSegmented, int maxReadMultipleReferencesNonsegmented, int networkNumber, String mac, String linkIp, int remoteDeviceId)
  {
    OctetString link = null;
    if (!StringUtils.isBlank(linkIp))
      link = new OctetString(linkIp, port);
    Network network = BACnetIPDefinition.createNetwork(broadcastIp, port, localBindAddress);
    return sendObjectListRequest(deviceId, network, timeout, segTimeout, segWindow, retries, maxReadMultipleReferencesSegmented, maxReadMultipleReferencesNonsegmented, networkNumber, mac, link, remoteDeviceId);
  }
}