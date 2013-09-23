package com.serotonin.m2m2.jmxds;

import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.dataImage.SetPointSource;
import com.serotonin.m2m2.rt.dataSource.PollingDataSource;
import com.serotonin.m2m2.vo.DataPointVO;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import javax.management.Attribute;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JmxDataSourceRT extends PollingDataSource
{
  public static final int DATA_SOURCE_EXCEPTION_EVENT = 1;
  public static final int POINT_READ_EXCEPTION_EVENT = 2;
  public static final int POINT_WRITE_EXCEPTION_EVENT = 3;
  private final Log log = LogFactory.getLog(JmxDataSourceRT.class);
  private final JmxDataSourceVO vo;
  private JMXConnector connector;
  private MBeanServerConnection server;

  public JmxDataSourceRT(JmxDataSourceVO vo)
  {
    super(vo);
    this.vo = vo;
    setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), vo.isQuantize());
  }

  public void terminate()
  {
    super.terminate();
    closeServerConnection();
  }

  protected void doPoll(long time)
  {
    openServerConnection();
    if (this.server == null) {
      return;
    }
    for (DataPointRT dprt : this.dataPoints) {
      if (!updateDataPoint(dprt)) { continue; }

      JmxPointLocatorRT loc = (JmxPointLocatorRT)dprt.getPointLocator();
      Object attr;
      try {
        attr = this.server.getAttribute(loc.getObjectName(), loc.getPointLocatorVO().getAttributeName());
      }
      catch (Exception e) {
        raiseEvent(1, System.currentTimeMillis(), true, new TranslatableMessage("common.default", new Object[] { e.getMessage() }));

        return;
      }
      Object value;
      if (loc.isComposite()) {
        if (!(attr instanceof CompositeData))
        {
          this.log.warn("CompositeData attribute was expected. Received " + attr);
          continue;
        }

        CompositeData cd = (CompositeData)attr;
        value = cd.get(loc.getPointLocatorVO().getCompositeItemName());
      }
      else {
        value = attr;
      }
      PointValueTime pvt = new PointValueTime(loc.managementValueToMangoValue(value), time);
      dprt.updatePointValue(pvt, true);
    }
  }

  public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source)
  {
    if (this.server == null) {
      raiseEvent(3, System.currentTimeMillis(), true, new TranslatableMessage("dsEdit.jmx.writeFailed", new Object[] { dataPoint.getVO().getName() }));

      return;
    }

    updateDataPoint(dataPoint);
    JmxPointLocatorRT loc = (JmxPointLocatorRT)dataPoint.getPointLocator();
    if (loc.getObjectName() == null) {
      raiseEvent(3, System.currentTimeMillis(), true, new TranslatableMessage("dsEdit.jmx.writeFailed", new Object[] { dataPoint.getVO().getName() }));

      return;
    }

    if (loc.isComposite()) {
      raiseEvent(3, System.currentTimeMillis(), true, new TranslatableMessage("dsEdit.jmx.writeFailed.composite", new Object[] { dataPoint.getVO().getName() }));

      return;
    }

    Attribute attr = new Attribute(loc.getPointLocatorVO().getAttributeName(), loc.mangoValueToManagementValue(valueTime.getValue()));
    try
    {
      this.server.setAttribute(loc.getObjectName(), attr);
    }
    catch (Exception e) {
      raiseEvent(3, System.currentTimeMillis(), true, new TranslatableMessage("dsEdit.jmx.writeFailed.msg", new Object[] { dataPoint.getVO().getName(), e.getMessage() }));
    }
  }

  private void openServerConnection()
  {
    if (this.server == null) {
      if (this.vo.isUseLocalServer()) {
        this.server = ManagementFactory.getPlatformMBeanServer();
      } else {
        String url = "service:jmx:rmi:///jndi/rmi://" + this.vo.getRemoteServerAddr() + "/jmxrmi";
        try {
          this.connector = JMXConnectorFactory.connect(new JMXServiceURL(url), null);
          this.server = this.connector.getMBeanServerConnection();
        }
        catch (MalformedURLException e) {
          raiseEvent(1, System.currentTimeMillis(), true, new TranslatableMessage("common.default", new Object[] { e.getMessage() }));

          return;
        }
        catch (IOException e) {
          raiseEvent(1, System.currentTimeMillis(), true, new TranslatableMessage("common.default", new Object[] { e.getMessage() }));

          return;
        }
      }

      returnToNormal(1, System.currentTimeMillis());
    }
  }

  private void closeServerConnection() {
    if (this.connector != null) {
      try {
        this.connector.close();
      }
      catch (IOException e)
      {
      }
      this.connector = null;
    }

    if (this.server != null)
      this.server = null;
  }

  private boolean updateDataPoint(DataPointRT dp) {
    JmxPointLocatorRT loc = (JmxPointLocatorRT)dp.getPointLocator();

    boolean updated = false;

    if (loc.getObjectName() == null) {
      try {
        loc.setObjectName(new ObjectName(loc.getPointLocatorVO().getObjectName()));
      }
      catch (MalformedObjectNameException e) {
        raiseEvent(2, System.currentTimeMillis(), true, new TranslatableMessage("dsEdit.jmx.objectNameError", new Object[] { loc.getPointLocatorVO().getObjectName(), dp.getVO().getName(), e.getMessage() }));

        return false;
      }
      catch (NullPointerException e) {
        raiseEvent(2, System.currentTimeMillis(), true, new TranslatableMessage("dsEdit.jmx.objectNameNotFound", new Object[] { loc.getPointLocatorVO().getObjectName(), dp.getVO().getName() }));

        return false;
      }
      updated = true;
    }

    if ((loc.getType() == null) && (this.server != null)) {
      MBeanInfo info;
      try { info = this.server.getMBeanInfo(loc.getObjectName());
      } catch (Exception e)
      {
        raiseEvent(1, System.currentTimeMillis(), true, new TranslatableMessage("common.default", new Object[] { e.getMessage() }));

        return false;
      }

      MBeanAttributeInfo attr = getAttributeInfo(info, loc.getPointLocatorVO().getAttributeName());
      if (attr == null) {
        raiseEvent(2, System.currentTimeMillis(), true, new TranslatableMessage("dsEdit.jmx.attributeNameNotFound", new Object[] { loc.getPointLocatorVO().getAttributeName(), dp.getVO().getName() }));

        return false;
      }

      String type = null;
      if (!loc.isComposite()) {
        type = attr.getType();

        if (!JmxPointLocatorRT.isValidType(type)) {
          raiseEvent(2, System.currentTimeMillis(), true, new TranslatableMessage("dsEdit.jmx.attributeTypeNotSupported", new Object[] { type, dp.getVO().getName() }));

          return false;
        }
      }
      else
      {
        if (!attr.getType().equals("javax.management.openmbean.CompositeData")) {
          raiseEvent(2, System.currentTimeMillis(), true, new TranslatableMessage("dsEdit.jmx.attributeNotComposite", new Object[] { loc.getPointLocatorVO().getAttributeName(), dp.getVO().getName() }));

          return false;
        }
        CompositeData cd;
        try {
          cd = (CompositeData)this.server.getAttribute(loc.getObjectName(), attr.getName());
        }
        catch (Exception e) {
          raiseEvent(1, System.currentTimeMillis(), true, new TranslatableMessage("common.default", new Object[] { e.getMessage() }));

          return false;
        }

        OpenType openType = cd.getCompositeType().getType(loc.getPointLocatorVO().getCompositeItemName());
        if (openType == null) {
          raiseEvent(2, System.currentTimeMillis(), true, new TranslatableMessage("dsEdit.jmx.compositeNameNotFound", new Object[] { loc.getPointLocatorVO().getCompositeItemName(), dp.getVO().getName() }));

          return false;
        }

        type = openType.getTypeName();

        if (!JmxPointLocatorRT.isValidType(type)) {
          raiseEvent(2, System.currentTimeMillis(), true, new TranslatableMessage("dsEdit.jmx.compositeTypeNotSupported", new Object[] { type, dp.getVO().getName() }));

          return false;
        }
      }

      loc.setType(type);
      updated = true;
    }

    if (updated) {
      returnToNormal(1, System.currentTimeMillis());
      returnToNormal(2, System.currentTimeMillis());
    }

    return true;
  }

  private MBeanAttributeInfo getAttributeInfo(MBeanInfo info, String attributeName) {
    for (MBeanAttributeInfo attr : info.getAttributes()) {
      if (attr.getName().equals(attributeName))
        return attr;
    }
    return null;
  }
}