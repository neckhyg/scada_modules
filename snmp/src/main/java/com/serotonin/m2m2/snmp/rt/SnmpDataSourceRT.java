package com.serotonin.m2m2.snmp.rt;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataImage.DataPointRT;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.dataImage.SetPointSource;
import com.serotonin.m2m2.rt.dataSource.DataSourceRT;
import com.serotonin.m2m2.rt.dataSource.PollingDataSource;
import com.serotonin.m2m2.snmp.vo.SnmpDataSourceVO;
import com.serotonin.m2m2.snmp.vo.SnmpPointLocatorVO;
import com.serotonin.util.properties.ReloadingProperties;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SnmpDataSourceRT extends PollingDataSource
{
  public static final int DATA_SOURCE_EXCEPTION_EVENT = 1;
  public static final int PDU_EXCEPTION_EVENT = 2;
  private final Log log = LogFactory.getLog(SnmpDataSourceRT.class);
  private final SnmpDataSourceVO vo;
  private final Version version;
  private String address;
  private Target target;
  private Snmp snmp;

  public SnmpDataSourceRT(SnmpDataSourceVO vo)
  {
    super(vo);
    setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), false);
    this.vo = vo;
    this.version = Version.getVersion(vo.getSnmpVersion(), vo.getCommunity(), vo.getSecurityName(), vo.getAuthProtocol(), vo.getAuthPassphrase(), vo.getPrivProtocol(), vo.getPrivPassphrase(), vo.getEngineId(), vo.getContextEngineId(), vo.getContextName());
  }

  public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source)
  {
    PDU request = this.version.createPDU();
    SnmpPointLocatorRT locator = (SnmpPointLocatorRT)dataPoint.getPointLocator();
    request.add(new VariableBinding(getOid(dataPoint), locator.valueToVariable(valueTime.getValue())));
    try {
      PDU response = this.snmp.set(request, this.target).getResponse();

      TranslatableMessage message = validatePdu(response);
      if (message != null)
        raiseEvent(2, valueTime.getTime(), false, message);
      else
        dataPoint.setPointValue(valueTime, source);
    }
    catch (IOException e) {
      this.log.warn("", e);
      raiseEvent(2, valueTime.getTime(), false, new TranslatableMessage("event.snmp.ioex", new Object[] { e.getMessage() }));
    }
  }

  protected void doPoll(long time)
  {
    try
    {
      doPollImpl(time);
    }
    catch (Exception e) {
      raiseEvent(2, time, true, DataSourceRT.getExceptionMessage(e));
    }
  }

  private void doPollImpl(long time) throws IOException {
    PDU request = this.version.createPDU();
    PDU response = null;

    List requestPoints = new ArrayList();

    for (DataPointRT dp : this.dataPoints) {
      if (!getLocatorVO(dp).isTrapOnly()) {
        request.add(new VariableBinding(getOid(dp)));
        requestPoints.add(dp);
      }
    }

    if (request.getVariableBindings().size() == 0)
    {
      returnToNormal(2, time);
      return;
    }

    long responseTime = System.currentTimeMillis();
    response = this.snmp.get(request, this.target).getResponse();
    responseTime = System.currentTimeMillis() - responseTime;
    this.log.debug("Snmp request/response time: " + responseTime);

    TranslatableMessage message = validatePdu(response);
    if (message != null) {
      raiseEvent(2, time, true, message);
    } else {
      boolean error = false;

      for (int i = 0; i < response.size(); i++) {
        VariableBinding vb = response.get(i);

        DataPointRT dp = null;
        for (DataPointRT requestPoint : requestPoints) {
          if (getOid(requestPoint).equals(vb.getOid())) {
            dp = requestPoint;
            break;
          }
        }

        if (dp != null) {
          requestPoints.remove(dp);

          Variable variable = vb.getVariable();
          if (vb.getVariable().isException()) {
            error = true;
            raiseEvent(2, time, true, new TranslatableMessage("event.snmp.oidError", new Object[] { this.address, getOid(dp), variable.toString() }));
          }
          else
          {
            updatePoint(dp, variable, time);
          }
        } else {
          error = true;
          raiseEvent(2, time, true, new TranslatableMessage("event.snmp.unknownOid", new Object[] { vb.getOid(), this.address }));
        }

      }

      for (DataPointRT requestPoint : requestPoints) {
        error = true;
        raiseEvent(2, time, true, new TranslatableMessage("event.snmp.noBinding", new Object[] { getOid(requestPoint), this.address }));
      }

      if (!error)
      {
        returnToNormal(2, time);
      }
    }
  }

  private TranslatableMessage validatePdu(PDU pdu) {
    if (pdu == null) {
      return new TranslatableMessage("event.snmp.noResponse");
    }
    if (pdu.getErrorIndex() != 0) {
      return new TranslatableMessage("event.snmp.pduOidError", new Object[] { pdu.get(pdu.getErrorIndex() - 1).getOid(), pdu.getErrorStatusText() });
    }

    if (pdu.getErrorStatus() != 0) {
      return new TranslatableMessage("event.snmp.pduErrorStatus", new Object[] { Integer.valueOf(pdu.getErrorStatus()), pdu.getErrorStatusText() });
    }
    return null;
  }

  private OID getOid(DataPointRT dp) {
    return ((SnmpPointLocatorRT)dp.getPointLocator()).getOid();
  }

  private SnmpPointLocatorVO getLocatorVO(DataPointRT dp) {
    return ((SnmpPointLocatorRT)dp.getPointLocator()).getVO();
  }

  int getTrapPort() {
    return this.vo.getTrapPort();
  }

  String getLocalAddress() {
    return this.vo.getLocalAddress();
  }

  String getAddress() {
    return this.address;
  }

  void receivedPDU(CommandResponderEvent evt) {
    long time = System.currentTimeMillis();

    PDU pdu = evt.getPDU();
    TranslatableMessage message = validatePdu(pdu);
    if (message != null)
      raiseEvent(2, time, false, message);
    else
      synchronized (this.pointListChangeLock) {
        updateChangedPoints();

        for (int i = 0; i < pdu.size(); i++) {
          VariableBinding vb = pdu.get(i);
          boolean found = false;

          for (DataPointRT dp : this.dataPoints) {
            if (getOid(dp).equals(vb.getOid())) {
              updatePoint(dp, vb.getVariable(), time);
              found = true;
            }
          }

          if (found) {
            if (Common.envProps.getBoolean("x.sct.snmp.trapResponse")) {
              PDU ack = this.version.createPDU();
              ack.add(new VariableBinding(vb.getOid(), new Integer32(1)));
              try {
                ResponseEvent res = this.snmp.set(ack, this.target);
                TranslatableMessage msg = validatePdu(res.getResponse());
                if (msg != null)
                  this.log.warn("Error sending trap response: " + msg.getKey());
              }
              catch (IOException e) {
                this.log.warn("Exception sending trap response", e);
              }
            }
          }
          else
            this.log.warn("Trap not handled: " + vb);
        }
      }
  }

  private void updatePoint(DataPointRT dp, Variable variable, long time)
  {
    SnmpPointLocatorRT locator = (SnmpPointLocatorRT)dp.getPointLocator();
    dp.updatePointValue(new PointValueTime(locator.variableToValue(variable), time));
  }

  public void initialize()
  {
    try
    {
      this.address = InetAddress.getByName(this.vo.getHost()).getHostAddress();
      this.target = this.version.getTarget(this.vo.getHost(), this.vo.getPort(), this.vo.getRetries(), this.vo.getTimeout());
      this.snmp = new Snmp(new DefaultUdpTransportMapping());
      this.snmp.listen();

      SnmpTrapRouter.addDataSource(this);

      returnToNormal(1, System.currentTimeMillis());
    }
    catch (BindException e)
    {
      TranslatableMessage msg;
      TranslatableMessage msg;
      if (e.getMessage() == null) {
        msg = new TranslatableMessage("snmp.bindException");
      }
      else
      {
        TranslatableMessage msg;
        if (e.getMessage().contains("Permission denied"))
          msg = new TranslatableMessage("snmp.bindException.permission");
        else
          msg = new TranslatableMessage("snmp.bindException.other", new Object[] { e.getMessage() });
      }
      raiseEvent(1, System.currentTimeMillis(), true, msg);
      this.log.debug("Error while initializing data source", e);
      return;
    }
    catch (Exception e) {
      raiseEvent(1, System.currentTimeMillis(), true, DataSourceRT.getExceptionMessage(e));

      this.log.debug("Error while initializing data source", e);
      return;
    }

    super.initialize();
  }

  public void terminate()
  {
    super.terminate();

    SnmpTrapRouter.removeDataSource(this);
    try
    {
      if (this.snmp != null)
        this.snmp.close();
    }
    catch (IOException e) {
      throw new ShouldNeverHappenException(e);
    }
  }
}