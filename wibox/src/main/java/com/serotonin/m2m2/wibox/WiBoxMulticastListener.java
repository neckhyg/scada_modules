package com.serotonin.m2m2.wibox;

import com.serotonin.m2m2.wibox.request.WiBoxRequest;

public abstract interface WiBoxMulticastListener
{
  public abstract String getPassword();

  public abstract void wiBoxRequest(WiBoxRequest paramWiBoxRequest);
}