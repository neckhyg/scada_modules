package com.serotonin.ma.ascii.file;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.DataSourceRT;
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

public class FileDataSourceVO extends DataSourceVO<FileDataSourceVO>
{
  private static final ExportCodes EVENT_CODES = new ExportCodes();

  private int updatePeriodType = 1;

  @JsonProperty
  private int updatePeriods = 1;

  @JsonProperty
  private String filePath = "";

  @JsonProperty
  private boolean quantize;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  protected void addEventTypes(List<EventTypeVO> ets)
  {
    ets.add(createEventType(1, new TranslatableMessage("event.ds.pointRead")));

    ets.add(createEventType(2, new TranslatableMessage("event.ds.dataSource")));
  }

  public ExportCodes getEventCodes()
  {
    return EVENT_CODES;
  }

  public TranslatableMessage getConnectionDescription()
  {
    return new TranslatableMessage("common.default", new Object[] { this.filePath });
  }

  public DataSourceRT createDataSourceRT()
  {
    return new FileDataSourceRT(this);
  }

  public PointLocatorVO createPointLocator()
  {
    return new FilePointLocatorVO();
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

  public String getFilePath() {
    return this.filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
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
    if (StringUtils.isBlank(this.filePath))
      response.addContextualMessage("filePath", "validate.required", new Object[0]);
  }

  protected void addPropertiesImpl(List<TranslatableMessage> list)
  {
  }

  protected void addPropertyChangesImpl(List<TranslatableMessage> list, FileDataSourceVO from)
  {
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    out.writeInt(this.updatePeriodType);
    out.writeInt(this.updatePeriods);
    SerializationHelper.writeSafeUTF(out, this.filePath);
    out.writeBoolean(this.quantize);
  }

  private void readObject(ObjectInputStream in) throws IOException
  {
    int ver = in.readInt();
    if (ver == 1) {
      this.updatePeriodType = in.readInt();
      this.updatePeriods = in.readInt();
      this.filePath = SerializationHelper.readSafeUTF(in);
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
    EVENT_CODES.addElement(2, "DATA_SOURCE_EXCEPTION");
    EVENT_CODES.addElement(1, "POINT_READ_EXCEPTION");
  }
}