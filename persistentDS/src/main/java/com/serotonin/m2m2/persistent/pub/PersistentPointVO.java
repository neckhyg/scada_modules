package com.serotonin.m2m2.persistent.pub;

import com.serotonin.json.spi.JsonEntity;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.m2m2.vo.publish.PublishedPointVO;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@JsonEntity
public class PersistentPointVO extends PublishedPointVO
{
  private int index;
  private DataPointVO vo;
  private byte[] serializedDataPoint;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  public int getIndex()
  {
    return this.index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public DataPointVO getVo() {
    return this.vo;
  }

  public void setVo(DataPointVO vo) {
    this.vo = vo;
  }

  public byte[] getSerializedDataPoint() {
    return this.serializedDataPoint;
  }

  public void setSerializedDataPoint(byte[] serializedDataPoint) {
    this.serializedDataPoint = serializedDataPoint;
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1);
  }
}