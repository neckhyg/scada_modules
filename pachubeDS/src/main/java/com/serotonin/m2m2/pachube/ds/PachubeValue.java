package com.serotonin.m2m2.pachube.ds;

public class PachubeValue
{
  private final String value;
  private final String timestamp;

  public PachubeValue(String value, String timestamp)
  {
    this.value = value;
    this.timestamp = timestamp;
  }

  public String getValue() {
    return this.value;
  }

  public String getTimestamp() {
    return this.timestamp;
  }
}