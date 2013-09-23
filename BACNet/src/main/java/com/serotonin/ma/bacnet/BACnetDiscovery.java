package com.serotonin.ma.bacnet;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.event.DefaultDeviceEventListener;
import com.serotonin.bacnet4j.event.DeviceEventHandler;
import com.serotonin.bacnet4j.event.ExceptionDispatch;
import com.serotonin.bacnet4j.event.ExceptionListener;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.exception.PropertyValueException;
import com.serotonin.bacnet4j.npdu.Network;
import com.serotonin.bacnet4j.obj.ObjectCovSubscription;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.transport.Transport;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.constructed.ObjectPropertyReference;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.constructed.ServicesSupported;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.CharacterString;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.bacnet4j.util.PropertyReferences;
import com.serotonin.bacnet4j.util.PropertyValues;
import com.serotonin.m2m2.i18n.Translations;
import com.serotonin.m2m2.web.dwr.beans.AutoShutOff;
import com.serotonin.m2m2.web.dwr.beans.TestingUtility;
import com.serotonin.util.queue.ByteQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BACnetDiscovery extends DefaultDeviceEventListener
  implements TestingUtility, ExceptionListener
{
  private static final Log LOG = LogFactory.getLog(BACnetDiscovery.class);
  final Translations translations;
  private int nextDeviceId;
  private final LocalDevice localDevice;
  private final AutoShutOff autoShutOff;
  private final Map<Integer, RemoteDevice> iamResponders = new HashMap();
  private final List<BACnetDeviceBean> freshIAms = new ArrayList();
  String message;
  private boolean finished;

  public BACnetDiscovery(Translations translations, int deviceId, Network network, int timeout, int segTimeout, int segWindow, int retries, int maxReadMultipleReferencesSegmented, int maxReadMultipleReferencesNonsegmented)
  {
    this.translations = translations;

    this.autoShutOff = new AutoShutOff()
    {
      protected void shutOff() {
        BACnetDiscovery.this.message = BACnetDiscovery.this.translations.translate("mod.bacnet.tester.auto");
        BACnetDiscovery.this.cleanup();
      }
    };
    ExceptionDispatch.addListener(this);

    Transport transport = new Transport(network, true);
    transport.setTimeout(timeout);
    transport.setSegTimeout(segTimeout);
    transport.setSegWindow(segWindow);
    transport.setRetries(retries);

    this.localDevice = new LocalDevice(deviceId, transport);
    this.localDevice.getEventHandler().addListener(this);
    this.localDevice.setMaxReadMultipleReferencesSegmented(maxReadMultipleReferencesSegmented);
    this.localDevice.setMaxReadMultipleReferencesNonsegmented(maxReadMultipleReferencesNonsegmented);
    try
    {
      this.localDevice.initialize();
    }
    catch (Exception e) {
      LOG.warn("", e);
      this.message = e.getMessage();
      cleanup();
      return;
    }

    WhoIsRequest whoIs = new WhoIsRequest();
    try {
      this.localDevice.sendGlobalBroadcast(whoIs);
    }
    catch (BACnetException e) {
      LOG.warn("", e);
      this.message = e.getMessage();
      cleanup();
      return;
    }

    this.message = translations.translate("mod.bacnet.tester.listening");
  }

  void cleanup() {
    if (!this.finished) {
      this.finished = true;
      this.autoShutOff.cancel();
      this.localDevice.terminate();
      ExceptionDispatch.removeListener(this);
    }
  }

  public void cancel()
  {
    this.message = this.translations.translate("mod.bacnet.tester.cancelled");
    cleanup();
  }

  public LocalDevice getLocalDevice() {
    return this.localDevice;
  }

  public void iAmReceived(RemoteDevice d)
  {
    synchronized (this.iamResponders) {
      if (!this.iamResponders.containsValue(d)) {
        int referenceId = this.nextDeviceId++;
        this.iamResponders.put(Integer.valueOf(referenceId), d);

        synchronized (this.freshIAms) {
          this.freshIAms.add(new BACnetDeviceBean(d));
        }
      }
    }
  }

  public void listenerException(Throwable e)
  {
    this.message = e.getMessage();
    cleanup();
  }

  public void addUpdateInfo(Map<String, Object> result) {
    this.autoShutOff.update();

    result.put("devices", getIAmsReceived());
    result.put("message", this.message);
    result.put("finished", Boolean.valueOf(this.finished));
  }

  private List<BACnetDeviceBean> getIAmsReceived() {
    synchronized (this.freshIAms) {
      List result = new ArrayList(this.freshIAms);
      this.freshIAms.clear();
      return result;
    }
  }

  public static String getDeviceDescription(RemoteDevice d) {
    return d.getInstanceNumber() + " @ " + d.getAddress().getDescription();
  }

  public void receivedException(Exception e)
  {
    this.message = e.getMessage();
  }

  public void receivedThrowable(Throwable t)
  {
    this.message = t.getMessage();
  }

  public void unimplementedVendorService(UnsignedInteger vendorId, UnsignedInteger serviceNumber, ByteQueue queue)
  {
    this.message = ("Received unimplemented vendor service: vendor id=" + vendorId + ", service number=" + serviceNumber + ", bytes (with context id)=" + queue);
  }

  public static List<BACnetObjectBean> getObjects(LocalDevice localDevice, RemoteDevice d)
    throws Exception
  {
    localDevice.getExtendedDeviceInformation(d);

    List<ObjectIdentifier> oids = ((SequenceOf)localDevice.sendReadPropertyAllowNull(d, d.getObjectIdentifier(), PropertyIdentifier.objectList)).getValues();

    PropertyReferences refs = new PropertyReferences();
    Map objectProperties = new HashMap();
    for (ObjectIdentifier oid : oids) {
      addPropertyReferences(refs, oid);

      BACnetObjectBean bean = new BACnetObjectBean();
      bean.setObjectTypeId(oid.getObjectType().intValue());
      bean.setObjectTypeDescription(oid.getObjectType().toString());
      bean.setInstanceNumber(oid.getInstanceNumber());
      bean.setCov((d.getServicesSupported().isSubscribeCov()) && (ObjectCovSubscription.supportedObjectType(oid.getObjectType())));

      if ((ObjectType.binaryInput.equals(oid.getObjectType())) || (ObjectType.binaryOutput.equals(oid.getObjectType())) || (ObjectType.binaryValue.equals(oid.getObjectType())))
      {
        bean.setDataTypeId(1);
        bean.getUnitsDescription().add("");
        bean.getUnitsDescription().add("");
      }
      else if ((ObjectType.multiStateInput.equals(oid.getObjectType())) || (ObjectType.multiStateOutput.equals(oid.getObjectType())) || (ObjectType.multiStateValue.equals(oid.getObjectType())) || (ObjectType.lifeSafetyPoint.equals(oid.getObjectType())) || (ObjectType.lifeSafetyZone.equals(oid.getObjectType())))
      {
        bean.setDataTypeId(2);
      }
      else {
        bean.setDataTypeId(3);
      }
      objectProperties.put(oid, bean);
    }

    PropertyValues values = localDevice.readProperties(d, refs);

    for (ObjectPropertyReference value : values) {
      ObjectIdentifier oid = value.getObjectIdentifier();
      PropertyIdentifier pid = value.getPropertyIdentifier();

      BACnetObjectBean bean = (BACnetObjectBean)objectProperties.get(oid);
      if (pid.equals(PropertyIdentifier.objectName)) {
        bean.setObjectName(values.getString(oid, pid));
      } else if (pid.equals(PropertyIdentifier.presentValue)) {
        bean.setPresentValue(values.getString(oid, pid));
      } else if (pid.equals(PropertyIdentifier.modelName)) {
        bean.setPresentValue(values.getString(oid, pid));
      } else if (pid.equals(PropertyIdentifier.units)) {
        bean.getUnitsDescription().add(values.getString(oid, pid));
      } else if (pid.equals(PropertyIdentifier.inactiveText)) {
        Encodable e = values.getNullOnError(oid, pid);
        bean.getUnitsDescription().set(0, e == null ? "0" : e.toString());
      }
      else if (pid.equals(PropertyIdentifier.activeText)) {
        Encodable e = values.getNullOnError(oid, pid);
        bean.getUnitsDescription().set(1, e == null ? "1" : e.toString());
      }
      else if (pid.equals(PropertyIdentifier.outputUnits)) {
        bean.getUnitsDescription().add(values.getString(oid, pid));
      } else if (pid.equals(PropertyIdentifier.stateText)) {
        try {
          SequenceOf<CharacterString> states = (SequenceOf)values.get(oid, pid);
          for (CharacterString state : states)
            bean.getUnitsDescription().add(state.toString());
        }
        catch (PropertyValueException e) {
          LOG.warn("Error in stateText result: " + e.getError());
        }
      }
      else if (pid.equals(PropertyIdentifier.modelName)) {
        bean.setPresentValue(values.getString(oid, pid));
      }
    }

    return new ArrayList(objectProperties.values());
  }

  private static void addPropertyReferences(PropertyReferences refs, ObjectIdentifier oid) {
    refs.add(oid, PropertyIdentifier.objectName);

    ObjectType type = oid.getObjectType();
    if (ObjectType.accumulator.equals(type)) {
      refs.add(oid, PropertyIdentifier.units);
      refs.add(oid, PropertyIdentifier.presentValue);
    }
    else if ((ObjectType.analogInput.equals(type)) || (ObjectType.analogOutput.equals(type)) || (ObjectType.analogValue.equals(type)) || (ObjectType.pulseConverter.equals(type)))
    {
      refs.add(oid, PropertyIdentifier.units);
      refs.add(oid, PropertyIdentifier.presentValue);
    }
    else if ((ObjectType.binaryInput.equals(type)) || (ObjectType.binaryOutput.equals(type)) || (ObjectType.binaryValue.equals(type)))
    {
      refs.add(oid, PropertyIdentifier.inactiveText);
      refs.add(oid, PropertyIdentifier.activeText);
      refs.add(oid, PropertyIdentifier.presentValue);
    }
    else if (ObjectType.device.equals(type)) {
      refs.add(oid, PropertyIdentifier.modelName);
    }
    else if (ObjectType.lifeSafetyPoint.equals(type)) {
      refs.add(oid, PropertyIdentifier.units);
      refs.add(oid, PropertyIdentifier.presentValue);
    }
    else if (ObjectType.loop.equals(type)) {
      refs.add(oid, PropertyIdentifier.outputUnits);
      refs.add(oid, PropertyIdentifier.presentValue);
    }
    else if ((ObjectType.multiStateInput.equals(type)) || (ObjectType.multiStateOutput.equals(type)) || (ObjectType.multiStateValue.equals(type)))
    {
      refs.add(oid, PropertyIdentifier.stateText);
      refs.add(oid, PropertyIdentifier.presentValue);
    }
  }
}