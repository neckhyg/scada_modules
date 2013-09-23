package com.serotonin.m2m2.snmp.rt;

import com.serotonin.ShouldNeverHappenException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.Snmp;
import org.snmp4j.mp.CounterSupport;
import org.snmp4j.mp.DefaultCounterListener;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SnmpTrapRouter
{
  private static SnmpTrapRouter instance;
  private final List<PortListener> portListeners;

  public SnmpTrapRouter()
  {
    this.portListeners = new LinkedList();
  }

  public static synchronized void addDataSource(SnmpDataSourceRT ds)
    throws IOException
  {
    if (instance == null) {
      CounterSupport.getInstance().addCounterListener(new DefaultCounterListener());
      instance = new SnmpTrapRouter();
    }
    instance.addDataSourceImpl(ds);
  }

  public static synchronized void removeDataSource(SnmpDataSourceRT ds) {
    if (instance != null)
      instance.removeDataSourceImpl(ds);
  }

  private void addDataSourceImpl(SnmpDataSourceRT ds)
    throws IOException
  {
    PortListener l = getPortListener(ds.getTrapPort());
    if (l == null) {
      l = new PortListener(ds.getTrapPort());
      this.portListeners.add(l);
    }
    l.addDataSource(ds);
  }

  private void removeDataSourceImpl(SnmpDataSourceRT ds) {
    PortListener l = getPortListener(ds.getTrapPort());
    if (l != null) {
      l.removeDataSource(ds);
      if (l.dataSources.size() == 0) {
        l.close();
        this.portListeners.remove(l);
      }
    }
  }

  private PortListener getPortListener(int port) {
    for (PortListener l : this.portListeners) {
      if (l.port == port)
        return l;
    }
    return null;
  }

  private class PortListener
    implements CommandResponder
  {
    private final Snmp snmp;
    final int port;
    final List<SnmpDataSourceRT> dataSources = new LinkedList();

    PortListener(int port) throws IOException {
      this.port = port;

      this.snmp = new Snmp(new DefaultUdpTransportMapping(new UdpAddress("0.0.0.0/" + port)));
      this.snmp.addCommandResponder(this);
      this.snmp.listen();
    }

    public synchronized void processPdu(CommandResponderEvent evt) {
      PDU command = evt.getPDU();
      String peer;
      String localAddress;
      if (command != null)
      {
        peer = evt.getPeerAddress().toString();
        int slash = peer.indexOf('/');
        if (slash > 0) {
          peer = peer.substring(0, slash);
        }
        localAddress = "";
        if ((command instanceof PDUv1)) {
          localAddress = ((PDUv1)command).getAgentAddress().toString();
        }

        for (SnmpDataSourceRT ds : this.dataSources)
          if ((ds.getAddress().equals(peer)) && (
            (StringUtils.isBlank(ds.getLocalAddress())) || (localAddress.equals(ds.getLocalAddress()))))
            ds.receivedPDU(evt);
      }
    }

    synchronized void addDataSource(SnmpDataSourceRT ds)
    {
      this.dataSources.add(ds);
    }

    synchronized void removeDataSource(SnmpDataSourceRT ds) {
      this.dataSources.remove(ds);
    }

    void close() {
      try {
        this.snmp.close();
      }
      catch (IOException e) {
        throw new ShouldNeverHappenException(e);
      }
    }
  }
}