package com.serotonin.m2m2.bacnet;

import com.serotonin.bacnet4j.type.primitive.OctetString;
import com.serotonin.m2m2.db.dao.DataPointDao;
import com.serotonin.m2m2.db.dao.DataSourceDao;
import com.serotonin.m2m2.module.LifecycleDefinition;
import com.serotonin.m2m2.module.ModuleRegistry;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class Conversion extends LifecycleDefinition
{
  public void postDatabase()
  {
    DataSourceDao dataSourceDao = new DataSourceDao();
    DataPointDao dataPointDao = new DataPointDao();

    List<DataSourceVO<?>> dss = dataSourceDao.getDataSourcesForType("BACnet");
    for (DataSourceVO ds : dss) {
      BACnetIPDataSourceVO oldds = (BACnetIPDataSourceVO)ds;

      com.serotonin.ma.bacnet.ip.BACnetIPDataSourceVO newds = new com.serotonin.ma.bacnet.ip.BACnetIPDataSourceVO();
      newds.setDefinition(ModuleRegistry.getDataSourceDefinition("BACnetIP"));

      newds.setId(oldds.getId());
      newds.setXid(oldds.getXid());
      newds.setName(oldds.getName());
      newds.setEnabled(oldds.isEnabled());

      setAlarmLevel(oldds, newds, 1);
      setAlarmLevel(oldds, newds, 2);
      setAlarmLevel(oldds, newds, 3);

      newds.setPurgeOverride(oldds.isPurgeOverride());
      newds.setPurgeType(oldds.getPurgeType());
      newds.setPurgePeriod(oldds.getPurgePeriod());

      newds.setUpdatePeriodType(oldds.getUpdatePeriodType());
      newds.setUpdatePeriods(oldds.getUpdatePeriods());
      newds.setDeviceId(oldds.getDeviceId());
      newds.setBroadcastAddress(oldds.getBroadcastAddress());
      newds.setPort(oldds.getPort());
      newds.setTimeout(oldds.getTimeout());
      newds.setSegTimeout(oldds.getSegTimeout());
      newds.setSegWindow(oldds.getSegWindow());
      newds.setRetries(oldds.getRetries());
      newds.setCovSubscriptionTimeoutMinutes(oldds.getCovSubscriptionTimeoutMinutes());
      newds.setMaxReadMultipleReferencesSegmented(oldds.getMaxReadMultipleReferencesSegmented());
      newds.setMaxReadMultipleReferencesNonsegmented(oldds.getMaxReadMultipleReferencesNonsegmented());

      dataSourceDao._updateDataSource(newds);

      List<DataPointVO> dpvos = dataPointDao.getDataPoints(newds.getId(), null);
      for (DataPointVO dpvo : dpvos) {
        BACnetIPPointLocatorVO oldl = (BACnetIPPointLocatorVO)dpvo.getPointLocator();
        com.serotonin.ma.bacnet.ip.BACnetIPPointLocatorVO newl = new com.serotonin.ma.bacnet.ip.BACnetIPPointLocatorVO();

        if (StringUtils.isBlank(oldl.getNetworkAddress())) {
          newl.setMac(new OctetString(oldl.getRemoteDeviceIp(), oldl.getRemoteDevicePort()).getDescription());
        } else {
          newl.setMac(oldl.getNetworkAddress());
          newl.setLink(new OctetString(oldl.getRemoteDeviceIp(), oldl.getRemoteDevicePort()).getDescription());
        }

        newl.setNetworkNumber(oldl.getNetworkNumber());
        newl.setRemoteDeviceInstanceNumber(oldl.getRemoteDeviceInstanceNumber());
        newl.setObjectTypeId(oldl.getObjectTypeId());
        newl.setObjectInstanceNumber(oldl.getObjectInstanceNumber());
        newl.setPropertyIdentifierId(oldl.getPropertyIdentifierId());
        newl.setUseCovSubscription(oldl.isUseCovSubscription());
        newl.setSettable(oldl.isSettable());
        newl.setWritePriority(oldl.getWritePriority());
        newl.setDataTypeId(oldl.getDataTypeId());

        dpvo.setPointLocator(newl);

        dataPointDao.updateDataPointShallow(dpvo);
      }
    }
  }

  private void setAlarmLevel(BACnetIPDataSourceVO oldds, com.serotonin.ma.bacnet.ip.BACnetIPDataSourceVO newds, int eventId)
  {
    int level = oldds.getAlarmLevel(eventId, -1);
    if (level != -1)
      newds.setAlarmLevel(eventId, level);
  }
}