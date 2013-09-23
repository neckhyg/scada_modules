package com.serotonin.m2m2.mbus.dwr;

import com.serotonin.m2m2.i18n.Translations;
import com.serotonin.m2m2.web.dwr.beans.AutoShutOff;
import com.serotonin.m2m2.web.dwr.beans.TestingUtility;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.io.IOException;
import java.util.Map;
import net.sf.mbus4j.MBusAddressing;
import net.sf.mbus4j.SerialPortTools;
import net.sf.mbus4j.dataframes.MBusResponseFramesContainer;
import net.sf.mbus4j.dataframes.ResponseFrameContainer;
import net.sf.mbus4j.master.MBusMaster;
import net.sf.mbus4j.master.MasterEventListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MBusDiscovery
  implements MasterEventListener, TestingUtility
{
  static final Log LOG = LogFactory.getLog(MBusDiscovery.class);
  final Translations translations;
  final MBusAddressing mBusAddressing;
  final MBusMaster master;
  SerialPort sPort;
  private final AutoShutOff autoShutOff;
  String message;
  boolean finished;
  private final SearchThread searchThread;
  private String comPortId;
  private int baudrate;
  int lastPrimaryAddress;
  int firstPrimaryAddress;

  public static MBusDiscovery createPrimaryAddressingSearch(Translations translations, String commPortId, String phonenumber, int baudrate, int flowControlIn, int flowcontrolOut, int dataBits, int stopBits, int parity, int firstPrimaryAddress, int lastPrimaryAddress)
  {
    MBusDiscovery result = new MBusDiscovery(translations, commPortId, phonenumber, MBusAddressing.PRIMARY, baudrate, flowControlIn, flowcontrolOut, dataBits, stopBits, parity);

    result.firstPrimaryAddress = firstPrimaryAddress;
    result.lastPrimaryAddress = lastPrimaryAddress;
    result.searchThread.start();
    return result;
  }

  public static MBusDiscovery createSecondaryAddressingSearch(Translations translations, String commPortId, String phonenumber, int baudrate, int flowControlIn, int flowcontrolOut, int dataBits, int stopBits, int parity)
  {
    MBusDiscovery result = new MBusDiscovery(translations, commPortId, phonenumber, MBusAddressing.SECONDARY, baudrate, flowControlIn, flowcontrolOut, dataBits, stopBits, parity);

    result.searchThread.start();
    return result;
  }

  public MBusResponseFramesContainer getDevice(int deviceIndex) {
    return this.master.getDevice(deviceIndex);
  }

  private MBusDiscovery(Translations translations, String comPortId, String phonenumber, MBusAddressing mBusAddressing, int baudrate, int flowControlIn, int flowcontrolOut, int dataBits, int stopBits, int parity)
  {
    if ((phonenumber != null) && (phonenumber.length() > 0)) {
      throw new IllegalArgumentException("Modem with Phonenumber not implemented yet!");
    }
    LOG.info("MBusDiscovery(...)");
    this.translations = translations;

    this.autoShutOff = new AutoShutOff(240000L)
    {
      public void shutOff() {
        MBusDiscovery.this.message = MBusDiscovery.this.translations.translate("dsEdit.mbus.tester.autoShutOff");
        MBusDiscovery.this.cleanup();
      }
    };
    this.mBusAddressing = mBusAddressing;

    this.master = new MBusMaster();
    try {
      this.comPortId = comPortId;
      this.baudrate = baudrate;
      this.sPort = SerialPortTools.openPort(this.comPortId, this.baudrate);

      this.master.setStreams(this.sPort.getInputStream(), this.sPort.getOutputStream(), baudrate);
    }
    catch (NoSuchPortException ex) {
      LOG.warn("MBusDiscovery(...)", ex);
    }
    catch (PortInUseException ex) {
      LOG.warn("MBusDiscovery(...)", ex);
    }
    catch (UnsupportedCommOperationException ex) {
      LOG.warn("MBusDiscovery(...)", ex);
    }
    catch (IOException ex)
    {
    }

    this.message = translations.translate("dsEdit.mbus.tester.searchingDevices");
    this.searchThread = new SearchThread();
  }

  public void addUpdateInfo(Map<String, Object> result) {
    LOG.info("addUpdateInfo()");
    this.autoShutOff.update();

    MBusDeviceBean[] devs = new MBusDeviceBean[this.master.deviceCount()];
    for (int i = 0; i < devs.length; i++) {
      MBusResponseFramesContainer dev = this.master.getDevice(i);
      devs[i] = new MBusDeviceBean(i, dev);
    }

    result.put("addressing", this.mBusAddressing.getLabel());
    result.put("devices", devs);
    result.put("message", this.message);
    result.put("finished", Boolean.valueOf(this.finished));
  }

  public void cancel()
  {
    LOG.info("cancel()");
    this.message = this.translations.translate("dsEdit.mbus.tester.cancelled");
    cleanup();
  }

  void cleanup() {
    LOG.info("cleanup()");
    if (!this.finished) {
      this.finished = true;
      this.master.cancel();
      this.autoShutOff.cancel();
      this.searchThread.interrupt();
    }
  }

  public void getDeviceDetails(int deviceIndex, Map<String, Object> result) {
    MBusResponseFramesContainer dev = this.master.getDevice(deviceIndex);
    result.put("addressing", this.mBusAddressing.getLabel());
    result.put("deviceName", String.format("%s %s 0x%02X %08d @0x%02X)", new Object[] { dev.getManufacturer(), dev.getMedium(), Byte.valueOf(dev.getVersion()), Integer.valueOf(dev.getIdentNumber()), Byte.valueOf(dev.getAddress()) }));

    result.put("deviceIndex", Integer.valueOf(deviceIndex));

    MBusResponseFrameBean[] responseFrames = new MBusResponseFrameBean[dev.getResponseFrameContainerCount()];
    for (int i = 0; i < dev.getResponseFrameContainerCount(); i++) {
      responseFrames[i] = new MBusResponseFrameBean(dev.getResponseFrameContainer(i).getResponseFrame(), deviceIndex, i, dev.getResponseFrameContainer(i).getName());
    }

    result.put("responseFrames", responseFrames);
  }

  class SearchThread extends Thread
  {
    SearchThread()
    {
    }

    public void run()
    {
      MBusDiscovery.LOG.info("start search");
      try {
        if (MBusDiscovery.this.mBusAddressing == MBusAddressing.PRIMARY) {
          MBusDiscovery.this.master.searchDevicesByPrimaryAddress(MBusDiscovery.this.firstPrimaryAddress, MBusDiscovery.this.lastPrimaryAddress);
        }
        else
          MBusDiscovery.this.master.searchDevicesBySecondaryAddressing();
      }
      catch (InterruptedException ex)
      {
        MBusDiscovery.LOG.info("Interrupted)");
      }
      catch (IOException ex) {
        MBusDiscovery.LOG.warn("SearchThread.run", ex);
      }
      catch (Exception ex) {
        MBusDiscovery.LOG.warn("SearchThread.run", ex);
      }
      MBusDiscovery.LOG.info("Search finished!");
      try {
        MBusDiscovery.this.finished = true;
        MBusDiscovery.this.master.close();
        MBusDiscovery.this.sPort.close();
      }
      catch (InterruptedException ex) {
        MBusDiscovery.LOG.info("Interrupted)");
      }
    }
  }
}