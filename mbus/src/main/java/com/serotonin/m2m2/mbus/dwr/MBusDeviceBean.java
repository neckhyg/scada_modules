package com.serotonin.m2m2.mbus.dwr;

import net.sf.mbus4j.dataframes.MBusMedium;
import net.sf.mbus4j.dataframes.MBusResponseFramesContainer;

public class MBusDeviceBean
{
  private final int index;
  private final MBusResponseFramesContainer dev;

  public static boolean compare(MBusResponseFramesContainer dev, String address, String id, String man, String medium, String version)
  {
    boolean result = address.equals(String.format("0x%02X", new Object[] { Byte.valueOf(dev.getAddress()) }));
    result &= id.equals(String.format("%08d", new Object[] { Integer.valueOf(dev.getIdentNumber()) }));
    result &= man.equals(dev.getManufacturer());
    result &= medium.equals(dev.getMedium().name());
    result &= version.equals(String.format("0x%02X", new Object[] { Byte.valueOf(dev.getAddress()) }));
    return result;
  }

  public MBusDeviceBean(int index, MBusResponseFramesContainer dev) {
    this.index = index;
    this.dev = dev;
  }

  public byte getAddress()
  {
    return this.dev.getAddress();
  }

  public String getAddressHex()
  {
    return String.format("0x%02X", new Object[] { Byte.valueOf(this.dev.getAddress()) });
  }

  public String getIdentNumber()
  {
    return String.format("%08d", new Object[] { Integer.valueOf(this.dev.getIdentNumber()) });
  }

  public String getManufacturer()
  {
    return this.dev.getManufacturer();
  }

  public String getMedium()
  {
    return this.dev.getMedium().name();
  }

  public byte getVersion()
  {
    return this.dev.getVersion();
  }

  public String getVersionHex()
  {
    return String.format("0x%02X", new Object[] { Byte.valueOf(this.dev.getVersion()) });
  }

  public int getIndex()
  {
    return this.index;
  }
}