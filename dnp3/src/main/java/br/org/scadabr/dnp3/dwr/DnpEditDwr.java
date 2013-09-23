package br.org.scadabr.dnp3.dwr;

import br.org.scadabr.dnp3.vo.Dnp3IpDataSourceVO;
import br.org.scadabr.dnp3.vo.Dnp3PointLocatorVO;
import br.org.scadabr.dnp3.vo.Dnp3SerialDataSourceVO;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.db.dao.DataPointDao;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.rt.RuntimeManager;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.vo.dataSource.BasicDataSourceVO;
import com.serotonin.m2m2.web.dwr.DataSourceEditDwr;
import com.serotonin.m2m2.web.dwr.beans.DataPointDefaulter;
import com.serotonin.m2m2.web.dwr.util.DwrPermission;
import com.serotonin.validation.StringValidation;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class DnpEditDwr extends DataSourceEditDwr
{
  @DwrPermission(user=true)
  public ProcessResult saveDNP3IpDataSource(BasicDataSourceVO basic, int sourceAddress, int slaveAddress, String host, int port, int staticPollPeriods, int rbePollPeriods, int rbePeriodType, int timeout, int retries)
  {
    Dnp3IpDataSourceVO ds = (Dnp3IpDataSourceVO)Common.getUser().getEditDataSource();

    setBasicProps(ds, basic);
    ds.setSourceAddress(sourceAddress);
    ds.setSlaveAddress(slaveAddress);
    ds.setHost(host);
    ds.setPort(port);

    ds.setStaticPollPeriods(staticPollPeriods);
    ds.setRbePeriodType(rbePeriodType);
    ds.setRbePollPeriods(rbePollPeriods);
    ds.setTimeout(timeout);
    ds.setRetries(retries);

    return tryDataSourceSave(ds);
  }

  @DwrPermission(user=true)
  public ProcessResult saveDNP3SerialDataSource(BasicDataSourceVO basic, int sourceAddress, int slaveAddress, String commPortId, int baudRate, int staticPollPeriods, int rbePollPeriods, int rbePeriodType, int timeout, int retries)
  {
    Dnp3SerialDataSourceVO ds = (Dnp3SerialDataSourceVO)Common.getUser().getEditDataSource();

    setBasicProps(ds, basic);
    ds.setSourceAddress(sourceAddress);
    ds.setSlaveAddress(slaveAddress);
    ds.setCommPortId(commPortId);
    ds.setBaudRate(baudRate);
    ds.setStaticPollPeriods(staticPollPeriods);
    ds.setRbePeriodType(rbePeriodType);
    ds.setRbePollPeriods(rbePollPeriods);
    ds.setTimeout(timeout);
    ds.setRetries(retries);

    return tryDataSourceSave(ds);
  }
  @DwrPermission(user=true)
  public ProcessResult saveDnp3PointLocator(int id, String xid, String name, Dnp3PointLocatorVO locator) {
    ProcessResult response = new ProcessResult();

    if (locator.getTimeOn() < 0)
      response.addContextualMessage("timeOn", "reports.validate.lessThan0", new Object[0]);
    if (locator.getTimeOff() < 0) {
      response.addContextualMessage("timeOff", "reports.validate.lessThan0", new Object[0]);
    }
    List points = getPoints();
    Iterator itr = points.iterator();
    while (itr.hasNext()) {
      DataPointVO vo = (DataPointVO)itr.next();
      Dnp3PointLocatorVO loc = (Dnp3PointLocatorVO)vo.getPointLocator();
      if ((loc.getDnp3DataType() == locator.getDnp3DataType()) && (loc.getIndex() == locator.getIndex()) && (id != vo.getId()))
      {
        response.addContextualMessage("index", "dsEdit.dnp3.validate.indexUsed", new Object[0]);
      }
    }

    if (!response.getHasMessages())
      return validatePoint(id, xid, name, locator, null);
    return response;
  }
  @DwrPermission(user=true)
  public ProcessResult saveMultipleDnp3PointLocator(String[] names, int[] index, Dnp3PointLocatorVO[] locators) {
    return validateMultipleDnp3Points(names, index, locators, null);
  }

  private ProcessResult validateMultipleDnp3Points(String[] names, int[] index, Dnp3PointLocatorVO[] locators, DataPointDefaulter defaulter)
  {
    ProcessResult response = new ProcessResult();

    if (locators[0].getClass().equals(Dnp3PointLocatorVO.class)) {
      if (locators[0].getTimeOn() < 0)
        response.addContextualMessage("timeOn", "reports.validate.lessThan0", new Object[0]);
      if (locators[0].getTimeOff() < 0) {
        response.addContextualMessage("timeOff", "reports.validate.lessThan0", new Object[0]);
      }
    }
    for (int i = 0; i < names.length; i++) {
      locators[i].setIndex(index[i]);

      List points = getPoints();
      Iterator itr = points.iterator();
      while (itr.hasNext()) {
        Dnp3PointLocatorVO loc = (Dnp3PointLocatorVO)((DataPointVO)itr.next()).getPointLocator();
        if ((loc.getDnp3DataType() == locators[i].getDnp3DataType()) && (loc.getIndex() == locators[i].getIndex())) {
          response.addContextualMessage("index", "dsEdit.dnp3.validate.someIndexUsed", new Object[0]);
        }
      }
    }
    if (response.getHasMessages()) {
      return response;
    }
    for (int i = 0; i < names.length; i++) {
      DataPointVO dp = getPoint(-1, defaulter);
      dp.setName(names[i]);
      locators[i].setIndex(index[i]);
      dp.setPointLocator(locators[i]);

      if (StringUtils.isEmpty(dp.getXid()))
        response.addContextualMessage("xid", "validate.required", new Object[0]);
      else if (!new DataPointDao().isXidUnique(dp.getXid(), -1))
        response.addContextualMessage("xid", "validate.xidUsed", new Object[0]);
      else if (StringValidation.isLengthGreaterThan(dp.getXid(), 50)) {
        response.addContextualMessage("xid", "validate.notLongerThan", new Object[] { Integer.valueOf(50) });
      }
      locators[i].validate(response);

      if (!response.getHasMessages()) {
        Common.runtimeManager.saveDataPoint(dp);
        response.addData("id", Integer.valueOf(dp.getId()));
        response.addData("points", getPoints());
      }
    }
    return response;
  }
}