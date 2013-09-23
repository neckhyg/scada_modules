package com.serotonin.m2m2.mbus.dwr;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.mbus.MBusConnectionType;
import com.serotonin.m2m2.mbus.MBusDataSourceVO;
import com.serotonin.m2m2.mbus.MBusPointLocatorVO;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.vo.dataSource.BasicDataSourceVO;
import com.serotonin.m2m2.vo.permission.Permissions;
import com.serotonin.m2m2.web.dwr.DataSourceEditDwr;
import com.serotonin.m2m2.web.dwr.util.DwrPermission;
import java.util.HashMap;
import java.util.Map;
import net.sf.mbus4j.dataframes.MBusMedium;
import net.sf.mbus4j.dataframes.MBusResponseFramesContainer;
import net.sf.mbus4j.dataframes.ResponseFrameContainer;
import net.sf.mbus4j.dataframes.UserDataResponse;
import net.sf.mbus4j.dataframes.datablocks.DataBlock;
import net.sf.mbus4j.dataframes.datablocks.dif.DataFieldCode;
import net.sf.mbus4j.dataframes.datablocks.dif.FunctionField;
import net.sf.mbus4j.dataframes.datablocks.vif.SiPrefix;
import net.sf.mbus4j.dataframes.datablocks.vif.UnitOfMeasurement;
import net.sf.mbus4j.dataframes.datablocks.vif.Vif;
import net.sf.mbus4j.dataframes.datablocks.vif.VifTypes;
import net.sf.mbus4j.dataframes.datablocks.vif.Vife;
import net.sf.mbus4j.dataframes.datablocks.vif.VifeTypes;

public class MBusEditDwr extends DataSourceEditDwr
{
  @DwrPermission(user=true)
  public ProcessResult saveMBusDataSourceConnection(BasicDataSourceVO basic, String connectionType, String commPortId, String phonenumber, int baudRate, int flowControlIn, int flowControlOut, int dataBits, int stopBits, int parity, int updatePeriodType, int updatePeriods)
  {
    MBusDataSourceVO ds = (MBusDataSourceVO)Common.getUser().getEditDataSource();

    setBasicProps(ds, basic);
    ds.setConnectionType(MBusConnectionType.valueOf(connectionType));
    ds.setPhonenumber(phonenumber);
    ds.setCommPortId(commPortId);
    ds.setBaudRate(baudRate);
    ds.setFlowControlIn(flowControlIn);
    ds.setFlowControlOut(flowControlOut);
    ds.setDataBits(dataBits);
    ds.setStopBits(stopBits);
    ds.setParity(parity);
    ds.setUpdatePeriodType(updatePeriodType);
    ds.setUpdatePeriods(updatePeriods);
    return tryDataSourceSave(ds);
  }
  @DwrPermission(user=true)
  public ProcessResult saveMBusPointLocator(int id, String xid, String name, MBusPointLocatorVO locator) {
    return validatePoint(id, xid, name, locator, null);
  }

  @DwrPermission(user=true)
  public void searchMBusByPrimaryAddressing(String commPortId, String phonenumber, int baudrate, int flowControlIn, int flowcontrolOut, int dataBits, int stopBits, int parity, String firstPrimaryAddress, String lastPrimaryAddress)
  {
    User user = Common.getUser();
    Permissions.ensureDataSourcePermission(user);

    MBusDiscovery discovery = MBusDiscovery.createPrimaryAddressingSearch(getTranslations(), commPortId, phonenumber, baudrate, flowControlIn, flowcontrolOut, dataBits, stopBits, parity, Integer.parseInt(firstPrimaryAddress, 16), Integer.parseInt(lastPrimaryAddress, 16));

    user.setTestingUtility(discovery);
  }

  @DwrPermission(user=true)
  public void searchMBusBySecondaryAddressing(String commPortId, String phonenumber, int baudrate, int flowControlIn, int flowcontrolOut, int dataBits, int stopBits, int parity) {
    User user = Common.getUser();
    Permissions.ensureDataSourcePermission(user);

    MBusDiscovery discovery = MBusDiscovery.createSecondaryAddressingSearch(getTranslations(), commPortId, phonenumber, baudrate, flowControlIn, flowcontrolOut, dataBits, stopBits, parity);

    user.setTestingUtility(discovery);
  }
  @DwrPermission(user=true)
  public Map<String, Object> mBusSearchUpdate() {
    Map result = new HashMap();
    MBusDiscovery test = (MBusDiscovery)Common.getUser().getTestingUtility(MBusDiscovery.class);
    if (test == null) {
      return null;
    }
    test.addUpdateInfo(result);
    return result;
  }
  @DwrPermission(user=true)
  public Map<String, Object> getMBusResponseFrames(int deviceIndex) {
    Map result = new HashMap();
    MBusDiscovery test = (MBusDiscovery)Common.getUser().getTestingUtility(MBusDiscovery.class);
    if (test == null) {
      return null;
    }
    test.getDeviceDetails(deviceIndex, result);
    return result;
  }
  @DwrPermission(user=true)
  public DataPointVO addMBusPoint(String addressing, int deviceIndex, int rsIndex, int dbIndex) {
    DataPointVO dp = getPoint(-1, null);
    MBusPointLocatorVO locator = (MBusPointLocatorVO)dp.getPointLocator();

    MBusDiscovery test = (MBusDiscovery)Common.getUser().getTestingUtility(MBusDiscovery.class);
    if (test == null) {
      return null;
    }
    MBusResponseFramesContainer dev = test.getDevice(deviceIndex);
    if ((dev.getResponseFrameContainer(rsIndex).getResponseFrame() instanceof UserDataResponse)) {
      UserDataResponse udr = (UserDataResponse)dev.getResponseFrameContainer(rsIndex).getResponseFrame();
      DataBlock db = udr.getDataBlock(dbIndex);

      dp.setName(db.getParamDescr());

      locator.setAddressing(addressing);
      locator.setAddress(dev.getAddress());
      locator.setMedium(dev.getMedium().getLabel());
      locator.setManufacturer(dev.getManufacturer());
      locator.setVersion(dev.getVersion());
      locator.setIdentNumber(dev.getIdentNumber());
      locator.setResponseFrame(dev.getResponseFrameContainer(rsIndex).getName());
      locator.setDeviceUnit(db.getSubUnit());
      locator.setDifCode(db.getDataFieldCode().getLabel());
      locator.setFunctionField(db.getFunctionField().getLabel());
      locator.setStorageNumber(db.getStorageNumber());
      locator.setTariff(db.getTariff());
      locator.setSiPrefix(db.getVif().getSiPrefix() == null ? null : db.getVif().getSiPrefix().getLabel());
      locator.setUnitOfMeasurement(db.getVif().getUnitOfMeasurement() == null ? null : db.getVif().getUnitOfMeasurement().getLabel());

      locator.setVifType(db.getVif().getVifType().getLabel());
      locator.setVifLabel(db.getVif().getLabel());
      locator.setExponent(db.getVif().getExponent());
      if (db.getVifes() != null) {
        String[] vifeLabels = new String[db.getVifes().length];
        String[] vifeTypes = new String[db.getVifes().length];
        for (int i = 0; i < vifeLabels.length; i++) {
          vifeTypes[i] = db.getVifes()[i].getVifeType().getLabel();
          vifeLabels[i] = db.getVifes()[i].getLabel();
        }
        locator.setVifeTypes(vifeTypes);
        locator.setVifeLabels(vifeLabels);
      }
      else {
        locator.setVifeLabels(null);
      }
    }
    return dp;
  }
}