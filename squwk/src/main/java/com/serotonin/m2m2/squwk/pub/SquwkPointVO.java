package com.serotonin.m2m2.squwk.pub;

import com.serotonin.json.spi.JsonProperty;
import com.serotonin.m2m2.vo.publish.PublishedPointVO;
import com.serotonin.squwk.client.vo.DataType;
import com.serotonin.util.SerializationHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SquwkPointVO extends PublishedPointVO
{

  @JsonProperty
  private String guid;
  private DataType dataType;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  public String getGuid()
  {
    return this.guid;
  }

  public void setGuid(String guid) {
    this.guid = guid;
  }

  public DataType getDataType() {
    return this.dataType;
  }

  public void setDataType(DataType dataType) {
    this.dataType = dataType;
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    SerializationHelper.writeSafeUTF(out, this.guid);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1)
      this.guid = SerializationHelper.readSafeUTF(in);
  }
}