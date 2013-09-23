package com.serotonin.ma.bacnet.ip;

import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.m2m2.module.DataSourceDefinition;
import com.serotonin.m2m2.module.Module;
import com.serotonin.m2m2.module.ModuleRegistry;
import com.serotonin.m2m2.module.license.DataSourceTypePointsLimit;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;

public class BACnetIPDefinition extends DataSourceDefinition
{
  public static final String DATA_SOURCE_TYPE = "BACnetIP";

  public void preInitialize()
  {
    ModuleRegistry.addLicenseEnforcement(new DataSourceTypePointsLimit(getModule().getName(), "BACnetIP", 20, "ip-points"));
  }

  public String getDataSourceTypeName()
  {
    return "BACnetIP";
  }

  public String getDescriptionKey()
  {
    return "mod.bacnetIp.dataSource";
  }

  public DataSourceVO<?> createDataSourceVO()
  {
    return new BACnetIPDataSourceVO();
  }

  public String getEditPagePath()
  {
    return "web/editBACnetIP.jspf";
  }

  public Class<?> getDwrClass()
  {
    return BACnetIPEditDwr.class;
  }

  static IpNetwork createNetwork(String broadcastIp, int port, String localBindAddress) {
    return new IpNetwork(broadcastIp, port, localBindAddress);
  }
}