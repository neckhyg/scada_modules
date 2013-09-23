package com.serotonin.ma.bacnet.mstp;

import com.serotonin.bacnet4j.npdu.mstp.MasterNode;
import com.serotonin.bacnet4j.npdu.mstp.MstpNetwork;
import com.serotonin.io.serial.SerialParameters;
import com.serotonin.m2m2.module.DataSourceDefinition;
import com.serotonin.m2m2.module.Module;
import com.serotonin.m2m2.module.ModuleRegistry;
import com.serotonin.m2m2.module.license.DataSourceTypePointsLimit;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;

public class BACnetMSTPDefinition extends DataSourceDefinition
{
  public void preInitialize()
  {
    ModuleRegistry.addLicenseEnforcement(new DataSourceTypePointsLimit(getModule().getName(), "BACnetMSTP", 20, "mstp-points"));
  }

  public String getDataSourceTypeName()
  {
    return "BACnetMSTP";
  }

  public String getDescriptionKey()
  {
    return "mod.bacnetMstp.dataSource";
  }

  public DataSourceVO<?> createDataSourceVO()
  {
    return new BACnetMSTPDataSourceVO();
  }

  public String getEditPagePath()
  {
    return "web/editBACnetMSTP.jspf";
  }

  public Class<?> getDwrClass()
  {
    return BACnetMSTPEditDwr.class;
  }

  static MstpNetwork createNetwork(String commPortId, int baudRate, int thisStation, int retryCount) {
    SerialParameters params = new SerialParameters();
    params.setCommPortId(commPortId);
    params.setBaudRate(baudRate);

    MasterNode node = new MasterNode(params, (byte)thisStation, retryCount);

    return new MstpNetwork(node);
  }
}