package com.serotonin.m2m2.vmstat;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableJsonException;
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

public class VMStatDataSourceVO extends DataSourceVO<VMStatDataSourceVO>
{
  private static final ExportCodes EVENT_CODES = new ExportCodes();
  public static final ExportCodes OUTPUT_SCALE_CODES;

  @JsonProperty
  private int pollSeconds = 60;

  private int outputScale = 1;
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
    return new TranslatableMessage("dsEdit.vmstat.dsconn", new Object[] { Integer.valueOf(this.pollSeconds) });
  }

  public DataSourceRT createDataSourceRT()
  {
    return new VMStatDataSourceRT(this);
  }

  public VMStatPointLocatorVO createPointLocator()
  {
    return new VMStatPointLocatorVO();
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

  public void validate(ProcessResult response)
  {
    super.validate(response);

    if (this.pollSeconds < 1) {
      response.addContextualMessage("pollSeconds", "validate.greaterThanZero", new Object[] { Integer.valueOf(this.pollSeconds) });
    }
    if (!OUTPUT_SCALE_CODES.isValidId(this.outputScale, new int[0]))
      response.addContextualMessage("outputScale", "validate.invalidValue", new Object[0]);
  }

  protected void addPropertiesImpl(List<TranslatableMessage> list)
  {
    AuditEventType.addPropertyMessage(list, "dsEdit.vmstat.pollSeconds", Integer.valueOf(this.pollSeconds));
    AuditEventType.addExportCodeMessage(list, "dsEdit.vmstat.outputScale", OUTPUT_SCALE_CODES, this.outputScale);
  }

  protected void addPropertyChangesImpl(List<TranslatableMessage> list, VMStatDataSourceVO from)
  {
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.vmstat.pollSeconds", from.pollSeconds, this.pollSeconds);
    AuditEventType.maybeAddExportCodeChangeMessage(list, "dsEdit.vmstat.outputScale", OUTPUT_SCALE_CODES, from.outputScale, this.outputScale);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    out.writeInt(this.pollSeconds);
    out.writeInt(this.outputScale);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.pollSeconds = in.readInt();
      this.outputScale = in.readInt();
    }
  }

  public void jsonWrite(ObjectWriter writer) throws IOException, JsonException
  {
    super.jsonWrite(writer);
    writer.writeEntry("outputScale", OUTPUT_SCALE_CODES.getCode(this.outputScale));
  }

  public void jsonRead(JsonReader reader, JsonObject jsonObject) throws JsonException
  {
    super.jsonRead(reader, jsonObject);

    String text = jsonObject.getString("outputScale");
    if (text != null) {
      this.outputScale = OUTPUT_SCALE_CODES.getId(text, new int[0]);
      if (this.outputScale == -1)
        throw new TranslatableJsonException("emport.error.invalid", new Object[] { "outputScale", text, OUTPUT_SCALE_CODES.getCodeList(new int[0]) });
    }
  }

  static
  {
    EVENT_CODES.addElement(1, "DATA_SOURCE_EXCEPTION");
    EVENT_CODES.addElement(2, "PARSE_EXCEPTION");

    OUTPUT_SCALE_CODES = new ExportCodes();

    OUTPUT_SCALE_CODES.addElement(1, "NONE", "dsEdit.vmstat.scale.none");
    OUTPUT_SCALE_CODES.addElement(2, "LOWER_K", "dsEdit.vmstat.scale.k");
    OUTPUT_SCALE_CODES.addElement(3, "UPPER_K", "dsEdit.vmstat.scale.K");
    OUTPUT_SCALE_CODES.addElement(4, "LOWER_M", "dsEdit.vmstat.scale.m");
    OUTPUT_SCALE_CODES.addElement(5, "UPPER_M", "dsEdit.vmstat.scale.M");
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