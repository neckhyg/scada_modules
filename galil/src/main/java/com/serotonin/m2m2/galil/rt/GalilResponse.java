package com.serotonin.m2m2.galil.rt;

import com.serotonin.messaging.IncomingResponseMessage;

public class GalilResponse
  implements IncomingResponseMessage
{
  private final boolean errorResponse;
  private final byte[] messageData;

  public GalilResponse()
  {
    this.errorResponse = true;
    this.messageData = new byte[0];
  }

  public GalilResponse(byte[] messageData) {
    this.errorResponse = false;
    this.messageData = messageData;
  }

  public boolean isErrorResponse() {
    return this.errorResponse;
  }

  public String getResponseData() {
    return new String(this.messageData, GalilDataSourceRT.CHARSET).trim();
  }
}