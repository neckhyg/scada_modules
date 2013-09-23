package com.serotonin.m2m2.onewire.common;

import com.dalsemi.onewire.container.ADContainer;
import com.dalsemi.onewire.container.HumidityContainer;
import com.dalsemi.onewire.container.OneWireContainer;
import com.dalsemi.onewire.container.OneWireContainer1D;
import com.dalsemi.onewire.container.PotentiometerContainer;
import com.dalsemi.onewire.container.SwitchContainer;
import com.dalsemi.onewire.container.TemperatureContainer;
import com.dalsemi.onewire.utils.Address;
import java.util.ArrayList;
import java.util.List;

public class OneWireContainerInfo
{
  private Long address;
  private String description;
  private List<OneWireContainerAttribute> attributes;

  public Long getAddress()
  {
    return this.address;
  }

  public void setAddress(Long address) {
    this.address = address;
  }

  public String getAddressString() {
    return Address.toString(this.address.longValue());
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<OneWireContainerAttribute> getAttributes() {
    return this.attributes;
  }

  public void setAttributes(List<OneWireContainerAttribute> attributes) {
    this.attributes = attributes;
  }

  public void inspect(OneWireContainer container, byte[] state) {
    this.description = (container.getAlternateNames() + " (" + container.getName() + ")");
    this.attributes = new ArrayList();

    if ((container instanceof TemperatureContainer)) {
      this.attributes.add(new OneWireContainerAttribute(1));
    }
    if ((container instanceof HumidityContainer)) {
      this.attributes.add(new OneWireContainerAttribute(2));
    }
    if ((container instanceof ADContainer)) {
      ADContainer ac = (ADContainer)container;
      OneWireContainerAttribute attr = new OneWireContainerAttribute(3, 0, ac.getNumberADChannels());

      this.attributes.add(attr);
    }

    if ((container instanceof SwitchContainer)) {
      SwitchContainer sc = (SwitchContainer)container;
      OneWireContainerAttribute attr = new OneWireContainerAttribute(4, 0, sc.getNumberChannels(state));

      this.attributes.add(attr);
    }

    if ((container instanceof PotentiometerContainer)) {
      PotentiometerContainer pc = (PotentiometerContainer)container;
      OneWireContainerAttribute attr = new OneWireContainerAttribute(5, 0, pc.numberOfPotentiometers(state));

      this.attributes.add(attr);
    }

    if ((container instanceof OneWireContainer1D)) {
      OneWireContainerAttribute attr = new OneWireContainerAttribute(6, 12, 4);

      this.attributes.add(attr);
    }
  }
}