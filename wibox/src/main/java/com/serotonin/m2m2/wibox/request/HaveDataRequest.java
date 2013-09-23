package com.serotonin.m2m2.wibox.request;

public class HaveDataRequest extends WiBoxRequest
{
  private final String isReader;
  private final String count;
  private final String resets;
  private final String assPrd;
  private final String mac;
  private final String apChannel;
  private final String time;
  private final String storeFwdEnabled;
  private final String moteId;
  private final String gr1;
  private final String gr2;
  private final String gr3;
  private final String gr4;
  private final String gr1p;
  private final String gr2p;
  private final String gr3p;
  private final String gr4p;
  private final String retryCount;
  private final String apmac;
  private final String rssi;
  private final String parent;
  private final String product;
  private final String upTime;
  private final String voltage;
  private final String codeVersion;

  public HaveDataRequest(String password, String isReader, String count, String resets, String assPrd, String mac, String apChannel, String time, String storeFwdEnabled, String moteId, String gr1, String gr2, String gr3, String gr4, String gr1p, String gr2p, String gr3p, String gr4p, String retryCount, String apmac, String rssi, String parent, String product, String upTime, String voltage, String codeVersion)
  {
    super(password);
    this.isReader = isReader;
    this.count = count;
    this.resets = resets;
    this.assPrd = assPrd;
    this.mac = mac;
    this.apChannel = apChannel;
    this.time = time;
    this.storeFwdEnabled = storeFwdEnabled;
    this.moteId = moteId;
    this.gr1 = gr1;
    this.gr2 = gr2;
    this.gr3 = gr3;
    this.gr4 = gr4;
    this.gr1p = gr1p;
    this.gr2p = gr2p;
    this.gr3p = gr3p;
    this.gr4p = gr4p;
    this.retryCount = retryCount;
    this.apmac = apmac;
    this.rssi = rssi;
    this.parent = parent;
    this.product = product;
    this.upTime = upTime;
    this.voltage = voltage;
    this.codeVersion = codeVersion;
  }

  public String getIsReader()
  {
    return this.isReader;
  }

  public String getCount()
  {
    return this.count;
  }

  public String getResets()
  {
    return this.resets;
  }

  public String getAssPrd()
  {
    return this.assPrd;
  }

  public String getMac()
  {
    return this.mac;
  }

  public String getApChannel()
  {
    return this.apChannel;
  }

  public String getTime()
  {
    return this.time;
  }

  public String getStoreFwdEnabled()
  {
    return this.storeFwdEnabled;
  }

  public String getMoteId()
  {
    return this.moteId;
  }

  public String getGr1()
  {
    return this.gr1;
  }

  public String getGr2()
  {
    return this.gr2;
  }

  public String getGr3()
  {
    return this.gr3;
  }

  public String getGr4()
  {
    return this.gr4;
  }

  public String getGr1p()
  {
    return this.gr1p;
  }

  public String getGr2p()
  {
    return this.gr2p;
  }

  public String getGr3p()
  {
    return this.gr3p;
  }

  public String getGr4p()
  {
    return this.gr4p;
  }

  public String getRetryCount()
  {
    return this.retryCount;
  }

  public String getApmac()
  {
    return this.apmac;
  }

  public String getRssi()
  {
    return this.rssi;
  }

  public String getParent()
  {
    return this.parent;
  }

  public String getProduct()
  {
    return this.product;
  }

  public String getUpTime()
  {
    return this.upTime;
  }

  public String getVoltage()
  {
    return this.voltage;
  }

  public String getCodeVersion()
  {
    return this.codeVersion;
  }

  public String describe()
  {
    return "HaveData: moteId=" + this.moteId + ", product=" + this.product + ", isReader=" + this.isReader + ", count=" + this.count + ", resets=" + this.resets + ", assPrd=" + this.assPrd + ", mac=" + this.mac + ", apChannel=" + this.apChannel + ", time=" + this.time + ", storeFwdEnabled=" + this.storeFwdEnabled + ", gr1=" + this.gr1 + ", gr2=" + this.gr2 + ", gr3=" + this.gr3 + ", gr4=" + this.gr4 + ", gr1p=" + this.gr1p + ", gr2p=" + this.gr2p + ", gr3p=" + this.gr3p + ", gr4p=" + this.gr4p + ", retryCount=" + this.retryCount + ", apmac=" + this.apmac + ", rssi=" + this.rssi + ", parent=" + this.parent + ", upTime=" + this.upTime + ", voltage=" + this.voltage + ", codeVersion=" + this.codeVersion;
  }
}