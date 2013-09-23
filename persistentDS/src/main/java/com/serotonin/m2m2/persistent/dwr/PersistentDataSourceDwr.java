package com.serotonin.m2m2.persistent.dwr;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.persistent.ds.PersistentDataSourceRT;
import com.serotonin.m2m2.persistent.ds.PersistentDataSourceVO;
import com.serotonin.m2m2.persistent.ds.PersistentPointLocatorVO;
import com.serotonin.m2m2.rt.RuntimeManager;
import com.serotonin.m2m2.util.DateUtils;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.vo.dataSource.BasicDataSourceVO;
import com.serotonin.m2m2.web.dwr.DataSourceEditDwr;
import com.serotonin.m2m2.web.dwr.util.DwrPermission;
import com.serotonin.util.NumberUtils;

public class PersistentDataSourceDwr extends DataSourceEditDwr
{
  @DwrPermission(user=true)
  public ProcessResult savePersistentDataSource(BasicDataSourceVO basic, int port, String authorizationKey, boolean acceptPointUpdates)
  {
    PersistentDataSourceVO ds = (PersistentDataSourceVO)Common.getUser().getEditDataSource();

    setBasicProps(ds, basic);
    ds.setPort(port);
    ds.setAuthorizationKey(authorizationKey);
    ds.setAcceptPointUpdates(acceptPointUpdates);

    return tryDataSourceSave(ds);
  }
  @DwrPermission(user=true)
  public ProcessResult savePersistentPointLocator(int id, String xid, String name, PersistentPointLocatorVO locator) {
    return validatePoint(id, xid, name, locator, null);
  }
  @DwrPermission(user=true)
  public ProcessResult getPersistentStatus() {
    PersistentDataSourceVO ds = (PersistentDataSourceVO)Common.getUser().getEditDataSource();
    PersistentDataSourceRT rt = (PersistentDataSourceRT)Common.runtimeManager.getRunningDataSource(ds.getId());

    ProcessResult response = new ProcessResult();
    if (rt == null) {
      response.addGenericMessage("dsEdit.persistent.status.notEnabled", new Object[0]);
    } else {
      int conns = rt.getConnectionCount();
      if (conns == 0) {
        response.addGenericMessage("dsEdit.persistent.status.noConnections", new Object[0]);
      } else {
        long now = System.currentTimeMillis();
        for (int i = 0; i < conns; i++) {
          response.addGenericMessage("dsEdit.persistent.status.connection", new Object[] { rt.getConnectionAddress(i), DateUtils.getDuration(now - rt.getConnectionTime(i)), NumberUtils.countDescription(rt.getPacketsReceived(i)) });
        }
      }

    }

    return response;
  }
}