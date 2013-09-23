package com.serotonin.m2m2.persistent.ds;

import com.serotonin.json.spi.JsonProperty;
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

public class PersistentDataSourceVO extends DataSourceVO<PersistentDataSourceVO>
{
  private static final ExportCodes EVENT_CODES = new ExportCodes();

  @JsonProperty
  private int port;

  @JsonProperty
  private String authorizationKey;

  @JsonProperty
  private boolean acceptPointUpdates;
  private static final long serialVersionUID = -1L;
  private static final int version = 2;

  protected void addEventTypes(List<EventTypeVO> ets)
  {
    ets.add(createEventType(1, new TranslatableMessage("event.ds.dataSource")));
  }

  public ExportCodes getEventCodes()
  {
    return EVENT_CODES;
  }

  public TranslatableMessage getConnectionDescription()
  {
    return new TranslatableMessage("dsEdit.persistent.dsconn", new Object[] { Integer.valueOf(this.port) });
  }

  public DataSourceRT createDataSourceRT()
  {
    return new PersistentDataSourceRT(this);
  }

  public PersistentPointLocatorVO createPointLocator()
  {
    return new PersistentPointLocatorVO();
  }

  public int getPort()
  {
    return this.port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getAuthorizationKey() {
    return this.authorizationKey;
  }

  public void setAuthorizationKey(String authorizationKey) {
    this.authorizationKey = authorizationKey;
  }

  public boolean isAcceptPointUpdates() {
    return this.acceptPointUpdates;
  }

  public void setAcceptPointUpdates(boolean acceptPointUpdates) {
    this.acceptPointUpdates = acceptPointUpdates;
  }

  public void validate(ProcessResult response)
  {
    super.validate(response);
    if ((this.port <= 0) || (this.port > 65535))
      response.addContextualMessage("port", "validate.invalidValue", new Object[0]);
  }

  protected void addPropertiesImpl(List<TranslatableMessage> list)
  {
    AuditEventType.addPropertyMessage(list, "dsEdit.persistent.port", Integer.valueOf(this.port));
    AuditEventType.addPropertyMessage(list, "dsEdit.persistent.authorizationKey", this.authorizationKey);
    AuditEventType.addPropertyMessage(list, "dsEdit.persistent.acceptPointUpdates", this.acceptPointUpdates);
  }

  protected void addPropertyChangesImpl(List<TranslatableMessage> list, PersistentDataSourceVO from)
  {
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.persistent.port", from.port, this.port);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.persistent.authorizationKey", from.authorizationKey, this.authorizationKey);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.persistent.acceptPointUpdates", from.acceptPointUpdates, this.acceptPointUpdates);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(2);
    out.writeInt(this.port);
    SerializationHelper.writeSafeUTF(out, this.authorizationKey);
    out.writeBoolean(this.acceptPointUpdates);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.port = in.readInt();
      this.authorizationKey = SerializationHelper.readSafeUTF(in);
      this.acceptPointUpdates = false;
    }
    else if (ver == 2) {
      this.port = in.readInt();
      this.authorizationKey = SerializationHelper.readSafeUTF(in);
      this.acceptPointUpdates = in.readBoolean();
    }
  }

  static
  {
    EVENT_CODES.addElement(1, "DATA_SOURCE_EXCEPTION_EVENT");
  }
}