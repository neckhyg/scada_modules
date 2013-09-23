package com.serotonin.m2m2.openv;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonSerializable;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.util.SerializationHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import net.sf.openv4j.AccessType;
import net.sf.openv4j.DataPoint;
import net.sf.openv4j.Group;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OpenV4JPointLocatorVO extends AbstractPointLocatorVO
  implements JsonSerializable
{
  private static final Log LOG = LogFactory.getLog(OpenV4JPointLocatorVO.class);

  private DataPoint dataPoint = DataPoint.COMMON_CONFIG_DEVICE_TYPE_ID;
  private static final long serialVersionUID = -1L;
  private static final int serialVersion = 1;

  public int getDataTypeId()
  {
    if (this.dataPoint == null) {
      return 0;
    }
    switch (1.$SwitchMap$net$sf$openv4j$DataType[this.dataPoint.getType().ordinal()]) {
    case 1:
      return 1;
    case 2:
    case 3:
    case 4:
    case 5:
      return 4;
    case 6:
    case 7:
    case 8:
      return 3;
    }
    return 0;
  }

  public TranslatableMessage getConfigurationDescription()
  {
    return new TranslatableMessage("dsEdit.openv4j", new Object[] { "Something", "I dont know" });
  }

  public boolean isSettable()
  {
    return (AccessType.RW.equals(this.dataPoint.getAccess())) || (AccessType.WO.equals(this.dataPoint.getAccess()));
  }

  public PointLocatorRT createRuntime()
  {
    return new OpenV4JPointLocatorRT(this);
  }

  public void validate(ProcessResult response)
  {
  }

  public void addProperties(List<TranslatableMessage> list)
  {
    AuditEventType.addPropertyMessage(list, "dsEdit.openv4j.dataPoint", this.dataPoint);
  }

  public void addPropertyChanges(List<TranslatableMessage> list, Object o)
  {
    OpenV4JPointLocatorVO from = (OpenV4JPointLocatorVO)o;
    AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.openv4j.dataPoint", from.dataPoint, this.dataPoint);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    SerializationHelper.writeSafeUTF(out, this.dataPoint.name());
  }

  private void readObject(ObjectInputStream in) throws IOException {
    int ver = in.readInt();

    switch (ver) {
    case 1:
      String s = SerializationHelper.readSafeUTF(in);
      try {
        this.dataPoint = DataPoint.valueOf(s);
      }
      catch (IllegalArgumentException ex) {
        LOG.fatal("UNKNOWN DataPoint: " + s);
        this.dataPoint = DataPoint.COMMON_CONFIG_DEVICE_TYPE_ID;
      }

    default:
      LOG.fatal("Version fall trough DataPoint unknown");
      this.dataPoint = DataPoint.COMMON_CONFIG_DEVICE_TYPE_ID;
    }
  }

  public void jsonWrite(ObjectWriter writer)
    throws IOException, JsonException
  {
    writer.writeEntry("dataPointName", this.dataPoint.name());
  }

  public void jsonRead(JsonReader reader, JsonObject jsonObject) throws JsonException
  {
    String name = jsonObject.getString("dataPointName");
    if (name != null)
      this.dataPoint = DataPoint.valueOf(name);
  }

  public String getGroupName() {
    return this.dataPoint.getGroup().getName();
  }

  public String getGroupLabel() {
    return this.dataPoint.getGroup().getLabel();
  }

  public String getLabel() {
    return this.dataPoint.getLabel();
  }

  public String getDataPointName()
  {
    return this.dataPoint.getName();
  }

  public void setDataPointName(String dataPointName)
  {
    this.dataPoint = DataPoint.valueOf(dataPointName);
  }
}