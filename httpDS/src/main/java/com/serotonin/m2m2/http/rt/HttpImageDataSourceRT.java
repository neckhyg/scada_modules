package com.serotonin.m2m2.http.rt;

import com.serotonin.io.StreamUtils;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.ImageSaveException;
import com.serotonin.m2m2.http.vo.HttpImageDataSourceVO;
import com.serotonin.m2m2.http.vo.HttpImagePointLocatorVO;
import com.serotonin.m2m2.i18n.TranslatableException;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.dataImage.SetPointSource;
import com.serotonin.m2m2.rt.dataImage.types.ImageValue;
import com.serotonin.m2m2.rt.dataSource.DataSourceRT;
import com.serotonin.m2m2.rt.dataSource.PollingDataSource;
import com.serotonin.m2m2.rt.maint.BackgroundProcessing;
import com.serotonin.m2m2.rt.maint.work.WorkItem;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.util.image.BoxScaledImage;
import com.serotonin.util.image.ImageUtils;
import com.serotonin.util.image.JpegImageFormat;
import com.serotonin.util.image.PercentScaledImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

public class HttpImageDataSourceRT extends PollingDataSource
{
  static final Log LOG = LogFactory.getLog(HttpImageDataSourceRT.class);
  public static final int DATA_RETRIEVAL_FAILURE_EVENT = 1;
  public static final int FILE_SAVE_EXCEPTION_EVENT = 2;

  public HttpImageDataSourceRT(HttpImageDataSourceVO vo)
  {
    super(vo);
    setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), false);
  }

  public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source)
  {
  }

  protected void doPoll(long time)
  {
    ImageRetrieverMonitor monitor = new ImageRetrieverMonitor();

    for (DataPointRT dp : this.dataPoints) {
      ImageRetriever retriever = new ImageRetriever(monitor, dp, time);
      monitor.addRetriever(retriever);
    }

    while (!monitor.isEmpty()) {
      synchronized (monitor) {
        try {
          monitor.wait(1000L);
        }
        catch (InterruptedException e)
        {
        }
      }

    }

    if (monitor.getRetrievalFailure() != null)
      raiseEvent(1, time, true, monitor.getRetrievalFailure());
    else {
      returnToNormal(1, time);
    }
    if (monitor.getSaveFailure() != null)
      raiseEvent(2, time, true, monitor.getSaveFailure());
    else
      returnToNormal(2, time);
  }

  public static byte[] getData(String url, int timeoutSeconds, int retries, int readLimitKb) throws TranslatableException
  {
      byte[] data;
    while (true) {
      HttpClient client = Common.getHttpClient(timeoutSeconds * 1000);
      HttpGet request = null;
      TranslatableMessage message;
      try {
        request = new HttpGet(url);
        HttpResponse response = client.execute(request);

        if (response.getStatusLine().getStatusCode() == 200) {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          StreamUtils.transfer(response.getEntity().getContent(), baos, readLimitKb * 1024);
          data = baos.toByteArray();

          if (request == null) break;
          request.reset(); break;
        }
        message = new TranslatableMessage("event.http.response", new Object[] { url, Integer.valueOf(response.getStatusLine().getStatusCode()) });
      }
      catch (Exception e) {
        message = DataSourceRT.getExceptionMessage(e);
      }
      finally {
        if (request != null) {
          request.reset();
        }
      }
      if (retries <= 0)
        throw new TranslatableException(message);
      retries--;
      try
      {
        Thread.sleep(1000L);
      }
      catch (InterruptedException e)
      {
      }
    }
    return data;
  }

  class ImageRetriever
    implements WorkItem
  {
    private final HttpImageDataSourceRT.ImageRetrieverMonitor monitor;
    private final DataPointRT dp;
    private final long time;
    private TranslatableMessage retrievalFailure;
    private TranslatableMessage saveFailure;

    ImageRetriever(HttpImageDataSourceRT.ImageRetrieverMonitor monitor, DataPointRT dp, long time)
    {
      this.monitor = monitor;
      this.dp = dp;
      this.time = time;
    }

    public void execute()
    {
      try {
        executeImpl();
      }
      finally {
        this.monitor.removeRetriever(this);
      }
    }

    private void executeImpl() { HttpImagePointLocatorVO vo = (HttpImagePointLocatorVO)this.dp.getVO().getPointLocator();
      byte[] data;
      try {
        data = HttpImageDataSourceRT.getData(vo.getUrl(), vo.getTimeoutSeconds(), vo.getRetries(), vo.getReadLimit());
      }
      catch (Exception e) {
        if ((e instanceof TranslatableException))
          this.retrievalFailure = ((TranslatableException)e).getTranslatableMessage();
        else {
          this.retrievalFailure = new TranslatableMessage("event.httpImage.retrievalError", new Object[] { vo.getUrl(), e.getMessage() });
        }
        if (HttpImageDataSourceRT.LOG.isDebugEnabled())
          HttpImageDataSourceRT.LOG.debug("Error retrieving page '" + vo.getUrl() + "'", e);
        return;
      }
      try
      {
        if (vo.getScaleType() == 1)
        {
          PercentScaledImage scaler = new PercentScaledImage(vo.getScalePercent() / 100.0F);
          data = ImageUtils.scaleImage(scaler, data, new JpegImageFormat(0.85F));
        }
        else if (vo.getScaleType() == 2)
        {
          BoxScaledImage scaler = new BoxScaledImage(vo.getScaleWidth(), vo.getScaleHeight());
          data = ImageUtils.scaleImage(scaler, data, new JpegImageFormat(0.85F));
        }
      }
      catch (Exception e) {
        this.saveFailure = new TranslatableMessage("event.httpImage.scalingError", new Object[] { e.getMessage() });
        if (HttpImageDataSourceRT.LOG.isDebugEnabled())
          HttpImageDataSourceRT.LOG.debug("Error scaling image", e);
        return;
      }

      try
      {
        this.dp.updatePointValue(new PointValueTime(new ImageValue(data, 1), this.time), false);
      }
      catch (ImageSaveException e) {
        this.saveFailure = new TranslatableMessage("event.httpImage.saveError", new Object[] { e.getMessage() });
        if (HttpImageDataSourceRT.LOG.isDebugEnabled())
          HttpImageDataSourceRT.LOG.debug("Error saving image data", e);
        return;
      } }

    public TranslatableMessage getRetrievalFailure()
    {
      return this.retrievalFailure;
    }

    public TranslatableMessage getSaveFailure() {
      return this.saveFailure;
    }

    public int getPriority()
    {
      return 1;
    }
  }

  class ImageRetrieverMonitor
  {
    private final List<HttpImageDataSourceRT.ImageRetriever> retrievers = new ArrayList();
    private TranslatableMessage retrievalFailure;
    private TranslatableMessage saveFailure;

    ImageRetrieverMonitor()
    {
    }

    synchronized void addRetriever(HttpImageDataSourceRT.ImageRetriever retriever)
    {
      this.retrievers.add(retriever);
      Common.backgroundProcessing.addWorkItem(retriever);
    }

    synchronized void removeRetriever(HttpImageDataSourceRT.ImageRetriever retriever) {
      this.retrievers.remove(retriever);

      if ((this.retrievalFailure == null) && (retriever.getRetrievalFailure() != null)) {
        this.retrievalFailure = retriever.getRetrievalFailure();
      }
      if ((this.saveFailure == null) && (retriever.getSaveFailure() != null)) {
        this.saveFailure = retriever.getSaveFailure();
      }
      if (this.retrievers.isEmpty())
        synchronized (this) {
          notifyAll();
        }
    }

    public TranslatableMessage getRetrievalFailure()
    {
      return this.retrievalFailure;
    }

    public TranslatableMessage getSaveFailure() {
      return this.saveFailure;
    }

    public boolean isEmpty() {
      return this.retrievers.isEmpty();
    }
  }
}