package com.serotonin.m2m2.http.dwr;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.http.common.HttpReceiverData;
import com.serotonin.m2m2.http.rt.HttpRetrieverDataSourceRT;
import com.serotonin.m2m2.http.vo.HttpImageDataSourceVO;
import com.serotonin.m2m2.http.vo.HttpImagePointLocatorVO;
import com.serotonin.m2m2.http.vo.HttpReceiverDataSourceVO;
import com.serotonin.m2m2.http.vo.HttpReceiverPointLocatorVO;
import com.serotonin.m2m2.http.vo.HttpRetrieverDataSourceVO;
import com.serotonin.m2m2.http.vo.HttpRetrieverPointLocatorVO;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableException;
import com.serotonin.m2m2.rt.dataImage.types.DataValue;
import com.serotonin.m2m2.rt.dataSource.DataSourceUtils;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.vo.dataSource.BasicDataSourceVO;
import com.serotonin.m2m2.vo.permission.Permissions;
import com.serotonin.m2m2.web.dwr.DataSourceEditDwr;
import com.serotonin.m2m2.web.dwr.beans.DataPointDefaulter;
import com.serotonin.m2m2.web.dwr.util.DwrPermission;
import com.serotonin.m2m2.web.taglib.Functions;
import com.serotonin.util.IpAddressUtils;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class HttpDataSourceDwr extends DataSourceEditDwr
{
  @DwrPermission(user=true)
  public ProcessResult saveHttpReceiverDataSource(BasicDataSourceVO basic, String[] ipWhiteList, String[] deviceIdWhiteList, String setPointUrl)
  {
    HttpReceiverDataSourceVO ds = (HttpReceiverDataSourceVO)Common.getUser().getEditDataSource();

    setBasicProps(ds, basic);
    ds.setIpWhiteList(ipWhiteList);
    ds.setDeviceIdWhiteList(deviceIdWhiteList);
    ds.setSetPointUrl(setPointUrl);

    return tryDataSourceSave(ds);
  }

  @DwrPermission(user=true)
  public ProcessResult saveHttpReceiverPointLocator(int id, String xid, String name, HttpReceiverPointLocatorVO locator) {
    return validatePoint(id, xid, name, locator, null);
  }
  @DwrPermission(user=true)
  public void httpReceiverListenForData(String[] ipWhiteList, String[] deviceIdWhiteList) {
    User user = Common.getUser();
    Permissions.ensureDataSourcePermission(user);
    user.setTestingUtility(new HttpReceiverDataListener(getTranslations(), ipWhiteList, deviceIdWhiteList));
  }
  @DwrPermission(user=true)
  public Map<String, Object> httpReceiverListenerUpdate() {
    Map result = new HashMap();
    HttpReceiverDataListener l = (HttpReceiverDataListener)Common.getUser().getTestingUtility(HttpReceiverDataListener.class);
    if (l == null) {
      return null;
    }
    HttpReceiverData data = l.getData();
    if (data != null) {
      result.put("remoteIp", data.getRemoteIp());
      result.put("deviceId", data.getDeviceId());
      result.put("time", Functions.getTime(data.getTime()));
      result.put("data", data.getData());
    }
    result.put("message", l.getMessage());

    return result;
  }
  @DwrPermission(user=true)
  public String validateIpMask(String ipMask) {
    return IpAddressUtils.checkIpMask(ipMask);
  }

  @DwrPermission(user=true)
  public ProcessResult saveHttpRetrieverDataSource(BasicDataSourceVO basic, int updatePeriods, int updatePeriodType, boolean quantize, String url, int timeoutSeconds, int retries, String setPointUrl)
  {
    HttpRetrieverDataSourceVO ds = (HttpRetrieverDataSourceVO)Common.getUser().getEditDataSource();

    setBasicProps(ds, basic);
    ds.setUpdatePeriods(updatePeriods);
    ds.setUpdatePeriodType(updatePeriodType);
    ds.setQuantize(quantize);
    ds.setUrl(url);
    ds.setTimeoutSeconds(timeoutSeconds);
    ds.setRetries(retries);
    ds.setSetPointUrl(setPointUrl);

    return tryDataSourceSave(ds);
  }

  @DwrPermission(user=true)
  public ProcessResult saveHttpRetrieverPointLocator(int id, String xid, String name, HttpRetrieverPointLocatorVO locator) {
    return validatePoint(id, xid, name, locator, null);
  }

  @DwrPermission(user=true)
  public String testHttpRetrieverValueParams(String url, int timeoutSeconds, int retries, String valueRegex, int dataTypeId, String valueFormat) {
    try {
      String data = HttpRetrieverDataSourceRT.getData(url, timeoutSeconds, retries);

      Pattern valuePattern = Pattern.compile(valueRegex);
      DecimalFormat decimalFormat = null;
      if ((dataTypeId == 3) && (!StringUtils.isBlank(valueFormat)))
        decimalFormat = new DecimalFormat(valueFormat);
      DataValue value = DataSourceUtils.getValue(valuePattern, data, dataTypeId, valueFormat, null, decimalFormat, null);

      return translate("common.result", new Object[0]) + ": " + value.toString();
    }
    catch (TranslatableException e) {
      return translate(e.getTranslatableMessage());
    } catch (Exception e) {
        return e.getMessage();
    }
  }

  @DwrPermission(user=true)
  public String testHttpRetrieverTimeParams(String url, int timeoutSeconds, int retries, String timeRegex, String timeFormat)
  {
    try {
      String data = HttpRetrieverDataSourceRT.getData(url, timeoutSeconds, retries);

      Pattern timePattern = Pattern.compile(timeRegex);
      DateFormat dateFormat = new SimpleDateFormat(timeFormat);
      long time = DataSourceUtils.getValueTime(System.currentTimeMillis(), timePattern, data, dateFormat, null);
      return Functions.getTime(time);
    }
    catch (TranslatableException e) {
      return translate(e.getTranslatableMessage());
    } catch (Exception e) {
        return e.getMessage();
    }
  }

  @DwrPermission(user=true)
  public ProcessResult saveHttpImageDataSource(BasicDataSourceVO basic, int updatePeriods, int updatePeriodType)
  {
    HttpImageDataSourceVO ds = (HttpImageDataSourceVO)Common.getUser().getEditDataSource();

    setBasicProps(ds, basic);
    ds.setUpdatePeriods(updatePeriods);
    ds.setUpdatePeriodType(updatePeriodType);

    return tryDataSourceSave(ds);
  }
  @DwrPermission(user=true)
  public ProcessResult saveHttpImagePointLocator(int id, String xid, String name, HttpImagePointLocatorVO locator) {
    return validatePoint(id, xid, name, locator, new DataPointDefaulter()
    {
      public void setDefaultValues(DataPointVO dp) {
        if (dp.isNew())
          dp.setLoggingType(3);
      }

      public void updateDefaultValues(DataPointVO dp)
      {
      }

      public void postSave(DataPointVO dp)
      {
      }
    });
  }
}