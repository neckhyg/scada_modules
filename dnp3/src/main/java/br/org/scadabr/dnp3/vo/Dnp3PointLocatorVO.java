package br.org.scadabr.dnp3.vo;

import br.org.scadabr.dnp3.rt.Dnp3PointLocatorRT;
import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.spi.JsonSerializable;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;
import com.serotonin.m2m2.vo.dataSource.AbstractPointLocatorVO;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class Dnp3PointLocatorVO extends AbstractPointLocatorVO
  implements JsonSerializable
{
  public static final int BINARY_INPUT = 1;
  public static final int BINARY_OUTPUT = 16;
  public static final int RUNNING_COUNTER = 32;
  public static final int ANALOG_INPUT = 48;
  public static final int ANALOG_OUTPUT = 64;
  public static final int SBO = 1;
  public static final int DIRECT = 2;
  public static final int DIRECT_NO_ACK = 3;
  public static final int CLOSE_TRIP = 1;
  public static final int PULSE = 2;
  public static final int LATCH = 3;

  @JsonProperty
  private int dnp3DataType = 1;

  @JsonProperty
  private int index = 1;

  @JsonProperty
  private double multiplier = 1.0D;

  @JsonProperty
  private double additive = 0.0D;

  @JsonProperty
  private int operateMode = 2;

  @JsonProperty
  private int controlCommand = 3;

  @JsonProperty
  private int timeOn = 0;

  @JsonProperty
  private int timeOff = 0;

  @JsonProperty
  private boolean settable;
  private static final long serialVersionUID = -1L;
  private static final int version = 2;

  public PointLocatorRT createRuntime()
  {
    return new Dnp3PointLocatorRT(this);
  }

  public TranslatableMessage getConfigurationDescription()
  {
    return null;
  }

  public int getDataTypeId()
  {
    if (this.dnp3DataType == 1)
      return 1;
    if (this.dnp3DataType == 16)
      return 4;
    return 3;
  }

  public int getDnp3DataType()
  {
    return this.dnp3DataType;
  }

  public void setDnp3DataType(int dnp3DataType) {
    this.dnp3DataType = dnp3DataType;
  }

  public int getIndex() {
    return this.index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public double getMultiplier() {
    return this.multiplier;
  }

  public void setMultiplier(double multiplier) {
    this.multiplier = multiplier;
  }

  public double getAdditive() {
    return this.additive;
  }

  public void setAdditive(double additive) {
    this.additive = additive;
  }

  public int getOperateMode() {
    return this.operateMode;
  }

  public void setOperateMode(int operateMode) {
    this.operateMode = operateMode;
  }

  public int getControlCommand() {
    return this.controlCommand;
  }

  public void setControlCommand(int controlCommand) {
    this.controlCommand = controlCommand;
  }

  public int getTimeOn() {
    return this.timeOn;
  }

  public void setTimeOn(int timeOn) {
    this.timeOn = timeOn;
  }

  public int getTimeOff() {
    return this.timeOff;
  }

  public void setTimeOff(int timeOff) {
    this.timeOff = timeOff;
  }

  public void validate(ProcessResult response)
  {
    if (this.index < 0)
      response.addContextualMessage("index", "validate.invalidValue", new Object[0]);
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
    out.writeInt(2);
    out.writeInt(this.dnp3DataType);
    out.writeInt(this.index);
    out.writeDouble(this.multiplier);
    out.writeDouble(this.additive);
    out.writeInt(this.operateMode);
    out.writeInt(this.controlCommand);
    out.writeInt(this.timeOn);
    out.writeInt(this.timeOff);
    out.writeBoolean(this.settable);
  }

  private void readObject(ObjectInputStream in) throws IOException
  {
    int ver = in.readInt();

    if (ver == 2) {
      this.dnp3DataType = in.readInt();
      this.index = in.readInt();
      this.multiplier = in.readDouble();
      this.additive = in.readDouble();
      this.operateMode = in.readInt();
      this.controlCommand = in.readInt();
      this.timeOn = in.readInt();
      this.timeOff = in.readInt();
      this.settable = in.readBoolean();
    }
  }

  public void jsonWrite(ObjectWriter writer)
    throws IOException, JsonException
  {
  }

  public void jsonRead(JsonReader reader, JsonObject jsonObject)
    throws JsonException
  {
  }

  public boolean isSettable()
  {
    return this.settable;
  }

  public void setSettable(boolean settable) {
    this.settable = settable;
  }
}