package com.serotonin.ma.ascii;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.vo.dataSource.BasicDataSourceVO;
import com.serotonin.m2m2.web.dwr.DataSourceEditDwr;
import com.serotonin.m2m2.web.dwr.util.DwrPermission;
import com.serotonin.ma.ascii.file.FileDataSourceVO;
import com.serotonin.ma.ascii.file.FilePointLocatorVO;
import com.serotonin.ma.ascii.serial.SerialDataSourceVO;
import com.serotonin.ma.ascii.serial.SerialPointLocatorVO;
import java.io.File;

public class AsciiEditDwr extends DataSourceEditDwr
{
  @DwrPermission(user=true)
  public ProcessResult saveFileDataSource(BasicDataSourceVO basic, int updatePeriods, int updatePeriodType, String filePath, boolean quantize)
  {
    FileDataSourceVO ds = (FileDataSourceVO)Common.getUser().getEditDataSource();

    setBasicProps(ds, basic);
    ds.setUpdatePeriods(updatePeriods);
    ds.setUpdatePeriodType(updatePeriodType);
    ds.setFilePath(filePath);
    ds.setQuantize(quantize);

    return tryDataSourceSave(ds);
  }
  @DwrPermission(user=true)
  public ProcessResult saveFilePointLocator(int id, String xid, String name, FilePointLocatorVO locator) {
    return validatePoint(id, xid, name, locator, null);
  }
  @DwrPermission(user=true)
  public String checkFile(String filePath) {
    if (new File(filePath).exists())
      return translate("ascii.file.fileExists", new Object[0]);
    return translate("ascii.file.fileNotExists", new Object[0]);
  }

  @DwrPermission(user=true)
  public ProcessResult saveSerialDataSource(BasicDataSourceVO basic, int updatePeriods, int updatePeriodType, String commPortId, int baudRate, int dataBits, int stopBits, int parity, int timeout, int retries, int stopMode, int nChar, int charStopMode, String charX, String hexValue, int stopTimeout, String initString, int bufferSize, boolean quantize)
  {
    SerialDataSourceVO ds = (SerialDataSourceVO)Common.getUser().getEditDataSource();

    setBasicProps(ds, basic);
    ds.setUpdatePeriods(updatePeriods);
    ds.setUpdatePeriodType(updatePeriodType);
    ds.setCommPortId(commPortId);
    ds.setBaudRate(baudRate);
    ds.setDataBits(dataBits);
    ds.setStopBits(stopBits);
    ds.setParity(parity);
    ds.setTimeout(timeout);
    ds.setRetries(retries);
    ds.setStopMode(stopMode);
    ds.setnChar(nChar);
    ds.setCharStopMode(charStopMode);
    ds.setCharX(charX);
    ds.setHexValue(hexValue);
    ds.setStopTimeout(stopTimeout);
    ds.setInitString(initString);
    ds.setBufferSize(bufferSize);
    ds.setQuantize(quantize);

    return tryDataSourceSave(ds);
  }
  @DwrPermission(user=true)
  public ProcessResult saveSerialPointLocator(int id, String xid, String name, SerialPointLocatorVO locator) {
    return validatePoint(id, xid, name, locator, null);
  }
}