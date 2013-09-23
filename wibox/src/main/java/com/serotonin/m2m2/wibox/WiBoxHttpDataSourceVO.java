package com.serotonin.m2m2.wibox;

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
import org.apache.commons.lang3.StringUtils;

public class WiBoxHttpDataSourceVO extends DataSourceVO<WiBoxHttpDataSourceVO>
{
  private static final ExportCodes EVENT_CODES = new ExportCodes();

  @JsonProperty
  private String password;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  protected void addEventTypes(List<EventTypeVO> ets)
  {
    ets.add(createEventType(1, new TranslatableMessage("event.ds.dataParse")));

    ets.add(createEventType(2, new TranslatableMessage("dsEdit.wiboxHttp.dataConversionError")));
  }

  public ExportCodes getEventCodes()
  {
    return EVENT_CODES;
  }

  public TranslatableMessage getConnectionDescription()
  {
    return new TranslatableMessage("common.default", new Object[] { this.password });
  }

  public DataSourceRT createDataSourceRT()
  {
    return new WiBoxHttpDataSourceRT(this);
  }

  public WiBoxHttpPointLocatorVO createPointLocator()
  {
    return new WiBoxHttpPointLocatorVO();
  }

  public String getPassword()
  {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void validate(ProcessResult response)
  {
    super.validate(response);

    if (StringUtils.isEmpty(this.password))
      response.addContextualMessage("password", "validate.required", new Object[0]);
  }

  protected void addPropertiesImpl(List<TranslatableMessage> list)
  {
    AuditEventType.addPropertyMessage(list, "dsEdit.wiboxHttp.password", this.password);
  }

  protected void addPropertyChangesImpl(List<TranslatableMessage> list, WiBoxHttpDataSourceVO from)
  {
    AuditEventType.addPropertyChangeMessage(list, "dsEdit.wiboxHttp.password", from.password, this.password);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    SerializationHelper.writeSafeUTF(out, this.password);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1)
      this.password = SerializationHelper.readSafeUTF(in);
  }

  static
  {
    EVENT_CODES.addElement(1, "PARSE_EXCEPTION");
    EVENT_CODES.addElement(2, "DATA_CONVERSION_ERROR");
  }
}