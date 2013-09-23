package com.serotonin.m2m2.mbus.dwr;

import net.sf.mbus4j.dataframes.datablocks.DataBlock;

public class MBusDataBlockBean
{
  private final int dbIndex;
  private final int devIndex;
  private final int rsIndex;
  private final DataBlock db;

  MBusDataBlockBean(int devIndex, int rsIndex, int dbIndex, DataBlock db)
  {
    this.devIndex = devIndex;
    this.rsIndex = rsIndex;
    this.dbIndex = dbIndex;
    this.db = db;
  }

  public String getName() {
    return this.db.getParamDescr();
  }

  public int getDbIndex() {
    return this.dbIndex;
  }

  public int getRsIndex() {
    return this.rsIndex;
  }

  public int getDevIndex() {
    return this.devIndex;
  }

  public String getParams() {
    return this.db.toString();
  }

  public String getValue() {
    return this.db.getValueAsString();
  }
}