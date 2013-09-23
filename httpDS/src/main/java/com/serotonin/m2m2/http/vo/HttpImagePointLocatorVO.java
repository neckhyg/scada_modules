package com.serotonin.m2m2.http.vo;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.spi.JsonSerializable;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.http.rt.HttpImagePointLocatorRT;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableJsonException;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.util.SerializationHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class HttpImagePointLocatorVO extends AbstractPointLocatorVO
  implements JsonSerializable
{
  public static final int SCALE_TYPE_NONE = 0;
  public static final int SCALE_TYPE_PERCENT = 1;
  public static final int SCALE_TYPE_BOX = 2;
  private static final ExportCodes SCALE_TYPE_CODES = new ExportCodes();

  @JsonProperty
  private String url;

  @JsonProperty
  private int timeoutSeconds = 30;

  @JsonProperty
  private int retries = 2;
  private int scaleType;

  @JsonProperty
  private int scalePercent = 25;

  @JsonProperty
  private int scaleWidth = 100;

  @JsonProperty
  private int scaleHeight = 100;

  @JsonProperty
  private int readLimit = 10000;

  @JsonProperty
  private String webcamLiveFeedCode;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  public boolean isSettable()
  {
    return false;
  }

  public PointLocatorRT createRuntime()
  {
    return new HttpImagePointLocatorRT(this);
  }

  public TranslatableMessage getConfigurationDescription()
  {
    return new TranslatableMessage("common.default", new Object[] { this.url });
  }

  public String getUrl()
  {
    return this.url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public int getTimeoutSeconds() {
    return this.timeoutSeconds;
  }

  public void setTimeoutSeconds(int timeoutSeconds) {
    this.timeoutSeconds = timeoutSeconds;
  }

  public int getRetries() {
    return this.retries;
  }

  public void setRetries(int retries) {
    this.retries = retries;
  }

  public int getScaleType() {
    return this.scaleType;
  }

  public void setScaleType(int scaleType) {
    this.scaleType = scaleType;
  }

  public int getScalePercent() {
    return this.scalePercent;
  }

  public void setScalePercent(int scalePercent) {
    this.scalePercent = scalePercent;
  }

  public int getScaleWidth() {
    return this.scaleWidth;
  }

  public void setScaleWidth(int scaleWidth) {
    this.scaleWidth = scaleWidth;
  }

  public int getScaleHeight() {
    return this.scaleHeight;
  }

  public void setScaleHeight(int scaleHeight) {
    this.scaleHeight = scaleHeight;
  }

  public int getReadLimit() {
    return this.readLimit;
  }

  public void setReadLimit(int readLimit) {
    this.readLimit = readLimit;
  }

  public String getWebcamLiveFeedCode() {
    return this.webcamLiveFeedCode;
  }

  public void setWebcamLiveFeedCode(String webcamLiveFeedCode) {
    this.webcamLiveFeedCode = webcamLiveFeedCode;
  }

  public int getDataTypeId()
  {
    return 5;
  }

  public void validate(ProcessResult response)
  {
    if (StringUtils.isBlank(this.url))
      response.addContextualMessage("url", "validate.required", new Object[0]);
    if (this.timeoutSeconds <= 0)
      response.addContextualMessage("timeoutSeconds", "validate.greaterThanZero", new Object[0]);
    if (this.retries < 0)
      response.addContextualMessage("retries", "validate.cannotBeNegative", new Object[0]);
    if (!SCALE_TYPE_CODES.isValidId(this.scaleType, new int[0]))
      response.addContextualMessage("scaleType", "validate.invalidValue", new Object[0]);
    if (this.scaleType == 1) {
      if (this.scalePercent <= 0)
        response.addContextualMessage("scalePercent", "validate.greaterThanZero", new Object[0]);
      else if (this.scalePercent > 100)
        response.addContextualMessage("scalePercent", "validate.lessThan100", new Object[0]);
    }
    else if (this.scaleType == 2) {
      if (this.scaleWidth <= 0)
        response.addContextualMessage("scaleWidth", "validate.greaterThanZero", new Object[0]);
      if (this.scaleHeight <= 0)
        response.addContextualMessage("scaleHeight", "validate.greaterThanZero", new Object[0]);
    }
    if (this.readLimit <= 0)
      response.addContextualMessage("readLimit", "validate.greaterThanZero", new Object[0]);
  }

  public void addProperties(List<TranslatableMessage> list)
  {
    AuditEventType.addPropertyMessage(list, "dsEdit.httpImage.url", this.url);
    AuditEventType.addPropertyMessage(list, "dsEdit.httpImage.timeout", Integer.valueOf(this.timeoutSeconds));
    AuditEventType.addPropertyMessage(list, "dsEdit.httpImage.retries", Integer.valueOf(this.retries));
    AuditEventType.addExportCodeMessage(list, "dsEdit.httpImage.scalingType", SCALE_TYPE_CODES, this.scaleType);
    AuditEventType.addPropertyMessage(list, "dsEdit.httpImage.scalePercent", Integer.valueOf(this.scalePercent));
    AuditEventType.addPropertyMessage(list, "dsEdit.httpImage.scaleWidth", Integer.valueOf(this.scaleWidth));
    AuditEventType.addPropertyMessage(list, "dsEdit.httpImage.scaleHeight", Integer.valueOf(this.scaleHeight));
    AuditEventType.addPropertyMessage(list, "dsEdit.httpImage.readLimit", Integer.valueOf(this.readLimit));
    AuditEventType.addPropertyMessage(list, "dsEdit.httpImage.liveFeed", this.webcamLiveFeedCode);
  }

  public void addPropertyChanges(List<TranslatableMessage> list, Object o)
  {
    HttpImagePointLocatorVO from = (HttpImagePointLocatorVO)o;
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.httpImage.url", from.url, this.url);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.httpImage.timeout", from.timeoutSeconds, this.timeoutSeconds);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.httpImage.retries", from.retries, this.retries);
    AuditEventType.maybeAddExportCodeChangeMessage(list, "dsEdit.httpImage.scalingType", SCALE_TYPE_CODES, from.scaleType, this.scaleType);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.httpImage.scalePercent", from.scalePercent, this.scalePercent);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.httpImage.scaleWidth", from.scaleWidth, this.scaleWidth);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.httpImage.scaleHeight", from.scaleHeight, this.scaleHeight);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.httpImage.readLimit", from.readLimit, this.readLimit);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.httpImage.liveFeed", from.webcamLiveFeedCode, this.webcamLiveFeedCode);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    SerializationHelper.writeSafeUTF(out, this.url);
    out.writeInt(this.timeoutSeconds);
    out.writeInt(this.retries);
    out.writeInt(this.scaleType);
    out.writeInt(this.scalePercent);
    out.writeInt(this.scaleWidth);
    out.writeInt(this.scaleHeight);
    out.writeInt(this.readLimit);
    SerializationHelper.writeSafeUTF(out, this.webcamLiveFeedCode);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.url = SerializationHelper.readSafeUTF(in);
      this.timeoutSeconds = in.readInt();
      this.retries = in.readInt();
      this.scaleType = in.readInt();
      this.scalePercent = in.readInt();
      this.scaleWidth = in.readInt();
      this.scaleHeight = in.readInt();
      this.readLimit = in.readInt();
      this.webcamLiveFeedCode = SerializationHelper.readSafeUTF(in);
    }
  }

  public void jsonWrite(ObjectWriter writer) throws IOException, JsonException
  {
    writer.writeEntry("scaleType", SCALE_TYPE_CODES.getCode(this.scaleType));
  }

  public void jsonRead(JsonReader reader, JsonObject jsonObject) throws JsonException
  {
    String text = jsonObject.getString("scaleType");
    if (text != null) {
      this.scaleType = SCALE_TYPE_CODES.getId(text, new int[0]);
      if (this.scaleType == -1)
        throw new TranslatableJsonException("emport.error.invalid", new Object[] { "scaleType", text, SCALE_TYPE_CODES });
    }
  }

  static
  {
    SCALE_TYPE_CODES.addElement(0, "SCALE_TYPE_NONE", "dsEdit.httpImage.scalingType.none");
    SCALE_TYPE_CODES.addElement(1, "SCALE_TYPE_PERCENT", "dsEdit.httpImage.scalingType.percent");
    SCALE_TYPE_CODES.addElement(2, "SCALE_TYPE_BOX", "dsEdit.httpImage.scalingType.box");
  }
}