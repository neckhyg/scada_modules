package com.serotonin.m2m2.snmp.rt;

public class Version2c extends Version1
{
  public Version2c(String community)
  {
    super(community);
  }

  public int getVersionId()
  {
    return 1;
  }
}