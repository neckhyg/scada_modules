package com.serotonin.m2m2.http.common;

import com.serotonin.util.StringUtils;
import java.util.ArrayList;
import java.util.List;

public class HttpReceiverData
{
  private String remoteIp;
  private String deviceId;
  private long time = -1L;
  private final List<HttpReceiverPointSample> data = new ArrayList();
  private final List<String> unconsumedKeys = new ArrayList();

  public List<HttpReceiverPointSample> getData() {
    return this.data;
  }

  public void addData(String key, String value, long time)
  {
    value = StringUtils.escapeLT(value);
    this.data.add(new HttpReceiverPointSample(key, value, time));
    this.unconsumedKeys.add(key);
  }

  public void consume(String key) {
    this.unconsumedKeys.remove(key);
  }

  public List<String> getUnconsumedKeys() {
    return this.unconsumedKeys;
  }

  public String getRemoteIp() {
    return this.remoteIp;
  }

  public void setRemoteIp(String remoteIp) {
    this.remoteIp = remoteIp;
  }

  public long getTime() {
    return this.time;
  }

  public void setTime(long time) {
    this.time = time;
  }

  public String getDeviceId() {
    return this.deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }
}