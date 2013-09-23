package com.serotonin.m2m2.galil.vo;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonSerializable;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.galil.rt.PointTypeRT;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.util.ChangeComparableObject;
import com.serotonin.m2m2.util.ExportCodes;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public abstract class PointTypeVO
  implements Serializable, JsonSerializable, ChangeComparableObject
{
  public static final ExportCodes POINT_TYPE_CODES = new ExportCodes();
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  public abstract int typeId();

  public abstract TranslatableMessage getDescription();

  public abstract PointTypeRT createRuntime();

  public abstract int getDataTypeId();

  public abstract boolean isSettable();

  public abstract void validate(ProcessResult paramProcessResult);

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1);
  }

  public void jsonWrite(ObjectWriter writer)
    throws IOException, JsonException
  {
    writer.writeEntry("type", POINT_TYPE_CODES.getCode(typeId()));
  }

  public void jsonRead(JsonReader reader, JsonObject jsonObject)
    throws JsonException
  {
  }

  static
  {
    POINT_TYPE_CODES.addElement(1, "COMMAND", "dsEdit.galil.pointType.command");
    POINT_TYPE_CODES.addElement(2, "INPUT", "dsEdit.galil.pointType.input");
    POINT_TYPE_CODES.addElement(3, "OUTPUT", "dsEdit.galil.pointType.output");
    POINT_TYPE_CODES.addElement(4, "TELL_POSITION", "dsEdit.galil.pointType.tellPosition");
    POINT_TYPE_CODES.addElement(5, "VARIABLE", "dsEdit.galil.pointType.variable");
  }

  public static abstract interface Types
  {
    public static final int COMMAND = 1;
    public static final int INPUT = 2;
    public static final int OUTPUT = 3;
    public static final int TELL_POSITION = 4;
    public static final int VARIABLE = 5;
  }
}