package com.serotonin.m2m2.globalScripts;

import com.serotonin.db.spring.ExtendedJdbcTemplate;
import com.serotonin.m2m2.db.dao.BaseDao;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.rt.script.ScriptUtils;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;

public class GlobalScriptsDao extends BaseDao
{
  private static final String SELECT = "SELECT id, xid, name, script FROM globalScripts ";

  public String generateUniqueXid()
  {
    return generateUniqueXid("GS_", "globalScripts");
  }

  public boolean isXidUnique(String xid, int scriptId) {
    return isXidUnique(xid, scriptId, "globalScripts");
  }

  public List<GlobalScript> getAll() {
    return query("SELECT id, xid, name, script FROM globalScripts ", new GlobalScriptRowMapper());
  }

  public GlobalScript get(int id) {
    return (GlobalScript)queryForObject("SELECT id, xid, name, script FROM globalScripts WHERE id=?", new Object[] { Integer.valueOf(id) }, new GlobalScriptRowMapper());
  }

  public GlobalScript get(String xid) {
    return (GlobalScript)queryForObject("SELECT id, xid, name, script FROM globalScripts WHERE xid=?", new Object[] { xid }, new GlobalScriptRowMapper(), null);
  }

  public void save(GlobalScript gs)
  {
    if (gs.getId() == -1)
      insert(gs);
    else
      update(gs);
    ScriptUtils.clearGlobalFunctions();
  }

  private void insert(GlobalScript gs) {
    gs.setId(doInsert("INSERT INTO globalScripts (xid, name, script) VALUES (?,?,?)", new Object[] { gs.getXid(), gs.getName(), gs.getScript() }));

    AuditEventType.raiseAddedEvent("SST_GLOBAL_SCRIPT", gs);
  }

  private void update(GlobalScript gs) {
    GlobalScript old = get(gs.getId());
    this.ejt.update("UPDATE globalScripts SET xid=?, name=?, script=? WHERE id=?", new Object[] { gs.getXid(), gs.getName(), gs.getScript(), Integer.valueOf(gs.getId()) });

    AuditEventType.raiseChangedEvent("SST_GLOBAL_SCRIPT", old, gs);
  }

  public void delete(int id) {
    GlobalScript gs = get(id);
    if (gs != null) {
      this.ejt.update("DELETE FROM globalScripts WHERE id=?", new Object[] { Integer.valueOf(id) });
      AuditEventType.raiseDeletedEvent("SST_GLOBAL_SCRIPT", gs);
    }
    ScriptUtils.clearGlobalFunctions();
  }

  class GlobalScriptRowMapper
    implements RowMapper<GlobalScript>
  {
    GlobalScriptRowMapper()
    {
    }

    public GlobalScript mapRow(ResultSet rs, int rowNum)
      throws SQLException
    {
      GlobalScript gs = new GlobalScript();
      int i = 0;
      i++; gs.setId(rs.getInt(i));
      i++; gs.setXid(rs.getString(i));
      i++; gs.setName(rs.getString(i));
      i++; gs.setScript(rs.getString(i));
      return gs;
    }
  }
}