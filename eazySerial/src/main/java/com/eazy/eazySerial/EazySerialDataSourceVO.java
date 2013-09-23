package com.eazy.eazySerial;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableJsonException;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.DataSourceRT;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;
import com.serotonin.m2m2.vo.event.EventTypeVO;
import com.serotonin.util.SerializationHelper;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class EazySerialDataSourceVO extends DataSourceVO<EazySerialDataSourceVO>
{
  private static final ExportCodes EVENT_CODES = new ExportCodes();
  public static final ExportCodes OUTPUT_SCALE_CODES;

  @JsonProperty
  private int pollSeconds = 60;

  private int outputScale = 1;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

    private int updatePeriodType = 2;

    @JsonProperty
    private int updatePeriods = 5;
    @JsonProperty
    private String commPortId;

    @JsonProperty
    private int baudRate = 9600;

    @JsonProperty
    private int flowControlIn = 0;

    @JsonProperty
    private int flowControlOut = 0;

    @JsonProperty
    private int dataBits = 8;

    @JsonProperty
    private int stopBits = 1;

    @JsonProperty
    private int parity = 0;
  protected void addEventTypes(List<EventTypeVO> ets)
  {
    ets.add(createEventType(1, new TranslatableMessage("event.ds.dataSource"), 3, 2));

    ets.add(createEventType(2, new TranslatableMessage("event.ds.dataParse")));
  }

  public ExportCodes getEventCodes()
  {
    return EVENT_CODES;
  }

  public TranslatableMessage getConnectionDescription()
  {
//    return new TranslatableMessage("dsEdit.eazySerial.dsconn", new Object[] { Integer.valueOf(this.getUpdatePeriods()) });
      return Common.getPeriodDescription(updatePeriodType, updatePeriods);
  }

  public DataSourceRT createDataSourceRT()
  {
    return new EazySerialDataSourceRT(this);
  }

  public EazySerialPointLocatorVO createPointLocator()
  {
    return new EazySerialPointLocatorVO();
  }

  public int getPollSeconds()
  {
    return this.pollSeconds;
  }

  public void setPollSeconds(int pollSeconds) {
    this.pollSeconds = pollSeconds;
  }

  public int getOutputScale() {
    return this.outputScale;
  }

  public void setOutputScale(int outputScale) {
    this.outputScale = outputScale;
  }

    public int getUpdatePeriodType() {
        return updatePeriodType;
    }

    public void setUpdatePeriodType(int updatePeriodType) {
        this.updatePeriodType = updatePeriodType;
    }

    public int getUpdatePeriods() {
        return updatePeriods;
    }

    public void setUpdatePeriods(int updatePeriods) {
        this.updatePeriods = updatePeriods;
    }

    public String getCommPortId() {
        return commPortId;
    }

    public void setCommPortId(String commPortId) {
        this.commPortId = commPortId;
    }

    public int getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    public int getFlowControlIn() {
        return flowControlIn;
    }

    public void setFlowControlIn(int flowControlIn) {
        this.flowControlIn = flowControlIn;
    }

    public int getFlowControlOut() {
        return flowControlOut;
    }

    public void setFlowControlOut(int flowControlOut) {
        this.flowControlOut = flowControlOut;
    }

    public int getDataBits() {
        return dataBits;
    }

    public void setDataBits(int dataBits) {
        this.dataBits = dataBits;
    }

    public int getStopBits() {
        return stopBits;
    }

    public void setStopBits(int stopBits) {
        this.stopBits = stopBits;
    }

    public int getParity() {
        return parity;
    }

    public void setParity(int parity) {
        this.parity = parity;
    }

    public void validate(ProcessResult response)
  {
    super.validate(response);

//    if (this.pollSeconds < 1) {
//      response.addContextualMessage("pollSeconds", "validate.greaterThanZero", new Object[] { Integer.valueOf(this.pollSeconds) });
//    }
//    if (!OUTPUT_SCALE_CODES.isValidId(this.outputScale, new int[0]))
//      response.addContextualMessage("outputScale", "validate.invalidValue", new Object[0]);
  }

  protected void addPropertiesImpl(List<TranslatableMessage> list)
  {
//    AuditEventType.addPropertyMessage(list, "dsEdit.eazySerial.pollSeconds", Integer.valueOf(this.pollSeconds));
//    AuditEventType.addExportCodeMessage(list, "dsEdit.eazySerial.outputScale", OUTPUT_SCALE_CODES, this.outputScale);
    AuditEventType.addPeriodMessage(list, "dsEdit.updatePeriod", this.updatePeriodType, this.updatePeriods);
    AuditEventType.addPropertyMessage(list, "dsEdit.modbusSerial.port", this.commPortId);
    AuditEventType.addPropertyMessage(list, "dsEdit.modbusSerial.baud", Integer.valueOf(this.baudRate));
    AuditEventType.addPropertyMessage(list, "dsEdit.modbusSerial.flowControlIn", Integer.valueOf(this.flowControlIn));
    AuditEventType.addPropertyMessage(list, "dsEdit.modbusSerial.flowControlOut", Integer.valueOf(this.flowControlOut));
    AuditEventType.addPropertyMessage(list, "dsEdit.modbusSerial.dataBits", Integer.valueOf(this.dataBits));
    AuditEventType.addPropertyMessage(list, "dsEdit.modbusSerial.stopBits", Integer.valueOf(this.stopBits));
    AuditEventType.addPropertyMessage(list, "dsEdit.modbusSerial.parity", Integer.valueOf(this.parity));
  }

  protected void addPropertyChangesImpl(List<TranslatableMessage> list, EazySerialDataSourceVO from)
  {
//    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.eazySerial.pollSeconds", from.pollSeconds, this.pollSeconds);
//    AuditEventType.maybeAddExportCodeChangeMessage(list, "dsEdit.eazySerial.outputScale", OUTPUT_SCALE_CODES, from.outputScale, this.outputScale);
    AuditEventType.maybeAddPeriodChangeMessage(list, "dsEdit.updatePeriod", from.updatePeriodType, from.updatePeriods, this.updatePeriodType, this.updatePeriods);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbusSerial.port", from.commPortId, this.commPortId);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbusSerial.baud", from.baudRate, this.baudRate);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbusSerial.flowControlIn", from.flowControlIn, this.flowControlIn);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbusSerial.flowControlOut", from.flowControlOut, this.flowControlOut);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbusSerial.dataBits", from.dataBits, this.dataBits);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbusSerial.stopBits", from.stopBits, this.stopBits);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.modbusSerial.parity", from.parity, this.parity);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
//    out.writeInt(this.pollSeconds);
//    out.writeInt(this.outputScale);
    out.writeInt(this.updatePeriodType);
    out.writeInt(this.updatePeriods);
    SerializationHelper.writeSafeUTF(out, this.commPortId);
    out.writeInt(this.baudRate);
    out.writeInt(this.flowControlIn);
    out.writeInt(this.flowControlOut);
    out.writeInt(this.dataBits);
    out.writeInt(this.stopBits);
    out.writeInt(this.parity);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

//    if (ver == 1) {
//      this.pollSeconds = in.readInt();
//      this.outputScale = in.readInt();
      this.updatePeriodType = in.readInt();
      this.updatePeriods = in.readInt();
      this.commPortId = SerializationHelper.readSafeUTF(in);
      this.baudRate = in.readInt();
      this.flowControlIn = in.readInt();
      this.flowControlOut = in.readInt();
      this.dataBits = in.readInt();
      this.stopBits = in.readInt();
      this.parity = in.readInt();
//    }
  }

  public void jsonWrite(ObjectWriter writer) throws IOException, JsonException
  {
    super.jsonWrite(writer);
//    writer.writeEntry("outputScale", OUTPUT_SCALE_CODES.getCode(this.outputScale));
  }

  public void jsonRead(JsonReader reader, JsonObject jsonObject) throws JsonException
  {
    super.jsonRead(reader, jsonObject);

//    String text = jsonObject.getString("outputScale");
//    if (text != null) {
//      this.outputScale = OUTPUT_SCALE_CODES.getId(text, new int[0]);
//      if (this.outputScale == -1)
//        throw new TranslatableJsonException("emport.error.invalid", new Object[] { "outputScale", text, OUTPUT_SCALE_CODES.getCodeList(new int[0]) });
//    }
  }

  static
  {
    EVENT_CODES.addElement(1, "DATA_SOURCE_EXCEPTION");
    EVENT_CODES.addElement(2, "PARSE_EXCEPTION");

    OUTPUT_SCALE_CODES = new ExportCodes();

    OUTPUT_SCALE_CODES.addElement(1, "NONE", "dsEdit.eazySerial.scale.none");
    OUTPUT_SCALE_CODES.addElement(2, "LOWER_K", "dsEdit.eazySerial.scale.k");
    OUTPUT_SCALE_CODES.addElement(3, "UPPER_K", "dsEdit.eazySerial.scale.K");
    OUTPUT_SCALE_CODES.addElement(4, "LOWER_M", "dsEdit.eazySerial.scale.m");
    OUTPUT_SCALE_CODES.addElement(5, "UPPER_M", "dsEdit.eazySerial.scale.M");
  }

  public static abstract interface OutputScale
  {
    public static final int NONE = 1;
    public static final int LOWER_K = 2;
    public static final int UPPER_K = 3;
    public static final int LOWER_M = 4;
    public static final int UPPER_M = 5;
  }
}