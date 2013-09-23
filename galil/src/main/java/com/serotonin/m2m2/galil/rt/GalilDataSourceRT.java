package com.serotonin.m2m2.galil.rt;

import com.serotonin.m2m2.galil.vo.GalilDataSourceVO;
import com.serotonin.m2m2.i18n.TranslatableException;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.dataImage.SetPointSource;
import com.serotonin.m2m2.rt.dataImage.types.DataValue;
import com.serotonin.m2m2.rt.dataSource.PollingDataSource;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.messaging.MessageControl;
import com.serotonin.messaging.MessagingExceptionHandler;
import com.serotonin.messaging.StreamTransport;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GalilDataSourceRT extends PollingDataSource
  implements MessagingExceptionHandler
{
  public static final Charset CHARSET = Charset.forName("US-ASCII");

  private final Log LOG = LogFactory.getLog(GalilDataSourceRT.class);
  public static final int DATA_SOURCE_EXCEPTION_EVENT = 1;
  public static final int POINT_READ_EXCEPTION_EVENT = 2;
  public static final int POINT_WRITE_EXCEPTION_EVENT = 3;
  private final GalilDataSourceVO vo;
  private Socket socket;
  private MessageControl conn;

  public GalilDataSourceRT(GalilDataSourceVO vo)
  {
    super(vo);
    this.vo = vo;
    setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), false);
  }

  protected synchronized void doPoll(long time)
  {
    if (this.socket == null) {
      try {
        openConnection();
      }
      catch (IOException e) {
        return;
      }
    }

    Exception messageException = null;
    TranslatableMessage pointError = null;

    for (DataPointRT dataPoint : this.dataPoints) {
      GalilPointLocatorRT locator = (GalilPointLocatorRT)dataPoint.getPointLocator();

      GalilRequest request = locator.getPollRequest();
      if (request != null) {
        TranslatableMessage sendMsg = null;
        try
        {
          sendMsg = sendRequest(request, dataPoint, locator, time);
        }
        catch (IOException e)
        {
          try {
            this.LOG.debug("Keep-alive connection may have been reset. Attempting to re-open.");
            closeConnection();
            openConnection();
            sendMsg = sendRequest(request, dataPoint, locator, time);
          }
          catch (Exception e2) {
            messageException = e2;
            closeConnection();
            break;
          }
        }

        if ((sendMsg != null) && (pointError == null)) {
          pointError = sendMsg;
        }
      }
    }
    if (messageException != null)
    {
      raiseEvent(1, time, true, new TranslatableMessage("event.pollingError", new Object[] { messageException.getMessage() }));

      this.LOG.info("Error while polling Galil device", messageException);
    }
    else
    {
      returnToNormal(1, time);
    }
    if (pointError != null)
    {
      raiseEvent(2, time, true, pointError);
    }
    else
    {
      returnToNormal(2, time);
    }
  }

  private TranslatableMessage sendRequest(GalilRequest request, DataPointRT dataPoint, GalilPointLocatorRT locator, long time) throws IOException {
    GalilResponse response = (GalilResponse)this.conn.send(request);

    if (response.isErrorResponse())
      return new TranslatableMessage("event.galil.errorResponse", new Object[] { dataPoint.getVO().getName() });
    try
    {
      DataValue value = locator.parsePollResponse(response.getResponseData(), dataPoint.getVO().getName());

      dataPoint.updatePointValue(new PointValueTime(value, time));
    }
    catch (TranslatableException e) {
      return new TranslatableMessage("event.galil.parsingError", new Object[] { dataPoint.getVO().getName(), response.getResponseData() });
    }

    return null;
  }

  public void initialize()
  {
    try
    {
      openConnection();
      returnToNormal(1, System.currentTimeMillis());
    }
    catch (Exception e) {
      raiseEvent(1, System.currentTimeMillis(), true, new TranslatableMessage("event.initializationError", new Object[] { e.getMessage() }));

      this.LOG.debug("Error while initializing data source", e);
      return;
    }
  }

  public synchronized void terminate()
  {
    super.terminate();
    closeConnection();
  }

  public synchronized void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source)
  {
    if (this.socket == null) {
      try {
        openConnection();
      }
      catch (IOException e) {
        raiseEvent(1, System.currentTimeMillis(), true, new TranslatableMessage("event.galil.setPointFailed", new Object[] { dataPoint.getVO().getName(), e.getMessage() }));

        this.LOG.debug("Error while initializing data source", e);
        return;
      }
      returnToNormal(1, System.currentTimeMillis());
    }

    GalilPointLocatorRT locator = (GalilPointLocatorRT)dataPoint.getPointLocator();

    GalilRequest request = locator.getSetRequest(valueTime.getValue());
    if (request == null)
      raiseEvent(3, System.currentTimeMillis(), false, new TranslatableMessage("event.galil.setRequest", new Object[] { dataPoint.getVO().getName(), valueTime.getValue() }));
    else
      try
      {
        GalilResponse response = (GalilResponse)this.conn.send(request);

        if (response.isErrorResponse()) {
          raiseEvent(3, System.currentTimeMillis(), false, new TranslatableMessage("event.galil.setResponse", new Object[] { dataPoint.getVO().getName() }));
        }
        else {
          try
          {
            dataPoint.updatePointValue(new PointValueTime(valueTime.getValue(), valueTime.getTime()));

            DataValue value = locator.parseSetResponse(response.getResponseData());
            if (value != null)
            {
              dataPoint.updatePointValue(new PointValueTime(value, System.currentTimeMillis()));
            }
          } catch (TranslatableException e) {
            raiseEvent(3, System.currentTimeMillis(), false, new TranslatableMessage("event.galil.parsingError", new Object[] { dataPoint.getVO().getName(), response.getResponseData() }));
          }
        }

      }
      catch (IOException e)
      {
        raiseEvent(3, System.currentTimeMillis(), false, new TranslatableMessage("event.galil.sendError", new Object[] { dataPoint.getVO().getName(), e.getMessage() }));
      }
  }

  public void receivedException(Exception e)
  {
    raiseEvent(1, System.currentTimeMillis(), true, new TranslatableMessage("event.galil.connectionError", new Object[] { e.getMessage() }));
  }

  public void receivedMessageMismatchException(Exception e)
  {
    raiseEvent(1, System.currentTimeMillis(), true, new TranslatableMessage("event.galil.connectionError", new Object[] { e.getMessage() }));
  }

  public void receivedResponseException(Exception e)
  {
    raiseEvent(1, System.currentTimeMillis(), true, new TranslatableMessage("event.galil.connectionError", new Object[] { e.getMessage() }));
  }

  private void openConnection() throws IOException {
    int retries = this.vo.getRetries();
    StreamTransport transport;
    while (true) {
      try {
        this.socket = new Socket();
        this.socket.connect(new InetSocketAddress(this.vo.getHost(), this.vo.getPort()), this.vo.getTimeout());
        transport = new StreamTransport(this.socket.getInputStream(), this.socket.getOutputStream());
          break;
      }
      catch (IOException e)
      {
        closeConnection();

        if (retries <= 0)
          throw e;
        this.LOG.warn("Open connection failed, trying again.");
        retries--;
        try
        {
          Thread.sleep(500L);
        }
        catch (InterruptedException e1)
        {
        }
      }
    }

    this.conn = new MessageControl();
    this.conn.setRetries(this.vo.getRetries());
    this.conn.setTimeout(this.vo.getTimeout());
    this.conn.setExceptionHandler(this);
    this.conn.start(transport, new GalilMessageParser(), null, new GalilWaitingRoomKeyFactory());
    transport.start("Galil data source");
  }

  private void closeConnection() {
    if (this.conn != null)
      this.conn.close();
    try
    {
      if (this.socket != null)
        this.socket.close();
    }
    catch (IOException e) {
      receivedException(e);
    }

    this.conn = null;
    this.socket = null;
  }
}