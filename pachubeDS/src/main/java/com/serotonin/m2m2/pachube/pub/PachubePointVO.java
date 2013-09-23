package com.serotonin.m2m2.pachube.pub;

import com.serotonin.json.spi.JsonProperty;
import com.serotonin.m2m2.vo.publish.PublishedPointVO;
import com.serotonin.util.SerializationHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PachubePointVO extends PublishedPointVO
{

  @JsonProperty
  private int feedId;

  @JsonProperty
  private String dataStreamId;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  public int getFeedId()
  {
    return this.feedId;
  }

  public void setFeedId(int feedId) {
    this.feedId = feedId;
  }

  public String getDataStreamId() {
    return this.dataStreamId;
  }

  public void setDataStreamId(String dataStreamId) {
    this.dataStreamId = dataStreamId;
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    out.writeInt(this.feedId);
    SerializationHelper.writeSafeUTF(out, this.dataStreamId);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.feedId = in.readInt();
      this.dataStreamId = SerializationHelper.readSafeUTF(in);
    }
  }
}