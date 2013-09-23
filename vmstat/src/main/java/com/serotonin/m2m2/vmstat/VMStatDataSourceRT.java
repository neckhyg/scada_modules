package com.serotonin.m2m2.vmstat;

import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.dataSource.EventDataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class VMStatDataSourceRT extends EventDataSource
  implements Runnable
{
  public static final int DATA_SOURCE_EXCEPTION_EVENT = 1;
  public static final int PARSE_EXCEPTION_EVENT = 2;
  private final Log log = LogFactory.getLog(VMStatDataSourceRT.class);
  private final VMStatDataSourceVO vo;
  private Process vmstatProcess;
  private BufferedReader in;
  private Map<Integer, Integer> attributePositions;
  private boolean terminated;

  public VMStatDataSourceRT(VMStatDataSourceVO vo)
  {
    super(vo);
    this.vo = vo;
  }

  public void initialize()
  {
    super.initialize();

    String command = "vmstat -n ";
    switch (this.vo.getOutputScale()) {
    case 2:
      command = new StringBuilder().append(command).append("-S k ").toString();
      break;
    case 3:
      command = new StringBuilder().append(command).append("-S K ").toString();
      break;
    case 4:
      command = new StringBuilder().append(command).append("-S m ").toString();
      break;
    case 5:
      command = new StringBuilder().append(command).append("-S M ").toString();
    }

    command = new StringBuilder().append(command).append(this.vo.getPollSeconds()).toString();
    try
    {
      this.vmstatProcess = Runtime.getRuntime().exec(command);

      this.in = new BufferedReader(new InputStreamReader(this.vmstatProcess.getInputStream()));

      this.in.readLine();
      String headers = this.in.readLine();

      this.attributePositions = new HashMap();
      String[] headerParts = headers.split("\\s+");
      for (int i = 0; i < headerParts.length; i++) {
        int attributeId = -1;
        if ("r".equals(headerParts[i]))
          attributeId = 1;
        else if ("b".equals(headerParts[i]))
          attributeId = 2;
        else if ("swpd".equals(headerParts[i]))
          attributeId = 3;
        else if ("free".equals(headerParts[i]))
          attributeId = 4;
        else if ("buff".equals(headerParts[i]))
          attributeId = 5;
        else if ("cache".equals(headerParts[i]))
          attributeId = 6;
        else if ("si".equals(headerParts[i]))
          attributeId = 7;
        else if ("so".equals(headerParts[i]))
          attributeId = 8;
        else if ("bi".equals(headerParts[i]))
          attributeId = 9;
        else if ("bo".equals(headerParts[i]))
          attributeId = 10;
        else if ("in".equals(headerParts[i]))
          attributeId = 11;
        else if ("cs".equals(headerParts[i]))
          attributeId = 12;
        else if ("us".equals(headerParts[i]))
          attributeId = 13;
        else if ("sy".equals(headerParts[i]))
          attributeId = 14;
        else if ("id".equals(headerParts[i]))
          attributeId = 15;
        else if ("wa".equals(headerParts[i]))
          attributeId = 16;
        else if ("st".equals(headerParts[i])) {
          attributeId = 17;
        }
        if (attributeId != -1) {
          this.attributePositions.put(Integer.valueOf(attributeId), Integer.valueOf(i));
        }

      }

      this.in.readLine();

      returnToNormal(1, System.currentTimeMillis());
    }
    catch (IOException e) {
      raiseEvent(1, System.currentTimeMillis(), true, new TranslatableMessage("event.initializationError", new Object[] { e.getMessage() }));
    }
  }

  public void terminate()
  {
    super.terminate();

    this.terminated = true;

    if (this.vmstatProcess != null)
      this.vmstatProcess.destroy();
  }

  public void beginPolling()
  {
    if (this.vmstatProcess != null)
      new Thread(this, "VMStat data source").start();
  }

  public void run() {
    try {
      while (true) {
        String line = this.in.readLine();

        if (line == null) {
          if (this.terminated)
            break;
          throw new IOException("no data");
        }

        readParts(line.split("\\s+"));
        readError();
      }
    }
    catch (IOException e)
    {
      readError();

      if (!this.terminated)
        raiseEvent(1, System.currentTimeMillis(), true, new TranslatableMessage("event.vmstat.process", new Object[] { e.getMessage() }));
    }
  }

  private void readParts(String[] parts)
  {
    TranslatableMessage error = null;
    long time = System.currentTimeMillis();

    synchronized (this.pointListChangeLock) {
      for (DataPointRT dp : this.dataPoints) {
        VMStatPointLocatorVO locator = ((VMStatPointLocatorRT)dp.getPointLocator()).getPointLocatorVO();

        Integer position = (Integer)this.attributePositions.get(Integer.valueOf(locator.getAttributeId()));
        if (position == null) {
          if (error != null)
            error = new TranslatableMessage("event.vmstat.attributeNotFound", new Object[] { locator.getConfigurationDescription() });
        }
        else {
          try
          {
            String data = parts[position.intValue()];
            Double value = new Double(data);
            dp.updatePointValue(new PointValueTime(value.doubleValue(), time));
          }
          catch (NumberFormatException e) {
            this.log.error(new StringBuilder().append("Weird. We couldn't parse the value ").append(parts[position.intValue()]).append(" into a double. attribute=").append(locator.getAttributeId()).toString());
          }
          catch (ArrayIndexOutOfBoundsException e)
          {
            this.log.error(new StringBuilder().append("Weird. We need element ").append(position).append(" but the vmstat data is only ").append(parts.length).append(" elements long").toString());
          }
        }
      }

    }

    if (error == null)
      returnToNormal(2, time);
    else
      raiseEvent(2, time, true, error);
  }

  private void readError() {
    Process p = this.vmstatProcess;
    if (p != null)
      try {
        if (p.getErrorStream().available() > 0) {
          StringBuilder errorMessage = new StringBuilder();
          InputStreamReader err = new InputStreamReader(p.getErrorStream());
          char[] buf = new char[1024];

          while (p.getErrorStream().available() > 0) {
            int read = err.read(buf);
            if (read == -1)
              break;
            errorMessage.append(buf, 0, read);
          }

          if (!this.terminated)
            this.log.warn(new StringBuilder().append("Error message from vmstat process: ").append(errorMessage).toString());
        }
      }
      catch (IOException e) {
        this.log.warn("Exception while reading error stream", e);
      }
  }
}