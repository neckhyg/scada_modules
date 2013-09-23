package com.serotonin.ma.bacnet.mstp;

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

public class BACnetMSTPEditDwr extends BACnetEditDwr
{
  @DwrPermission(user=true)
  public ProcessResult saveBACnetMstpDataSource(BasicDataSourceVO basic, int updatePeriods, int updatePeriodType, int deviceId, String commPortId, int baudRate, int thisStation, int retryCount, boolean strict, int timeout, int segTimeout, int segWindow, int retries, int covSubscriptionTimeoutMinutes, int maxReadMultipleReferencesSegmented, int maxReadMultipleReferencesNonsegmented)
  {
    BACnetMSTPDataSourceVO ds = (BACnetMSTPDataSourceVO)Common.getUser().getEditDataSource();

    ds.setCommPortId(commPortId);
    ds.setBaudRate(baudRate);
    ds.setThisStation(thisStation);
    ds.setRetryCount(retryCount);

    return saveBACnetDataSource(ds, basic, updatePeriods, updatePeriodType, deviceId, strict, timeout, segTimeout, segWindow, retries, covSubscriptionTimeoutMinutes, maxReadMultipleReferencesSegmented, maxReadMultipleReferencesNonsegmented);
  }

  @DwrPermission(user=true)
  public ProcessResult saveBACnetMSTPPointLocator(int id, String xid, String name, BACnetMSTPointLocatorVO locator)
  {
    return validatePoint(id, xid, name, locator, null);
  }

  protected ProcessResult saveBacnetPoint(DataPointVO dp)
  {
    BACnetMSTPointLocatorVO l = (BACnetMSTPointLocatorVO)dp.getPointLocator();
    return validatePoint(dp.getId(), dp.getXid(), dp.getName(), l, null, false);
  }

  @DwrPermission(user=true)
  public void sendBACnetMstpWhoIs(int deviceId, String commPortId, int baudRate, int thisStation, int retryCount, int timeout, int segTimeout, int segWindow, int retries, int maxReadMultipleReferencesSegmented, int maxReadMultipleReferencesNonsegmented)
  {
    Network network = BACnetMSTPDefinition.createNetwork(commPortId, baudRate, thisStation, retryCount);
    sendBACnetWhoIs(deviceId, network, timeout, segTimeout, segWindow, retries, maxReadMultipleReferencesSegmented, maxReadMultipleReferencesNonsegmented);
  }

  @DwrPermission(user=true)
  public ProcessResult sendMstpObjectListRequest(int deviceId, String commPortId, int baudRate, int thisStation, int retryCount, int timeout, int segTimeout, int segWindow, int retries, int maxReadMultipleReferencesSegmented, int maxReadMultipleReferencesNonsegmented, int networkNumber, String mac, String linkStation, int remoteDeviceId)
  {
    OctetString link = null;
    if (!StringUtils.isBlank(linkStation))
      link = new OctetString(linkStation);
    Network network = BACnetMSTPDefinition.createNetwork(commPortId, baudRate, thisStation, retryCount);
    return sendObjectListRequest(deviceId, network, timeout, segTimeout, segWindow, retries, maxReadMultipleReferencesSegmented, maxReadMultipleReferencesNonsegmented, networkNumber, mac, link, remoteDeviceId);
  }
}