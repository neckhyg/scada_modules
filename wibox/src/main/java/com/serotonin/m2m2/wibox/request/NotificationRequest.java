package com.serotonin.m2m2.wibox.request;

public class NotificationRequest extends WiBoxRequest
{
  private final String uniqueId;
  private final String moteId;
  private final String code;
  private final String msg;

  public NotificationRequest(String password, String uniqueId, String moteId, String code, String msg)
  {
    super(password);
    this.uniqueId = uniqueId;
    this.moteId = moteId;
    this.code = code;
    this.msg = msg;
  }

  public String getUniqueId()
  {
    return this.uniqueId;
  }

  public String getMoteId()
  {
    return this.moteId;
  }

  public String getCode()
  {
    return this.code;
  }

  public String getMsg()
  {
    return this.msg;
  }

  public String describe()
  {
    return "Notification: moteId=" + this.moteId + ", uniqueId=" + this.uniqueId + ", code=" + this.code + ", msg='" + this.msg + "'";
  }
}