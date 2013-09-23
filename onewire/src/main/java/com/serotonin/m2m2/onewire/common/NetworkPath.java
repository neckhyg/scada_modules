package com.serotonin.m2m2.onewire.common;

import com.dalsemi.onewire.OneWireException;
import com.dalsemi.onewire.adapter.OneWireIOException;
import com.dalsemi.onewire.container.OneWireContainer;
import com.dalsemi.onewire.container.OneWireContainer1F;
import com.dalsemi.onewire.container.SwitchContainer;
import java.util.ArrayList;
import java.util.List;

public class NetworkPath
{
  private final List<NetworkPathElement> elements = new ArrayList();
  private final Network network;
  private final OneWireContainer target;
  private final OneWireContainerInfo targetInfo;

  public NetworkPath(Network network)
  {
    this.network = network;
    this.target = null;
    this.targetInfo = null;
  }

  public NetworkPath(NetworkPath currentPath, OneWireContainer target, OneWireContainerInfo targetInfo) {
    this.network = currentPath.network;
    this.elements.addAll(currentPath.elements);
    this.target = target;
    this.targetInfo = targetInfo;
  }

  public NetworkPath(NetworkPath currentPath, SwitchContainer sc, Long address, int channel) {
    this.network = currentPath.network;
    this.elements.addAll(currentPath.elements);
    this.elements.add(new NetworkPathElement(sc, address, channel));
    this.target = null;
    this.targetInfo = null;
  }

  public OneWireContainer getTarget() {
    return this.target;
  }

  public OneWireContainerInfo getTargetInfo() {
    return this.targetInfo;
  }

  public boolean isCoupler() {
    return this.target instanceof OneWireContainer1F;
  }

  public boolean equals(NetworkPath otherPath) {
    return toString().equals(otherPath.toString());
  }

  public NetworkPathElement getTail() {
    if (this.elements.size() == 0)
      return null;
    return (NetworkPathElement)this.elements.get(this.elements.size() - 1);
  }

  public String toString()
  {
    StringBuilder sb = new StringBuilder();

    sb.append(this.network.getAdapterName());
    try {
      String portName = this.network.getPortName();
      sb.append('_').append(portName);
    }
    catch (OneWireException e)
    {
    }
    sb.append('/');

    for (NetworkPathElement element : this.elements) {
      sb.append(((OneWireContainer)element.getContainer()).getAddressAsString());
      sb.append('_');
      sb.append(element.getChannel());
      sb.append('/');
    }

    return sb.toString();
  }

  public void open()
    throws OneWireException, OneWireIOException
  {
    open(null);
  }

  public void open(NetworkPath lastPath)
    throws OneWireException, OneWireIOException
  {
    if (this.elements.size() == 0) {
      if (lastPath != null)
        lastPath.close();
      this.network.reset();
      return;
    }

    int uncommonIndex = 0;
    if (lastPath != null)
    {
      List lastElements = lastPath.elements;
      int minSize = lastElements.size();
      if (this.elements.size() < minSize) {
        minSize = this.elements.size();
      }
      for (int i = 0; (i < minSize) && 
        (((NetworkPathElement)lastElements.get(i)).equals(this.elements.get(i))); i++)
      {
        uncommonIndex++;
      }

      lastPath.close(uncommonIndex);
    }

    for (int i = uncommonIndex; i < this.elements.size(); i++) {
      NetworkPathElement element = (NetworkPathElement)this.elements.get(i);

      SwitchContainer sw = element.getContainer();

      byte[] state = sw.readDevice();

      sw.setLatchState(element.getChannel(), true, sw.hasSmartOn(), state);
      sw.writeDevice(state);
    }
  }

  public void close()
    throws OneWireException, OneWireIOException
  {
    close(0);
  }

  private void close(int downToInclusive)
    throws OneWireException, OneWireIOException
  {
    for (int i = this.elements.size() - 1; i >= downToInclusive; i--) {
      NetworkPathElement element = (NetworkPathElement)this.elements.get(i);
      SwitchContainer sw = element.getContainer();

      byte[] state = sw.readDevice();

      sw.setLatchState(element.getChannel(), false, false, state);
      sw.writeDevice(state);
    }
  }
}