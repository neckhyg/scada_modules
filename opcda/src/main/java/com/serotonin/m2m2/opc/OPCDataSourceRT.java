package com.serotonin.m2m2.opc;

import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.dataImage.SetPointSource;
import com.serotonin.m2m2.rt.dataImage.types.DataValue;
import com.serotonin.m2m2.rt.dataSource.PollingDataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinterop.dcom.core.JIVariant;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.da.AutoReconnectController;
import org.openscada.opc.lib.da.AutoReconnectListener;
import org.openscada.opc.lib.da.AutoReconnectState;
import org.openscada.opc.lib.da.Group;
import org.openscada.opc.lib.da.Item;
import org.openscada.opc.lib.da.ItemState;
import org.openscada.opc.lib.da.Server;

public class OPCDataSourceRT extends PollingDataSource
{
  private final Log LOG = LogFactory.getLog(OPCDataSourceRT.class);
  public static final int POINT_READ_EXCEPTION_EVENT = 1;
  public static final int DATA_SOURCE_EXCEPTION_EVENT = 2;
  public static final int POINT_WRITE_EXCEPTION_EVENT = 3;
  private final OPCDataSourceVO vo;
  private Server server;
  private AutoReconnectController autoReconnectController;
  private Group group;
  private Map<String, Item> tags = new HashMap();
  private AutoReconnectState connectionState;
  private AutoReconnectListener connectionListener;

  public OPCDataSourceRT(OPCDataSourceVO vo)
  {
    super(vo);
    this.vo = vo;
    setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), false);
    this.connectionListener = new AutoReconnectListener()
    {
      public void stateChanged(AutoReconnectState state) {
// TODO
//        OPCDataSourceRT.access$002(OPCDataSourceRT.this, state);
      }
    };
  }

  protected void doPoll(long time) {
    boolean hasError = false;

    if (this.connectionState != AutoReconnectState.CONNECTED) {
      raiseEvent(2, System.currentTimeMillis(), true, new TranslatableMessage("event.exception2", new Object[] { getClass().getName(), "Not connected." }));

      return;
    }
    try
    {
      if (this.group == null) {
        this.group = this.server.addGroup(String.valueOf(Math.random() * 1000.0D));
      }
      returnToNormal(2, System.currentTimeMillis());
    } catch (Exception e) {
      raiseEvent(2, System.currentTimeMillis(), true, new TranslatableMessage("event.exception2", new Object[] { e.getClass().getName(), OPCUtils.getExceptionMessage(e, this.server) }));

      this.LOG.error("Error creating Group.", e);
      return;
    }

    for (DataPointRT dataPoint : this.dataPoints) {
      try
      {
        OPCPointLocatorVO dataPointVO = (OPCPointLocatorVO)dataPoint.getVO().getPointLocator();

        if (!this.tags.containsKey(dataPointVO.getTag())) {
          this.tags.put(dataPointVO.getTag(), this.group.addItem(dataPointVO.getTag()));
        }

        ItemState state = ((Item)this.tags.get(dataPointVO.getTag())).read(false);

        short quality = state.getQuality().shortValue();

        if (quality != 192) {
          throw new Exception("Not GOOD Quality Received: " + OPCUtils.qualityToString(quality));
        }

        String value = OPCUtils.getValueOPC(state.getValue());

        DataValue dataValue = DataValue.stringToValue(value, dataPointVO.getDataTypeId());
        dataPoint.updatePointValue(new PointValueTime(dataValue, time));
      } catch (Exception e) {
        hasError = true;
        raiseEvent(1, time, true, new TranslatableMessage("event.exception2", new Object[] { e.getClass().getName(), OPCUtils.getExceptionMessage(e, this.server) }));

        this.LOG.error("Error reading point.", e);
      }
    }

    if (!hasError)
      returnToNormal(1, System.currentTimeMillis());
  }

  public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source)
  {
    String tag = ((OPCPointLocatorVO)dataPoint.getVO().getPointLocator()).getTag();
    Object value = valueTime.getValue().getObjectValue();
    try
    {
      if (!this.tags.containsKey(tag)) {
        this.tags.put(tag, this.group.addItem(tag));
      }

      ((Item)this.tags.get(tag)).write(JIVariant.makeVariant(value));

      returnToNormal(3, System.currentTimeMillis());
    }
    catch (Exception e) {
      raiseEvent(3, System.currentTimeMillis(), true, new TranslatableMessage("event.exception2", new Object[] { e.getClass().getName(), OPCUtils.getExceptionMessage(e, this.server) }));

      this.LOG.error("Error writing point.", e);
    }
  }

  public void initialize()
  {
    try
    {
      ConnectionInformation ci = new ConnectionInformation();
      ci.setHost(this.vo.getHost());
      ci.setDomain(this.vo.getDomain());
      ci.setUser(this.vo.getUser());
      ci.setPassword(this.vo.getPassword());
      ci.setClsid(OPCUtils.getClsId(this.vo.getUser(), this.vo.getPassword(), this.vo.getHost(), this.vo.getDomain(), this.vo.getServer()));

      this.server = new Server(ci, Executors.newSingleThreadScheduledExecutor());

      this.autoReconnectController = new AutoReconnectController(this.server);

      this.autoReconnectController.addListener(this.connectionListener);

      this.autoReconnectController.connect();

      returnToNormal(2, System.currentTimeMillis());
    }
    catch (Exception e) {
      raiseEvent(2, System.currentTimeMillis(), true, new TranslatableMessage("event.exception2", new Object[] { e.getClass().getName(), OPCUtils.getExceptionMessage(e, this.server) }));

      this.LOG.error("Error while initializing data source", e);
    }
  }

  public void terminate()
  {
    try
    {
      this.tags.clear();

      if (this.group != null) {
        this.group.clear();
        this.group = null;
      }

      if (this.autoReconnectController != null) {
        this.autoReconnectController.disconnect();
        this.autoReconnectController.removeListener(this.connectionListener);
      }

      if (this.server != null) {
        this.server.disconnect();
        this.server.dispose();
      }
    }
    catch (Exception e) {
      raiseEvent(2, System.currentTimeMillis(), false, new TranslatableMessage("event.exception2", new Object[] { e.getClass().getName(), OPCUtils.getExceptionMessage(e, this.server) }));
    }

    super.terminate();
  }
}