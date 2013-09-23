package com.serotonin.m2m2.persistent.pub;

import com.serotonin.timer.AbstractTimer;
import com.serotonin.timer.FixedDelayTrigger;
import com.serotonin.timer.TimerTask;
import com.serotonin.timer.TimerTrigger;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TimedOutputStream extends OutputStream
{
  static AtomicInteger counter = new AtomicInteger();
  static final Log LOG = LogFactory.getLog(TimedOutputStream.class);
  private static final long OUT_TIME = 9223372036854775807L;
  final String desc;
  private final OutputStream delegate;
  final AbstractTimer timer;
  final long timeout;
  private final CheckTask task;
  Thread thread;
  long inTime;
  int inCount;

  public TimedOutputStream(String desc, OutputStream delegate, AbstractTimer timer, long timeout)
  {
    if (desc == null)
      this.desc = Integer.toString(counter.incrementAndGet());
    else
      this.desc = (Integer.toString(counter.incrementAndGet()) + " (" + desc + ")");
    this.delegate = delegate;
    this.timer = timer;
    this.timeout = timeout;

    this.task = new CheckTask(new FixedDelayTrigger(timeout / 4L, timeout / 4L));
    timer.schedule(this.task);
    this.inTime = 9223372036854775807L;
  }

  public void write(int b) throws IOException
  {
    in();
    try {
      this.delegate.write(b);
    }
    finally {
      out();
    }
  }

  public void write(byte[] b) throws IOException
  {
    in();
    try {
      this.delegate.write(b);
    }
    finally {
      out();
    }
  }

  public void write(byte[] b, int off, int len) throws IOException
  {
    in();
    try {
      this.delegate.write(b, off, len);
    }
    finally {
      out();
    }
  }

  public void flush() throws IOException
  {
    in();
    try {
      this.delegate.flush();
    }
    finally {
      out();
    }
  }

  protected void finalize() throws Throwable
  {
    close();
  }

  public void close() throws IOException
  {
    in();
    try {
      this.delegate.close();
    }
    finally {
      this.task.cancel();
      out();
    }
  }

  private void in() {
    this.inCount += 1;
    this.inTime = this.timer.currentTimeMillis();
    this.thread = Thread.currentThread();

    Thread.interrupted();
  }

  private void out() throws IOException {
    this.inTime = 9223372036854775807L;
    this.thread = null;
    if (Thread.interrupted()) {
      if (LOG.isDebugEnabled())
        LOG.debug(this.desc + ": write thread interrupted");
      throw new IOException("Write timeout");
    }
  }

  class CheckTask extends TimerTask {
    public CheckTask(TimerTrigger trigger) {
      super(trigger);
    }

    public void run(long fireTime)
    {
      if (TimedOutputStream.this.inTime == 9223372036854775807L) {
        if (TimedOutputStream.LOG.isDebugEnabled())
          TimedOutputStream.LOG.debug(TimedOutputStream.this.desc + ": not in a write operation");
      }
      else {
        long time = TimedOutputStream.this.timer.currentTimeMillis() - TimedOutputStream.this.inTime;
        if (TimedOutputStream.LOG.isDebugEnabled())
          TimedOutputStream.LOG.debug(TimedOutputStream.this.desc + ": time in write operation: " + time + ", total incount: " + TimedOutputStream.this.inCount);
        if (time > TimedOutputStream.this.timeout) {
          if (TimedOutputStream.LOG.isDebugEnabled())
            TimedOutputStream.LOG.debug(TimedOutputStream.this.desc + ": interrupting");
          Thread _thread = TimedOutputStream.this.thread;
          if (_thread != null)
            _thread.interrupt();
        }
      }
    }
  }
}