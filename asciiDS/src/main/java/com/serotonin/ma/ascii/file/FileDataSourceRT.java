package com.serotonin.ma.ascii.file;

import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.dataImage.SetPointSource;
import com.serotonin.m2m2.rt.dataImage.types.DataValue;
import com.serotonin.m2m2.rt.dataSource.PollingDataSource;
import com.serotonin.m2m2.vo.DataPointVO;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileDataSourceRT extends PollingDataSource
{
  private final Log LOG = LogFactory.getLog(FileDataSourceRT.class);
  public static final int POINT_READ_EXCEPTION_EVENT = 1;
  public static final int DATA_SOURCE_EXCEPTION_EVENT = 2;
  private final FileDataSourceVO vo;

  public FileDataSourceRT(FileDataSourceVO vo)
  {
    super(vo);

    this.vo = vo;
    setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), vo.isQuantize());
  }

  protected void doPoll(long time)
  {
    File file = new File(this.vo.getFilePath());
    String content;
    if (!file.exists()) {
      raiseEvent(2, time, true, new TranslatableMessage("ascii.file.rt.fileNotExists", new Object[] { this.vo.getName() }));
    }
    else {
      content = null;
      try {
        content = readFile(file);
      }
      catch (IOException e) {
        raiseEvent(2, time, true, new TranslatableMessage("ascii.file.rt.fileError", new Object[] { this.vo.getName(), e.getMessage() }));

        this.LOG.error("Error reading file", e);
        return;
      }

      for (DataPointRT dataPoint : this.dataPoints)
        try {
          FilePointLocatorVO dataPointVO = (FilePointLocatorVO)dataPoint.getVO().getPointLocator();
          DataValue value = getValue(dataPointVO, content);

          long timestamp = time;
          if (dataPointVO.isCustomTimestamp()) {
            try {
              timestamp = getTimestamp(dataPointVO, content);
            }
            catch (Exception e) {
              raiseEvent(1, time, true, new TranslatableMessage("event.exception2", new Object[] { this.vo.getName(), e.getMessage() }));

              timestamp = time;
            }
          }

          dataPoint.updatePointValue(new PointValueTime(value, timestamp));
        }
        catch (Exception e) {
          raiseEvent(1, time, true, new TranslatableMessage("event.exception2", new Object[] { this.vo.getName(), e.getMessage() }));

          this.LOG.error("Error parsing point", e);
        }
    }
  }

  private DataValue getValue(FilePointLocatorVO point, String content) throws Exception
  {
    String valueRegex = point.getValueRegex();
    Pattern valuePattern = Pattern.compile(valueRegex);
    Matcher matcher = valuePattern.matcher(content);
    DataValue value = null;
    String strValue = null;
    boolean found = false;
    while (matcher.find()) {
      found = true;
      strValue = matcher.group();
      value = DataValue.stringToValue(strValue, point.getDataTypeId());
    }

    if (!found) {
      throw new Exception("Value string not found (regex: " + valueRegex + ")");
    }

    return value;
  }

  private long getTimestamp(FilePointLocatorVO point, String content) throws Exception {
    long timestamp = System.currentTimeMillis();
    String dataFormat = point.getTimestampFormat();
    String tsRegex = point.getTimestampRegex();
    Pattern tsPattern = Pattern.compile(tsRegex);
    Matcher tsMatcher = tsPattern.matcher(content);

    boolean found = false;
    while (tsMatcher.find()) {
      found = true;
      String tsValue = tsMatcher.group();
      timestamp = new SimpleDateFormat(dataFormat).parse(tsValue).getTime();
    }

    if (!found) {
      throw new Exception("Timestamp string not found (regex: " + tsRegex + ")");
    }

    return timestamp;
  }

  public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source)
  {
    throw new RuntimeException("not implemented");
  }

  private String readFile(File file) throws IOException {
    StringWriter sw = new StringWriter();

    FileReader reader = null;
    try {
      reader = new FileReader(file);
      IOUtils.copy(reader, sw);
    }
    finally {
      IOUtils.closeQuietly(reader);
    }

    return sw.toString();
  }
}