package com.serotonin.m2m2.maintenanceEvents;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.type.JsonObject;
import com.serotonin.json.type.JsonValue;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableJsonException;
import com.serotonin.m2m2.module.EmportDefinition;
import com.serotonin.m2m2.web.dwr.emport.ImportContext;
import org.apache.commons.lang3.StringUtils;

public class MaintenanceEventEmportDefinition extends EmportDefinition
{
  public String getElementId()
  {
    return "maintenanceEvents";
  }

  public String getDescriptionKey()
  {
    return "header.maintenanceEvents";
  }

  public Object getExportData()
  {
    return new MaintenanceEventDao().getMaintenanceEvents();
  }

  public void doImport(JsonValue jsonValue, ImportContext importContext) throws JsonException
  {
    MaintenanceEventDao maintenanceEventDao = new MaintenanceEventDao();
    JsonObject maintenanceEvent = jsonValue.toJsonObject();

    String xid = maintenanceEvent.getString("xid");
    if (StringUtils.isBlank(xid)) {
      xid = maintenanceEventDao.generateUniqueXid();
    }
    MaintenanceEventVO vo = maintenanceEventDao.getMaintenanceEvent(xid);
    if (vo == null) {
      vo = new MaintenanceEventVO();
      vo.setXid(xid);
    }
    try
    {
      importContext.getReader().readInto(vo, maintenanceEvent);

      ProcessResult voResponse = new ProcessResult();
      vo.validate(voResponse);
      if (voResponse.getHasMessages())
      {
        importContext.copyValidationMessages(voResponse, "emport.maintenanceEvent.prefix", xid);
      }
      else {
        boolean isnew = vo.isNew();
        RTMDefinition.instance.saveMaintenanceEvent(vo);
        importContext.addSuccessMessage(isnew, "emport.maintenanceEvent.prefix", xid);
      }
    }
    catch (TranslatableJsonException e) {
      importContext.getResult().addGenericMessage("emport.maintenanceEvent.prefix", new Object[] { xid, e.getMsg() });
    }
    catch (JsonException e) {
      importContext.getResult().addGenericMessage("emport.maintenanceEvent.prefix", new Object[] { xid, importContext.getJsonExceptionMessage(e) });
    }
  }
}