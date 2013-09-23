package com.serotonin.m2m2.wibox.request;

public class InfoRequest extends WiBoxRequest
{
  public InfoRequest(String password)
  {
    super(password);
  }

  public String describe()
  {
    return "Info";
  }
}