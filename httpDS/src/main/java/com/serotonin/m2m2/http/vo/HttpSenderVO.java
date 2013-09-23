package com.serotonin.m2m2.http.vo;

import com.serotonin.db.KeyValuePair;
import com.serotonin.db.pair.StringStringPair;
import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.http.rt.HttpSenderRT;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableJsonException;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.publish.PublisherRT;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.vo.event.EventTypeVO;
import com.serotonin.m2m2.vo.publish.PublisherVO;
import com.serotonin.util.SerializationHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;

public class HttpSenderVO extends PublisherVO<HttpPointVO>
{
  private static final ExportCodes EVENT_CODES = new ExportCodes();
  public static final int DATE_FORMAT_BASIC = 1;
  public static final int DATE_FORMAT_TZ = 2;
  public static final int DATE_FORMAT_UTC = 3;
  private static ExportCodes DATE_FORMAT_CODES;

  @JsonProperty
  private String url;

  @JsonProperty
  private boolean usePost;

  @JsonProperty
  private List<StringStringPair> staticHeaders = new ArrayList();

  @JsonProperty
  private List<StringStringPair> staticParameters = new ArrayList();

  @JsonProperty
  private boolean raiseResultWarning = true;

  private int dateFormat = 1;
  private static final long serialVersionUID = -1L;
  private static final int version = 4;

  protected void getEventTypesImpl(List<EventTypeVO> eventTypes)
  {
    eventTypes.add(new EventTypeVO("PUBLISHER", null, getId(), 11, new TranslatableMessage("event.pb.httpSend"), 2));

    eventTypes.add(new EventTypeVO("PUBLISHER", null, getId(), 12, new TranslatableMessage("event.pb.resultWarnings"), 1));
  }

  public ExportCodes getEventCodes()
  {
    return EVENT_CODES;
  }

  public TranslatableMessage getConfigDescription()
  {
    return new TranslatableMessage("common.default", new Object[] { this.url });
  }

  public PublisherRT<HttpPointVO> createPublisherRT()
  {
    return new HttpSenderRT(this);
  }

  protected HttpPointVO createPublishedPointInstance()
  {
    return new HttpPointVO();
  }

  public String getUrl()
  {
    return this.url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public boolean isUsePost() {
    return this.usePost;
  }

  public void setUsePost(boolean usePost) {
    this.usePost = usePost;
  }

  public List<StringStringPair> getStaticHeaders() {
    return this.staticHeaders;
  }

  public void setStaticHeaders(List<StringStringPair> staticHeaders) {
    this.staticHeaders = staticHeaders;
  }

  public List<StringStringPair> getStaticParameters() {
    return this.staticParameters;
  }

  public void setStaticParameters(List<StringStringPair> staticParameters) {
    this.staticParameters = staticParameters;
  }

  public boolean isRaiseResultWarning() {
    return this.raiseResultWarning;
  }

  public void setRaiseResultWarning(boolean raiseResultWarning) {
    this.raiseResultWarning = raiseResultWarning;
  }

  public int getDateFormat() {
    return this.dateFormat;
  }

  public void setDateFormat(int dateFormat) {
    this.dateFormat = dateFormat;
  }

  public void validate(ProcessResult response)
  {
    super.validate(response);

    if (StringUtils.isBlank(this.url))
      response.addContextualMessage("url", "validate.required", new Object[0]);
    else {
      try {
        new URIBuilder(this.url);
      }
      catch (URISyntaxException e) {
        response.addContextualMessage("url", "httpSender.invalidUrl", new Object[0]);
      }
    }

    for (HttpPointVO point : this.points) {
      if (StringUtils.isBlank(point.getParameterName())) {
        response.addContextualMessage("points", "validate.parameterRequired", new Object[0]);
        break;
      }
    }

    if (!DATE_FORMAT_CODES.isValidId(this.dateFormat, new int[0]))
      response.addContextualMessage("dateFormat", "validate.invalidValue", new Object[0]);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(4);
    SerializationHelper.writeSafeUTF(out, this.url);
    out.writeBoolean(this.usePost);
    out.writeObject(this.staticHeaders);
    out.writeObject(this.staticParameters);
    out.writeBoolean(this.raiseResultWarning);
    out.writeInt(this.dateFormat);
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
  {
    int ver = in.readInt();

    if (ver == 1) {
      this.url = SerializationHelper.readSafeUTF(in);
      this.usePost = in.readBoolean();
      this.staticHeaders = convertList(in);
      this.staticParameters = convertList(in);
      this.raiseResultWarning = in.readBoolean();
      this.dateFormat = 1;
    }
    else if (ver == 2) {
      this.url = SerializationHelper.readSafeUTF(in);
      this.usePost = in.readBoolean();
      this.staticHeaders = convertList(in);
      this.staticParameters = convertList(in);
      this.raiseResultWarning = in.readBoolean();
      this.dateFormat = 1;
    }
    else if (ver == 3) {
      this.url = SerializationHelper.readSafeUTF(in);
      this.usePost = in.readBoolean();
      this.staticHeaders = convertList(in);
      this.staticParameters = convertList(in);
      this.raiseResultWarning = in.readBoolean();
      this.dateFormat = in.readInt();
    }
    else if (ver == 4) {
      this.url = SerializationHelper.readSafeUTF(in);
      this.usePost = in.readBoolean();
      this.staticHeaders = ((List)in.readObject());
      this.staticParameters = ((List)in.readObject());
      this.raiseResultWarning = in.readBoolean();
      this.dateFormat = in.readInt();
    }
  }

  public List<StringStringPair> convertList(ObjectInputStream in) throws IOException, ClassNotFoundException
  {
    List<KeyValuePair> old = (List)in.readObject();
    List list = new ArrayList();
    for (KeyValuePair kvp : old)
      list.add(new StringStringPair(kvp.getKey(), kvp.getValue()));
    return list;
  }

  public void jsonWrite(ObjectWriter writer) throws IOException, JsonException
  {
    super.jsonWrite(writer);
    writer.writeEntry("dateFormat", DATE_FORMAT_CODES.getCode(this.dateFormat));
  }

  public void jsonRead(JsonReader reader, JsonObject jsonObject) throws JsonException
  {
    super.jsonRead(reader, jsonObject);

    String text = jsonObject.getString("dateFormat");
    if (text != null) {
      this.dateFormat = DATE_FORMAT_CODES.getId(text, new int[0]);
      if (this.dateFormat == -1)
        throw new TranslatableJsonException("emport.error.invalid", new Object[] { "dateFormat", text, DATE_FORMAT_CODES.getCodeList(new int[0]) });
    }
  }

  static
  {
    PublisherVO.addDefaultEventCodes(EVENT_CODES);
    EVENT_CODES.addElement(11, "SEND_EXCEPTION_EVENT");
    EVENT_CODES.addElement(12, "RESULT_WARNINGS_EVENT");

    DATE_FORMAT_CODES = new ExportCodes();

    DATE_FORMAT_CODES.addElement(1, "DATE_FORMAT_BASIC", "publisherEdit.httpSender.dateFormat.basic");

    DATE_FORMAT_CODES.addElement(2, "DATE_FORMAT_TZ", "publisherEdit.httpSender.dateFormat.tz");
    DATE_FORMAT_CODES.addElement(3, "DATE_FORMAT_UTC", "publisherEdit.httpSender.dateFormat.utc");
  }
}