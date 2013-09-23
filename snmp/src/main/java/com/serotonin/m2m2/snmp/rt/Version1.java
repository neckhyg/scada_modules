package com.serotonin.m2m2.snmp.rt;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.smi.OctetString;

public class Version1 extends Version
{
  private final OctetString community;

  public Version1(String community)
  {
    this.community = SnmpUtils.createOctetString(community);
  }

  public int getVersionId()
  {
    return 0;
  }

  public void addUser(Snmp snmp)
  {
  }

  public PDU createPDU()
  {
    return new PDU();
  }

  public Target getTarget()
  {
    CommunityTarget target = new CommunityTarget();
    target.setCommunity(this.community);
    return target;
  }
}