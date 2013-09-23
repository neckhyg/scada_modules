package com.serotonin.m2m2.openv.dwr;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.openv.OpenV4JDataSourceVO;
import com.serotonin.m2m2.openv.OpenV4JPointLocatorVO;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.vo.dataSource.BasicDataSourceVO;
import com.serotonin.m2m2.vo.permission.Permissions;
import com.serotonin.m2m2.web.dwr.DataSourceEditDwr;
import com.serotonin.m2m2.web.dwr.util.DwrPermission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.openv4j.DataPoint;
import net.sf.openv4j.Devices;
import net.sf.openv4j.Group;
import net.sf.openv4j.Protocol;

public class OpenVEditDwr extends DataSourceEditDwr
{
  @DwrPermission(user=true)
  public void searchOpenV4J(String commPortId)
  {
    User user = Common.getUser();
    Permissions.ensureDataSourcePermission(user);

    OpenV4JDiscovery discovery = OpenV4JDiscovery.searchDataPoints(getTranslations(), commPortId);
    user.setTestingUtility(discovery);
  }
  @DwrPermission(user=true)
  public void detectOpenV4JDevice(String commPortId) {
    User user = Common.getUser();
    Permissions.ensureDataSourcePermission(user);

    OpenV4JDiscovery discovery = OpenV4JDiscovery.detectDevice(getTranslations(), commPortId);

    user.setTestingUtility(discovery);
  }

  @DwrPermission(user=true)
  public ProcessResult saveOpenV4JDataSource(BasicDataSourceVO basic, String commPortId, int updatePeriodType, int updatePeriods, String device, String protocol) {
    OpenV4JDataSourceVO ds = (OpenV4JDataSourceVO)Common.getUser().getEditDataSource();

    setBasicProps(ds, basic);
    ds.setCommPortId(commPortId);
    ds.setUpdatePeriodType(updatePeriodType);
    ds.setUpdatePeriods(updatePeriods);
    ds.setDevice(Devices.valueOf(device));
    ds.setProtocol(Protocol.valueOf(protocol));
    return tryDataSourceSave(ds);
  }
  @DwrPermission(user=true)
  public ProcessResult saveOpenV4JPointLocator(int id, String xid, String name, OpenV4JPointLocatorVO locator) {
    return validatePoint(id, xid, name, locator, null);
  }
  @DwrPermission(user=true)
  public Map<String, Object> openV4JSearchUpdate() {
    Map result = new HashMap();
    OpenV4JDiscovery test = (OpenV4JDiscovery)Common.getUser().getTestingUtility(OpenV4JDiscovery.class);
    if (test == null) {
      return null;
    }
    test.addUpdateInfo(result);
    return result;
  }
  @DwrPermission(user=true)
  public Map<String, Object> openV4JDetectDeviceUpdate() {
    Map result = new HashMap();
    OpenV4JDiscovery test = (OpenV4JDiscovery)Common.getUser().getTestingUtility(OpenV4JDiscovery.class);
    if (test == null) {
      return null;
    }
    test.addDeviceInfo(result);
    return result;
  }
  @DwrPermission(user=true)
  public OpenV4JProtocolBean[] getOpenV4jProtocolsOfDevice(String deviceName) {
    return OpenV4JProtocolBean.fromDevice(Devices.valueOf(deviceName));
  }
  @DwrPermission(user=true)
  public OpenV4JDataPointBean[] getOpenV4jDataPointsOfGroup(String groupName) {
    Group g = Group.valueOf(groupName);
    List result = new ArrayList();
    for (DataPoint dp : DataPoint.values()) {
      if (dp.getGroup().equals(g)) {
        result.add(new OpenV4JDataPointBean(dp));
      }
    }
    return (OpenV4JDataPointBean[])result.toArray(new OpenV4JDataPointBean[result.size()]);
  }
  @DwrPermission(user=true)
  public DataPointVO addOpenV4JPoint(String openV4JEnumName) {
    DataPointVO result = getPoint(-1, null);
    OpenV4JPointLocatorVO locator = (OpenV4JPointLocatorVO)result.getPointLocator();

    OpenV4JDiscovery test = (OpenV4JDiscovery)Common.getUser().getTestingUtility(OpenV4JDiscovery.class);
    if (test == null) {
      return null;
    }

    DataPoint dp = DataPoint.valueOf(openV4JEnumName);
    result.setName(dp.getGroup().getLabel() + " " + dp.getLabel());
    locator.setDataPointName(dp.getName());
    return result;
  }
}