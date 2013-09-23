package com.serotonin.m2m2.persistent.dwr;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.persistent.pub.PersistentPointVO;
import com.serotonin.m2m2.persistent.pub.PersistentSenderRT;
import com.serotonin.m2m2.persistent.pub.PersistentSenderVO;
import com.serotonin.m2m2.rt.RuntimeManager;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.web.dwr.PublisherEditDwr;
import com.serotonin.m2m2.web.dwr.util.DwrPermission;
import java.util.List;

public class PersistentPublisherDwr extends PublisherEditDwr
{
  @DwrPermission(admin=true)
  public ProcessResult savePersistentSender(String name, String xid, boolean enabled, List<PersistentPointVO> points, String host, int port, String authorizationKey, String xidPrefix, int syncType, int cacheWarningSize, int cacheDiscardSize, boolean changesOnly, boolean sendSnapshot, int snapshotSendPeriods, int snapshotSendPeriodType)
  {
    PersistentSenderVO p = (PersistentSenderVO)Common.getUser().getEditPublisher();

    p.setName(name);
    p.setXid(xid);
    p.setEnabled(enabled);
    p.setPoints(points);
    p.setHost(host);
    p.setPort(port);
    p.setAuthorizationKey(authorizationKey);
    p.setXidPrefix(xidPrefix);
    p.setSyncType(syncType);
    p.setCacheWarningSize(cacheWarningSize);
    p.setCacheDiscardSize(cacheDiscardSize);
    p.setChangesOnly(changesOnly);
    p.setSendSnapshot(sendSnapshot);
    p.setSnapshotSendPeriods(snapshotSendPeriods);
    p.setSnapshotSendPeriodType(snapshotSendPeriodType);

    return trySave(p);
  }
  @DwrPermission(admin=true)
  public ProcessResult getPersistentSenderStatus() {
    PersistentSenderVO p = (PersistentSenderVO)Common.getUser().getEditPublisher();
    PersistentSenderRT rt = (PersistentSenderRT)Common.runtimeManager.getRunningPublisher(p.getId());

    ProcessResult response = new ProcessResult();
    if (rt == null) {
      response.addGenericMessage("publisherEdit.persistent.status.notEnabled", new Object[0]);
    } else {
      response.addGenericMessage("publisherEdit.persistent.status.pointCount", new Object[] { Integer.valueOf(rt.getPointCount()) });
      response.addGenericMessage("publisherEdit.persistent.status.queueSize", new Object[] { Integer.valueOf(rt.getQueueSize()) });
      if (rt.getConnectingIndex() != -1) {
        response.addGenericMessage("publisherEdit.persistent.status.connectionState", new Object[] { new TranslatableMessage("publisherEdit.persistent.status.connecting", new Object[] { Integer.valueOf(rt.getConnectingIndex()), Integer.valueOf(rt.getPointCount()) }) });
      }
      else if (rt.isConnected()) {
        response.addGenericMessage("publisherEdit.persistent.status.connectionState", new Object[] { new TranslatableMessage("publisherEdit.persistent.status.connected", new Object[] { Integer.valueOf(rt.getConnectionPort()) }) });
      }
      else {
        response.addGenericMessage("publisherEdit.persistent.status.connectionState", new Object[] { new TranslatableMessage("publisherEdit.persistent.status.notConnected") });
      }
      response.addGenericMessage("publisherEdit.persistent.status.packetQueueSize", new Object[] { Integer.valueOf(rt.getPacketsToSend()) });

      int syncStatus = rt.getSyncStatus();
      if (syncStatus == -1)
        response.addGenericMessage("publisherEdit.persistent.status.syncNotRunning", new Object[0]);
      else {
        response.addGenericMessage("publisherEdit.persistent.status.syncStatus", new Object[] { Integer.valueOf(syncStatus), Integer.valueOf(rt.getPointCount()), Integer.valueOf(rt.getSyncRequestsSent()) });
      }

      response.addGenericMessage("publisherEdit.persistent.status.packetsSentInInterval", new Object[] { Integer.valueOf(rt.getPacketsSentInInterval()) });
    }

    return response;
  }
  @DwrPermission(admin=true)
  public ProcessResult startPersistentSync() {
    PersistentSenderVO p = (PersistentSenderVO)Common.getUser().getEditPublisher();
    PersistentSenderRT rt = (PersistentSenderRT)Common.runtimeManager.getRunningPublisher(p.getId());

    ProcessResult response = new ProcessResult();
    if (rt == null)
      response.addGenericMessage("publisherEdit.persistent.status.notEnabled", new Object[0]);
    else if (rt.startSync())
      response.addGenericMessage("publisherEdit.persistent.syncStarted", new Object[0]);
    else {
      response.addGenericMessage("publisherEdit.persistent.syncNotStarted", new Object[0]);
    }
    return response;
  }
}