package com.serotonin.m2m2.galil.vo;

import com.serotonin.json.spi.JsonProperty;
import com.serotonin.m2m2.galil.rt.PointTypeRT;
import com.serotonin.m2m2.galil.rt.TellPositionPointTypeRT;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.util.SerializationHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class TellPositionPointTypeVO extends PointTypeVO
{

  @JsonProperty
  private String axis;

  @JsonProperty
  private double scaleRawLow = 0.0D;

  @JsonProperty
  private double scaleRawHigh = 1.0D;

  @JsonProperty
  private double scaleEngLow = 0.0D;

  @JsonProperty
  private double scaleEngHigh = 1.0D;

  @JsonProperty
  private boolean roundToInteger;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  public PointTypeRT createRuntime() { return new TellPositionPointTypeRT(this);
  }

  public int typeId()
  {
    return 4;
  }

  public int getDataTypeId()
  {
    return 3;
  }

  public TranslatableMessage getDescription()
  {
    return new TranslatableMessage("dsEdit.galil.pointType.tellPosition");
  }

  public boolean isSettable()
  {
    return false;
  }

  public void validate(ProcessResult response)
  {
    if ((!"A".equals(this.axis)) && (!"B".equals(this.axis)) && (!"C".equals(this.axis)) && (!"D".equals(this.axis)) && (!"E".equals(this.axis)) && (!"F".equals(this.axis)) && (!"G".equals(this.axis)) && (!"H".equals(this.axis)))
    {
      response.addContextualMessage("tellPositionPointType.axis", "validate.axis.invalid", new Object[0]);
    }if (this.scaleRawHigh <= this.scaleRawLow)
      response.addContextualMessage("tellPositionPointType.scaleRawHighId", "validate.greaterThanRawLow", new Object[0]);
    if (this.scaleEngHigh <= this.scaleEngLow)
      response.addContextualMessage("tellPositionPointType.scaleEngHighId", "validate.greaterThanEngLow", new Object[0]);
  }

  public void setAxis(String axis) {
    this.axis = axis;
  }

  public String getAxis() {
    return this.axis;
  }

  public double getScaleRawLow() {
    return this.scaleRawLow;
  }

  public void setScaleRawLow(double scaleRawLow) {
    this.scaleRawLow = scaleRawLow;
  }

  public double getScaleRawHigh() {
    return this.scaleRawHigh;
  }

  public void setScaleRawHigh(double scaleRawHigh) {
    this.scaleRawHigh = scaleRawHigh;
  }

  public double getScaleEngLow() {
    return this.scaleEngLow;
  }

  public void setScaleEngLow(double scaleEngLow) {
    this.scaleEngLow = scaleEngLow;
  }

  public double getScaleEngHigh() {
    return this.scaleEngHigh;
  }

  public void setScaleEngHigh(double scaleEngHigh) {
    this.scaleEngHigh = scaleEngHigh;
  }

  public boolean isRoundToInteger() {
    return this.roundToInteger;
  }

  public void setRoundToInteger(boolean roundToInteger) {
    this.roundToInteger = roundToInteger;
  }

  public void addProperties(List<TranslatableMessage> list)
  {
    AuditEventType.addPropertyMessage(list, "dsEdit.galil.axis", this.axis);
    AuditEventType.addPropertyMessage(list, "dsEdit.galil.scaleLow", Double.valueOf(this.scaleRawLow));
    AuditEventType.addPropertyMessage(list, "dsEdit.galil.scaleHigh", Double.valueOf(this.scaleRawHigh));
    AuditEventType.addPropertyMessage(list, "dsEdit.galil.engLow", Double.valueOf(this.scaleEngLow));
    AuditEventType.addPropertyMessage(list, "dsEdit.galil.engHigh", Double.valueOf(this.scaleEngHigh));
    AuditEventType.addPropertyMessage(list, "dsEdit.galil.round", this.roundToInteger);
  }

  public void addPropertyChanges(List<TranslatableMessage> list, Object o)
  {
    TellPositionPointTypeVO from = (TellPositionPointTypeVO)o;
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.galil.axis", from.axis, this.axis);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.galil.scaleLow", Double.valueOf(from.scaleRawLow), Double.valueOf(this.scaleRawLow));
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.galil.scaleHigh", Double.valueOf(from.scaleRawHigh), Double.valueOf(this.scaleRawHigh));
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.galil.engLow", Double.valueOf(from.scaleEngLow), Double.valueOf(this.scaleEngLow));
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.galil.engHigh", Double.valueOf(from.scaleEngHigh), Double.valueOf(this.scaleEngHigh));
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.galil.round", from.roundToInteger, this.roundToInteger);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    SerializationHelper.writeSafeUTF(out, this.axis);
    out.writeDouble(this.scaleRawLow);
    out.writeDouble(this.scaleRawHigh);
    out.writeDouble(this.scaleEngLow);
    out.writeDouble(this.scaleEngHigh);
    out.writeBoolean(this.roundToInteger);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1) {
      this.axis = SerializationHelper.readSafeUTF(in);
      this.scaleRawLow = in.readDouble();
      this.scaleRawHigh = in.readDouble();
      this.scaleEngLow = in.readDouble();
      this.scaleEngHigh = in.readDouble();
      this.roundToInteger = in.readBoolean();
    }
  }
}