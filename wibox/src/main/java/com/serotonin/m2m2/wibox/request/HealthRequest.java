package com.serotonin.m2m2.wibox.request;

public class HealthRequest extends WiBoxRequest
{
  private final String seqno;
  private final String voltage;
  private final String originaddr;
  private final String sourceaddr;
  private final String quality;
  private final String rssi;

  public HealthRequest(String password, String seqno, String voltage, String originaddr, String sourceaddr, String quality, String rssi)
  {
    super(password);
    this.seqno = seqno;
    this.voltage = voltage;
    this.originaddr = originaddr;
    this.sourceaddr = sourceaddr;
    this.quality = quality;
    this.rssi = rssi;
  }

  public String getSeqno() {
    return this.seqno;
  }

  public String getVoltage() {
    return this.voltage;
  }

  public String getOriginaddr() {
    return this.originaddr;
  }

  public String getSourceaddr() {
    return this.sourceaddr;
  }

  public String getQuality() {
    return this.quality;
  }

  public String getRssi() {
    return this.rssi;
  }

  public String describe()
  {
    return "Health: seqno=" + this.seqno + ", voltage=" + this.voltage + ", originaddr=" + this.originaddr + ", sourceaddr=" + this.sourceaddr + ", quality=" + this.quality + ", rssi=" + this.rssi;
  }
}