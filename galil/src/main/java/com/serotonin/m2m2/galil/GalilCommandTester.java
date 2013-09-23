package com.serotonin.m2m2.galil;

import com.serotonin.m2m2.galil.rt.GalilDataSourceRT;
import com.serotonin.m2m2.galil.rt.GalilMessageParser;
import com.serotonin.m2m2.galil.rt.GalilResponse;
import com.serotonin.m2m2.i18n.Translations;
import com.serotonin.util.queue.ByteQueue;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class GalilCommandTester extends Thread
{
  private final Translations translations;
  private final long timeout;
  private final Socket socket;
  private String result;

  public GalilCommandTester(Translations translations, String host, int port, int timeout, String command)
    throws IOException
  {
    this.translations = translations;
    this.timeout = timeout;
    this.socket = new Socket(host, port);
    this.socket.getOutputStream().write((command + "\r\n").getBytes(GalilDataSourceRT.CHARSET));
    start();
  }

  public void run()
  {
    try {
      GalilMessageParser parser = new GalilMessageParser();
      ByteQueue queue = new ByteQueue();
      InputStream in = this.socket.getInputStream();
      long deadline = System.currentTimeMillis() + this.timeout;
      byte[] buf = new byte[32];
      GalilResponse response = null;

      while (System.currentTimeMillis() < deadline) {
        if (in.available() > 0) {
          int len = in.read(buf);
          if (len == -1)
            break;
          queue.push(buf, 0, len);

          response = (GalilResponse)parser.parseMessage(queue);
          if (response != null)
            break;
          continue;
        }
        Thread.sleep(20L);
      }

      if (response == null)
        this.result = this.translations.translate("dsEdit.galil.tester.timeout");
      else if (response.isErrorResponse())
        this.result = this.translations.translate("dsEdit.galil.tester.noResponse");
      else
        this.result = response.getResponseData();
    }
    catch (Exception e) {
      this.result = e.getMessage();
    }
    finally {
      try {
        if (this.socket != null)
          this.socket.close();
      }
      catch (IOException e)
      {
      }
    }
  }

  public String getResult() {
    return this.result;
  }
}