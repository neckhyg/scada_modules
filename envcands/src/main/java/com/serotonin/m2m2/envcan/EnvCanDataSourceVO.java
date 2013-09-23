package com.serotonin.m2m2.envcan;

import com.serotonin.json.spi.JsonProperty;
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

public class EnvCanDataSourceVO extends DataSourceVO<EnvCanDataSourceVO>
{
  private static final ExportCodes EVENT_CODES = new ExportCodes();

  @JsonProperty
  private int stationId;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

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
    return new TranslatableMessage("envcands.dsconn", new Object[] { Integer.valueOf(this.stationId) });
  }

  public DataSourceRT createDataSourceRT()
  {
    return new EnvCanDataSourceRT(this);
  }

  public EnvCanPointLocatorVO createPointLocator()
  {
    return new EnvCanPointLocatorVO();
  }

  public int getStationId()
  {
    return this.stationId;
  }

  public void setStationId(int stationId) {
    this.stationId = stationId;
  }

  public void validate(ProcessResult response)
  {
    super.validate(response);

    if (this.stationId < 1)
      response.addContextualMessage("stationId", "validate.greaterThanZero", new Object[] { Integer.valueOf(this.stationId) });
  }

  protected void addPropertiesImpl(List<TranslatableMessage> list)
  {
    AuditEventType.addPropertyMessage(list, "envcands.stationId", Integer.valueOf(this.stationId));
  }

  protected void addPropertyChangesImpl(List<TranslatableMessage> list, EnvCanDataSourceVO from)
  {
    AuditEventType.maybeAddPropertyChangeMessage(list, "envcands.stationId", from.stationId, this.stationId);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    out.writeInt(this.stationId);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1)
      this.stationId = in.readInt();
  }

  static
  {
    EVENT_CODES.addElement(1, "DATA_RETRIEVAL_FAILURE_EVENT");
    EVENT_CODES.addElement(2, "PARSE_EXCEPTION");
  }
}