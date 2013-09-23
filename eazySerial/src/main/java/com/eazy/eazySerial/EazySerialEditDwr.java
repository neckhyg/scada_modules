package com.eazy.eazySerial;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.vo.dataSource.BasicDataSourceVO;
import com.serotonin.m2m2.web.dwr.DataSourceEditDwr;
import com.serotonin.m2m2.web.dwr.util.DwrPermission;

public class EazySerialEditDwr extends DataSourceEditDwr
{

    @DwrPermission(user=true)
    public ProcessResult saveEazySerialDataSource(BasicDataSourceVO basic, int updatePeriods, int updatePeriodType,
            String commPortId, int baudRate, int flowControlIn, int flowControlOut,
            int dataBits, int stopBits, int parity)
    {
        EazySerialDataSourceVO ds = (EazySerialDataSourceVO)Common.getUser().getEditDataSource();

        setBasicProps(ds, basic);
        ds.setUpdatePeriods(updatePeriods);
        ds.setUpdatePeriodType(updatePeriodType);
        ds.setCommPortId(commPortId);
        ds.setBaudRate(baudRate);
        ds.setFlowControlIn(flowControlIn);
        ds.setFlowControlOut(flowControlOut);
        ds.setDataBits(dataBits);
        ds.setStopBits(stopBits);
        ds.setParity(parity);

        return tryDataSourceSave(ds);
    }

  @DwrPermission(user=true)
  public ProcessResult saveEazySerialPointLocator(int id, String xid, String name, EazySerialPointLocatorVO locator) {
    return validatePoint(id, xid, name, locator, null);
  }
}