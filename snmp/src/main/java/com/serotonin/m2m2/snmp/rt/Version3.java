package com.serotonin.m2m2.snmp.rt;

import org.apache.commons.lang3.StringUtils;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.UserTarget;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.AuthSHA;
import org.snmp4j.security.PrivAES128;
import org.snmp4j.security.PrivAES192;
import org.snmp4j.security.PrivAES256;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;

public class Version3 extends Version
{
  private final OctetString securityName;
  private OID authProtocol;
  private final OctetString authPassphrase;
  private OID privProtocol;
  private final OctetString privPassphrase;
  private final OctetString engineId;
  private final OctetString contextEngineId;
  private final OctetString contextName;

  public Version3(String securityName, String authProtocol, String authPassphrase, String privProtocol, String privPassphrase, String engineId, String contextEngineId, String contextName)
  {
    this.securityName = SnmpUtils.createOctetString(securityName);

    if (!StringUtils.isBlank(authProtocol)) {
      if (authProtocol.equals("MD5"))
        this.authProtocol = AuthMD5.ID;
      else if (authProtocol.equals("SHA"))
        this.authProtocol = AuthSHA.ID;
      else {
        throw new IllegalArgumentException("Authentication protocol unsupported: " + authProtocol);
      }
    }
    this.authPassphrase = SnmpUtils.createOctetString(authPassphrase);

    if (!StringUtils.isBlank(privProtocol)) {
      if (privProtocol.equals("DES"))
        this.privProtocol = PrivDES.ID;
      else if ((privProtocol.equals("AES128")) || (privProtocol.equals("AES")))
        this.privProtocol = PrivAES128.ID;
      else if (privProtocol.equals("AES192"))
        this.privProtocol = PrivAES192.ID;
      else if (privProtocol.equals("AES256"))
        this.privProtocol = PrivAES256.ID;
      else {
        throw new IllegalArgumentException("Privacy protocol " + privProtocol + " not supported");
      }
    }
    this.privPassphrase = SnmpUtils.createOctetString(privPassphrase);
    this.engineId = SnmpUtils.createOctetString(engineId);
    this.contextEngineId = SnmpUtils.createOctetString(contextEngineId);
    this.contextName = SnmpUtils.createOctetString(contextName);
  }

  public int getVersionId()
  {
    return 3;
  }

  public void addUser(Snmp snmp)
  {
    USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
    SecurityModels.getInstance().addSecurityModel(usm);
    if (this.engineId != null)
      snmp.setLocalEngine(this.engineId.getValue(), 0, 0);
    snmp.getUSM().addUser(this.securityName, new UsmUser(this.securityName, this.authProtocol, this.authPassphrase, this.privProtocol, this.privPassphrase));
  }

  public Target getTarget()
  {
    UserTarget target = new UserTarget();
    if (this.authPassphrase != null) {
      if (this.privPassphrase != null)
        target.setSecurityLevel(3);
      else
        target.setSecurityLevel(2);
    }
    else {
      target.setSecurityLevel(1);
    }
    target.setSecurityName(this.securityName);
    return target;
  }

  public PDU createPDU()
  {
    ScopedPDU scopedPDU = new ScopedPDU();
    if (this.contextEngineId != null)
      scopedPDU.setContextEngineID(this.contextEngineId);
    if (this.contextName != null)
      scopedPDU.setContextName(this.contextName);
    return scopedPDU;
  }
}