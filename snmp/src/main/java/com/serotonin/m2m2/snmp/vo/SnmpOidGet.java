package com.serotonin.m2m2.snmp.vo;

import com.serotonin.m2m2.i18n.Translations;
import com.serotonin.m2m2.snmp.rt.Version;
import com.serotonin.m2m2.web.dwr.beans.TestingUtility;
import java.io.IOException;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SnmpOidGet extends Thread
  implements TestingUtility
{
  private final Translations translations;
  private final String host;
  private final int port;
  private final Version version;
  private final String oid;
  private final int retries;
  private final int timeout;
  private String result;

  public SnmpOidGet(Translations translations, String host, int port, Version version, String oid, int retries, int timeout)
  {
    this.translations = translations;
    this.host = host;
    this.port = port;
    this.version = version;
    this.oid = oid;
    this.retries = retries;
    this.timeout = timeout;
    start();
  }

  public void run()
  {
    Snmp snmp = null;
    try {
      snmp = new Snmp(new DefaultUdpTransportMapping());
      this.version.addUser(snmp);
      snmp.listen();

      PDU pdu = this.version.createPDU();
      pdu.setType(-96);
      pdu.add(new VariableBinding(new OID(this.oid)));

      PDU response = snmp.send(pdu, this.version.getTarget(this.host, this.port, this.retries, this.timeout)).getResponse();
      if (response == null)
        this.result = this.translations.translate("dsEdit.snmp.tester.noResponse");
      else
        this.result = response.get(0).getVariable().toString();
    }
    catch (IOException e) {
      this.result = e.getMessage();
    }
    finally {
      try {
        if (snmp != null)
          snmp.close();
      }
      catch (IOException e)
      {
      }
    }
  }

  public String getResult() {
    return this.result;
  }

  public void cancel()
  {
  }
}