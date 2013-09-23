package com.serotonin.m2m2.globalScripts;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.type.JsonObject;
import com.serotonin.json.type.JsonValue;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableJsonException;
import com.serotonin.m2m2.module.EmportDefinition;
import com.serotonin.m2m2.web.dwr.emport.ImportContext;
import org.apache.commons.lang3.StringUtils;

public class Emport extends EmportDefinition
{
  public String getElementId()
  {
    return "sstGlobalScripts";
  }

  public String getDescriptionKey()
  {
    return "header.globalScripts";
  }

  public Object getExportData()
  {
    return new GlobalScriptsDao().getAll();
  }

  public void doImport(JsonValue jsonValue, ImportContext importContext) throws JsonException
  {
    GlobalScriptsDao globalScriptsDao = new GlobalScriptsDao();
    JsonObject globalScript = jsonValue.toJsonObject();

    String xid = globalScript.getString("xid");
    if (StringUtils.isBlank(xid)) {
      xid = globalScriptsDao.generateUniqueXid();
    }
    GlobalScript vo = globalScriptsDao.get(xid);
    if (vo == null) {
      vo = new GlobalScript();
      vo.setXid(xid);
    }
    try
    {
      importContext.getReader().readInto(vo, globalScript);

      ProcessResult voResponse = new ProcessResult();
      vo.validate(voResponse);
      if (voResponse.getHasMessages())
      {
        importContext.copyValidationMessages(voResponse, "emport.globalScript.prefix", xid);
      }
      else {
        boolean isnew = vo.isNew();
        globalScriptsDao.save(vo);
        importContext.addSuccessMessage(isnew, "emport.globalScript.prefix", xid);
      }
    }
    catch (TranslatableJsonException e) {
      importContext.getResult().addGenericMessage("emport.globalScript.prefix", new Object[] { xid, e.getMsg() });
    }
    catch (JsonException e) {
      importContext.getResult().addGenericMessage("emport.globalScript.prefix", new Object[] { xid, importContext.getJsonExceptionMessage(e) });
    }
  }
}