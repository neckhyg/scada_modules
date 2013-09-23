package com.serotonin.m2m2.globalScripts;

import com.serotonin.m2m2.module.ScriptSourceDefinition;
import java.util.ArrayList;
import java.util.List;

public class ScriptSource extends ScriptSourceDefinition
{
  public List<String> getScripts()
  {
    List scripts = new ArrayList();
    for (GlobalScript gs : new GlobalScriptsDao().getAll())
      scripts.add(gs.getScript());
    return scripts;
  }
}