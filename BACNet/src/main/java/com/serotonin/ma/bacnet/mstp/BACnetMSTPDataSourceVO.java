package com.serotonin.ma.bacnet.mstp;

import com.serotonin.json.spi.JsonProperty;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.DataSourceRT;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.ma.bacnet.BACnetDataSourceVO;
import com.serotonin.util.SerializationHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class BACnetMSTPDataSourceVO extends BACnetDataSourceVO<BACnetMSTPDataSourceVO>
{

  @JsonProperty
  private String commPortId;

  @JsonProperty
  private int baudRate = 9600;

  @JsonProperty
  private int thisStation;

  @JsonProperty
  private int retryCount = 1;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  public DataSourceRT createDataSourceRT()
  {
    return new BACnetMSTPDataSourceRT(this);
  }

  public BACnetMSTPointLocatorVO createPointLocator()
  {
    return new BACnetMSTPointLocatorVO();
  }

  public String getCommPortId()
  {
    return this.commPortId;
  }

  public void setCommPortId(String commPortId) {
    this.commPortId = commPortId;
  }

  public int getBaudRate() {
    return this.baudRate;
  }

  public void setBaudRate(int baudRate) {
    this.baudRate = baudRate;
  }

  public int getThisStation() {
    return this.thisStation;
  }

  public void setThisStation(int thisStation) {
    this.thisStation = thisStation;
  }

  public int getRetryCount() {
    return this.retryCount;
  }

  public void setRetryCount(int retryCount) {
    this.retryCount = retryCount;
  }

  public void validate(ProcessResult response)
  {
    super.validate(response);

    if (StringUtils.isBlank(this.commPortId))
      response.addContextualMessage("commPortId", "validate.required", new Object[0]);
    if (this.baudRate <= 0)
      response.addContextualMessage("baudRate", "validate.invalidValue", new Object[0]);
    if ((this.thisStation < 0) || (this.thisStation > 127))
      response.addContextualMessage("thisStation", "validate.invalidValue", new Object[0]);
    if (this.retryCount <= 0)
      response.addContextualMessage("retryCount", "validate.invalidValue", new Object[0]);
  }

  public void addPropertiesImpl(List<TranslatableMessage> list)
  {
    AuditEventType.addPropertyMessage(list, "mod.bacnetMstp.commPortId", this.commPortId);
    AuditEventType.addPropertyMessage(list, "mod.bacnetMstp.baudRate", Integer.valueOf(this.baudRate));
    AuditEventType.addPropertyMessage(list, "mod.bacnetMstp.thisStation", Integer.valueOf(this.thisStation));
    AuditEventType.addPropertyMessage(list, "mod.bacnetMstp.retryCount", Integer.valueOf(this.retryCount));
  }

  public void addPropertyChangesImpl(List<TranslatableMessage> list, BACnetMSTPDataSourceVO from)
  {
    AuditEventType.maybeAddPropertyChangeMessage(list, "mod.bacnetMstp.commPortId", from.commPortId, this.commPortId);
    AuditEventType.maybeAddPropertyChangeMessage(list, "mod.bacnetMstp.baudRate", from.baudRate, this.baudRate);
    AuditEventType.maybeAddPropertyChangeMessage(list, "mod.bacnetMstp.thisStation", from.thisStation, this.thisStation);
    AuditEventType.maybeAddPropertyChangeMessage(list, "mod.bacnetMstp.retryCount", from.retryCount, this.retryCount);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    SerializationHelper.writeSafeUTF(out, this.commPortId);
    out.writeInt(this.baudRate);
    out.writeInt(this.thisStation);
    out.writeInt(this.retryCount);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.commPortId = SerializationHelper.readSafeUTF(in);
      this.baudRate = in.readInt();
      this.thisStation = in.readInt();
      this.retryCount = in.readInt();
    }
  }
}