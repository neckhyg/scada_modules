package com.serotonin.m2m2.onewire.vo;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableJsonException;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.onewire.rt.OneWireDataSourceRT;
import com.serotonin.m2m2.rt.dataSource.DataSourceRT;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;
import com.serotonin.m2m2.vo.event.EventTypeVO;
import com.serotonin.util.SerializationHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class OneWireDataSourceVO extends DataSourceVO<OneWireDataSourceVO>
{
  public static final int RESCAN_NONE = 0;
  public static final String RESCAN_NONE_TEXT = "NONE";
  private static final ExportCodes EVENT_CODES = new ExportCodes();

  @JsonProperty
  private String commPortId;
  private int updatePeriodType = 2;

  @JsonProperty
  private int updatePeriods = 5;

  private int rescanPeriodType = 0;

  @JsonProperty
  private int rescanPeriods = 1;
  private static final long serialVersionUID = -1L;
  private static final int version = 2;

  protected void addEventTypes(List<EventTypeVO> ets)
  {
    ets.add(createEventType(1, new TranslatableMessage("event.ds.dataSource")));

    ets.add(createEventType(2, new TranslatableMessage("event.ds.pointRead")));

    ets.add(createEventType(3, new TranslatableMessage("event.ds.pointWrite")));
  }

  public ExportCodes getEventCodes()
  {
    return EVENT_CODES;
  }

  public TranslatableMessage getConnectionDescription()
  {
    return new TranslatableMessage("common.default", new Object[] { this.commPortId });
  }

  public DataSourceRT createDataSourceRT()
  {
    return new OneWireDataSourceRT(this);
  }

  public OneWirePointLocatorVO createPointLocator()
  {
    return new OneWirePointLocatorVO();
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

  public int getRescanPeriodType() {
    return this.rescanPeriodType;
  }

  public void setRescanPeriodType(int rescanPeriodType) {
    this.rescanPeriodType = rescanPeriodType;
  }

  public int getRescanPeriods() {
    return this.rescanPeriods;
  }

  public void setRescanPeriods(int rescanPeriods) {
    this.rescanPeriods = rescanPeriods;
  }

  public void validate(ProcessResult response)
  {
    super.validate(response);

    if (StringUtils.isBlank(this.commPortId))
      response.addContextualMessage("commPortId", "validate.required", new Object[0]);
    if (!Common.TIME_PERIOD_CODES.isValidId(this.updatePeriodType, new int[0]))
      response.addContextualMessage("updatePeriodType", "validate.invalidValue", new Object[0]);
    if (this.updatePeriods <= 0)
      response.addContextualMessage("updatePeriods", "validate.greaterThanZero", new Object[0]);
    if ((this.rescanPeriodType != 0) && (this.rescanPeriods <= 0))
      response.addContextualMessage("rescanPeriods", "validate.greaterThanZero", new Object[0]);
  }

  protected void addPropertiesImpl(List<TranslatableMessage> list)
  {
    AuditEventType.addPropertyMessage(list, "dsEdit.1wire.port", this.commPortId);
    AuditEventType.addPeriodMessage(list, "dsEdit.updatePeriod", this.updatePeriodType, this.updatePeriods);
    if (this.rescanPeriodType == 0) {
      AuditEventType.addPropertyMessage(list, "dsEdit.1wire.scheduledRescan", new TranslatableMessage("dsEdit.1wire.none"));
    }
    else
      AuditEventType.addPeriodMessage(list, "dsEdit.1wire.scheduledRescan", this.rescanPeriodType, this.rescanPeriods);
  }

  protected void addPropertyChangesImpl(List<TranslatableMessage> list, OneWireDataSourceVO from)
  {
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.1wire.port", from.commPortId, this.commPortId);
    AuditEventType.maybeAddPeriodChangeMessage(list, "dsEdit.updatePeriod", from.updatePeriodType, from.updatePeriods, this.updatePeriodType, this.updatePeriods);

    if ((from.rescanPeriodType != this.rescanPeriodType) || (from.rescanPeriods != this.rescanPeriods))
    {
      TranslatableMessage fromMessage;
      TranslatableMessage fromMessage;
      if (from.rescanPeriodType == 0)
        fromMessage = new TranslatableMessage("dsEdit.1wire.none");
      else
        fromMessage = Common.getPeriodDescription(from.rescanPeriodType, from.rescanPeriods);
      TranslatableMessage toMessage;
      TranslatableMessage toMessage;
      if (this.rescanPeriodType == 0)
        toMessage = new TranslatableMessage("dsEdit.1wire.none");
      else {
        toMessage = Common.getPeriodDescription(this.rescanPeriodType, this.rescanPeriods);
      }
      AuditEventType.addPropertyChangeMessage(list, "dsEdit.1wire.scheduledRescan", fromMessage, toMessage);
    }
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(2);
    SerializationHelper.writeSafeUTF(out, this.commPortId);
    out.writeInt(this.updatePeriodType);
    out.writeInt(this.updatePeriods);
    out.writeInt(this.rescanPeriodType);
    out.writeInt(this.rescanPeriods);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.commPortId = SerializationHelper.readSafeUTF(in);
      this.updatePeriodType = in.readInt();
      this.updatePeriods = in.readInt();
      this.rescanPeriodType = 0;
      this.rescanPeriods = 1;
    }
    else if (ver == 2) {
      this.commPortId = SerializationHelper.readSafeUTF(in);
      this.updatePeriodType = in.readInt();
      this.updatePeriods = in.readInt();
      this.rescanPeriodType = in.readInt();
      this.rescanPeriods = in.readInt();
    }
  }

  public void jsonWrite(ObjectWriter writer) throws IOException, JsonException
  {
    super.jsonWrite(writer);
    writeUpdatePeriodType(writer, this.updatePeriodType);

    if (this.rescanPeriodType == 0)
      writer.writeEntry("rescanPeriodType", "NONE");
    else
      writer.writeEntry("rescanPeriodType", Common.TIME_PERIOD_CODES.getCode(this.rescanPeriodType));
  }

  public void jsonRead(JsonReader reader, JsonObject jsonObject) throws JsonException
  {
    super.jsonRead(reader, jsonObject);

    Integer value = readUpdatePeriodType(jsonObject);
    if (value != null) {
      this.updatePeriodType = value.intValue();
    }
    String text = jsonObject.getString("rescanPeriodType");
    if (text != null)
      if ("NONE".equalsIgnoreCase(text)) {
        this.rescanPeriodType = 0;
      } else {
        this.rescanPeriodType = Common.TIME_PERIOD_CODES.getId(text, new int[0]);
        if (this.rescanPeriodType == -1) {
          List result = new ArrayList();
          result.add("NONE");
          result.addAll(Common.TIME_PERIOD_CODES.getCodeList(new int[0]));
          throw new TranslatableJsonException("emport.error.invalid", new Object[] { "rescanPeriodType", text, result });
        }
      }
  }

  static
  {
    EVENT_CODES.addElement(1, "DATA_SOURCE_EXCEPTION");
    EVENT_CODES.addElement(2, "POINT_READ_EXCEPTION");
    EVENT_CODES.addElement(3, "POINT_WRITE_EXCEPTION");
  }
}