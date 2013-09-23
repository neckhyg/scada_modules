package com.serotonin.m2m2.vmstat;

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

public class VMStatPointLocatorVO extends AbstractPointLocatorVO
  implements JsonSerializable
{
  public static ExportCodes ATTRIBUTE_CODES = new ExportCodes();

  private int attributeId = 15;
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  public boolean isSettable()
  {
    return false;
  }

  public PointLocatorRT createRuntime()
  {
    return new VMStatPointLocatorRT(this);
  }

  public TranslatableMessage getConfigurationDescription()
  {
    if (ATTRIBUTE_CODES.isValidId(this.attributeId, new int[0]))
      return new TranslatableMessage(ATTRIBUTE_CODES.getKey(this.attributeId));
    return new TranslatableMessage("common.unknown");
  }

  public int getDataTypeId()
  {
    return 3;
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
    AuditEventType.addExportCodeMessage(list, "dsEdit.vmstat.attribute", ATTRIBUTE_CODES, this.attributeId);
  }

  public void addPropertyChanges(List<TranslatableMessage> list, Object o)
  {
    VMStatPointLocatorVO from = (VMStatPointLocatorVO)o;
    AuditEventType.maybeAddExportCodeChangeMessage(list, "dsEdit.vmstat.attribute", ATTRIBUTE_CODES, from.attributeId, this.attributeId);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    out.writeInt(this.attributeId);
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    if (ver == 1)
      this.attributeId = in.readInt();
  }

  public void jsonWrite(ObjectWriter writer) throws IOException, JsonException
  {
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
  }

  static
  {
    ATTRIBUTE_CODES.addElement(1, "PROCS_R", "dsEdit.vmstat.attr.procsR");
    ATTRIBUTE_CODES.addElement(2, "PROCS_B", "dsEdit.vmstat.attr.procsB");
    ATTRIBUTE_CODES.addElement(3, "MEMORY_SWPD", "dsEdit.vmstat.attr.memorySwpd");
    ATTRIBUTE_CODES.addElement(4, "MEMORY_FREE", "dsEdit.vmstat.attr.memoryFree");
    ATTRIBUTE_CODES.addElement(5, "MEMORY_BUFF", "dsEdit.vmstat.attr.memoryBuff");
    ATTRIBUTE_CODES.addElement(6, "MEMORY_CACHE", "dsEdit.vmstat.attr.memoryCache");
    ATTRIBUTE_CODES.addElement(7, "SWAP_SI", "dsEdit.vmstat.attr.swapSi");
    ATTRIBUTE_CODES.addElement(8, "SWAP_SO", "dsEdit.vmstat.attr.swapSo");
    ATTRIBUTE_CODES.addElement(9, "IO_BI", "dsEdit.vmstat.attr.ioBi");
    ATTRIBUTE_CODES.addElement(10, "IO_BO", "dsEdit.vmstat.attr.ioBo");
    ATTRIBUTE_CODES.addElement(11, "SYSTEM_IN", "dsEdit.vmstat.attr.systemIn");
    ATTRIBUTE_CODES.addElement(12, "SYSTEM_CS", "dsEdit.vmstat.attr.systemCs");
    ATTRIBUTE_CODES.addElement(13, "CPU_US", "dsEdit.vmstat.attr.cpuUs");
    ATTRIBUTE_CODES.addElement(14, "CPU_SY", "dsEdit.vmstat.attr.cpuSy");
    ATTRIBUTE_CODES.addElement(15, "CPU_ID", "dsEdit.vmstat.attr.cpuId");
    ATTRIBUTE_CODES.addElement(16, "CPU_WA", "dsEdit.vmstat.attr.cpuWa");
    ATTRIBUTE_CODES.addElement(17, "CPU_ST", "dsEdit.vmstat.attr.cpuSt");
  }

  public static abstract interface Attributes
  {
    public static final int PROCS_R = 1;
    public static final int PROCS_B = 2;
    public static final int MEMORY_SWPD = 3;
    public static final int MEMORY_FREE = 4;
    public static final int MEMORY_BUFF = 5;
    public static final int MEMORY_CACHE = 6;
    public static final int SWAP_SI = 7;
    public static final int SWAP_SO = 8;
    public static final int IO_BI = 9;
    public static final int IO_BO = 10;
    public static final int SYSTEM_IN = 11;
    public static final int SYSTEM_CS = 12;
    public static final int CPU_US = 13;
    public static final int CPU_SY = 14;
    public static final int CPU_ID = 15;
    public static final int CPU_WA = 16;
    public static final int CPU_ST = 17;
  }
}