package com.serotonin.m2m2.wibox.request;

public abstract class WiBoxRequest
{
  private static final long MOTE_START_TIME = 1136073600000L;
  private final String password;
  private boolean handled;

  public WiBoxRequest(String password)
  {
    this.password = password;
  }

  public String getPassword()
  {
    return this.password;
  }

  public boolean isHandled()
  {
    return this.handled;
  }

  public void setHandled(boolean handled)
  {
    this.handled = handled;
  }

  public long toUTC(int sensorTime) {
    return sensorTime * 1000L + 1136073600000L;
  }

  public abstract String describe();
}