package com.serotonin.m2m2.jmxds;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.ProcessResult;
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
import org.apache.commons.lang3.StringUtils;

public class JmxDataSourceVO extends DataSourceVO<JmxDataSourceVO>
{
  private static final ExportCodes EVENT_CODES = new ExportCodes();

  @JsonProperty
  private boolean useLocalServer;

  @JsonProperty
  private String remoteServerAddr;
  private int updatePeriodType = 2;

  @JsonProperty
  private int updatePeriods = 5;

  @JsonProperty
  private boolean quantize;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  protected void addEventTypes(List<EventTypeVO> ets)
  {
    ets.add(createEventType(1, new TranslatableMessage("event.ds.dataSource"), 3, 2));

    ets.add(createEventType(2, new TranslatableMessage("event.ds.pointRead")));

    ets.add(createEventType(3, new TranslatableMessage("event.ds.pointWrite")));
  }

  public ExportCodes getEventCodes()
  {
    return EVENT_CODES;
  }

  public TranslatableMessage getConnectionDescription()
  {
    if (this.useLocalServer)
      return new TranslatableMessage("dsEdit.jmx.dsconn.local");
    return new TranslatableMessage("dsEdit.jmx.dsconn.remote", new Object[] { this.remoteServerAddr });
  }

  public DataSourceRT createDataSourceRT()
  {
    return new JmxDataSourceRT(this);
  }

  public JmxPointLocatorVO createPointLocator()
  {
    return new JmxPointLocatorVO();
  }

  public boolean isUseLocalServer()
  {
    return this.useLocalServer;
  }

  public void setUseLocalServer(boolean useLocalServer) {
    this.useLocalServer = useLocalServer;
  }

  public String getRemoteServerAddr() {
    return this.remoteServerAddr;
  }

  public void setRemoteServerAddr(String remoteServerAddr) {
    this.remoteServerAddr = remoteServerAddr;
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

  public boolean isQuantize() {
    return this.quantize;
  }

  public void setQuantize(boolean quantize) {
    this.quantize = quantize;
  }

  public void validate(ProcessResult response)
  {
    super.validate(response);

    if ((!this.useLocalServer) && (StringUtils.isBlank(this.remoteServerAddr)))
      response.addContextualMessage("remoteServerAddr", "validate.required", new Object[0]);
    if (!Common.TIME_PERIOD_CODES.isValidId(this.updatePeriodType, new int[0]))
      response.addContextualMessage("updatePeriodType", "validate.invalidValue", new Object[0]);
    if (this.updatePeriods <= 0)
      response.addContextualMessage("updatePeriods", "validate.greaterThanZero", new Object[0]);
  }

  protected void addPropertiesImpl(List<TranslatableMessage> list)
  {
    AuditEventType.addPropertyMessage(list, "dsEdit.jmx.useLocalServer", this.useLocalServer);
    AuditEventType.addPropertyMessage(list, "dsEdit.jmx.remoteServerAddr", this.remoteServerAddr);
    AuditEventType.addPeriodMessage(list, "dsEdit.updatePeriod", this.updatePeriodType, this.updatePeriods);
    AuditEventType.addPropertyMessage(list, "dsEdit.quantize", this.quantize);
  }

  protected void addPropertyChangesImpl(List<TranslatableMessage> list, JmxDataSourceVO from)
  {
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.jmx.useLocalServer", from.useLocalServer, this.useLocalServer);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.jmx.remoteServerAddr", from.remoteServerAddr, this.remoteServerAddr);

    AuditEventType.maybeAddPeriodChangeMessage(list, "dsEdit.updatePeriod", from.updatePeriodType, from.updatePeriods, this.updatePeriodType, this.updatePeriods);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.quantize", from.quantize, this.quantize);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    out.writeBoolean(this.useLocalServer);
    SerializationHelper.writeSafeUTF(out, this.remoteServerAddr);
    out.writeInt(this.updatePeriodType);
    out.writeInt(this.updatePeriods);
    out.writeBoolean(this.quantize);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.useLocalServer = in.readBoolean();
      this.remoteServerAddr = SerializationHelper.readSafeUTF(in);
      this.updatePeriodType = in.readInt();
      this.updatePeriods = in.readInt();
      this.quantize = in.readBoolean();
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
    EVENT_CODES.addElement(1, "DATA_SOURCE_EXCEPTION");
    EVENT_CODES.addElement(2, "POINT_READ_EXCEPTION");
    EVENT_CODES.addElement(3, "POINT_WRITE_EXCEPTION");
  }
}