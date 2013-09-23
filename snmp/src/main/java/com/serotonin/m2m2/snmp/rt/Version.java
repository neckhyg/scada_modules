package com.serotonin.m2m2.snmp.rt;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.UdpAddress;

public abstract class Version
{
  public static Version getVersion(int version, String community, String securityName, String authProtocol, String authPassphrase, String privProtocol, String privPassphrase, String engineId, String contextEngineId, String contextName)
  {
    if (version == 0)
      return new Version1(community);
    if (version == 1)
      return new Version2c(community);
    if (version == 3) {
      return new Version3(securityName, authProtocol, authPassphrase, privProtocol, privPassphrase, engineId, contextEngineId, contextName);
    }

    throw new IllegalArgumentException("Invalid version value: " + version); } 
  public abstract int getVersionId();

  public abstract void addUser(Snmp paramSnmp);

  public abstract PDU createPDU();

  protected abstract Target getTarget();

  public Target getTarget(String host, int port, int retries, int timeout) throws UnknownHostException { Target target = getTarget();

    Address address = new UdpAddress(InetAddress.getByName(host), port);
    target.setAddress(address);
    target.setRetries(retries);
    target.setTimeout(timeout);

    return target;
  }
}