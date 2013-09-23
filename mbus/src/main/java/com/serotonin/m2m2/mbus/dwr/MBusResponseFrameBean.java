package com.serotonin.m2m2.mbus.dwr;

import java.util.ArrayList;
import java.util.List;
import net.sf.mbus4j.dataframes.Frame;
import net.sf.mbus4j.dataframes.UserDataResponse;

public class MBusResponseFrameBean
{
  private final String name;
  private final Frame rsf;
  private final List<MBusDataBlockBean> dataBlocks = new ArrayList();

  public MBusResponseFrameBean(Frame rsf, int devIndex, int frameIndex, String name)
  {
    this.rsf = rsf;
    this.name = name;
    if ((rsf instanceof UserDataResponse)) {
      UserDataResponse rf = (UserDataResponse)rsf;
      for (int i = 0; i < rf.getDataBlockCount(); i++)
        this.dataBlocks.add(new MBusDataBlockBean(devIndex, frameIndex, i, rf.getDataBlock(i)));
    }
  }

  public boolean addDataBlock(MBusDataBlockBean bean)
  {
    return this.dataBlocks.add(bean);
  }

  public MBusDataBlockBean[] getDataBlocks() {
    return (MBusDataBlockBean[])this.dataBlocks.toArray(new MBusDataBlockBean[this.dataBlocks.size()]);
  }

  public String getName() {
    return this.name;
  }

  public Frame getRsf() {
    return this.rsf;
  }
}