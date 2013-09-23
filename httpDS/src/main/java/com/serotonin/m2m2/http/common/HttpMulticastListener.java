package com.serotonin.m2m2.http.common;

public abstract interface HttpMulticastListener
{
  public abstract String[] getIpWhiteList();

  public abstract String[] getDeviceIdWhiteList();

  public abstract void data(HttpReceiverData paramHttpReceiverData);

  public abstract void ipWhiteListError(String paramString);
}