package com.serotonin.m2m2.http.dwr;

import com.serotonin.db.pair.StringStringPair;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.http.vo.HttpPointVO;
import com.serotonin.m2m2.http.vo.HttpSenderVO;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.web.dwr.PublisherEditDwr;
import com.serotonin.m2m2.web.dwr.util.DwrPermission;
import java.util.List;

public class HttpPublisherDwr extends PublisherEditDwr
{
  @DwrPermission(admin=true)
  public ProcessResult saveHttpSender(String name, String xid, boolean enabled, List<HttpPointVO> points, String url, boolean usePost, List<StringStringPair> staticHeaders, List<StringStringPair> staticParameters, int cacheWarningSize, int cacheDiscardSize, boolean changesOnly, boolean raiseResultWarning, int dateFormat, boolean sendSnapshot, int snapshotSendPeriods, int snapshotSendPeriodType)
  {
    HttpSenderVO p = (HttpSenderVO)Common.getUser().getEditPublisher();

    p.setName(name);
    p.setXid(xid);
    p.setEnabled(enabled);
    p.setPoints(points);
    p.setUrl(url);
    p.setUsePost(usePost);
    p.setStaticHeaders(staticHeaders);
    p.setStaticParameters(staticParameters);
    p.setCacheWarningSize(cacheWarningSize);
    p.setCacheDiscardSize(cacheDiscardSize);
    p.setChangesOnly(changesOnly);
    p.setRaiseResultWarning(raiseResultWarning);
    p.setDateFormat(dateFormat);
    p.setSendSnapshot(sendSnapshot);
    p.setSnapshotSendPeriods(snapshotSendPeriods);
    p.setSnapshotSendPeriodType(snapshotSendPeriodType);

    return trySave(p);
  }

  @DwrPermission(admin=true)
  public void httpSenderTest(String url, boolean usePost, List<StringStringPair> staticHeaders, List<StringStringPair> staticParameters) {
    Common.getUser().setTestingUtility(new HttpSenderTester(url, usePost, staticHeaders, staticParameters));
  }
  @DwrPermission(admin=true)
  public String httpSenderTestUpdate() {
    HttpSenderTester test = (HttpSenderTester)Common.getUser().getTestingUtility(HttpSenderTester.class);
    if (test == null)
      return null;
    return test.getResult();
  }
}