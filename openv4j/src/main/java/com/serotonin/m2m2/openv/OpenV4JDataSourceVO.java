package com.serotonin.m2m2.openv;

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
import com.serotonin.m2m2.vo.dataSource.PointLocatorVO;
import com.serotonin.m2m2.vo.event.EventTypeVO;
import com.serotonin.util.SerializationHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import net.sf.openv4j.DataPoint;
import net.sf.openv4j.Devices;
import net.sf.openv4j.Group;
import net.sf.openv4j.Protocol;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OpenV4JDataSourceVO extends DataSourceVO<OpenV4JDataSourceVO>
{
  private static final Log LOG = LogFactory.getLog(OpenV4JDataSourceVO.class);
  private static final ExportCodes EVENT_CODES = new ExportCodes();

  @JsonProperty
  private String commPortId;
  private int updatePeriodType = 2;

  @JsonProperty
  private int updatePeriods = 1;

  @JsonProperty
  private Devices device;

  @JsonProperty
  private Protocol protocol;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

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
    return new OpenV4JPointLocatorVO();
  }

  public DataSourceRT createDataSourceRT()
  {
    return new OpenV4JDataSourceRT(this);
  }

  public ExportCodes getEventCodes()
  {
    return EVENT_CODES;
  }

  protected void addPropertiesImpl(List<TranslatableMessage> list)
  {
    AuditEventType.addPropertyMessage(list, "dsEdit.openv4j.port", this.commPortId);
    AuditEventType.addPeriodMessage(list, "dsEdit.updatePeriod", this.updatePeriodType, this.updatePeriods);
  }

  protected void addPropertyChangesImpl(List<TranslatableMessage> list, OpenV4JDataSourceVO from)
  {
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.openv4j.port", from.commPortId, this.commPortId);
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
    out.writeInt(1);
    out.writeInt(this.updatePeriodType);
    out.writeInt(this.updatePeriods);
    SerializationHelper.writeSafeUTF(out, this.commPortId);
    SerializationHelper.writeSafeUTF(out, this.device.name());
    SerializationHelper.writeSafeUTF(out, this.protocol.name());
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    switch (ver) {
    case 1:
      this.updatePeriodType = in.readInt();
      this.updatePeriods = in.readInt();
      this.commPortId = SerializationHelper.readSafeUTF(in);
      this.device = Devices.valueOf(SerializationHelper.readSafeUTF(in));
      this.protocol = Protocol.valueOf(SerializationHelper.readSafeUTF(in));
      break;
    case 2:
      this.updatePeriodType = in.readInt();
      this.updatePeriods = in.readInt();
      this.commPortId = SerializationHelper.readSafeUTF(in);
      this.device = Devices.valueOf(SerializationHelper.readSafeUTF(in));
      this.protocol = Protocol.valueOf(SerializationHelper.readSafeUTF(in));
    }
  }

  public void jsonWrite(ObjectWriter writer)
    throws IOException, JsonException
  {
    super.jsonWrite(writer);
    writeUpdatePeriodType(writer, this.updatePeriodType);
  }

  public void jsonRead(JsonReader reader, JsonObject jsonObject) throws JsonException
  {
    LOG.info("WRITE TO JSON");
    super.jsonRead(reader, jsonObject);
    LOG.info("SUPER TO JSON");
    Integer value = readUpdatePeriodType(jsonObject);
    if (value != null)
      this.updatePeriodType = value.intValue();
    LOG.info("JSON OK");
  }

  public Devices getDevice()
  {
    return this.device;
  }

  public void setDevice(Devices device)
  {
    this.device = device;
  }

  public Devices[] getDevices()
  {
    return Devices.values();
  }

  public DataPoint[] getProperties(Group g)
  {
    return DataPoint.values();
  }

  public Protocol getProtocol()
  {
    return this.protocol;
  }

  public void setProtocol(Protocol protocol)
  {
    this.protocol = protocol;
  }

  public Protocol[] getProtocols()
  {
    return Protocol.values();
  }

  public Group[] getGroups() {
    return Group.values();
  }

  public DataPoint[] getDataPoints() {
    return DataPoint.values();
  }

  static
  {
    EVENT_CODES.addElement(1, "DATA_SOURCE_EXCEPTION");
    EVENT_CODES.addElement(2, "POINT_READ_EXCEPTION");
    EVENT_CODES.addElement(3, "POINT_WRITE_EXCEPTION");
  }
}