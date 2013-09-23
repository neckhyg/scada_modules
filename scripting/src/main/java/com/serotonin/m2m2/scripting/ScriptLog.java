package com.serotonin.m2m2.scripting;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScriptLog
{
  private static final String[] LEVEL_STRINGS = { "", "TRACE", "DEBUG", "INFO ", "WARN ", "ERROR", "FATAL" };
  private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss,SSS";
  private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
  private final PrintWriter out;
  private final int logLevel;

  public ScriptLog(PrintWriter out, int logLevel)
  {
    this.out = out;
    this.logLevel = logLevel;
  }

  public void close() {
    this.out.close();
  }

  public boolean trouble() {
    return this.out.checkError();
  }

  public void trace(Object o) {
    log(o, 1);
  }

  public void debug(Object o) {
    log(o, 2);
  }

  public void info(Object o) {
    log(o, 3);
  }

  public void warn(Object o) {
    log(o, 4);
  }

  public void error(Object o) {
    log(o, 5);
  }

  public void fatal(Object o) {
    log(o, 6);
  }

  private void log(Object o, int level) {
    if (level < this.logLevel) {
      return;
    }
    synchronized (this.out) {
      this.out.append(LEVEL_STRINGS[level]).append(' ');
      this.out.append(this.sdf.format(new Date())).append(" - ");
      this.out.println(o == null ? "null" : o.toString());
      this.out.flush();
    }
  }
}