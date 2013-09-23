package com.serotonin.m2m2.http.common;

import com.serotonin.cache.ObjectCreator;
import com.serotonin.cache.ThreadSafeCache;
import com.serotonin.m2m2.db.dao.SystemSettingsDao;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class HttpDataSourceServlet extends HttpServlet
{
  public static HttpReceiverMulticaster httpReceiverMulticaster = new HttpReceiverMulticaster();
  private static final String DEVICE_ID_KEY = "__device";
  public static final String TIME_OVERRIDE_KEY = "__time";
  public static final ThreadSafeCache<SimpleDateFormat> BASIC_SDF_CACHE = new ThreadSafeCache(new ObjectCreator()
  {
    public SimpleDateFormat create()
    {
      return new SimpleDateFormat("yyyyMMddHHmmss");
    } } );
  private static final String GROUPED_PARAM_KEY_PREFIX = "__point";
  private static final String GROUPED_PARAM_VALUE_PREFIX = "__value";
  private static final String GROUPED_PARAM_TIME_PREFIX = "__time";
  private static final long serialVersionUID = -1L;

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    doImpl(request, response);
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
  {
    doImpl(request, response);
  }

  private void doImpl(HttpServletRequest request, HttpServletResponse response) throws IOException
  {
    List<String> messages = new LinkedList();

    HttpReceiverData data = new HttpReceiverData();
    data.setRemoteIp(request.getRemoteAddr());

    Enumeration paramNames = request.getParameterNames();

    while (paramNames.hasMoreElements()) {
      String key = (String)paramNames.nextElement();
      String value = request.getParameter(key);

      if ("__time".equals(key)) {
        long ts = stringToTime(value);
        if (ts == 0L)
          messages.add("Time override parse error");
        else
          data.setTime(ts);
        continue;
      }if ("__device".equals(key)) {
        data.setDeviceId(value); continue;
      }if (key.startsWith("__point"))
      {
        String groupId = key.substring("__point".length());

        String[] pointNames = request.getParameterValues(key);
        String[] pointValues = request.getParameterValues("__value" + groupId);
        String[] pointTimes = request.getParameterValues("__time" + groupId);

        for (int i = 0; i < pointNames.length; i++) {
          if ((pointValues == null) || (pointValues.length <= i)) {
            messages.add("Value not found for grouped point key " + key + ", name=" + pointNames[i]);
          } else {
            String time = null;
            if ((pointTimes != null) && (pointTimes.length > i))
              time = pointTimes[i];
            addData(data, pointNames[i], pointValues[i], time);
          }
        }
        continue;
      }if ((key.startsWith("__value")) || 
        (key.startsWith("__time")))
      {
        continue;
      }

      for (String avalue : request.getParameterValues(key)) {
        addData(data, key, avalue, null);
      }
    }

    if (data.getTime() == -1L) {
      data.setTime(System.currentTimeMillis());
    }
    httpReceiverMulticaster.multicast(data);

    for (String unconsumed : data.getUnconsumedKeys()) {
      messages.add("Unconsumed key: " + unconsumed);
    }

    response.getWriter().write(SystemSettingsDao.getValue("httpdsPrologue"));

    for (String message : messages) {
      response.getWriter().write(message);
      response.getWriter().write("\r\n");
    }

    response.getWriter().write(SystemSettingsDao.getValue("httpdsEpilogue"));
  }

  private void addData(HttpReceiverData data, String name, String value, String time) {
    long timestamp = 0L;
    int atpos = value.lastIndexOf(64);
    if (atpos != -1)
    {
      timestamp = stringToTime(value.substring(atpos + 1));
      if (timestamp != 0L) {
        value = value.substring(0, atpos);
      }
    }
    if ((timestamp == 0L) && (time != null))
    {
      timestamp = stringToTime(time);
    }
    data.addData(name, value, timestamp);
  }

  private long stringToTime(String s) {
    if (StringUtils.isBlank(s)) {
      return 0L;
    }
    try
    {
      return ((SimpleDateFormat)BASIC_SDF_CACHE.getObject()).parse(s).getTime();
    }
    catch (ParseException e)
    {
      try {
        return ISODateTimeFormat.dateTime().parseMillis(s);
      }
      catch (IllegalArgumentException e1)
      {
        try {
          return Long.parseLong(s); } catch (NumberFormatException e2) {
        }
      }
    }
    return -1L;
  }
}