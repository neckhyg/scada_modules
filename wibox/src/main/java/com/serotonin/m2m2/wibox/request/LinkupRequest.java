package com.serotonin.m2m2.wibox.request;

public class LinkupRequest extends WiBoxRequest
{
  private final String moteId;

  public LinkupRequest(String password, String moteId)
  {
    super(password);
    this.moteId = moteId;
  }

  public String getMoteId()
  {
    return this.moteId;
  }

  public String describe()
  {
    return "Linkup: moteId=" + this.moteId;
  }
}