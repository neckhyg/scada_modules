package com.eazy.eazySerial;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonSerializable;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableJsonException;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.vo.dataSource.AbstractPointLocatorVO;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class EazySerialPointLocatorVO extends AbstractPointLocatorVO
  implements JsonSerializable
{
  public static ExportCodes ATTRIBUTE_CODES = new ExportCodes();

  private int attributeId = 1;
  private int dataTypeId = 3;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  public boolean isSettable()
  {
    return false;
  }

  public PointLocatorRT createRuntime()
  {
    return new EazySerialPointLocatorRT(this);
  }

  public TranslatableMessage getConfigurationDescription()
  {
    if (ATTRIBUTE_CODES.isValidId(this.attributeId, new int[0]))
      return new TranslatableMessage(ATTRIBUTE_CODES.getKey(this.attributeId));
    return new TranslatableMessage("common.unknown");
  }

//  public int getDataTypeId()
//  {
//    return 3;
//  }

    public int getDataTypeId() {
        return dataTypeId;
    }

    public void setDataTypeId(int dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    public int getAttributeId() {
    return this.attributeId;
  }

  public void setAttributeId(int attributeId) {
    this.attributeId = attributeId;
  }

  public void validate(ProcessResult response)
  {
    if (!ATTRIBUTE_CODES.isValidId(this.attributeId, new int[0]))
      response.addContextualMessage("attributeId", "validate.invalidValue", new Object[0]);
  }

  public void addProperties(List<TranslatableMessage> list)
  {
    AuditEventType.addExportCodeMessage(list, "dsEdit.eazySerial.attribute", ATTRIBUTE_CODES, this.attributeId);
//    AuditEventType.addExportCodeMessage(list, "dsEdit.eazySerial.attribute", ATTRIBUTE_CODES, this.attributeId);
      AuditEventType.addPropertyMessage(list, "dsEdit.pointDataType", this.dataTypeId);
  }

  public void addPropertyChanges(List<TranslatableMessage> list, Object o)
  {
    EazySerialPointLocatorVO from = (EazySerialPointLocatorVO)o;
    AuditEventType.maybeAddExportCodeChangeMessage(list, "dsEdit.eazySerial.attribute", ATTRIBUTE_CODES, from.attributeId, this.attributeId);
    AuditEventType.maybeAddPropertyChangeMessage(list,  "dsEdit.pointDataType",from.dataTypeId,this.dataTypeId);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    out.writeInt(this.attributeId);
    out.writeInt(this.dataTypeId);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1)   {
        this.attributeId = in.readInt();
        this.dataTypeId = in.readInt();
    }

  }

  public void jsonWrite(ObjectWriter writer) throws IOException, JsonException
  {
     writeDataType(writer);
    writer.writeEntry("attributeId", ATTRIBUTE_CODES.getCode(this.attributeId));
  }

  public void jsonRead(JsonReader reader, JsonObject jsonObject) throws JsonException
  {
    String text = jsonObject.getString("attributeId");
    if (text == null)
      throw new TranslatableJsonException("emport.error.missing", new Object[] { "attributeId", ATTRIBUTE_CODES.getCodeList(new int[0]) });
    this.attributeId = ATTRIBUTE_CODES.getId(text, new int[0]);
    if (!ATTRIBUTE_CODES.isValidId(this.attributeId, new int[0]))
      throw new TranslatableJsonException("emport.error.invalid", new Object[] { "attributeId", text, ATTRIBUTE_CODES.getCodeList(new int[0]) });

      Integer value = readDataType(jsonObject, new int[]{3});
      if(value != null)
          this.dataTypeId = value.intValue();
  }

  static
  {
    ATTRIBUTE_CODES.addElement(1, "INPUT_VOLT", "dsEdit.eazySerial.attr.inputV");
    ATTRIBUTE_CODES.addElement(2, "INPUT_FAULT_VOLT", "dsEdit.eazySerial.attr.inputFV");
    ATTRIBUTE_CODES.addElement(3, "OUTPUT_VOLT", "dsEdit.eazySerial.attr.outputV");
    ATTRIBUTE_CODES.addElement(4, "LOADER_VALUE", "dsEdit.eazySerial.attr.loaderV");
    ATTRIBUTE_CODES.addElement(5, "INPUT_FREQ", "dsEdit.eazySerial.attr.inputF");
    ATTRIBUTE_CODES.addElement(6, "BATT_VOLT", "dsEdit.eazySerial.attr.battV");
    ATTRIBUTE_CODES.addElement(7, "TEMP", "dsEdit.eazySerial.attr.temperature");
    ATTRIBUTE_CODES.addElement(10, "START_STATUS", "dsEdit.eazySerial.attr.start.status");
    ATTRIBUTE_CODES.addElement(11, "SHUTDOWN_STATUS", "dsEdit.eazySerial.attr.shutdown.status");
    ATTRIBUTE_CODES.addElement(12, "TEST_STATUS", "dsEdit.eazySerial.attr.test.status");
    ATTRIBUTE_CODES.addElement(13, "STANDBY_STATUS", "dsEdit.eazySerial.attr.standby.status");
    ATTRIBUTE_CODES.addElement(14, "UPSFAULT_STATUS", "dsEdit.eazySerial.attr.UPSfault.status");
    ATTRIBUTE_CODES.addElement(15, "BYPASS_STATUS", "dsEdit.eazySerial.attr.bypass.status");
    ATTRIBUTE_CODES.addElement(16, "LOWBATTVOLT_STATUS", "dsEdit.eazySerial.attr.lowbatt.status");
    ATTRIBUTE_CODES.addElement(17, "UTILITYFAIL_STATUS", "dsEdit.eazySerial.attr.utilityfail.status");

//    ATTRIBUTE_CODES.addElement(1, "PROCS_R", "dsEdit.eazySerial.attr.procsR");
//    ATTRIBUTE_CODES.addElement(2, "PROCS_B", "dsEdit.eazySerial.attr.procsB");
//    ATTRIBUTE_CODES.addElement(3, "MEMORY_SWPD", "dsEdit.eazySerial.attr.memorySwpd");
//    ATTRIBUTE_CODES.addElement(4, "MEMORY_FREE", "dsEdit.eazySerial.attr.memoryFree");
//    ATTRIBUTE_CODES.addElement(5, "MEMORY_BUFF", "dsEdit.eazySerial.attr.memoryBuff");
//    ATTRIBUTE_CODES.addElement(6, "MEMORY_CACHE", "dsEdit.eazySerial.attr.memoryCache");
//    ATTRIBUTE_CODES.addElement(7, "SWAP_SI", "dsEdit.eazySerial.attr.swapSi");
//    ATTRIBUTE_CODES.addElement(8, "SWAP_SO", "dsEdit.eazySerial.attr.swapSo");
//    ATTRIBUTE_CODES.addElement(9, "IO_BI", "dsEdit.eazySerial.attr.ioBi");
//    ATTRIBUTE_CODES.addElement(10, "IO_BO", "dsEdit.eazySerial.attr.ioBo");
//    ATTRIBUTE_CODES.addElement(11, "SYSTEM_IN", "dsEdit.eazySerial.attr.systemIn");
//    ATTRIBUTE_CODES.addElement(12, "SYSTEM_CS", "dsEdit.eazySerial.attr.systemCs");
//    ATTRIBUTE_CODES.addElement(13, "CPU_US", "dsEdit.eazySerial.attr.cpuUs");
//    ATTRIBUTE_CODES.addElement(14, "CPU_SY", "dsEdit.eazySerial.attr.cpuSy");
//    ATTRIBUTE_CODES.addElement(15, "CPU_ID", "dsEdit.eazySerial.attr.cpuId");
//    ATTRIBUTE_CODES.addElement(16, "CPU_WA", "dsEdit.eazySerial.attr.cpuWa");
//    ATTRIBUTE_CODES.addElement(17, "CPU_ST", "dsEdit.eazySerial.attr.cpuSt");
  }

  public static abstract interface Attributes
  {
    public static final int INPUT_VOLT = 1;
    public static final int INPUT_FAULT_VOLT = 2;
    public static final int OUTPUT_VOLT = 3;
    public static final int LOADER_VALUE = 4;
    public static final int INPUT_FREQ = 5;
    public static final int BATT_VOLT = 6;
    public static final int TEMP = 7;
    public static final int START_STATUS = 10;
    public static final int SHUTDOWN_STATUS = 11;
    public static final int TEST_STATUS = 12;
    public static final int STANDBY_STATUS = 13;
    public static final int UPSFAULT_STATUS = 14;
    public static final int BYPASS_STATUS = 15;
    public static final int LOWBATTVOLT_STATUS = 16;
    public static final int UTILITYFAIL_STATUS = 17;

//    public static final int PROCS_R = 1;
//    public static final int PROCS_B = 2;
//    public static final int MEMORY_SWPD = 3;
//    public static final int MEMORY_FREE = 4;
//    public static final int MEMORY_BUFF = 5;
//    public static final int MEMORY_CACHE = 6;
//    public static final int SWAP_SI = 7;
//    public static final int SWAP_SO = 8;
//    public static final int IO_BI = 9;
//    public static final int IO_BO = 10;
//    public static final int SYSTEM_IN = 11;
//    public static final int SYSTEM_CS = 12;
//    public static final int CPU_US = 13;
//    public static final int CPU_SY = 14;
//    public static final int CPU_ID = 15;
//    public static final int CPU_WA = 16;
//    public static final int CPU_ST = 17;
  }
}