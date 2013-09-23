package com.serotonin.ma.bacnet;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.RemoteObject;
import com.serotonin.bacnet4j.enums.MaxApduLength;
import com.serotonin.bacnet4j.event.DeviceEventHandler;
import com.serotonin.bacnet4j.event.DeviceEventListener;
import com.serotonin.bacnet4j.event.ExceptionDispatch;
import com.serotonin.bacnet4j.event.ExceptionListener;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.exception.PropertyValueException;
import com.serotonin.bacnet4j.npdu.Network;
import com.serotonin.bacnet4j.obj.BACnetObject;
import com.serotonin.bacnet4j.obj.ObjectProperties;
import com.serotonin.bacnet4j.obj.PropertyTypeDefinition;
import com.serotonin.bacnet4j.service.acknowledgement.ReadPropertyAck;
import com.serotonin.bacnet4j.service.confirmed.ReadPropertyRequest;
import com.serotonin.bacnet4j.service.confirmed.ReinitializeDeviceRequest;
import com.serotonin.bacnet4j.service.confirmed.ReinitializeDeviceRequest.ReinitializedStateOfDevice;
import com.serotonin.bacnet4j.service.confirmed.SubscribeCOVRequest;
import com.serotonin.bacnet4j.service.confirmed.WritePropertyRequest;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.transport.Transport;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.constructed.BACnetError;
import com.serotonin.bacnet4j.type.constructed.Choice;
import com.serotonin.bacnet4j.type.constructed.DateTime;
import com.serotonin.bacnet4j.type.constructed.PropertyValue;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.constructed.TimeStamp;
import com.serotonin.bacnet4j.type.enumerated.BinaryPV;
import com.serotonin.bacnet4j.type.enumerated.EventState;
import com.serotonin.bacnet4j.type.enumerated.EventType;
import com.serotonin.bacnet4j.type.enumerated.LifeSafetyState;
import com.serotonin.bacnet4j.type.enumerated.MessagePriority;
import com.serotonin.bacnet4j.type.enumerated.NotifyType;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.enumerated.Segmentation;
import com.serotonin.bacnet4j.type.notificationParameters.NotificationParameters;
import com.serotonin.bacnet4j.type.primitive.CharacterString;
import com.serotonin.bacnet4j.type.primitive.Enumerated;
import com.serotonin.bacnet4j.type.primitive.Null;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.OctetString;
import com.serotonin.bacnet4j.type.primitive.Real;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.bacnet4j.util.PropertyReferences;
import com.serotonin.bacnet4j.util.PropertyValues;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.dataImage.SetPointSource;
import com.serotonin.m2m2.rt.dataImage.types.AlphanumericValue;
import com.serotonin.m2m2.rt.dataImage.types.BinaryValue;
import com.serotonin.m2m2.rt.dataImage.types.DataValue;
import com.serotonin.m2m2.rt.dataImage.types.MultistateValue;
import com.serotonin.m2m2.rt.dataImage.types.NumericValue;
import com.serotonin.m2m2.rt.dataSource.PollingDataSource;
import com.serotonin.m2m2.rt.maint.BackgroundProcessing;
import com.serotonin.m2m2.rt.maint.work.WorkItem;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.m2m2.web.taglib.Functions;
import com.serotonin.timer.FixedRateTrigger;
import com.serotonin.timer.RealTimeTimer;
import com.serotonin.timer.TimerTask;
import com.serotonin.timer.TimerTrigger;
import com.serotonin.util.queue.ByteQueue;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class BACnetDataSourceRT extends PollingDataSource
  implements DeviceEventListener, ExceptionListener
{
  public static final int INITIALIZATION_EXCEPTION_EVENT = 1;
  public static final int MESSAGE_EXCEPTION_EVENT = 2;
  public static final int DEVICE_EXCEPTION_EVENT = 3;
  final Log log = LogFactory.getLog(BACnetDataSourceRT.class);
  final BACnetDataSourceVO<?> vo;
  private LocalDevice localDevice;
  private boolean initialized = false;
  final List<RemoteDevice> pollsInProgress = new ArrayList();
  private CovResubscriptionTask covResubscriptionTask;

  public BACnetDataSourceRT(BACnetDataSourceVO<?> vo)
  {
    super(vo);
    this.vo = vo;
    setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), false);
  }

  public void initialize()
  {
    ExceptionDispatch.addListener(this);
    try
    {
      Network network = createNetwork();

      Transport transport = new Transport(network);
      transport.setTimeout(this.vo.getTimeout());
      transport.setSegTimeout(this.vo.getSegTimeout());
      transport.setSegWindow(this.vo.getSegWindow());
      transport.setRetries(this.vo.getRetries());

      this.localDevice = new LocalDevice(this.vo.getDeviceId(), transport);
      this.localDevice.getEventHandler().addListener(this);
      this.localDevice.setMaxReadMultipleReferencesSegmented(this.vo.getMaxReadMultipleReferencesSegmented());
      this.localDevice.setMaxReadMultipleReferencesNonsegmented(this.vo.getMaxReadMultipleReferencesNonsegmented());
      this.localDevice.setStrict(this.vo.isStrict());

      this.localDevice.setExecutorService(Common.timer.getExecutorService());

      this.localDevice.initialize();

      returnToNormal(1, System.currentTimeMillis());
    } catch (Exception e) {
      raiseEvent(1, System.currentTimeMillis(), true, new TranslatableMessage("event.initializationError", new Object[] { e.getMessage() }));

      return;
    }

    super.initialize();
    try
    {
      this.localDevice.sendGlobalBroadcast(this.localDevice.getIAm());
    } catch (BACnetException e) {
      fireMessageExceptionEvent("event.bacnet.iamError", new Object[] { e.getMessage() });
    }

    try
    {
      this.localDevice.sendGlobalBroadcast(new WhoIsRequest());
    } catch (BACnetException e) {
      fireMessageExceptionEvent("event.bacnet.whoisError", new Object[] { e.getMessage() });
    }

    this.initialized = true;
  }

  protected abstract Network createNetwork() throws Exception;

  public void beginPolling() {
    if (!this.initialized) {
      return;
    }
    super.beginPolling();

    this.covResubscriptionTask = new CovResubscriptionTask(new FixedRateTrigger(0L, this.vo.getCovSubscriptionTimeoutMinutes() * 60 * 1000 / 2));

    Common.timer.schedule(this.covResubscriptionTask);
  }

  public void terminate()
  {
    super.terminate();

    if (this.covResubscriptionTask != null) {
      this.covResubscriptionTask.cancel();
      this.covResubscriptionTask.waitForFinish();
    }

    if (this.localDevice != null) {
      synchronized (this.pollsInProgress) {
        while (!this.pollsInProgress.isEmpty())
          try {
            this.pollsInProgress.wait(200L);
          }
          catch (InterruptedException e)
          {
          }
        this.localDevice.terminate();
      }
    }

    ExceptionDispatch.removeListener(this);
  }

  private void initializeDataPoint(DataPointRT dataPoint) {
    if (isTerminated()) {
      return;
    }
    synchronized (dataPoint) {
      BACnetPointLocatorRT locator = (BACnetPointLocatorRT)dataPoint.getPointLocator();
      if (locator.isInitialized())
      {
        return;
      }
      Address address = locator.getAddress(getDefaultPort());
      OctetString linkService = locator.getLinkService(getDefaultPort());

      RemoteDevice d = getRemoteDevice(locator.getRemoteDeviceInstanceNumber(), address, linkService);

      if (d == null) {
        try
        {
          d = this.localDevice.findRemoteDevice(address, linkService, locator.getRemoteDeviceInstanceNumber());
        }
        catch (BACnetException e) {
        }
        catch (PropertyValueException e) {
          this.log.error("Couldn't manually get segmentation and vendor id from device", e);
        }
      }

      if (d == null)
      {
        fireDeviceExceptionEvent("event.bacnet.deviceError", new Object[] { address.getDescription() });
      }
      else {
        locator.setRemoteDevice(d);

        if (locator.isUseCovSubscription())
          sendCovSubscription(dataPoint, false); 
      }
    }
  }

  protected abstract int getDefaultPort();

  private RemoteDevice getRemoteDevice(int instanceNumber, Address address, OctetString linkService) {
    try {
      return this.localDevice.getRemoteDevice(instanceNumber, address, linkService); } catch (BACnetException e) {
    }
    return null;
  }

  public void removeDataPoint(DataPointRT dataPoint)
  {
    super.removeDataPoint(dataPoint);
  }

  protected void doPoll(long time)
  {
    throw new ShouldNeverHappenException("should not be called");
  }

  protected void doPollNoSync(long time)
  {
    Map devicePoints = new HashMap();

    List<DataPointRT> allPoints = new ArrayList();
    synchronized (this.pointListChangeLock) {
      allPoints.addAll(this.dataPoints);
    }

    for (DataPointRT dp : allPoints) {
      BACnetPointLocatorRT locator = (BACnetPointLocatorRT)dp.getPointLocator();
      initializeDataPoint(dp);
      if (!locator.isInitialized())
      {
        continue;
      }

      if (isTerminated()) {
        return;
      }
      if ((locator.isUseCovSubscription()) && (dp.getPointValue() != null)) {
        continue;
      }
      List points = (List)devicePoints.get(locator.getRemoteDevice());
      if (points == null) {
        points = new ArrayList();
        devicePoints.put(locator.getRemoteDevice(), points);
      }

      points.add(dp);
    }

    for (RemoteDevice d : (Set<RemoteDevice>)devicePoints.keySet())
      //Common.backgroundProcessing.addWorkItem(new DevicePoller(d, (List)devicePoints.get(d), time));
      Common.backgroundProcessing.addWorkItem(new DevicePoller((List)devicePoints.get(d), time));
  }

  void pollDevice(RemoteDevice d, List<DataPointRT> points, long time)
  {
    PropertyReferences refs = new PropertyReferences();
    for (DataPointRT dp : points) {
      BACnetPointLocatorRT locator = (BACnetPointLocatorRT)dp.getPointLocator();
      refs.add(locator.getOid(), locator.getPid());
    }

    try
    {
      PropertyValues values = this.localDevice.readProperties(d, refs);

      for (DataPointRT dp : points) {
        BACnetPointLocatorRT locator = (BACnetPointLocatorRT)dp.getPointLocator();
        Encodable encodable = values.getNoErrorCheck(locator.getOid(), locator.getPid());
        dereferencePoint(dp, encodable, time);
      }
    }
    catch (BACnetException e)
    {
      PropertyValues values;
      fireMessageExceptionEvent("event.bacnet.readDevice", new Object[] { d.getAddress().getDescription(), e.getMessage() });
    }
  }

  public void forcePointRead(DataPointRT dataPoint)
  {
    BACnetPointLocatorRT locator = (BACnetPointLocatorRT)dataPoint.getPointLocator();
    RemoteDevice d = locator.getRemoteDevice();
    try
    {
      ReadPropertyRequest req = new ReadPropertyRequest(locator.getOid(), locator.getPid());
      ReadPropertyAck ack = (ReadPropertyAck)this.localDevice.send(d, req);
      dereferencePoint(dataPoint, ack.getValue(), System.currentTimeMillis());
    } catch (BACnetException e) {
      fireMessageExceptionEvent("event.bacnet.readDevice", new Object[] { d.getAddress().getDescription(), e.getMessage() });
    }
  }

  private void dereferencePoint(DataPointRT dp, Encodable encodable, long time) {
    if (encodable == null) {
      fireDeviceExceptionEvent("event.bacnet.readError", new Object[] { dp.getVO().getName(), "no value returned" });
    } else if ((encodable instanceof BACnetError)) {
      fireDeviceExceptionEvent("event.bacnet.readError", new Object[] { dp.getVO().getName(), ((BACnetError)encodable).getErrorCode() });
    }
    else {
      DataValue value = encodableToValue(encodable, dp.getDataTypeId(), dp.getVO().getName());
      dp.updatePointValue(new PointValueTime(value, time));
    }
  }

  public void setPointValue(DataPointRT dataPoint, PointValueTime pvt, SetPointSource source)
  {
    if (!this.initialized)
      return;
    try
    {
      BACnetPointLocatorRT locator = (BACnetPointLocatorRT)dataPoint.getPointLocator();
      if (!locator.isInitialized()) {
        return;
      }
      WritePropertyRequest writeRequest = new WritePropertyRequest(locator.getOid(), locator.getPid(), null, valueToEncodable(pvt.getValue(), locator.getOid().getObjectType(), locator.getPid()), new UnsignedInteger(locator.getWritePriority()));

      this.localDevice.send(locator.getRemoteDevice(), writeRequest);
      dataPoint.setPointValue(pvt, source);
    } catch (Throwable t) {
      fireMessageExceptionEvent("event.setPointFailed", new Object[] { t.getMessage() });
    }
  }

  public void relinquish(DataPointRT dataPoint)
  {
    if (!this.initialized)
      return;
    try
    {
      BACnetPointLocatorRT locator = (BACnetPointLocatorRT)dataPoint.getPointLocator();
      if (!locator.isInitialized()) {
        return;
      }
      WritePropertyRequest writeRequest = new WritePropertyRequest(locator.getOid(), locator.getPid(), null, new Null(), new UnsignedInteger(locator.getWritePriority()));

      this.localDevice.send(locator.getRemoteDevice(), writeRequest);
      forcePointRead(dataPoint);
    } catch (Throwable t) {
      fireMessageExceptionEvent("event.relinquishFailed", new Object[] { t.getMessage() });
    }
  }

  java.lang.Boolean getPointListChangeLock() {
    return this.pointListChangeLock;
  }

  List<DataPointRT> getDataPoints() {
    return this.dataPoints;
  }

  public void listenerException(Throwable e)
  {
    fireMessageExceptionEvent(e);
  }

  public boolean allowPropertyWrite(BACnetObject obj, PropertyValue pv)
  {
    return true;
  }

  public void iAmReceived(RemoteDevice d)
  {
    try {
      this.localDevice.getExtendedDeviceInformation(d);
    }
    catch (BACnetException e)
    {
      throw new ShouldNeverHappenException(e);
    }
  }

  public void propertyWritten(BACnetObject obj, PropertyValue pv)
  {
  }

  public void iHaveReceived(RemoteDevice d, RemoteObject o)
  {
  }

  public void covNotificationReceived(UnsignedInteger subscriberProcessIdentifier, RemoteDevice initiatingDevice, ObjectIdentifier monitoredObjectIdentifier, UnsignedInteger timeRemaining, SequenceOf<PropertyValue> listOfValues)
  {
    int covId = subscriberProcessIdentifier.intValue();

    List<PropertyValue> values = listOfValues.getValues();
    for (PropertyValue pv : values)
    {
      DataPointRT dataPoint = null;
      BACnetPointLocatorRT locator = null;
      synchronized (this.pointListChangeLock) {
        for (DataPointRT dp : this.dataPoints) {
          locator = (BACnetPointLocatorRT)dp.getPointLocator();
          if (locator.getCovId() == covId) {
            dataPoint = dp;
            break;
          }
        }
      }

      if (dataPoint != null) {
        if (pv.getPropertyIdentifier().equals(locator.getPid())) {
          DataValue value = encodableToValue(pv.getValue(), dataPoint.getDataTypeId(), dataPoint.getVO().getName());

          dataPoint.updatePointValue(new PointValueTime(value, System.currentTimeMillis()));
        }
      }
      else
        try {
          if (initiatingDevice.getSegmentationSupported() == null)
            initiatingDevice.setSegmentationSupported(Segmentation.noSegmentation);
          if (initiatingDevice.getMaxAPDULengthAccepted() == 0) {
            initiatingDevice.setMaxAPDULengthAccepted(MaxApduLength.UP_TO_50.getMaxLength());
          }
          sendCovSubscriptionImpl(initiatingDevice, monitoredObjectIdentifier, covId, true);
        }
        catch (BACnetException e)
        {
        }
    }
  }

  public void eventNotificationReceived(UnsignedInteger processIdentifier, RemoteDevice initiatingDevice, ObjectIdentifier eventObjectIdentifier, TimeStamp timeStamp, UnsignedInteger notificationClass, UnsignedInteger priority, EventType eventType, CharacterString messageText, NotifyType notifyType, com.serotonin.bacnet4j.type.primitive.Boolean ackRequired, EventState fromState, EventState toState, NotificationParameters eventValues)
  {
  }

  public void textMessageReceived(RemoteDevice textMessageSourceDevice, Choice messageClass, MessagePriority messagePriority, CharacterString message)
  {
  }

  public void privateTransferReceived(UnsignedInteger vendorId, UnsignedInteger serviceNumber, Encodable serviceParameters)
  {
  }

  public void reinitializeDevice(ReinitializeDeviceRequest.ReinitializedStateOfDevice reinitializedStateOfDevice)
  {
  }

  public void synchronizeTime(DateTime arg0, boolean arg1)
  {
  }

  public void receivedException(Exception e)
  {
    fireMessageExceptionEvent(e);
  }

  public void receivedThrowable(Throwable t)
  {
    fireMessageExceptionEvent(t);
  }

  public void unimplementedVendorService(UnsignedInteger vendorId, UnsignedInteger serviceNumber, ByteQueue queue)
  {
    this.log.warn("Received unimplemented vendor service: vendor id=" + vendorId + ", service number=" + serviceNumber + ", bytes (with context id)=" + queue);
  }

  void sendCovSubscription(DataPointRT dataPoint, boolean unsubscribe)
  {
    BACnetPointLocatorRT locator = (BACnetPointLocatorRT)dataPoint.getPointLocator();
    try
    {
      sendCovSubscriptionImpl(locator.getRemoteDevice(), locator.getOid(), locator.getCovId(), unsubscribe);
    }
    catch (BACnetException e)
    {
      if (!unsubscribe) {
        fireMessageExceptionEvent("event.bacnet.covFailed", new Object[] { locator.getRemoteDevice().getAddress().getDescription(), e.getMessage() });

        locator.cancelCov();
      }
    }
  }

  private void sendCovSubscriptionImpl(RemoteDevice remoteDevice, ObjectIdentifier oid, int covId, boolean unsubscribe)
    throws BACnetException
  {
    UnsignedInteger lifetime;
    com.serotonin.bacnet4j.type.primitive.Boolean confirm;
    if (unsubscribe) {
      confirm = null;
      lifetime = null;
    } else {
      confirm = new com.serotonin.bacnet4j.type.primitive.Boolean(false);
      lifetime = new UnsignedInteger(this.vo.getCovSubscriptionTimeoutMinutes() * 60);
    }

    this.localDevice.send(remoteDevice, new SubscribeCOVRequest(new UnsignedInteger(covId), oid, confirm, lifetime));
  }

  private void fireMessageExceptionEvent(Throwable t) {
    fireMessageExceptionEvent("common.default", new Object[] { t.getMessage() });
    this.log.info("", t);
  }

  private void fireMessageExceptionEvent(String key, Object[] args) {
    raiseEvent(2, System.currentTimeMillis(), false, new TranslatableMessage(key, args));
  }

  private void fireDeviceExceptionEvent(String key, Object[] args) {
    raiseEvent(3, System.currentTimeMillis(), false, new TranslatableMessage(key, args));
  }

  private DataValue encodableToValue(Encodable encodable, int dataTypeId, String pointName) {
    if (dataTypeId == 1) {
      if ((encodable instanceof Enumerated))
        return new BinaryValue(((Enumerated)encodable).intValue() != 0);
      if ((encodable instanceof Real))
        return new BinaryValue(((Real)encodable).floatValue() != 0.0F);
      this.log.warn("Unexpected Encodable type for data type Binary: " + encodable.getClass().getName() + ", pointName=" + pointName);

      return BinaryValue.ZERO;
    }if (dataTypeId == 2) {
      if ((encodable instanceof UnsignedInteger))
        return new MultistateValue(((UnsignedInteger)encodable).intValue());
      if ((encodable instanceof Enumerated))
        return new MultistateValue(((Enumerated)encodable).intValue());
      if ((encodable instanceof Real))
        return new MultistateValue((int)((Real)encodable).floatValue());
      this.log.warn("Unexpected Encodable type for data type Multistate: " + encodable.getClass().getName() + ", pointName=" + pointName);

      return new MultistateValue(1);
    }if (dataTypeId == 3) {
      if ((encodable instanceof Enumerated))
        return new NumericValue(((Enumerated)encodable).intValue());
      if ((encodable instanceof Real))
        return new NumericValue(((Real)encodable).floatValue());
      this.log.warn("Unexpected Encodable type for data type Numeric: " + encodable.getClass().getName() + ", pointName=" + pointName);

      return new NumericValue(0.0D);
    }if (dataTypeId == 4) {
      return new AlphanumericValue(encodable.toString());
    }

    throw new ShouldNeverHappenException("Unknown data type id: " + dataTypeId);
  }

  private Encodable valueToEncodable(DataValue value, ObjectType objectType, PropertyIdentifier pid) {
    Class clazz = ObjectProperties.getPropertyTypeDefinition(objectType, pid).getClazz();

    if ((value instanceof BinaryValue)) {
      boolean b = value.getBooleanValue();
      if (clazz == BinaryPV.class) {
        if (b)
          return BinaryPV.active;
        return BinaryPV.inactive;
      }

      if (clazz == UnsignedInteger.class) {
        return new UnsignedInteger(b ? 1 : 0);
      }
      if (clazz == LifeSafetyState.class) {
        return new LifeSafetyState(b ? 1 : 0);
      }
      if (clazz == Real.class)
        return new Real(b ? 1.0F : 0.0F);
    } else if ((value instanceof MultistateValue)) {
      int i = ((MultistateValue)value).getIntegerValue();
      if (clazz == BinaryPV.class) {
        if (i != 0)
          return BinaryPV.active;
        return BinaryPV.inactive;
      }

      if (clazz == UnsignedInteger.class) {
        return new UnsignedInteger(i);
      }
      if (clazz == LifeSafetyState.class) {
        return new LifeSafetyState(i);
      }
      if (clazz == Real.class)
        return new Real(i);
    } else if ((value instanceof NumericValue)) {
      double d = value.getDoubleValue();
      if (clazz == BinaryPV.class) {
        if (d != 0.0D)
          return BinaryPV.active;
        return BinaryPV.inactive;
      }

      if (clazz == UnsignedInteger.class) {
        return new UnsignedInteger((int)d);
      }
      if (clazz == LifeSafetyState.class) {
        return new LifeSafetyState((int)d);
      }
      if (clazz == Real.class)
        return new Real((float)d);
    } else if ((value instanceof AlphanumericValue)) {
      String s = value.getStringValue();
      if (clazz == BinaryPV.class) {
        if (BinaryValue.parseBinary(s).getBooleanValue())
          return BinaryPV.active;
        return BinaryPV.inactive;
      }

      if (clazz == UnsignedInteger.class) {
        return new UnsignedInteger(MultistateValue.parseMultistate(s).getIntegerValue());
      }
      if (clazz == LifeSafetyState.class) {
        return new LifeSafetyState(MultistateValue.parseMultistate(s).getIntegerValue());
      }
      if (clazz == Real.class) {
        return new Real(NumericValue.parseNumeric(s).getFloatValue());
      }
    }
    throw new ShouldNeverHappenException("Unknown data type: " + value.getClass().getName());
  }

  class CovResubscriptionTask extends TimerTask
  {
    public CovResubscriptionTask(TimerTrigger trigger)
    {
      super(trigger);
    }

    public void run(long fireTime)
    {
      synchronized (this) {
        List<DataPointRT> points = new ArrayList();
        synchronized (BACnetDataSourceRT.this.getPointListChangeLock()) {
          for (DataPointRT dp : BACnetDataSourceRT.this.getDataPoints()) {
            BACnetPointLocatorRT locator = (BACnetPointLocatorRT)dp.getPointLocator();
            if (locator.isUseCovSubscription()) {
              points.add(dp);
            }
          }
        }
        for (DataPointRT dp : points) {
          if (isCancelled()) {
            break;
          }
          BACnetDataSourceRT.this.initializeDataPoint(dp);
          BACnetPointLocatorRT locator = (BACnetPointLocatorRT)dp.getPointLocator();
          if (locator.isInitialized())
            BACnetDataSourceRT.this.sendCovSubscription(dp, false);
        }
      }
    }

    void waitForFinish() {
      synchronized (this)
      {
      }
    }
  }

  class DevicePoller
    implements WorkItem
  {
    private final RemoteDevice d;
    private final List<DataPointRT> points;
    private final long time;

    public DevicePoller(List<DataPointRT> points, long time)
    {
        //TODO
      this.d = null;
      this.points = points;
      this.time = time;
    }

    public void execute()
    {
      synchronized (BACnetDataSourceRT.this.pollsInProgress) {
        if (BACnetDataSourceRT.this.pollsInProgress.contains(this.d))
        {
          BACnetDataSourceRT.this.log.warn(BACnetDataSourceRT.this.vo.getName() + ": poll of " + this.d + " at " + Functions.getFullSecondTime(this.time) + " aborted because a previous poll is still running");

          return;
        }

        BACnetDataSourceRT.this.pollsInProgress.add(this.d);
      }

      BACnetDataSourceRT.this.pollDevice(this.d, this.points, this.time);

      synchronized (BACnetDataSourceRT.this.pollsInProgress) {
        BACnetDataSourceRT.this.pollsInProgress.remove(this.d);
        BACnetDataSourceRT.this.pollsInProgress.notify();
      }
    }

    public int getPriority()
    {
      return 1;
    }
  }
}