package com.serotonin.m2m2.wibox;

import com.serotonin.m2m2.i18n.Translations;
import com.serotonin.m2m2.web.dwr.beans.AutoShutOff;
import com.serotonin.m2m2.web.dwr.beans.TestingUtility;
import com.serotonin.m2m2.wibox.request.WiBoxRequest;
import java.util.ArrayList;
import java.util.List;

public class WiBoxHttpDataListener
  implements WiBoxMulticastListener, TestingUtility
{
  final Translations translations;
  private final String password;
  String message;
  private final List<String> data = new ArrayList();
  private final AutoShutOff autoShutOff;

  public WiBoxHttpDataListener(Translations translations, String password)
  {
    this.translations = translations;
    this.message = translations.translate("wibox.tester.listening");

    this.password = password;
    WiBoxDataSourceServlet.multicaster.addListener(this);

    this.autoShutOff = new AutoShutOff()
    {
      public void shutOff() {
        WiBoxHttpDataListener.this.message = WiBoxHttpDataListener.this.translations.translate("wibox.tester.auto");
        WiBoxHttpDataListener.this.cancel();
      } } ;
  }

  public List<String> getData() {
    this.autoShutOff.update();
    synchronized (this.data) {
      List result = new ArrayList(this.data);
      this.data.clear();
      return result;
    }
  }

  public String getMessage() {
    this.autoShutOff.update();
    return this.message;
  }

  public void cancel()
  {
    this.autoShutOff.cancel();
    WiBoxDataSourceServlet.multicaster.removeListener(this);
  }

  public String getPassword()
  {
    return this.password;
  }

  public void wiBoxRequest(WiBoxRequest req)
  {
    synchronized (this.data) {
      this.data.add(req.getPassword() + " &gt; " + req.describe());
    }
  }
}