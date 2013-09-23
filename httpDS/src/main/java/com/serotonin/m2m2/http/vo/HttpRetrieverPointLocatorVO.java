package com.serotonin.m2m2.http.vo;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.spi.JsonSerializable;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.DataTypes;
import com.serotonin.m2m2.db.dao.DataSourceDao;
import com.serotonin.m2m2.http.rt.HttpRetrieverPointLocatorRT;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.m2m2.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.web.taglib.Functions;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.commons.lang3.StringUtils;

public class HttpRetrieverPointLocatorVO extends AbstractPointLocatorVO
  implements JsonSerializable
{

  @JsonProperty
  private String valueRegex;

  @JsonProperty
  private boolean ignoreIfMissing;

  @JsonProperty
  private String valueFormat;
  private int dataTypeId;

  @JsonProperty
  private String timeRegex;

  @JsonProperty
  private String timeFormat;

  @JsonProperty
  private boolean settable;

  @JsonProperty
  private String setPointName;
  private static final long serialVersionUID = -1L;
  private static final int version = 2;

  public PointLocatorRT createRuntime()
  {
    return new HttpRetrieverPointLocatorRT(this);
  }

  public TranslatableMessage getConfigurationDescription()
  {
    return new TranslatableMessage("dsEdit.httpRetriever.dpconn", new Object[] { Functions.escapeLessThan(this.valueRegex) });
  }

  public String getValueRegex()
  {
    return this.valueRegex;
  }

  public void setValueRegex(String valueRegex) {
    this.valueRegex = valueRegex;
  }

  public boolean isIgnoreIfMissing() {
    return this.ignoreIfMissing;
  }

  public void setIgnoreIfMissing(boolean ignoreIfMissing) {
    this.ignoreIfMissing = ignoreIfMissing;
  }

  public String getValueFormat() {
    return this.valueFormat;
  }

  public void setValueFormat(String valueFormat) {
    this.valueFormat = valueFormat;
  }

  public int getDataTypeId()
  {
    return this.dataTypeId;
  }

  public void setDataTypeId(int dataTypeId) {
    this.dataTypeId = dataTypeId;
  }

  public String getTimeRegex() {
    return this.timeRegex;
  }

  public void setTimeRegex(String timeRegex) {
    this.timeRegex = timeRegex;
  }

  public String getTimeFormat() {
    return this.timeFormat;
  }

  public void setTimeFormat(String timeFormat) {
    this.timeFormat = timeFormat;
  }

  public boolean isSettable()
  {
    return this.settable;
  }

  public void setSettable(boolean settable) {
    this.settable = settable;
  }

  public String getSetPointName() {
    return this.setPointName;
  }

  public void setSetPointName(String setPointName) {
    this.setPointName = setPointName;
  }

  public void validate(ProcessResult response, DataPointVO dpvo)
  {
    if (!this.settable) {
      if (StringUtils.isBlank(this.valueRegex))
        response.addContextualMessage("valueRegex", "validate.required", new Object[0]);
      else {
        try {
          Pattern pattern = Pattern.compile(this.valueRegex);
          if (pattern.matcher("").groupCount() < 1)
            response.addContextualMessage("valueRegex", "validate.captureGroup", new Object[0]);
        }
        catch (PatternSyntaxException e) {
          response.addContextualMessage("valueRegex", "common.default", new Object[] { e.getMessage() });
        }
      }
    }

    if ((this.dataTypeId == 3) && (!StringUtils.isBlank(this.valueFormat))) {
      try {
        new DecimalFormat(this.valueFormat);
      }
      catch (IllegalArgumentException e) {
        response.addContextualMessage("valueFormat", "common.default", new Object[] { e.getMessage() });
      }
    }

    if (!DataTypes.CODES.isValidId(this.dataTypeId, new int[0])) {
      response.addContextualMessage("dataTypeId", "validate.invalidValue", new Object[0]);
    }
    if (!StringUtils.isBlank(this.timeRegex)) {
      try {
        Pattern pattern = Pattern.compile(this.timeRegex);
        if (pattern.matcher("").groupCount() < 1)
          response.addContextualMessage("timeRegex", "validate.captureGroup", new Object[0]);
      }
      catch (PatternSyntaxException e) {
        response.addContextualMessage("timeRegex", "common.default", new Object[] { e.getMessage() });
      }

      if (StringUtils.isBlank(this.timeFormat))
        response.addContextualMessage("timeFormat", "validate.required", new Object[0]);
      else {
        try {
          new SimpleDateFormat(this.timeFormat);
        }
        catch (IllegalArgumentException e) {
          response.addContextualMessage("timeFormat", "common.default", new Object[] { e.getMessage() });
        }
      }
    }

    if (this.settable) {
      if (StringUtils.isBlank(this.setPointName)) {
        response.addContextualMessage("setPointName", "validate.required", new Object[0]);
      }
      HttpRetrieverDataSourceVO ds = (HttpRetrieverDataSourceVO)new DataSourceDao().getDataSource(dpvo.getDataSourceId());

      if (StringUtils.isBlank(ds.getSetPointUrl()))
        response.addContextualMessage("setPointName", "http.validate.noSetPointUrl", new Object[0]);
    }
  }

  public void validate(ProcessResult response)
  {
    throw new ShouldNeverHappenException("Should not have been called");
  }

  public void addProperties(List<TranslatableMessage> list)
  {
    AuditEventType.addDataTypeMessage(list, "dsEdit.pointDataType", this.dataTypeId);
    AuditEventType.addPropertyMessage(list, "dsEdit.httpRetriever.valueRegex", this.valueRegex);
    AuditEventType.addPropertyMessage(list, "dsEdit.httpRetriever.ignoreIfMissing", this.ignoreIfMissing);
    AuditEventType.addPropertyMessage(list, "dsEdit.httpRetriever.numberFormat", this.valueFormat);
    AuditEventType.addPropertyMessage(list, "dsEdit.httpRetriever.timeRegex", this.timeRegex);
    AuditEventType.addPropertyMessage(list, "dsEdit.settable", this.settable);
    AuditEventType.addPropertyMessage(list, "http.dsEdit.setPointName", this.setPointName);
  }

  public void addPropertyChanges(List<TranslatableMessage> list, Object o)
  {
    HttpRetrieverPointLocatorVO from = (HttpRetrieverPointLocatorVO)o;
    AuditEventType.maybeAddDataTypeChangeMessage(list, "dsEdit.pointDataType", from.dataTypeId, this.dataTypeId);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.httpRetriever.valueRegex", from.valueRegex, this.valueRegex);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.httpRetriever.ignoreIfMissing", from.ignoreIfMissing, this.ignoreIfMissing);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.httpRetriever.numberFormat", from.valueFormat, this.valueFormat);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.httpRetriever.timeRegex", from.timeRegex, this.timeRegex);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.httpRetriever.timeFormat", from.timeFormat, this.timeFormat);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.settable", from.settable, this.settable);
    AuditEventType.maybeAddPropertyChangeMessage(list, "http.dsEdit.setPointName", from.setPointName, this.setPointName);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(2);
    SerializationHelper.writeSafeUTF(out, this.valueRegex);
    out.writeBoolean(this.ignoreIfMissing);
    out.writeInt(this.dataTypeId);
    SerializationHelper.writeSafeUTF(out, this.valueFormat);
    SerializationHelper.writeSafeUTF(out, this.timeRegex);
    SerializationHelper.writeSafeUTF(out, this.timeFormat);
    out.writeBoolean(this.settable);
    SerializationHelper.writeSafeUTF(out, this.setPointName);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.valueRegex = SerializationHelper.readSafeUTF(in);
      this.ignoreIfMissing = in.readBoolean();
      this.dataTypeId = in.readInt();
      this.valueFormat = SerializationHelper.readSafeUTF(in);
      this.timeRegex = SerializationHelper.readSafeUTF(in);
      this.timeFormat = SerializationHelper.readSafeUTF(in);
      this.settable = false;
      this.setPointName = null;
    }
    else if (ver == 2) {
      this.valueRegex = SerializationHelper.readSafeUTF(in);
      this.ignoreIfMissing = in.readBoolean();
      this.dataTypeId = in.readInt();
      this.valueFormat = SerializationHelper.readSafeUTF(in);
      this.timeRegex = SerializationHelper.readSafeUTF(in);
      this.timeFormat = SerializationHelper.readSafeUTF(in);
      this.settable = in.readBoolean();
      this.setPointName = SerializationHelper.readSafeUTF(in);
    }
  }

  public void jsonWrite(ObjectWriter writer) throws IOException, JsonException
  {
    writeDataType(writer);
  }

  public void jsonRead(JsonReader reader, JsonObject jsonObject) throws JsonException
  {
    Integer value = readDataType(jsonObject, new int[] { 5 });
    if (value != null)
      this.dataTypeId = value.intValue();
  }
}