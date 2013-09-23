package com.serotonin.m2m2.pachube.ds;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableException;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataImage.types.DataValue;
import com.serotonin.m2m2.rt.dataSource.DataSourceUtils;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.vo.dataSource.BasicDataSourceVO;
import com.serotonin.m2m2.web.dwr.DataSourceEditDwr;
import com.serotonin.m2m2.web.dwr.util.DwrPermission;
import java.util.Map;

public class PachubeDataSourceDwr extends DataSourceEditDwr
{
  @DwrPermission(user=true)
  public ProcessResult savePachubeDataSource(BasicDataSourceVO basic, String apiKey, int updatePeriods, int updatePeriodType, int timeoutSeconds, int retries)
  {
    PachubeDataSourceVO ds = (PachubeDataSourceVO)Common.getUser().getEditDataSource();

    setBasicProps(ds, basic);
    ds.setApiKey(apiKey);
    ds.setUpdatePeriods(updatePeriods);
    ds.setUpdatePeriodType(updatePeriodType);
    ds.setTimeoutSeconds(timeoutSeconds);
    ds.setRetries(retries);

    return tryDataSourceSave(ds);
  }
  @DwrPermission(user=true)
  public ProcessResult savePachubePointLocator(int id, String xid, String name, PachubePointLocatorVO locator) {
    return validatePoint(id, xid, name, locator, null);
  }

  @DwrPermission(user=true)
  public String testPachubeValueParams(String apiKey, int timeoutSeconds, int retries, int feedId, String dataStreamId, int dataTypeId, String binary0Value) {
    try {
      Map data = PachubeDataSourceRT.getData(PachubeDataSourceRT.createHttpClient(timeoutSeconds, retries), feedId, apiKey);

      PachubeValue pachubeValue = (PachubeValue)data.get(dataStreamId);
      if (pachubeValue == null) {
        return translate(new TranslatableMessage("event.pachube.dataStreamNotFound", new Object[] { dataStreamId, Integer.valueOf(feedId) }));
      }
      DataValue value = DataSourceUtils.getValue(pachubeValue.getValue(), dataTypeId, binary0Value, null, null, null);

      return translate("common.result", new Object[0]) + ": " + value.toString();
    }
    catch (TranslatableException e) {
      return translate(e.getTranslatableMessage());
    } catch (Exception e) {
    }
    return e.getMessage();
  }
}