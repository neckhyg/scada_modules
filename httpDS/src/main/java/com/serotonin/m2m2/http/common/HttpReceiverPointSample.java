package com.serotonin.m2m2.http.common;

import com.serotonin.m2m2.web.taglib.Functions;

public class HttpReceiverPointSample
{
  private final String key;
  private final String value;
  private final long time;

  public HttpReceiverPointSample(String key, String value, long time)
  {
    this.key = key;
    this.value = value;
    this.time = time;
  }

  public String getKey() {
    return this.key;
  }

  public String getValue() {
    return this.value;
  }

  public long getTime() {
    return this.time;
  }

  public String getPrettyTime() {
    if (this.time == 0L)
      return null;
    return Functions.getTime(this.time);
  }
}