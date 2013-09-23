package com.serotonin.m2m2.wibox;

import com.serotonin.m2m2.wibox.request.WiBoxRequest;
import java.util.concurrent.CopyOnWriteArraySet;
import org.apache.commons.lang3.StringUtils;

public class WiBoxMulticaster
{
  private final CopyOnWriteArraySet<WiBoxMulticastListener> listeners = new CopyOnWriteArraySet();

  public void addListener(WiBoxMulticastListener l) {
    this.listeners.add(l);
  }

  public void removeListener(WiBoxMulticastListener l) {
    this.listeners.remove(l);
  }

  public void multicast(WiBoxRequest req, String password) {
    for (WiBoxMulticastListener l : this.listeners)
    {
      if ((l.getPassword() == null) || (StringUtils.equals(password, l.getPassword())))
        l.wiBoxRequest(req);
    }
  }
}