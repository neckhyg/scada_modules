package com.serotonin.m2m2.openv.dwr;

import com.serotonin.m2m2.i18n.Translations;
import com.serotonin.m2m2.web.dwr.beans.AutoShutOff;
import com.serotonin.m2m2.web.dwr.beans.TestingUtility;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.mbus4j.master.MasterEventListener;
import net.sf.openv4j.DataPoint;
import net.sf.openv4j.Devices;
import net.sf.openv4j.Group;
import net.sf.openv4j.protocolhandlers.ProtocolHandler;
import net.sf.openv4j.protocolhandlers.SegmentedDataContainer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OpenV4JDiscovery
  implements MasterEventListener, TestingUtility
{
  static final Log LOG = LogFactory.getLog(OpenV4JDiscovery.class);
  final Translations translations;
  SerialPort sPort;
  final ProtocolHandler protocolHandler;
  private final AutoShutOff autoShutOff;
  String message;
  boolean finished;
  private SearchThread searchThread;
  final SegmentedDataContainer dc;

  private void addToDataContainer(DataPoint dataPoint)
  {
    this.dc.addToDataContainer(dataPoint);
  }

  private void setMessage(String message) {
    this.message = message;
  }

  public OpenV4JDiscovery(Translations translations)
  {
    LOG.info("OpenV4J Discovery(...)");
    this.translations = translations;
    this.autoShutOff = new AutoShutOff(240000L)
    {
      public void shutOff() {
        OpenV4JDiscovery.this.message = OpenV4JDiscovery.this.translations.translate("dsEdit.mbus.tester.auto");
        OpenV4JDiscovery.this.cleanup();
      }
    };
    this.protocolHandler = new ProtocolHandler();
    this.dc = new SegmentedDataContainer();
  }

  public static OpenV4JDiscovery searchDataPoints(Translations translations, String commPortId) {
    OpenV4JDiscovery result = new OpenV4JDiscovery(translations);
    for (DataPoint dp : DataPoint.values()) {
      result.addToDataContainer(dp);
    }
    result.start(commPortId);
    result.setMessage(translations.translate("dsEdit.openv4j.tester.searchingDataPoints"));
    return result;
  }

  public static OpenV4JDiscovery detectDevice(Translations translations, String commPortId) {
    OpenV4JDiscovery result = new OpenV4JDiscovery(translations);
    result.addToDataContainer(DataPoint.COMMON_CONFIG_DEVICE_TYPE_ID);
    result.start(commPortId);
    result.setMessage(translations.translate("dsEdit.openv4j.tester.detectingDevice"));
    return result;
  }

  private void start(String commPortId) {
    try {
      this.sPort = ProtocolHandler.openPort(commPortId);
      this.protocolHandler.setStreams(this.sPort.getInputStream(), this.sPort.getOutputStream());
    }
    catch (NoSuchPortException ex) {
      Logger.getLogger(OpenV4JDiscovery.class.getName()).log(Level.SEVERE, null, ex);
    }
    catch (PortInUseException ex) {
      Logger.getLogger(OpenV4JDiscovery.class.getName()).log(Level.SEVERE, null, ex);
    }
    catch (UnsupportedCommOperationException ex) {
      Logger.getLogger(OpenV4JDiscovery.class.getName()).log(Level.SEVERE, null, ex);
    }
    catch (IOException ex) {
      Logger.getLogger(OpenV4JDiscovery.class.getName()).log(Level.SEVERE, null, ex);
    }
    this.searchThread = new SearchThread();
    this.searchThread.start();
  }

  public void addDeviceInfo(Map<String, Object> result) {
    LOG.info("addDecviceInfo()");
    this.autoShutOff.update();
    result.put("finished", Boolean.valueOf(this.finished));
    if (this.finished) {
      result.put("deviceName", Devices.getDeviceById(this.dc.getShortHex(DataPoint.COMMON_CONFIG_DEVICE_TYPE_ID.getAddr()).shortValue()).name());

      result.put("message", this.message);
    }
  }

  public void addUpdateInfo(Map<String, Object> result) {
    LOG.info("addUpdateInfo()");
    this.autoShutOff.update();

    DataPoint[] p = DataPoint.values();
    List values = new ArrayList(p.length);
    DataPoint[] sortedPoints = DataPoint.getSortedPoints();
    for (Group g : Group.values()) {
      for (DataPoint pr : sortedPoints) {
        if (g == pr.getGroup()) {
          String valueAsString = String.format("%s", new Object[] { pr.decode(this.dc) });
          values.add(new OpenV4JDataPointBean(pr, valueAsString));
        }
      }
    }
    result.put("valuesByGroup", values);
    result.put("message", this.message);
    result.put("finished", Boolean.valueOf(this.finished));
  }

  public void cancel()
  {
    LOG.info("cancel()");
    this.message = this.translations.translate("dsEdit.openv4j.tester.cancelled");
    cleanup();
  }

  void cleanup() {
    LOG.info("cleanup()");
    if (!this.finished) {
      this.finished = true;
      try {
        this.protocolHandler.close();
      }
      catch (InterruptedException ex) {
        LOG.error("Shutdown comport", ex);
      }
      this.autoShutOff.cancel();
      this.searchThread.interrupt();
    }
  }

  class SearchThread extends Thread
  {
    SearchThread()
    {
    }

    public void run()
    {
      OpenV4JDiscovery.LOG.info("start search");
      try {
        OpenV4JDiscovery.this.protocolHandler.setReadRequest(OpenV4JDiscovery.this.dc);
        synchronized (OpenV4JDiscovery.this.dc) {
          OpenV4JDiscovery.this.dc.wait(4000 * OpenV4JDiscovery.this.dc.getDataBlockCount());
        }
      }
      catch (InterruptedException ex)
      {
        OpenV4JDiscovery.LOG.info("Interrupted)");
      }
      catch (Exception ex) {
        OpenV4JDiscovery.LOG.warn("SearchThread.run", ex);
      }
      OpenV4JDiscovery.LOG.info("Search finished!");
      try {
        OpenV4JDiscovery.this.finished = true;
        OpenV4JDiscovery.this.protocolHandler.close();
        if (OpenV4JDiscovery.this.sPort != null)
          OpenV4JDiscovery.this.sPort.close();
      }
      catch (InterruptedException ex)
      {
        OpenV4JDiscovery.LOG.info("Interrupted)");
      }
    }
  }
}