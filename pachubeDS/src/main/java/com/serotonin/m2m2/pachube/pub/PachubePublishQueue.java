package com.serotonin.m2m2.pachube.pub;

import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.publish.PublishQueue;
import com.serotonin.m2m2.rt.publish.PublishQueueEntry;
import com.serotonin.m2m2.rt.publish.PublisherRT;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PachubePublishQueue extends PublishQueue<PachubePointVO>
{
  public PachubePublishQueue(PublisherRT<PachubePointVO> owner, int warningSize, int discardSize)
  {
    super(owner, warningSize, discardSize);
  }

  public synchronized void add(PachubePointVO vo, PointValueTime pvt)
  {
    Iterator iter = this.queue.iterator();
    while (iter.hasNext()) {
      PachubePointVO entry = (PachubePointVO)((PublishQueueEntry)iter.next()).getVo();
      if ((entry.getFeedId() == vo.getFeedId()) && (entry.getDataStreamId() == vo.getDataStreamId())) {
        iter.remove();
      }
    }
    super.add(vo, pvt);
  }
}