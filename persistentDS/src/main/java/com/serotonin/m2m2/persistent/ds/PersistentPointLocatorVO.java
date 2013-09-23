package com.serotonin.m2m2.persistent.ds;

import com.serotonin.json.spi.JsonEntity;
import com.serotonin.m2m2.DataTypes;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.vo.dataSource.AbstractPointLocatorVO;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

@JsonEntity
public class PersistentPointLocatorVO extends AbstractPointLocatorVO
{
  private int dataTypeId;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  public PointLocatorRT createRuntime()
  {
    return new PersistentPointLocatorRT(this);
  }

  public TranslatableMessage getConfigurationDescription()
  {
    return new TranslatableMessage("common.noMessage");
  }

  public int getDataTypeId()
  {
    return this.dataTypeId;
  }

  public void setDataTypeId(int dataTypeId) {
    this.dataTypeId = dataTypeId;
  }

  public boolean isSettable()
  {
    return false;
  }

  public void validate(ProcessResult response)
  {
    if (!DataTypes.CODES.isValidId(this.dataTypeId, new int[0]))
      response.addContextualMessage("dataTypeId", "validate.invalidValue", new Object[0]);
  }

  public void addProperties(List<TranslatableMessage> list)
  {
  }

  public void addPropertyChanges(List<TranslatableMessage> list, Object o)
  {
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    out.writeInt(this.dataTypeId);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1)
      this.dataTypeId = in.readInt();
  }
}