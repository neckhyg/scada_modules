package com.serotonin.m2m2.http.dwr;

import com.serotonin.m2m2.http.common.HttpDataSourceServlet;
import com.serotonin.m2m2.http.common.HttpMulticastListener;
import com.serotonin.m2m2.http.common.HttpReceiverData;
import com.serotonin.m2m2.http.common.HttpReceiverMulticaster;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.i18n.Translations;
import com.serotonin.m2m2.web.dwr.beans.AutoShutOff;
import com.serotonin.m2m2.web.dwr.beans.TestingUtility;

public class HttpReceiverDataListener
  implements HttpMulticastListener, TestingUtility
{
  final Translations translations;
  private final String[] ipWhiteList;
  private final String[] deviceIdWhiteList;
  String message;
  private HttpReceiverData data;
  private final AutoShutOff autoShutOff;

  public HttpReceiverDataListener(Translations translations, String[] ipWhiteList, String[] deviceIdWhiteList)
  {
    this.translations = translations;
    this.message = translations.translate("dsEdit.httpReceiver.tester.listening");

    this.ipWhiteList = ipWhiteList;
    this.deviceIdWhiteList = deviceIdWhiteList;
    HttpDataSourceServlet.httpReceiverMulticaster.addListener(this);

    this.autoShutOff = new AutoShutOff()
    {
      protected void shutOff() {
        HttpReceiverDataListener.this.message = HttpReceiverDataListener.this.translations.translate("dsEdit.httpReceiver.tester.auto");
        HttpReceiverDataListener.this.cancel();
      } } ;
  }

  public HttpReceiverData getData() {
    this.autoShutOff.update();
    return this.data;
  }

  public String getMessage() {
    this.autoShutOff.update();
    return this.message;
  }

  public void cancel() {
    this.autoShutOff.cancel();
    HttpDataSourceServlet.httpReceiverMulticaster.removeListener(this);
  }

  public String[] getDeviceIdWhiteList()
  {
    return this.deviceIdWhiteList;
  }

  public String[] getIpWhiteList() {
    return this.ipWhiteList;
  }

  public void ipWhiteListError(String message) {
    message = new TranslatableMessage("dsEdit.httpReceiver.tester.whiteList", new Object[] { message }).translate(this.translations);
  }

  public void data(HttpReceiverData data) {
    this.message = this.translations.translate("dsEdit.httpReceiver.tester.data");
    this.data = data;
  }
}