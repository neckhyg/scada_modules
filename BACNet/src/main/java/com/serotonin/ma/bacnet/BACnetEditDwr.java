package com.serotonin.ma.bacnet;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.npdu.Network;
import com.serotonin.bacnet4j.transport.Transport;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.OctetString;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.ProcessMessage;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.vo.dataSource.BasicDataSourceVO;
import com.serotonin.m2m2.vo.permission.Permissions;
import com.serotonin.m2m2.web.dwr.DataSourceEditDwr;
import com.serotonin.m2m2.web.dwr.util.DwrPermission;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class BACnetEditDwr extends DataSourceEditDwr
{
  private static final Log LOG = LogFactory.getLog(BACnetEditDwr.class);

  protected ProcessResult saveBACnetDataSource(BACnetDataSourceVO<?> ds, BasicDataSourceVO basic, int updatePeriods, int updatePeriodType, int deviceId, boolean strict, int timeout, int segTimeout, int segWindow, int retries, int covSubscriptionTimeoutMinutes, int maxReadMultipleReferencesSegmented, int maxReadMultipleReferencesNonsegmented)
  {
    setBasicProps(ds, basic);
    ds.setUpdatePeriods(updatePeriods);
    ds.setUpdatePeriodType(updatePeriodType);
    ds.setDeviceId(deviceId);
    ds.setStrict(strict);
    ds.setTimeout(timeout);
    ds.setSegTimeout(segTimeout);
    ds.setSegWindow(segWindow);
    ds.setRetries(retries);
    ds.setCovSubscriptionTimeoutMinutes(covSubscriptionTimeoutMinutes);
    ds.setMaxReadMultipleReferencesSegmented(maxReadMultipleReferencesSegmented);
    ds.setMaxReadMultipleReferencesNonsegmented(maxReadMultipleReferencesNonsegmented);

    return tryDataSourceSave(ds);
  }

  protected void sendBACnetWhoIs(int deviceId, Network network, int timeout, int segTimeout, int segWindow, int retries, int maxReadMultipleReferencesSegmented, int maxReadMultipleReferencesNonsegmented)
  {
    User user = Common.getUser();
    Permissions.ensureDataSourcePermission(user);

    BACnetDiscovery discovery = new BACnetDiscovery(getTranslations(), deviceId, network, timeout, segTimeout, segWindow, retries, maxReadMultipleReferencesSegmented, maxReadMultipleReferencesNonsegmented);

    user.setTestingUtility(discovery);
  }
  @DwrPermission(user=true)
  public Map<String, Object> bacnetWhoIsUpdate() {
    Map result = new HashMap();
    BACnetDiscovery discovery = (BACnetDiscovery)Common.getUser().getTestingUtility(BACnetDiscovery.class);
    if (discovery == null) {
      return null;
    }
    discovery.addUpdateInfo(result);

    return result;
  }
  @DwrPermission(user=true)
  public ProcessResult sendDeviceObjectListRequest(int remoteDeviceId, int networkNumber, String mac, String linkStr) {
    BACnetDiscovery discovery = (BACnetDiscovery)Common.getUser().getTestingUtility(BACnetDiscovery.class);
    if (discovery != null) {
      LocalDevice localDevice = discovery.getLocalDevice();
      OctetString link = null;
      if (!org.apache.commons.lang3.StringUtils.isBlank(linkStr))
        link = new OctetString(linkStr);
      return getObjectListRequest(localDevice, networkNumber, mac, link, remoteDeviceId);
    }
    return null;
  }

  protected ProcessResult sendObjectListRequest(int deviceId, Network network, int timeout, int segTimeout, int segWindow, int retries, int maxReadMultipleReferencesSegmented, int maxReadMultipleReferencesNonsegmented, int networkNumber, String mac, OctetString link, int remoteDeviceId)
  {
    Transport transport = new Transport(network, true);
    transport.setTimeout(timeout);
    transport.setSegTimeout(segTimeout);
    transport.setSegWindow(segWindow);
    transport.setRetries(retries);
    LocalDevice localDevice = new LocalDevice(deviceId, transport);
    localDevice.setMaxReadMultipleReferencesSegmented(maxReadMultipleReferencesSegmented);
    localDevice.setMaxReadMultipleReferencesNonsegmented(maxReadMultipleReferencesNonsegmented);
    try
    {
      localDevice.initialize();
      ProcessResult localProcessResult1 = getObjectListRequest(localDevice, networkNumber, mac, link, remoteDeviceId);
      return localProcessResult1;
    }
    catch (Exception e)
    {
      ProcessResult result = new ProcessResult();
      result.addData("error", e.getMessage());
      ProcessResult localProcessResult2 = result;
      return localProcessResult2; } finally { localDevice.terminate(); }
  }

  private ProcessResult getObjectListRequest(LocalDevice localDevice, int networkNumber, String mac, OctetString link, int remoteDeviceId)
  {
    Address address = new Address(networkNumber, mac);

    ProcessResult result = new ProcessResult();
    try {
      RemoteDevice d = localDevice.findRemoteDevice(address, link, remoteDeviceId);
      List objects = BACnetDiscovery.getObjects(localDevice, d);
      Collections.sort(objects, new Comparator()
      {
        //public int compare((Object)BACnetObjectBean o1, (Object)BACnetObjectBean o2) {
              public int compare(Object o1, Object o2) {
          return com.serotonin.util.StringUtils.compareStrings(o1.toString(), o2.toString(), true);
        }
      });
      result.addData("deviceId", Integer.valueOf(d.getInstanceNumber()));
      result.addData("deviceName", d.getName());
      result.addData("deviceNetwork", Integer.valueOf(d.getAddress().getNetworkNumber().intValue()));
      result.addData("deviceMac", d.getAddress().getMacAddress().getMacAddressDottedString());
      if (link != null)
        result.addData("deviceLink", d.getLinkService().toIpPortString());
      result.addData("deviceDescription", BACnetDiscovery.getDeviceDescription(d));
      result.addData("deviceObjects", objects);
    }
    catch (Exception e) {
      LOG.warn(e);
      result.addData("error", e.getMessage());
    }

    return result;
  }

  @DwrPermission(user=true)
  public DataPointVO addBacnetPoint(int networkNumber, String mac, String link, int deviceInstanceNumber, BACnetObjectBean bean) {
    DataPointVO dp = getPoint(-1, null);
    BACnetPointLocatorVO locator = (BACnetPointLocatorVO)dp.getPointLocator();

    dp.setName(bean.getObjectName());

    mac = new OctetString(mac).getDescription();

    locator.setNetworkNumber(networkNumber);
    locator.setMac(mac);
    locator.setLink(link);
    locator.setRemoteDeviceInstanceNumber(deviceInstanceNumber);
    locator.setObjectTypeId(bean.getObjectTypeId());
    locator.setObjectInstanceNumber(bean.getInstanceNumber());
    locator.setPropertyIdentifierId(PropertyIdentifier.presentValue.intValue());
    locator.setDataTypeId(bean.getDataTypeId());
    locator.setUseCovSubscription(bean.isCov());
    locator.setSettable(settableDefault(bean.getObjectTypeId()));

    return dp;
  }

  private boolean settableDefault(int typeId) {
    return (typeId == ObjectType.analogOutput.intValue()) || (typeId == ObjectType.binaryOutput.intValue()) || (typeId == ObjectType.multiStateOutput.intValue());
  }

  @DwrPermission(user=true)
  public void cancelDiscovery() {
    BACnetDiscovery discovery = (BACnetDiscovery)Common.getUser().getTestingUtility(BACnetDiscovery.class);
    if (discovery != null) {
      discovery.cancel();
      Common.getUser().setTestingUtility(null);
    }
  }

  @DwrPermission(user=true)
  public List<DataPointVO> createPointsFromObjects(int networkNumber, String mac, String link, int deviceInstanceNumber, List<BACnetObjectBean> beans) {
    for (BACnetObjectBean bean : beans) {
      if (org.apache.commons.lang3.StringUtils.isEmpty(bean.getObjectName())) {
        bean.setObjectName(bean.getObjectTypeDescription());
      }
      DataPointVO dp = addBacnetPoint(networkNumber, mac, link, deviceInstanceNumber, bean);
      ProcessResult result = saveBacnetPoint(dp);

      if (result.getHasMessages()) {
        for (ProcessMessage m : result.getMessages())
          LOG.warn(m.toString(getTranslations()));
        break;
      }
    }

    return getPoints();
  }

  protected abstract ProcessResult saveBacnetPoint(DataPointVO paramDataPointVO);
}