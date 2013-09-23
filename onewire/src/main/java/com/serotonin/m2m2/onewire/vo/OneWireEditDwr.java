package com.serotonin.m2m2.onewire.vo;

import com.dalsemi.onewire.OneWireException;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.onewire.common.Network;
import com.serotonin.m2m2.onewire.common.NetworkPath;
import com.serotonin.m2m2.onewire.rt.OneWireDataSourceRT;
import com.serotonin.m2m2.rt.RuntimeManager;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.vo.dataSource.BasicDataSourceVO;
import com.serotonin.m2m2.web.dwr.DataSourceEditDwr;
import com.serotonin.m2m2.web.dwr.util.DwrPermission;
import java.util.ArrayList;
import java.util.List;

public class OneWireEditDwr extends DataSourceEditDwr
{
  @DwrPermission(user=true)
  public ProcessResult saveOneWireDataSource(BasicDataSourceVO basic, String commPortId, int updatePeriodType, int updatePeriods, int rescanPeriodType, int rescanPeriods)
  {
    OneWireDataSourceVO ds = (OneWireDataSourceVO)Common.getUser().getEditDataSource();

    setBasicProps(ds, basic);
    ds.setCommPortId(commPortId);
    ds.setUpdatePeriodType(updatePeriodType);
    ds.setUpdatePeriods(updatePeriods);
    ds.setRescanPeriodType(rescanPeriodType);
    ds.setRescanPeriods(rescanPeriods);

    return tryDataSourceSave(ds);
  }
  @DwrPermission(user=true)
  public ProcessResult saveOneWirePointLocator(int id, String xid, String name, OneWirePointLocatorVO locator) {
    return validatePoint(id, xid, name, locator, null);
  }
  @DwrPermission(user=true)
  public ProcessResult readOneWireNetwork(String commPointId) {
    ProcessResult response = new ProcessResult();

    Network network = null;
    try
    {
      OneWireDataSourceVO ds = (OneWireDataSourceVO)Common.getUser().getEditDataSource();
      OneWireDataSourceRT rt = (OneWireDataSourceRT)Common.runtimeManager.getRunningDataSource(ds.getId());
      if (rt != null)
      {
        network = rt.getNetwork();
      }
      if (network == null)
      {
        network = new Network(commPointId);
      }
      try {
        network.lock();
        network.quickInitialize();

        List addresses = network.getAddresses();
        List devices = new ArrayList();
        for (Long address : addresses) {
          NetworkPath path = network.getNetworkPath(address);
          if (!path.isCoupler()) {
            devices.add(path.getTargetInfo());
          }
        }
        response.addData("devices", devices);
      }
      finally {
        network.unlock();
      }
    }
    catch (Exception e) {
      response.addGenericMessage("common.default", new Object[] { e.getMessage() });
    }
    finally {
      try {
        if (network != null) {
          network.terminate();
        }
      }
      catch (OneWireException e)
      {
      }
    }
    return response;
  }
  @DwrPermission(user=true)
  public DataPointVO addOneWirePoint(String address) {
    DataPointVO dp = getPoint(-1, null);
    OneWirePointLocatorVO locator = (OneWirePointLocatorVO)dp.getPointLocator();
    locator.setAddress(address);
    return dp;
  }
}