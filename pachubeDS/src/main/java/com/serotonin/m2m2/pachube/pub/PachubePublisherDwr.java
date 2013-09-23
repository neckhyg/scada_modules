package com.serotonin.m2m2.pachube.pub;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.web.dwr.PublisherEditDwr;
import com.serotonin.m2m2.web.dwr.util.DwrPermission;
import java.util.List;

public class PachubePublisherDwr extends PublisherEditDwr
{
  @DwrPermission(admin=true)
  public ProcessResult savePachubeSender(String name, String xid, boolean enabled, List<PachubePointVO> points, String apiKey, int timeoutSeconds, int retries, int cacheWarningSize, int cacheDiscardSize, boolean changesOnly, boolean sendSnapshot, int snapshotSendPeriods, int snapshotSendPeriodType)
  {
    PachubeSenderVO p = (PachubeSenderVO)Common.getUser().getEditPublisher();

    p.setName(name);
    p.setXid(xid);
    p.setEnabled(enabled);
    p.setPoints(points);
    p.setApiKey(apiKey);
    p.setTimeoutSeconds(timeoutSeconds);
    p.setRetries(retries);
    p.setCacheWarningSize(cacheWarningSize);
    p.setCacheDiscardSize(cacheDiscardSize);
    p.setChangesOnly(changesOnly);
    p.setSendSnapshot(sendSnapshot);
    p.setSnapshotSendPeriods(snapshotSendPeriods);
    p.setSnapshotSendPeriodType(snapshotSendPeriodType);

    return trySave(p);
  }
}