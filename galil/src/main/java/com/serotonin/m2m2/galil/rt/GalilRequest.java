package com.serotonin.m2m2.galil.rt;

import com.serotonin.messaging.OutgoingRequestMessage;

public class GalilRequest
  implements OutgoingRequestMessage
{
  private final String data;

  public GalilRequest(String data)
  {
    this.data = (data + "\r\n");
  }

  public boolean expectsResponse()
  {
    return true;
  }

  public byte[] getMessageData() {
    return this.data.getBytes(GalilDataSourceRT.CHARSET);
  }
}