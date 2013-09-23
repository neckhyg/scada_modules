package com.serotonin.m2m2.http.common;

import com.serotonin.util.IpAddressUtils;
import com.serotonin.util.IpWhiteListException;
import java.util.concurrent.CopyOnWriteArraySet;

public class HttpReceiverMulticaster
{
  private final CopyOnWriteArraySet<HttpMulticastListener> listeners = new CopyOnWriteArraySet();

  public void addListener(HttpMulticastListener l) {
    this.listeners.add(l);
  }

  public void removeListener(HttpMulticastListener l) {
    this.listeners.remove(l);
  }

  public void multicast(HttpReceiverData data) {
    for (HttpMulticastListener l : this.listeners)
    {
      try {
        if (!IpAddressUtils.ipWhiteListCheck(l.getIpWhiteList(), data.getRemoteIp()))
          continue;
      }
      catch (IpWhiteListException e) {
        l.ipWhiteListError(e.getMessage());
      }

      if ((!org.apache.commons.lang3.StringUtils.isBlank(data.getDeviceId())) && 
        (!com.serotonin.util.StringUtils.globWhiteListMatchIgnoreCase(l.getDeviceIdWhiteList(), data.getDeviceId())))
      {
        continue;
      }

      l.data(data);
    }
  }
}