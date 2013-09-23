package com.serotonin.m2m2.wibox.request;

public class ErrorRequest extends WiBoxRequest
{
  public ErrorRequest(String password)
  {
    super(password);
  }

  public String describe()
  {
    return "Error";
  }
}