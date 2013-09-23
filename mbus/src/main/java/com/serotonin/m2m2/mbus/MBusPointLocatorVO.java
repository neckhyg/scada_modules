package com.serotonin.m2m2.mbus;

import com.serotonin.json.spi.JsonProperty;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.util.SerializationHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.List;
import net.sf.mbus4j.MBusAddressing;
import net.sf.mbus4j.dataframes.MBusMedium;
import net.sf.mbus4j.dataframes.datablocks.DataBlock;
import net.sf.mbus4j.dataframes.datablocks.dif.DataFieldCode;
import net.sf.mbus4j.dataframes.datablocks.dif.FunctionField;

public class MBusPointLocatorVO extends AbstractPointLocatorVO
{
  public static final String[] EMPTY_STRING_ARRAY = new String[0];

  @JsonProperty
  private byte address;

  @JsonProperty
  private String difCode;

  @JsonProperty
  private String functionField;

  @JsonProperty
  private int deviceUnit;

  @JsonProperty
  private int tariff;

  @JsonProperty
  private long storageNumber;

  @JsonProperty
  private String vifType;

  @JsonProperty
  private String vifLabel;

  @JsonProperty
  private String unitOfMeasurement;

  @JsonProperty
  private String siPrefix;

  @JsonProperty
  private Integer exponent;

  @JsonProperty
  private String[] vifeLabels = EMPTY_STRING_ARRAY;

  @JsonProperty
  private String[] vifeTypes = EMPTY_STRING_ARRAY;

  @JsonProperty
  private String medium;

  @JsonProperty
  private String responseFrame;

  @JsonProperty
  private byte version;

  @JsonProperty
  private int identNumber;

  @JsonProperty
  private String manufacturer;

  @JsonProperty
  private String addressing;
  private static final long serialVersionUID = -1L;
  private static final int serialVersion = 1;

  public int getDataTypeId() {
      //TODO
      switch ( DataFieldCode.fromLabel(this.difCode).ordinal()) {
          case 1:
          case 2:
          case 3:
          case 4:
          case 5:
          case 6:
          case 7:
          case 8:
          case 9:
          case 10:
          case 11:
          case 12:
              return 3;
      }
      return 0;
  }

  public TranslatableMessage getConfigurationDescription()
  {
    return new TranslatableMessage("common.default", new Object[] { this.address + " " + this.manufacturer });
  }

  public boolean isSettable()
  {
    return false;
  }

  public PointLocatorRT createRuntime()
  {
    return new MBusPointLocatorRT(this);
  }

  public void validate(ProcessResult response)
  {
    if ((this.address < 1) || (this.address > 250))
    {
      response.addContextualMessage("address", "validate.required", new Object[0]);
    }
    try {
      DataFieldCode.fromLabel(this.difCode);
    }
    catch (IllegalArgumentException ex) {
      response.addContextualMessage("difCode", "validate.required", new Object[0]);
    }
    try
    {
      FunctionField.fromLabel(this.functionField);
    }
    catch (IllegalArgumentException ex) {
      response.addContextualMessage("functionField", "validate.required", new Object[0]);
    }

    if (this.deviceUnit < 0) {
      response.addContextualMessage("deviceUnit", "validate.required", new Object[0]);
    }

    if (this.tariff < 0) {
      response.addContextualMessage("tariff", "validate.required", new Object[0]);
    }

    if (this.storageNumber < 0L) {
      response.addContextualMessage("storageNumber", "validate.required", new Object[0]);
    }
    try
    {
      DataBlock.getVif(this.vifType, this.vifLabel, this.unitOfMeasurement, this.siPrefix, this.exponent);
    }
    catch (IllegalArgumentException ex) {
      response.addContextualMessage("vif", "validate.required", new Object[0]);
    }

    if (this.vifeLabels.length > 0) {
      if (this.vifeLabels.length != this.vifeTypes.length) {
        response.addContextualMessage("vife and vifetype lenght mismatch", "validate.required", new Object[0]);
      }
      for (int i = 0; i < this.vifeLabels.length; i++)
        try {
          DataBlock.getVife(this.vifeTypes[i], this.vifeLabels[i]);
        }
        catch (IllegalArgumentException ex) {
          response.addContextualMessage("vife", "validate.required", new Object[0]);
        }
    }
    try
    {
      MBusMedium.fromLabel(this.medium);
    }
    catch (IllegalArgumentException ex) {
      response.addContextualMessage("medium", "validate.required", new Object[0]);
    }
    if ((this.responseFrame == null) || (this.responseFrame.length() == 0)) {
      response.addContextualMessage("responseFrame", "validate.required", new Object[0]);
    }
    if ((this.version < 0) || (this.version > 255)) {
      response.addContextualMessage("version", "validate.required", new Object[0]);
    }
    if (this.identNumber < 0) {
      response.addContextualMessage("id", "validate.required", new Object[0]);
    }
    if ((this.manufacturer == null) || (this.manufacturer.length() != 3))
      response.addContextualMessage("man", "validate.required", new Object[0]);
  }

  public void addProperties(List<TranslatableMessage> list)
  {
    AuditEventType.addPropertyMessage(list, "dsEdit.mbus.addressing", this.addressing);
    AuditEventType.addPropertyMessage(list, "dsEdit.mbus.address", Byte.valueOf(this.address));
    AuditEventType.addPropertyMessage(list, "dsEdit.mbus.difCode", this.difCode);
    AuditEventType.addPropertyMessage(list, "dsEdit.mbus.functionField", this.functionField);
    AuditEventType.addPropertyMessage(list, "dsEdit.mbus.deviceUnit", Integer.valueOf(this.deviceUnit));
    AuditEventType.addPropertyMessage(list, "dsEdit.mbus.tariff", Integer.valueOf(this.tariff));
    AuditEventType.addPropertyMessage(list, "dsEdit.mbus.storageNumber", Long.valueOf(this.storageNumber));
    AuditEventType.addPropertyMessage(list, "dsEdit.mbus.vifType", this.vifType);
    AuditEventType.addPropertyMessage(list, "dsEdit.mbus.vifLabel", this.vifLabel);
    AuditEventType.addPropertyMessage(list, "dsEdit.mbus.unitOfMeasurement", this.unitOfMeasurement);
    AuditEventType.addPropertyMessage(list, "dsEdit.mbus.siPrefix", this.siPrefix);
    AuditEventType.addPropertyMessage(list, "dsEdit.mbus.exponent", this.exponent);
    AuditEventType.addPropertyMessage(list, "dsEdit.mbus.vifeLabel", this.vifeLabels);
    AuditEventType.addPropertyMessage(list, "dsEdit.mbus.vifeLabel", this.vifeTypes);
    AuditEventType.addPropertyMessage(list, "dsEdit.mbus.medium", this.medium);
    AuditEventType.addPropertyMessage(list, "dsEdit.mbus.responseFrame", this.responseFrame);
    AuditEventType.addPropertyMessage(list, "dsEdit.mbus.version", Byte.valueOf(this.version));
    AuditEventType.addPropertyMessage(list, "dsEdit.mbus.id", Integer.valueOf(this.identNumber));
    AuditEventType.addPropertyMessage(list, "dsEdit.mbus.manufacturer", this.manufacturer);
  }

  public void addPropertyChanges(List<TranslatableMessage> list, Object o)
  {
    MBusPointLocatorVO from = (MBusPointLocatorVO)o;
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.addressing", from.addressing, this.addressing);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.address", from.address, this.address);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.difCode", from.difCode, this.difCode);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.functionField", from.functionField, this.functionField);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.deviceUnit", from.deviceUnit, this.deviceUnit);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.tariff", from.tariff, this.tariff);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.storageNumber", Long.valueOf(from.storageNumber), Long.valueOf(this.storageNumber));

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.vifType", from.vifType, this.vifType);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.vifLabel", from.vifLabel, this.vifLabel);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.unitOfMeasurement", from.unitOfMeasurement, this.unitOfMeasurement);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.siPrefix", from.siPrefix, this.siPrefix);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.exponent", from.exponent, this.exponent);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.vifeLabel", from.vifeLabels, this.vifeLabels);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.vifeLabel", from.vifeTypes, this.vifeTypes);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.medium", from.medium, this.medium);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.responseFrame", from.responseFrame, this.responseFrame);

    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.version", from.version, this.version);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.id", from.identNumber, this.identNumber);
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.manufacturer", from.manufacturer, this.manufacturer);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    SerializationHelper.writeSafeUTF(out, this.addressing);

    out.writeByte(this.address);
    out.writeByte(this.version);
    out.writeInt(this.identNumber);
    SerializationHelper.writeSafeUTF(out, this.manufacturer);
    SerializationHelper.writeSafeUTF(out, this.medium);

    SerializationHelper.writeSafeUTF(out, this.responseFrame);

    SerializationHelper.writeSafeUTF(out, this.difCode);
    SerializationHelper.writeSafeUTF(out, this.functionField);
    out.writeInt(this.deviceUnit);
    out.writeInt(this.tariff);
    out.writeLong(this.storageNumber);
    SerializationHelper.writeSafeUTF(out, this.vifType);
    SerializationHelper.writeSafeUTF(out, this.vifLabel);
    SerializationHelper.writeSafeUTF(out, this.unitOfMeasurement);
    SerializationHelper.writeSafeUTF(out, this.siPrefix);
    out.writeObject(this.exponent);
    out.writeInt(this.vifeLabels.length);
    for (int i = 0; i < this.vifeLabels.length; i++) {
      SerializationHelper.writeSafeUTF(out, this.vifeTypes[i]);
      SerializationHelper.writeSafeUTF(out, this.vifeLabels[i]);
    }
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    int ver = in.readInt();

    if (ver == 1) {
      this.addressing = SerializationHelper.readSafeUTF(in);

      this.address = in.readByte();
      this.version = in.readByte();
      this.identNumber = in.readInt();
      this.manufacturer = SerializationHelper.readSafeUTF(in);
      this.medium = SerializationHelper.readSafeUTF(in);

      this.responseFrame = SerializationHelper.readSafeUTF(in);

      this.difCode = SerializationHelper.readSafeUTF(in);
      this.functionField = SerializationHelper.readSafeUTF(in);
      this.deviceUnit = in.readInt();
      this.tariff = in.readInt();
      this.storageNumber = in.readLong();
      this.vifType = SerializationHelper.readSafeUTF(in);
      this.vifLabel = SerializationHelper.readSafeUTF(in);
      this.unitOfMeasurement = SerializationHelper.readSafeUTF(in);
      this.siPrefix = SerializationHelper.readSafeUTF(in);
      this.exponent = ((Integer)in.readObject());
      int vifeLength = in.readInt();
      if (vifeLength == 0) {
        this.vifeLabels = EMPTY_STRING_ARRAY;
        this.vifeTypes = EMPTY_STRING_ARRAY;
      }
      else {
        this.vifeLabels = new String[vifeLength];
        this.vifeTypes = new String[vifeLength];
        for (int i = 0; i < vifeLength; i++) {
          this.vifeTypes[i] = SerializationHelper.readSafeUTF(in);
          this.vifeLabels[i] = SerializationHelper.readSafeUTF(in);
        }
      }
    }
  }

  public byte getAddress()
  {
    return this.address;
  }

  public String getAddressHex()
  {
    return String.format("0x%02x", new Object[] { Byte.valueOf(this.address) });
  }

  public void setAddress(byte address)
  {
    this.address = address;
  }

  public void setAddressHex(String address)
  {
    this.address = (byte)Integer.parseInt(address.substring(2), 16);
  }

  public void setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
  }

  public void setIdentNumber(int identNumber) {
    this.identNumber = identNumber;
  }

  public void setVersion(byte version) {
    this.version = version;
  }

  public void setVersionHex(String version) {
    this.version = (byte)Integer.parseInt(version.substring(2), 16);
  }

  public void setResponseFrame(String responseFrame) {
    this.responseFrame = responseFrame;
  }

  public int getDeviceUnit()
  {
    return this.deviceUnit;
  }

  public void setDeviceUnit(int deviceUnit)
  {
    this.deviceUnit = deviceUnit;
  }

  public int getTariff()
  {
    return this.tariff;
  }

  public void setTariff(int tariff)
  {
    this.tariff = tariff;
  }

  public long getStorageNumber()
  {
    return this.storageNumber;
  }

  public void setStorageNumber(long storageNumber)
  {
    this.storageNumber = storageNumber;
  }

  public String getResponseFrame()
  {
    return this.responseFrame;
  }

  public byte getVersion()
  {
    return this.version;
  }

  public String getVersionHex()
  {
    return String.format("0x%02x", new Object[] { Byte.valueOf(this.version) });
  }

  public int getIdentNumber()
  {
    return this.identNumber;
  }

  public String getManufacturer()
  {
    return this.manufacturer;
  }

  public String getDifCode()
  {
    return this.difCode;
  }

  public void setDifCode(String difCode)
  {
    this.difCode = difCode;
  }

  public String getFunctionField()
  {
    return this.functionField;
  }

  public void setFunctionField(String functionField)
  {
    this.functionField = functionField;
  }

  public String getVifLabel()
  {
    return this.vifLabel;
  }

  public void setVifLabel(String vifLabel)
  {
    this.vifLabel = vifLabel;
  }

  public String getUnitOfMeasurement()
  {
    return this.unitOfMeasurement;
  }

  public void setUnitOfMeasurement(String unitOfMeasurement)
  {
    this.unitOfMeasurement = unitOfMeasurement;
  }

  public String getSiPrefix()
  {
    return this.siPrefix;
  }

  public void setSiPrefix(String siPrefix)
  {
    this.siPrefix = siPrefix;
  }

  public Integer getExponent()
  {
    return this.exponent;
  }

  public void setExponent(Integer exponent)
  {
    this.exponent = exponent;
  }

  public String[] getVifeLabels()
  {
    return this.vifeLabels;
  }

  public void setVifeLabels(String[] vifeLabel)
  {
    this.vifeLabels = vifeLabel;
  }

  public String getMedium()
  {
    return this.medium;
  }

  public void setMedium(String medium)
  {
    this.medium = medium;
    System.out.println("MEDIUM: " + this.medium);
  }

  public String getAddressing()
  {
    return this.addressing;
  }

  public void setAddressing(String addressing)
  {
    this.addressing = addressing;
  }

  public boolean isPrimaryAddressing()
  {
    return MBusAddressing.PRIMARY.getLabel().equals(this.addressing);
  }

  public boolean isSecondaryAddressing()
  {
    return MBusAddressing.SECONDARY.getLabel().equals(this.addressing);
  }

  public String getVifType()
  {
    return this.vifType;
  }

  public void setVifType(String vifType)
  {
    this.vifType = vifType;
  }

  public String[] getVifeTypes()
  {
    return this.vifeTypes;
  }

  public void setVifeTypes(String[] vifeTypes)
  {
    this.vifeTypes = vifeTypes;
  }
}