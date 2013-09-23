package com.serotonin.m2m2.wibox;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.vo.dataSource.BasicDataSourceVO;
import com.serotonin.m2m2.vo.permission.Permissions;
import com.serotonin.m2m2.web.dwr.DataSourceEditDwr;
import com.serotonin.m2m2.web.dwr.util.DwrPermission;
import java.util.HashMap;
import java.util.Map;

public class WiBoxEditDwr extends DataSourceEditDwr
{
  @DwrPermission(user=true)
  public ProcessResult saveDataSource(BasicDataSourceVO basic, String password)
  {
    WiBoxHttpDataSourceVO ds = (WiBoxHttpDataSourceVO)Common.getUser().getEditDataSource();

    setBasicProps(ds, basic);
    ds.setPassword(password);

    return tryDataSourceSave(ds);
  }
  @DwrPermission(user=true)
  public ProcessResult savePointLocator(int id, String xid, String name, WiBoxHttpPointLocatorVO locator) {
    return validatePoint(id, xid, name, locator, null);
  }
  @DwrPermission(user=true)
  public void wiBoxHttpListenForData(String password) {
    User user = Common.getUser();
    Permissions.ensureDataSourcePermission(user);
    user.setTestingUtility(new WiBoxHttpDataListener(getTranslations(), password));
  }
  @DwrPermission(user=true)
  public Map<String, Object> wiBoxHttpListenerUpdate() {
    WiBoxHttpDataListener l = (WiBoxHttpDataListener)Common.getUser().getTestingUtility(WiBoxHttpDataListener.class);
    if (l == null) {
      return null;
    }
    Map result = new HashMap();
    result.put("data", l.getData());
    result.put("message", l.getMessage());
    return result;
  }
}