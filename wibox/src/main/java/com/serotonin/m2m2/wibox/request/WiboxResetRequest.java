package com.serotonin.m2m2.wibox.request;

public class WiboxResetRequest extends WiBoxRequest
{
  public WiboxResetRequest(String password)
  {
    super(password);
  }

  public String describe()
  {
    return "Reset";
  }
}