package com.serotonin.m2m2.onewire.rt;

import com.dalsemi.onewire.OneWireException;
import com.dalsemi.onewire.adapter.OneWireIOException;
import com.dalsemi.onewire.container.ADContainer;
import com.dalsemi.onewire.container.HumidityContainer;
import com.dalsemi.onewire.container.OneWireContainer;
import com.dalsemi.onewire.container.OneWireContainer1D;
import com.dalsemi.onewire.container.OneWireSensor;
import com.dalsemi.onewire.container.PotentiometerContainer;
import com.dalsemi.onewire.container.SwitchContainer;
import com.dalsemi.onewire.container.TemperatureContainer;
import com.dalsemi.onewire.utils.Address;
import com.serotonin.ShouldNeverHappenException;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.DataTypes;
import com.serotonin.m2m2.i18n.TranslatableException;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.onewire.common.Network;
import com.serotonin.m2m2.onewire.common.NetworkPath;
import com.serotonin.m2m2.onewire.vo.OneWireDataSourceVO;
import com.serotonin.m2m2.onewire.vo.OneWirePointLocatorVO;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.dataImage.SetPointSource;
import com.serotonin.m2m2.rt.dataImage.types.BinaryValue;
import com.serotonin.m2m2.rt.dataImage.types.DataValue;
import com.serotonin.m2m2.rt.dataImage.types.MultistateValue;
import com.serotonin.m2m2.rt.dataImage.types.NumericValue;
import com.serotonin.m2m2.rt.dataSource.PollingDataSource;
import com.serotonin.m2m2.vo.DataPointVO;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OneWireDataSourceRT extends PollingDataSource
{
  private static final Log LOG = LogFactory.getLog(OneWireDataSourceRT.class);
  public static final int DATA_SOURCE_EXCEPTION_EVENT = 1;
  public static final int POINT_READ_EXCEPTION_EVENT = 2;
  public static final int POINT_WRITE_EXCEPTION_EVENT = 3;
  private final OneWireDataSourceVO vo;
  private Network network;
  private long nextRescan = 0L;

  public OneWireDataSourceRT(OneWireDataSourceVO vo) {
    super(vo);
    this.vo = vo;
    setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), false);
  }

  public Network getNetwork() {
    return this.network;
  }

  protected void doPoll(long time)
  {
    if (this.vo.getRescanPeriodType() != 0) {
      if (this.nextRescan == 0L) {
        updateNextRescan(time);
      }
      if (time >= this.nextRescan) {
        terminateNetwork();
        try {
          Thread.sleep(2000L);
        }
        catch (InterruptedException e)
        {
        }
        initializeNetwork();
        updateNextRescan(time);
      }
    }

    if (this.network == null) {
      initializeNetwork();
      if (this.network == null) {
        return;
      }
    }

    List points = new ArrayList(this.dataPoints);

    TranslatableMessage exceptionMessage = null;
    try {
      exceptionMessage = tryRead(points, time, true);
    }
    catch (NetworkReloadException e) {
      LOG.info("", e);

      exceptionMessage = e.getTranslatableMessage();
      try
      {
        terminateNetwork();
        try {
          Thread.sleep(2000L);
        }
        catch (InterruptedException e2)
        {
        }
        initializeNetwork();
        try
        {
          this.network.lock();
          this.network.quickInitialize();
        }
        finally {
          this.network.unlock();
        }
        try
        {
          TranslatableMessage msg = tryRead(points, time, false);
          if (exceptionMessage == null)
            exceptionMessage = msg;
        }
        catch (NetworkReloadException e1)
        {
        }
      }
      catch (Exception e2) {
        LOG.info("", e2);
        raiseEvent(1, System.currentTimeMillis(), true, getSerialExceptionMessage(e2, this.vo.getCommPortId()));

        terminateNetwork();
        return;
      }
    }

    if ((exceptionMessage == null) && (points.size() > 0))
    {
      exceptionMessage = new TranslatableMessage("event.1wire.noPointData", new Object[] { ((DataPointRT)points.get(0)).getVO().getName() });
    }

    if (exceptionMessage != null)
      raiseEvent(2, time, true, exceptionMessage);
    else
      returnToNormal(2, time);
  }

  private TranslatableMessage tryRead(List<DataPointRT> points, long time, boolean throwToReload) throws OneWireDataSourceRT.NetworkReloadException
  {
    TranslatableMessage exceptionMessage = null;
    try
    {
      this.network.lock();

      List addresses = this.network.getAddresses();

      NetworkPath lastPath = null;
      for (Long address : addresses)
      {
        if (!arePointsThatNeedAddress(address, points))
        {
          continue;
        }
        NetworkPath path = this.network.getNetworkPath(address);
        OneWireContainer owc = path.getTarget();
        try
        {
          path.open(lastPath);
          readSensor(owc, points, address, time);
          lastPath = path;
        }
        catch (Exception e)
        {
          try {
            path.close();
          }
          catch (OneWireException e1)
          {
          }

          lastPath = null;

          if (throwToReload) {
            throw new NetworkReloadException(exceptionMessage);
          }

          Iterator iter = points.iterator();
          while (iter.hasNext()) {
            DataPointRT point = (DataPointRT)iter.next();
            OneWirePointLocatorRT locator = (OneWirePointLocatorRT)point.getPointLocator();
            if (locator.getAddress().equals(address)) {
              iter.remove();
            }
          }
          if (exceptionMessage == null) {
            exceptionMessage = new TranslatableMessage("event.1wire.deviceRead", new Object[] { Address.toString(address.longValue()), e.getMessage() });
          }
        }
      }

      if (lastPath != null)
        lastPath.close();
    }
    catch (OneWireException e) {
      if (exceptionMessage == null)
        exceptionMessage = new TranslatableMessage("event.1wire.networkRead", new Object[] { e.getMessage() });
    }
    finally {
      this.network.unlock();
    }

    return exceptionMessage;
  }

  private boolean arePointsThatNeedAddress(Long address, List<DataPointRT> points) {
    for (DataPointRT point : points) {
      OneWirePointLocatorRT locator = (OneWirePointLocatorRT)point.getPointLocator();
      if (locator.getAddress().equals(address))
        return true;
    }
    return false;
  }

  private void readSensor(OneWireContainer owc, List<DataPointRT> points, Long address, long time) throws OneWireIOException, OneWireException
  {
    byte[] state = null;
    if ((owc instanceof OneWireSensor)) {
      state = ((OneWireSensor)owc).readDevice();
    }

    Iterator iter = points.iterator();
    while (iter.hasNext()) {
      DataPointRT point = (DataPointRT)iter.next();
      OneWirePointLocatorRT locator = (OneWirePointLocatorRT)point.getPointLocator();

      if (locator.getAddress().equals(address))
      {
        int attributeId = locator.getVo().getAttributeId();
        int index = locator.getVo().getIndex();
        DataValue result = null;

        if (attributeId == 1) {
          TemperatureContainer tc = (TemperatureContainer)owc;
          tc.doTemperatureConvert(state);
          result = new NumericValue(tc.getTemperature(state));
        }
        else if (attributeId == 2) {
          HumidityContainer hc = (HumidityContainer)owc;
          hc.doHumidityConvert(state);
          result = new NumericValue(hc.getHumidity(state));
        }
        else if (attributeId == 3) {
          ADContainer ac = (ADContainer)owc;
          ac.doADConvert(index, state);
          result = new NumericValue(ac.getADVoltage(index, state));
        }
        else if (attributeId == 4) {
          SwitchContainer sc = (SwitchContainer)owc;
          result = new BinaryValue(sc.getLatchState(index, state));
        }
        else if (attributeId == 5) {
          PotentiometerContainer pc = (PotentiometerContainer)owc;
          pc.setCurrentWiperNumber(index, state);
          result = new MultistateValue(pc.getWiperPosition());
        }
        else if (attributeId == 6) {
          OneWireContainer1D c1d = (OneWireContainer1D)owc;
          result = new NumericValue(c1d.readCounter(index));
        }

        if (result != null)
        {
          if (DataTypes.getDataType(result) != locator.getVo().getDataTypeId())
          {
            throw new ShouldNeverHappenException("Got " + DataTypes.getDataType(result) + ", expected " + locator.getVo().getDataTypeId());
          }

          point.updatePointValue(new PointValueTime(result, time));

          iter.remove();
        }
      }
    }
  }

  public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source)
  {
    TranslatableMessage exceptionMessage = null;

    Network localNetwork = this.network;
    if (localNetwork == null) {
      return;
    }

    synchronized (this.pointListChangeLock) {
      OneWirePointLocatorRT locator = (OneWirePointLocatorRT)dataPoint.getPointLocator();

      NetworkPath path = null;
      try {
        localNetwork.lock();

        path = localNetwork.getNetworkPath(locator.getAddress());
        if (path == null) {
          exceptionMessage = new TranslatableMessage("event.1wire.noDevice", new Object[] { Address.toString(locator.getAddress().longValue()), dataPoint.getVO().getName() });
        }
        else {
          path.open();

          int attributeId = locator.getVo().getAttributeId();
          int index = locator.getVo().getIndex();

          if (attributeId == 4) {
            SwitchContainer sc = (SwitchContainer)path.getTarget();
            byte[] state = sc.readDevice();
            boolean value = valueTime.getBooleanValue();
            sc.setLatchState(index, value, sc.hasSmartOn(), state);
            sc.writeDevice(state);
          }
          else if (attributeId == 5) {
            PotentiometerContainer pc = (PotentiometerContainer)path.getTarget();
            byte[] state = pc.readDevice();
            int value = valueTime.getIntegerValue();
            pc.setCurrentWiperNumber(index, state);
            boolean success = pc.setWiperPosition(value);
            if (success)
              pc.writeDevice(state);
            else
              exceptionMessage = new TranslatableMessage("event.1wire.setWiper", new Object[] { Address.toString(locator.getAddress().longValue()), dataPoint.getVO().getName() });
          }
        }
      }
      catch (Exception e)
      {
        exceptionMessage = getSerialExceptionMessage(e, this.vo.getCommPortId());
      }
      finally {
        try {
          if (path != null) {
            path.close();
          }
        }
        catch (Exception e)
        {
        }
        localNetwork.unlock();
      }

      if (exceptionMessage != null)
        raiseEvent(3, System.currentTimeMillis(), false, exceptionMessage);
      else
        dataPoint.setPointValue(valueTime, source);
    }
  }

  public void initialize()
  {
    initializeNetwork();
    super.initialize();
  }

  public void terminate()
  {
    super.terminate();
    terminateNetwork();
  }

  private void initializeNetwork() {
    try {
      this.network = new Network(this.vo.getCommPortId());
      try {
        this.network.lock();
        this.network.quickInitialize();
      }
      finally {
        this.network.unlock();
      }

      returnToNormal(1, System.currentTimeMillis());
    }
    catch (Exception e) {
      LOG.info("", e);
      raiseEvent(1, System.currentTimeMillis(), true, getSerialExceptionMessage(e, this.vo.getCommPortId()));

      terminateNetwork();
      return;
    }
  }

  private void terminateNetwork() {
    if (this.network != null) {
      try {
        this.network.terminate();
      }
      catch (OneWireException e1)
      {
      }
      this.network = null;
    }
  }

  private void updateNextRescan(long time)
  {
    this.nextRescan = (time + Common.getMillis(this.vo.getRescanPeriodType(), this.vo.getRescanPeriods()));
  }

  class NetworkReloadException extends TranslatableException
  {
    private static final long serialVersionUID = -1L;

    public NetworkReloadException(TranslatableMessage translatableMessage)
    {
      super();
    }
  }
}