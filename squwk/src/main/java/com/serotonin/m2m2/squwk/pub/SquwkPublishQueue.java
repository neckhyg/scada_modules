package com.serotonin.m2m2.squwk.pub;

import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.publish.PublishQueue;
import com.serotonin.m2m2.rt.publish.PublisherRT;

public class SquwkPublishQueue extends PublishQueue<SquwkPointVO>
{
  public SquwkPublishQueue(PublisherRT<SquwkPointVO> owner, int warningSize, int discardSize)
  {
    super(owner, warningSize, discardSize);
  }

  public synchronized void add(SquwkPointVO vo, PointValueTime pvt)
  {
    super.add(vo, pvt);
  }
}