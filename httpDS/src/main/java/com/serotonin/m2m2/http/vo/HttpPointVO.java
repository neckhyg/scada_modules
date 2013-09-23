package com.serotonin.m2m2.http.vo;

import com.serotonin.json.spi.JsonProperty;
import com.serotonin.m2m2.vo.publish.PublishedPointVO;
import com.serotonin.util.SerializationHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class HttpPointVO extends PublishedPointVO
{

  @JsonProperty
  private String parameterName;

  @JsonProperty
  private boolean includeTimestamp;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  public String getParameterName()
  {
    return this.parameterName;
  }

  public void setParameterName(String parameterName) {
    this.parameterName = parameterName;
  }

  public boolean isIncludeTimestamp() {
    return this.includeTimestamp;
  }

  public void setIncludeTimestamp(boolean includeTimestamp) {
    this.includeTimestamp = includeTimestamp;
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    SerializationHelper.writeSafeUTF(out, this.parameterName);
    out.writeBoolean(this.includeTimestamp);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.parameterName = SerializationHelper.readSafeUTF(in);
      this.includeTimestamp = in.readBoolean();
    }
  }
}