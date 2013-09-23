package com.serotonin.m2m2.onewire.common;

import com.dalsemi.onewire.OneWireAccessProvider;
import com.dalsemi.onewire.OneWireException;
import com.dalsemi.onewire.adapter.DSPortAdapter;
import com.dalsemi.onewire.adapter.OneWireIOException;
import com.dalsemi.onewire.container.OneWireContainer;
import com.dalsemi.onewire.container.OneWireContainer1D;
import com.dalsemi.onewire.container.OneWireContainer1F;
import com.dalsemi.onewire.container.OneWireSensor;
import com.dalsemi.onewire.container.SwitchContainer;
import com.dalsemi.onewire.utils.Address;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Network
{
  private static final String DEFAULT_ADAPTER_NAME = "DS9097U";
  private final DSPortAdapter adapter;
  final Map<Long, NetworkPath> pathsByAddress = new HashMap();

  public Network(DSPortAdapter adapter) {
    this.adapter = adapter;
  }

  public Network(String commPortId) throws OneWireIOException, OneWireException {
    this.adapter = OneWireAccessProvider.getAdapter("DS9097U", commPortId);
  }

  public Network(String adapterName, String commPortId) throws OneWireIOException, OneWireException {
    this.adapter = OneWireAccessProvider.getAdapter(adapterName, commPortId);
  }

  public void initialize() throws Exception
  {
    this.adapter.setSearchAllDevices();
    this.adapter.targetAllFamilies();
    this.adapter.setSpeed(0);

    List toTurnBackOn = new ArrayList();
    initializeImpl(new NetworkPath(this), toTurnBackOn);

    for (NetworkPathElement element : toTurnBackOn) {
      NetworkPath path = (NetworkPath)this.pathsByAddress.get(Long.valueOf(((OneWireContainer)element.getContainer()).getAddressAsLong()));
      path.open();

      SwitchContainer sc = element.getContainer();
      byte[] state = sc.readDevice();
      sc.setLatchState(element.getChannel(), true, sc.hasSmartOn(), state);
      sc.writeDevice(state);

      path.close();
    }
  }

  public void quickInitialize() throws Exception
  {
    this.adapter.setSearchAllDevices();
    this.adapter.targetAllFamilies();
    this.adapter.setSpeed(0);

    this.pathsByAddress.clear();
    quickInitializeImpl(new NetworkPath(this));
  }

  public void terminate() throws OneWireException {
    this.adapter.freePort();
  }

  public void lock() throws OneWireException {
    this.adapter.beginExclusive(true);
  }

  public void reset() throws OneWireException {
    this.adapter.reset();
  }

  public void unlock() {
    if (this.adapter != null)
      this.adapter.endExclusive();
  }

  public String getAdapterName() {
    if (this.adapter != null)
      return this.adapter.getAdapterName();
    return null;
  }

  public String getPortName() throws OneWireException {
    if (this.adapter != null)
      return this.adapter.getPortName();
    return null;
  }

  public List<Long> getAddresses() {
    List sorted = new ArrayList(this.pathsByAddress.keySet());
    Collections.sort(sorted, new Comparator() {
      public int compare(Long addr1, Long addr2) {
        String path1 = ((NetworkPath)Network.this.pathsByAddress.get(addr1)).toString();
        String path2 = ((NetworkPath)Network.this.pathsByAddress.get(addr2)).toString();
        return path1.compareTo(path2);
      }
    });
    return sorted;
  }

  public NetworkPath getNetworkPath(Long address) {
    return (NetworkPath)this.pathsByAddress.get(address);
  }

  public String addressPathsToString() {
    StringBuilder sb = new StringBuilder();
    sb.append('[');
    boolean first = true;
    for (Long address : this.pathsByAddress.keySet()) {
      if (first)
        first = false;
      else
        sb.append(", ");
      sb.append(this.pathsByAddress.get(address));
      sb.append(Address.toString(address.longValue()));
    }
    sb.append(']');
    return sb.toString();
  }

  private void initializeImpl(NetworkPath path, List<NetworkPathElement> toTurnBackOn)
    throws OneWireException
  {
    boolean searchResult = this.adapter.findFirstDevice();
    byte[] state;
    while (searchResult) {
      boolean reSearch = false;

      Long address = Long.valueOf(this.adapter.getAddressAsLong());
      if (!this.pathsByAddress.containsKey(address)) {
        OneWireContainer owc = this.adapter.getDeviceContainer();
        if ((owc instanceof SwitchContainer)) {
          SwitchContainer sc = (SwitchContainer)owc;
          try
          {
            state = sc.readDevice();
            for (int ch = 0; ch < sc.getNumberChannels(state); ch++) {
              if (!sc.getLatchState(ch, state))
              {
                continue;
              }
              sc.setLatchState(ch, false, false, state);
              sc.writeDevice(state);

              toTurnBackOn.add(new NetworkPathElement(sc, address, ch));

              reSearch = true;
            }
          }
          catch (OneWireIOException e)
          {
          }

        }

      }

      searchResult = this.adapter.findNextDevice();

      if ((!searchResult) && (reSearch))
      {
        searchResult = this.adapter.findFirstDevice();
      }
    }

    searchResult = this.adapter.findFirstDevice();

    List newBranches = new ArrayList();

    while (searchResult) {
      Long address = Long.valueOf(this.adapter.getAddressAsLong());

      if (!this.pathsByAddress.containsKey(address)) {
        OneWireContainer owc = this.adapter.getDeviceContainer(address.longValue());

        if ((owc instanceof OneWireSensor))
        {
          state = ((OneWireSensor)owc).readDevice();
          OneWireContainerInfo info = new OneWireContainerInfo();
          info.setAddress(address);
          info.inspect(owc, state);
          this.pathsByAddress.put(address, new NetworkPath(path, owc, info));

          if ((owc instanceof SwitchContainer)) {
            SwitchContainer sc = (SwitchContainer)owc;

            for (int ch = 0; ch < sc.getNumberChannels(state); ch++)
              newBranches.add(new NetworkPath(path, sc, address, ch));
          }
        }
        else if ((owc instanceof OneWireContainer1D))
        {
          OneWireContainerInfo info = new OneWireContainerInfo();
          info.setAddress(address);
          info.inspect(owc, null);
          this.pathsByAddress.put(address, new NetworkPath(path, owc, info));
        }
      }

      searchResult = this.adapter.findNextDevice();
    }

    for (NetworkPath newBranch : newBranches)
    {
      NetworkPathElement tail = newBranch.getTail();
      SwitchContainer sc = tail.getContainer();
      byte[] state = sc.readDevice();
      sc.setLatchState(tail.getChannel(), true, sc.hasSmartOn(), state);
      sc.writeDevice(state);

      initializeImpl(newBranch, toTurnBackOn);

      sc.setLatchState(tail.getChannel(), false, false, state);
      sc.writeDevice(state);
    }
  }

  private void quickInitializeImpl(NetworkPath path)
    throws OneWireException
  {
    boolean searchResult = this.adapter.findFirstDevice();

    List newBranches = new ArrayList();

    while (searchResult) {
      Long address = Long.valueOf(this.adapter.getAddressAsLong());

      if (!this.pathsByAddress.containsKey(address)) {
        OneWireContainer owc = this.adapter.getDeviceContainer(address.longValue());

        if ((owc instanceof OneWireSensor))
        {
          byte[] state = ((OneWireSensor)owc).readDevice();
          OneWireContainerInfo info = new OneWireContainerInfo();
          info.setAddress(address);
          info.inspect(owc, state);
          this.pathsByAddress.put(address, new NetworkPath(path, owc, info));

          if ((owc instanceof OneWireContainer1F)) {
            SwitchContainer sc = (SwitchContainer)owc;

            for (int ch = 0; ch < sc.getNumberChannels(state); ch++)
              newBranches.add(new NetworkPath(path, sc, address, ch));
          }
        }
        else if ((owc instanceof OneWireContainer1D))
        {
          OneWireContainerInfo info = new OneWireContainerInfo();
          info.setAddress(address);
          info.inspect(owc, null);
          this.pathsByAddress.put(address, new NetworkPath(path, owc, info));
        }
      }

      searchResult = this.adapter.findNextDevice();
    }

    for (NetworkPath newBranch : newBranches)
    {
      NetworkPathElement tail = newBranch.getTail();
      SwitchContainer sc = tail.getContainer();
      byte[] state = sc.readDevice();
      sc.setLatchState(tail.getChannel(), true, sc.hasSmartOn(), state);
      sc.writeDevice(state);

      quickInitializeImpl(newBranch);

      sc.setLatchState(tail.getChannel(), false, false, state);
      sc.writeDevice(state);
    }
  }
}