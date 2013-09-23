package com.serotonin.m2m2.wibox.request;

public class UpdateRequest extends WiBoxRequest
{
  private final String uniqueId;
  private final String moteId;
  private final String message;

  public UpdateRequest(String password, String uniqueId, String moteId, String message)
  {
    super(password);
    this.uniqueId = uniqueId;
    this.moteId = moteId;
    this.message = message;
  }

  public String getUniqueId()
  {
    return this.uniqueId;
  }

  public String getMoteId()
  {
    return this.moteId;
  }

  public String getMessage()
  {
    return this.message;
  }

  public String describe()
  {
    return "Update: moteId=" + this.moteId + ", uniqueId=" + this.uniqueId + ", message='" + this.message + "'";
  }
}