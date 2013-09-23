package com.serotonin.m2m2.jmxds;

import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.vo.User;
import com.serotonin.m2m2.vo.dataSource.BasicDataSourceVO;
import com.serotonin.m2m2.web.dwr.DataSourceEditDwr;
import com.serotonin.m2m2.web.dwr.util.DwrPermission;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.RuntimeMBeanException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class JmxEditDwr extends DataSourceEditDwr
{
  @DwrPermission(user=true)
  public ProcessResult saveJmxDataSource(BasicDataSourceVO basic, boolean useLocalServer, String remoteServerAddr, int updatePeriodType, int updatePeriods, boolean quantize)
  {
    JmxDataSourceVO ds = (JmxDataSourceVO)Common.getUser().getEditDataSource();

    setBasicProps(ds, basic);
    ds.setUseLocalServer(useLocalServer);
    ds.setRemoteServerAddr(remoteServerAddr);
    ds.setUpdatePeriodType(updatePeriodType);
    ds.setUpdatePeriods(updatePeriods);
    ds.setQuantize(quantize);

    return tryDataSourceSave(ds);
  }
  @DwrPermission(user=true)
  public ProcessResult saveJmxPointLocator(int id, String xid, String name, JmxPointLocatorVO locator) {
    return validatePoint(id, xid, name, locator, null);
  }
  @DwrPermission(user=true)
  public ProcessResult getJmxObjectNames(boolean useLocalServer, String remoteServerAddr) {
    ProcessResult response = new ProcessResult();
    JMXConnector connector = null;
    try
    {
      MBeanServerConnection server = null;
      if (useLocalServer) {
        server = ManagementFactory.getPlatformMBeanServer();
      } else {
        String url = "service:jmx:rmi:///jndi/rmi://" + remoteServerAddr + "/jmxrmi";
        try {
          connector = JMXConnectorFactory.connect(new JMXServiceURL(url), null);
          server = connector.getMBeanServerConnection();
        }
        catch (MalformedURLException e) {
          response.addGenericMessage("dsEdit.jmx.badUrl", new Object[] { e.getMessage() });
        }
        catch (IOException e) {
          response.addGenericMessage("dsEdit.jmx.connectionError", new Object[] { e.getMessage() });
        }
      }

      if (!response.getHasMessages())
        try {
         TreeMap names = new TreeMap();
          response.addData("names", names);

          for (ObjectName on : server.queryNames(null, null)) {
            List objectAttributesList = new ArrayList();
            names.put(on.getCanonicalName(), objectAttributesList);

            for (MBeanAttributeInfo attr : server.getMBeanInfo(on).getAttributes()) {
              if (attr.getType() == null) {
                continue;
              }
              Map objectAttributes = new HashMap();
              try {
                objectAttributes.put("name", attr.getName());
                CompositeData cd;
                List compositeItemsList;
                if (attr.getType().equals("javax.management.openmbean.CompositeData")) {
                  objectAttributes.put("type", "Composite");
                  cd = (CompositeData)server.getAttribute(on, attr.getName());
                  if (cd != null) {
                    compositeItemsList = new ArrayList();
                    objectAttributes.put("items", compositeItemsList);
                    for (String key : cd.getCompositeType().keySet()) {
                      Map compositeItems = new HashMap();
                      compositeItemsList.add(compositeItems);
                      compositeItems.put("name", key);
                      compositeItems.put("type", cd.getCompositeType().getType(key).getTypeName());
                    }
                  }
                }
                else
                {
                  objectAttributes.put("type", attr.getType());
                }objectAttributesList.add(objectAttributes);
              }
              catch (RuntimeMBeanException e)
              {
              }
            }
          }
        }
        catch (Exception e)
        {
          Map names;
          response.addGenericMessage("dsEdit.jmx.readError", new Object[] { e.getMessage() });
        }
    }
    finally
    {
      try {
        if (connector != null) {
          connector.close();
        }
      }
      catch (IOException e)
      {
      }
    }
    return response;
  }
}