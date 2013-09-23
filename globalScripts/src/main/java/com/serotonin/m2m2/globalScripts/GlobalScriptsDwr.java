package com.serotonin.m2m2.globalScripts;

import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.web.dwr.ModuleDwr;
import com.serotonin.m2m2.web.dwr.util.DwrPermission;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class GlobalScriptsDwr extends ModuleDwr
{
  @DwrPermission(admin=true)
  public List<GlobalScript> init()
  {
    return new GlobalScriptsDao().getAll();
  }
  @DwrPermission(admin=true)
  public GlobalScript get(int id) {
    GlobalScriptsDao globalScriptsDao = new GlobalScriptsDao();

    if (id == -1) {
      GlobalScript gs = new GlobalScript();
      gs.setXid(globalScriptsDao.generateUniqueXid());
      gs.setName(translate("common.newName", new Object[0]));
      gs.setScript("");
      return gs;
    }

    return globalScriptsDao.get(id);
  }

  @DwrPermission(admin=true)
  public ProcessResult save(GlobalScript gs) {
    ProcessResult response = new ProcessResult();
    GlobalScriptsDao globalScriptsDao = new GlobalScriptsDao();

    if (StringUtils.isBlank(gs.getXid()))
      response.addContextualMessage("xid", "validate.required", new Object[0]);
    else if (!globalScriptsDao.isXidUnique(gs.getXid(), gs.getId())) {
      response.addContextualMessage("xid", "validate.xidUsed", new Object[0]);
    }
    gs.validate(response);

    if (!response.getHasMessages()) {
      globalScriptsDao.save(gs);
      response.addData("script", gs);
    }

    return response;
  }
  @DwrPermission(admin=true)
  public void deleteScript(int id) {
    new GlobalScriptsDao().delete(id);
  }
}