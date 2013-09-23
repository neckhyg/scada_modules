package com.serotonin.m2m2.http.vo;

import com.serotonin.json.spi.JsonProperty;
import com.serotonin.m2m2.http.rt.HttpReceiverDataSourceRT;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.DataSourceRT;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;
import com.serotonin.m2m2.vo.event.EventTypeVO;
import com.serotonin.util.IpAddressUtils;
import com.serotonin.util.SerializationHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class HttpReceiverDataSourceVO extends DataSourceVO<HttpReceiverDataSourceVO>
{
  private static final ExportCodes EVENT_CODES = new ExportCodes();

  @JsonProperty
  private String[] ipWhiteList = { "*.*.*.*" };

  @JsonProperty
  private String[] deviceIdWhiteList = { "*" };

  @JsonProperty
  private String setPointUrl;
  private static final long serialVersionUID = -1L;
  private static final int version = 3;

  protected void addEventTypes(List<EventTypeVO> ets)
  {
    ets.add(createEventType(1, new TranslatableMessage("event.ds.setPointFail")));
  }

  public ExportCodes getEventCodes()
  {
    return EVENT_CODES;
  }

  public TranslatableMessage getConnectionDescription()
  {
    if ((this.ipWhiteList.length == 0) || (this.deviceIdWhiteList.length == 0)) {
      return new TranslatableMessage("dsEdit.httpReceiver.dsconn.blocked");
    }
    if (ArrayUtils.contains(this.deviceIdWhiteList, "*")) {
      if (this.ipWhiteList.length == 1)
        return new TranslatableMessage("dsEdit.httpReceiver.dsconn", new Object[] { this.ipWhiteList[0] });
      return new TranslatableMessage("dsEdit.httpReceiver.dsconn", new Object[] { this.ipWhiteList[0] + ", ..." });
    }

    if (ArrayUtils.contains(this.ipWhiteList, "*.*.*.*")) {
      if (this.deviceIdWhiteList.length == 1)
        return new TranslatableMessage("dsEdit.httpReceiver.dsconn", new Object[] { this.deviceIdWhiteList[0] });
      return new TranslatableMessage("dsEdit.httpReceiver.dsconn", new Object[] { this.deviceIdWhiteList[0] + ", ..." });
    }

    return new TranslatableMessage("dsEdit.httpReceiver.dsconn.combo");
  }

  public DataSourceRT createDataSourceRT()
  {
    return new HttpReceiverDataSourceRT(this);
  }

  public HttpReceiverPointLocatorVO createPointLocator()
  {
    return new HttpReceiverPointLocatorVO();
  }

  public String[] getIpWhiteList()
  {
    return this.ipWhiteList;
  }

  public void setIpWhiteList(String[] ipWhiteList) {
    this.ipWhiteList = ipWhiteList;
  }

  public String[] getDeviceIdWhiteList() {
    return this.deviceIdWhiteList;
  }

  public void setDeviceIdWhiteList(String[] deviceIdWhiteList) {
    this.deviceIdWhiteList = deviceIdWhiteList;
  }

  public String getSetPointUrl() {
    return this.setPointUrl;
  }

  public void setSetPointUrl(String setPointUrl) {
    this.setPointUrl = setPointUrl;
  }

  public void validate(ProcessResult response)
  {
    super.validate(response);

    for (String ipmask : this.ipWhiteList) {
      String msg = IpAddressUtils.checkIpMask(ipmask);
      if (msg != null) {
        response.addContextualMessage("ipWhiteList", "common.default", new Object[] { msg });
      }
    }
    for (String deviceId : this.deviceIdWhiteList) {
      if (StringUtils.isBlank(deviceId)) {
        response.addContextualMessage("deviceIdWhiteList", "validate.missingDeviceId", new Object[0]);
      }
    }
    if (!StringUtils.isBlank(this.setPointUrl))
      try {
        new URL(this.setPointUrl);
      }
      catch (MalformedURLException e) {
        response.addContextualMessage("setPointUrl", "validate.invalidValue", new Object[0]);
      }
  }

  protected void addPropertiesImpl(List<TranslatableMessage> list)
  {
    AuditEventType.addPropertyMessage(list, "dsEdit.httpReceiver.ipWhiteList", Arrays.toString(this.ipWhiteList));
    AuditEventType.addPropertyMessage(list, "dsEdit.httpReceiver.deviceWhiteList", Arrays.toString(this.deviceIdWhiteList));

    AuditEventType.addPropertyMessage(list, "http.dsEdit.setPointUrl", this.setPointUrl);
  }

  protected void addPropertyChangesImpl(List<TranslatableMessage> list, HttpReceiverDataSourceVO from)
  {
    if (Arrays.equals(from.ipWhiteList, this.ipWhiteList)) {
      AuditEventType.addPropertyChangeMessage(list, "dsEdit.httpReceiver.ipWhiteList", Arrays.toString(from.ipWhiteList), Arrays.toString(this.ipWhiteList));
    }
    if (Arrays.equals(from.deviceIdWhiteList, this.deviceIdWhiteList)) {
      AuditEventType.addPropertyChangeMessage(list, "dsEdit.httpReceiver.deviceWhiteList", Arrays.toString(from.deviceIdWhiteList), Arrays.toString(this.deviceIdWhiteList));
    }
    AuditEventType.maybeAddPropertyChangeMessage(list, "http.dsEdit.setPointUrl", from.setPointUrl, this.setPointUrl);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(3);
    out.writeObject(this.ipWhiteList);
    out.writeObject(this.deviceIdWhiteList);
    SerializationHelper.writeSafeUTF(out, this.setPointUrl);
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    int ver = in.readInt();

    if (ver == 1) {
      this.ipWhiteList = ((String[])(String[])in.readObject());
      this.deviceIdWhiteList = new String[] { "*" };
      this.setPointUrl = null;
    }
    else if (ver == 2) {
      this.ipWhiteList = ((String[])(String[])in.readObject());
      this.deviceIdWhiteList = ((String[])(String[])in.readObject());
      this.setPointUrl = null;
    }
    else if (ver == 3) {
      this.ipWhiteList = ((String[])(String[])in.readObject());
      this.deviceIdWhiteList = ((String[])(String[])in.readObject());
      this.setPointUrl = SerializationHelper.readSafeUTF(in);
    }
  }

  static
  {
    EVENT_CODES.addElement(1, "SET_POINT_FAILURE");
  }
}