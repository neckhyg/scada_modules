package com.serotonin.m2m2.http.vo;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.http.rt.HttpImageDataSourceRT;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.DataSourceRT;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;
import com.serotonin.m2m2.vo.event.EventTypeVO;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class HttpImageDataSourceVO extends DataSourceVO<HttpImageDataSourceVO>
{
  private static final ExportCodes EVENT_CODES = new ExportCodes();

  private int updatePeriodType = 2;

  @JsonProperty
  private int updatePeriods = 5;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  protected void addEventTypes(List<EventTypeVO> ets)
  {
    ets.add(createEventType(1, new TranslatableMessage("event.ds.dataRetrieval")));

    ets.add(createEventType(2, new TranslatableMessage("event.ds.fileSave")));
  }

  public ExportCodes getEventCodes()
  {
    return EVENT_CODES;
  }

  public TranslatableMessage getConnectionDescription()
  {
    return new TranslatableMessage("dsEdit.httpImage.dsconn", new Object[] { Common.getPeriodDescription(this.updatePeriodType, this.updatePeriods) });
  }

  public DataSourceRT createDataSourceRT()
  {
    return new HttpImageDataSourceRT(this);
  }

  public HttpImagePointLocatorVO createPointLocator()
  {
    return new HttpImagePointLocatorVO();
  }

  public int getUpdatePeriodType()
  {
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
    if (!Common.TIME_PERIOD_CODES.isValidId(this.updatePeriodType, new int[0]))
      response.addContextualMessage("updatePeriodType", "validate.invalidValue", new Object[0]);
    if (this.updatePeriods <= 0)
      response.addContextualMessage("updatePeriods", "validate.greaterThanZero", new Object[0]);
  }

  protected void addPropertiesImpl(List<TranslatableMessage> list)
  {
    AuditEventType.addPeriodMessage(list, "dsEdit.updatePeriod", this.updatePeriodType, this.updatePeriods);
  }

  protected void addPropertyChangesImpl(List<TranslatableMessage> list, HttpImageDataSourceVO from)
  {
    AuditEventType.maybeAddPeriodChangeMessage(list, "dsEdit.updatePeriod", from.updatePeriodType, from.updatePeriods, this.updatePeriodType, this.updatePeriods);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    out.writeInt(this.updatePeriodType);
    out.writeInt(this.updatePeriods);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.updatePeriodType = in.readInt();
      this.updatePeriods = in.readInt();
    }
  }

  public void jsonWrite(ObjectWriter writer) throws IOException, JsonException
  {
    super.jsonWrite(writer);
    writeUpdatePeriodType(writer, this.updatePeriodType);
  }

  public void jsonRead(JsonReader reader, JsonObject jsonObject) throws JsonException
  {
    super.jsonRead(reader, jsonObject);
    Integer value = readUpdatePeriodType(jsonObject);
    if (value != null)
      this.updatePeriodType = value.intValue();
  }

  static
  {
    EVENT_CODES.addElement(1, "DATA_RETRIEVAL_FAILURE");
    EVENT_CODES.addElement(2, "FILE_SAVE_EXCEPTION");
  }
}