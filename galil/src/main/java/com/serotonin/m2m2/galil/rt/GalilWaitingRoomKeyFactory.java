package com.serotonin.m2m2.galil.rt;

import com.serotonin.messaging.IncomingResponseMessage;
import com.serotonin.messaging.OutgoingRequestMessage;
import com.serotonin.messaging.WaitingRoomKey;
import com.serotonin.messaging.WaitingRoomKeyFactory;

public class GalilWaitingRoomKeyFactory
  implements WaitingRoomKeyFactory
{
  public WaitingRoomKey createWaitingRoomKey(OutgoingRequestMessage request)
  {
    return new GalilWaitingRoomKey();
  }

  public WaitingRoomKey createWaitingRoomKey(IncomingResponseMessage response)
  {
    return new GalilWaitingRoomKey();
  }

  static class GalilWaitingRoomKey implements WaitingRoomKey
  {
    public int hashCode() {
      return 31;
    }

    public boolean equals(Object obj)
    {
      if (this == obj)
        return true;
      if (obj == null) {
        return false;
      }
      return getClass() == obj.getClass();
    }
  }
}