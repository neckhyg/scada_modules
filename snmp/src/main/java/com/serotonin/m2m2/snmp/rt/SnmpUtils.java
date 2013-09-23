package com.serotonin.m2m2.snmp.rt;

import org.snmp4j.smi.OctetString;

public class SnmpUtils
{
  public static OctetString createOctetString(String s)
  {
    OctetString octetString;
    OctetString octetString;
    if (s.startsWith("0x"))
      octetString = OctetString.fromHexString(s.substring(2), ':');
    else {
      octetString = new OctetString(s);
    }
    return octetString;
  }
}