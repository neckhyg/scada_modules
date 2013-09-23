package com.serotonin.m2m2.mbus;

import com.serotonin.json.spi.JsonProperty;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.DataSourceRT;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;
import com.serotonin.m2m2.vo.dataSource.PointLocatorVO;
import com.serotonin.m2m2.vo.event.EventTypeVO;
import com.serotonin.util.SerializationHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class MBusDataSourceVO extends DataSourceVO<MBusDataSourceVO>
{
  private static final ExportCodes EVENT_CODES = new ExportCodes();

  @JsonProperty
  private String commPortId;

  @JsonProperty
  private int updatePeriodType = 4;

  @JsonProperty
  private int updatePeriods = 1;

  @JsonProperty
  private MBusConnectionType connectionType = MBusConnectionType.SERIAL_DIRECT;

  @JsonProperty
  private int baudRate = 2400;

  @JsonProperty
  private int flowControlIn = 1;

  @JsonProperty
  private int flowControlOut = 2;

  @JsonProperty
  private int dataBits = 8;

  @JsonProperty
  private int stopBits = 1;

  @JsonProperty
  private int parity = 2;

  @JsonProperty
  private String phonenumber = "";
  private static final long serialVersionUID = -1L;
  private static final int version = 2;

  protected void addEventTypes(List<EventTypeVO> eventTypes) { eventTypes.add(createEventType(1, new TranslatableMessage("event.ds.dataSource")));

    eventTypes.add(createEventType(2, new TranslatableMessage("event.ds.pointRead")));

    eventTypes.add(createEventType(3, new TranslatableMessage("event.ds.pointWrite")));
  }

  public TranslatableMessage getConnectionDescription()
  {
    return new TranslatableMessage("common.default", new Object[] { this.commPortId });
  }

  public PointLocatorVO createPointLocator()
  {
    return new MBusPointLocatorVO();
  }

  public DataSourceRT createDataSourceRT()
  {
    return new MBusDataSourceRT(this);
  }

  public ExportCodes getEventCodes()
  {
    return EVENT_CODES;
  }

  protected void addPropertiesImpl(List<TranslatableMessage> list)
  {
    AuditEventType.addPropertyMessage(list, "dsEdit.mbus.port", this.commPortId);
    AuditEventType.addPeriodMessage(list, "dsEdit.updatePeriod", this.updatePeriodType, this.updatePeriods);
  }

  protected void addPropertyChangesImpl(List<TranslatableMessage> list, MBusDataSourceVO from)
  {
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.port", from.commPortId, this.commPortId);
    AuditEventType.maybeAddPeriodChangeMessage(list, "dsEdit.updatePeriod", from.updatePeriodType, from.updatePeriods, this.updatePeriodType, this.updatePeriods);
  }

  public String getCommPortId()
  {
    return this.commPortId;
  }

  public void setCommPortId(String commPortId) {
    this.commPortId = commPortId;
  }

  public int getUpdatePeriodType() {
    return this.updatePeriodType;
  }

  public void setUpdatePeriodType(int updatePeriodType) {
    this.updatePeriodType = updatePeriodType;
  }

  public int getUpdatePeriods() {
    return this.updatePeriods;
  }

  public void setUpdatePeriods(int updatePeriods) {
    this.updatePeriods = updatePeriods;
  }

  public void validate(ProcessResult response)
  {
    super.validate(response);

    if (StringUtils.isBlank(this.commPortId)) {
      response.addContextualMessage("commPortId", "validate.required", new Object[0]);
    }
    if (!Common.TIME_PERIOD_CODES.isValidId(this.updatePeriodType, new int[0])) {
      response.addContextualMessage("updatePeriodType", "validate.invalidValue", new Object[0]);
    }
    if (this.updatePeriods <= 0)
      response.addContextualMessage("updatePeriods", "validate.greaterThanZero", new Object[0]);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(2);
    out.writeUTF(this.connectionType.name());
    switch (this.connectionType.ordinal()) {
    case 1:
      SerializationHelper.writeSafeUTF(out, this.commPortId);
      break;
    case 2:
    }

    out.writeInt(this.updatePeriodType);
    out.writeInt(this.updatePeriods);
    out.writeInt(this.baudRate);
    out.writeInt(this.flowControlIn);
    out.writeInt(this.flowControlOut);
    out.writeInt(this.dataBits);
    out.writeInt(this.stopBits);
    out.writeInt(this.parity);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    switch (ver) {
    case 2:
      this.connectionType = MBusConnectionType.valueOf(in.readUTF());
      switch (this.connectionType.ordinal()) {
      case 1:
        this.commPortId = SerializationHelper.readSafeUTF(in);
        break;
      case 2:
      }

      this.updatePeriodType = in.readInt();
      this.updatePeriods = in.readInt();
      this.baudRate = in.readInt();
      this.flowControlIn = in.readInt();
      this.flowControlOut = in.readInt();
      this.dataBits = in.readInt();
      this.stopBits = in.readInt();
      this.parity = in.readInt();
    }
  }

  public void setConnectionType(MBusConnectionType connectionType)
  {
    this.connectionType = connectionType;
  }

  public MBusConnectionType getConnectionType()
  {
    return this.connectionType;
  }

  public boolean isSerialDirect()
  {
    return MBusConnectionType.SERIAL_DIRECT.equals(this.connectionType);
  }

  public boolean isSerialAtModem()
  {
    return MBusConnectionType.SERIAL_AT_MODEM.equals(this.connectionType);
  }

  public int getFlowControlIn()
  {
    return this.flowControlIn;
  }

  public void setFlowControlIn(int flowControlIn)
  {
    this.flowControlIn = flowControlIn;
  }

  public int getBaudRate()
  {
    return this.baudRate;
  }

  public void setBaudRate(int baudRate)
  {
    this.baudRate = baudRate;
  }

  public int getFlowControlOut()
  {
    return this.flowControlOut;
  }

  public void setFlowControlOut(int flowControlOut)
  {
    this.flowControlOut = flowControlOut;
  }

  public int getDataBits()
  {
    return this.dataBits;
  }

  public void setDataBits(int dataBits)
  {
    this.dataBits = dataBits;
  }

  public int getStopBits()
  {
    return this.stopBits;
  }

  public void setStopBits(int stopBits)
  {
    this.stopBits = stopBits;
  }

  public int getParity()
  {
    return this.parity;
  }

  public void setParity(int parity)
  {
    this.parity = parity;
  }

  public String getPhonenumber()
  {
    return this.phonenumber;
  }

  public void setPhonenumber(String phonenumber)
  {
    this.phonenumber = phonenumber;
  }

  static
  {
    EVENT_CODES.addElement(1, "DATA_SOURCE_EXCEPTION");
    EVENT_CODES.addElement(2, "POINT_READ_EXCEPTION");
    EVENT_CODES.addElement(3, "POINT_WRITE_EXCEPTION");
  }
}