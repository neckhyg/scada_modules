package com.serotonin.m2m2.galil.vo;

import com.serotonin.json.spi.JsonEntity;
import com.serotonin.m2m2.galil.rt.CommandPointTypeRT;
import com.serotonin.m2m2.galil.rt.PointTypeRT;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

@JsonEntity
public class CommandPointTypeVO extends PointTypeVO
{
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  public PointTypeRT createRuntime()
  {
    return new CommandPointTypeRT(this);
  }

  public int typeId()
  {
    return 1;
  }

  public int getDataTypeId()
  {
    return 4;
  }

  public TranslatableMessage getDescription()
  {
    return new TranslatableMessage("dsEdit.galil.pointType.command");
  }

  public boolean isSettable()
  {
    return true;
  }

  public void validate(ProcessResult response)
  {
  }

  public void addProperties(List<TranslatableMessage> list)
  {
  }

  public void addPropertyChanges(List<TranslatableMessage> list, Object from)
  {
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