package com.serotonin.m2m2.galil;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.galil.vo.GalilDataSourceVO;
import com.serotonin.m2m2.galil.vo.GalilPointLocatorVO;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.vo.dataSource.BasicDataSourceVO;
import com.serotonin.m2m2.web.dwr.DataSourceEditDwr;
import com.serotonin.m2m2.web.dwr.util.DwrPermission;
import java.io.IOException;

public class GalilEditDwr extends DataSourceEditDwr
{
  @DwrPermission(user=true)
  public ProcessResult saveGalilDataSource(BasicDataSourceVO basic, String host, int port, int timeout, int retries, int updatePeriods, int updatePeriodType)
  {
    GalilDataSourceVO ds = (GalilDataSourceVO)Common.getUser().getEditDataSource();

    setBasicProps(ds, basic);
    ds.setHost(host);
    ds.setPort(port);
    ds.setTimeout(timeout);
    ds.setRetries(retries);
    ds.setUpdatePeriods(updatePeriods);
    ds.setUpdatePeriodType(updatePeriodType);

    return tryDataSourceSave(ds);
  }
  @DwrPermission(user=true)
  public ProcessResult saveGalilPointLocator(int id, String xid, String name, GalilPointLocatorVO locator) {
    return validatePoint(id, xid, name, locator, null);
  }
  @DwrPermission(user=true)
  public String galilTestCommand(String host, int port, int timeout, String command) {
    try {
      GalilCommandTester tester = new GalilCommandTester(getTranslations(), host, port, timeout, command);
      try {
        tester.join();
        return tester.getResult();
      }
      catch (InterruptedException e) {
        return e.getMessage();
      }
    } catch (IOException e) {
        return e.getMessage();
    }
  }
}