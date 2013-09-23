package com.serotonin.m2m2.galil.vo;

import com.serotonin.json.spi.JsonProperty;
import com.serotonin.m2m2.galil.rt.OutputPointTypeRT;
import com.serotonin.m2m2.galil.rt.PointTypeRT;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class OutputPointTypeVO extends PointTypeVO
{

  @JsonProperty
  private int outputId = 1;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  public PointTypeRT createRuntime()
  {
    return new OutputPointTypeRT(this);
  }

  public int typeId()
  {
    return 3;
  }

  public int getDataTypeId()
  {
    return 1;
  }

  public TranslatableMessage getDescription()
  {
    return new TranslatableMessage("dsEdit.galil.pointType.output");
  }

  public boolean isSettable()
  {
    return true;
  }

  public void validate(ProcessResult response)
  {
    if ((this.outputId < 1) || (this.outputId > 80))
      response.addContextualMessage("outputPointType.outputId", "validate.between", new Object[] { Integer.valueOf(1), Integer.valueOf(80) });
  }

  public int getOutputId() {
    return this.outputId;
  }

  public void setOutputId(int outputId) {
    this.outputId = outputId;
  }

  public void addProperties(List<TranslatableMessage> list)
  {
    AuditEventType.addPropertyMessage(list, "dsEdit.galil.outputNumber", Integer.valueOf(this.outputId));
  }

  public void addPropertyChanges(List<TranslatableMessage> list, Object o)
  {
    OutputPointTypeVO from = (OutputPointTypeVO)o;
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.galil.outputNumber", from.outputId, this.outputId);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    out.writeInt(this.outputId);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1)
      this.outputId = in.readInt();
  }
}