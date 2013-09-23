package com.serotonin.m2m2.wibox;

public class MoteData
{
  private final String moteId;
  private final String productNumber;

  public MoteData(String moteId, String productNumber)
  {
    this.moteId = moteId;
    this.productNumber = productNumber;
  }

  public String getModeId()
  {
    return this.moteId;
  }

  public String getProductNumber()
  {
    return this.productNumber;
  }
}