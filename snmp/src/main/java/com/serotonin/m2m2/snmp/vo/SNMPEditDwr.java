package com.serotonin.m2m2.snmp.vo;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.snmp.rt.Version;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.vo.dataSource.BasicDataSourceVO;
import com.serotonin.m2m2.vo.permission.Permissions;
import com.serotonin.m2m2.web.dwr.DataSourceEditDwr;
import com.serotonin.m2m2.web.dwr.util.DwrPermission;

public class SNMPEditDwr extends DataSourceEditDwr
{
  @DwrPermission(user=true)
  public ProcessResult saveSnmpDataSource(BasicDataSourceVO basic, int updatePeriods, int updatePeriodType, String host, int port, int snmpVersion, String community, String securityName, String authProtocol, String authPassphrase, String privProtocol, String privPassphrase, String engineId, String contextEngineId, String contextName, int retries, int timeout, int trapPort, String localAddress)
  {
    SnmpDataSourceVO ds = (SnmpDataSourceVO)Common.getUser().getEditDataSource();

    setBasicProps(ds, basic);
    ds.setUpdatePeriods(updatePeriods);
    ds.setUpdatePeriodType(updatePeriodType);
    ds.setHost(host);
    ds.setPort(port);
    ds.setSnmpVersion(snmpVersion);
    ds.setCommunity(community);
    ds.setSecurityName(securityName);
    ds.setAuthProtocol(authProtocol);
    ds.setAuthPassphrase(authPassphrase);
    ds.setPrivProtocol(privProtocol);
    ds.setPrivPassphrase(privPassphrase);
    ds.setEngineId(engineId);
    ds.setContextEngineId(contextEngineId);
    ds.setContextName(contextName);
    ds.setRetries(retries);
    ds.setTimeout(timeout);
    ds.setTrapPort(trapPort);
    ds.setLocalAddress(localAddress);

    return tryDataSourceSave(ds);
  }
  @DwrPermission(user=true)
  public ProcessResult saveSnmpPointLocator(int id, String xid, String name, SnmpPointLocatorVO locator) {
    return validatePoint(id, xid, name, locator, null);
  }

  @DwrPermission(user=true)
  public void snmpGetOid(String oid, String host, int port, int snmpVersion, String community, String securityName, String authProtocol, String authPassphrase, String privProtocol, String privPassphrase, String engineId, String contextEngineId, String contextName, int retries, int timeout)
  {
    User user = Common.getUser();
    Permissions.ensureDataSourcePermission(user);

    Version version = Version.getVersion(snmpVersion, community, securityName, authProtocol, authPassphrase, privProtocol, privPassphrase, engineId, contextEngineId, contextName);

    user.setTestingUtility(new SnmpOidGet(getTranslations(), host, port, version, oid, retries, timeout));
  }
  @DwrPermission(user=true)
  public String snmpGetOidUpdate() {
    SnmpOidGet snmpOidGet = (SnmpOidGet)Common.getUser().getTestingUtility(SnmpOidGet.class);
    if (snmpOidGet == null)
      return null;
    return snmpOidGet.getResult();
  }
}