package com.serotonin.m2m2.galil.vo;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonSerializable;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.galil.rt.GalilPointLocatorRT;
import com.serotonin.m2m2.galil.rt.PointTypeRT;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GalilPointLocatorVO extends AbstractPointLocatorVO
  implements JsonSerializable
{
  private static final Log LOG = LogFactory.getLog(GalilPointLocatorVO.class);

  private int pointTypeId = 1;
  private CommandPointTypeVO commandPointType = new CommandPointTypeVO();
  private InputPointTypeVO inputPointType = new InputPointTypeVO();
  private OutputPointTypeVO outputPointType = new OutputPointTypeVO();
  private TellPositionPointTypeVO tellPositionPointType = new TellPositionPointTypeVO();
  private VariablePointTypeVO variablePointType = new VariablePointTypeVO();
  private static final long serialVersionUID = -1L;
  private static final int version = 1;

  public TranslatableMessage getConfigurationDescription()
  {
    PointTypeVO pointType = getPointType();
    if (pointType == null)
      return new TranslatableMessage("common.unknown");
    return pointType.getDescription();
  }

  private PointTypeVO getPointType() {
    if (this.pointTypeId == 1)
      return this.commandPointType;
    if (this.pointTypeId == 2)
      return this.inputPointType;
    if (this.pointTypeId == 3)
      return this.outputPointType;
    if (this.pointTypeId == 4)
      return this.tellPositionPointType;
    if (this.pointTypeId == 5)
      return this.variablePointType;
    LOG.error("Failed to resolve pointTypeId " + this.pointTypeId + " for Galil point locator");
    return null;
  }

  public PointLocatorRT createRuntime()
  {
    PointTypeRT changeType = getPointType().createRuntime();
    return new GalilPointLocatorRT(changeType);
  }

  public void validate(ProcessResult response)
  {
    PointTypeVO pointType = getPointType();
    if (pointType == null)
      response.addContextualMessage("pointTypeId", "validate.invalidChoice", new Object[0]);
    else
      pointType.validate(response);
  }

  public int getDataTypeId()
  {
    return getPointType().getDataTypeId();
  }

  public boolean isSettable()
  {
    return getPointType().isSettable();
  }

  public int getPointTypeId()
  {
    return this.pointTypeId;
  }

  public void setPointTypeId(int pointTypeId) {
    this.pointTypeId = pointTypeId;
  }

  public CommandPointTypeVO getCommandPointType() {
    return this.commandPointType;
  }

  public InputPointTypeVO getInputPointType() {
    return this.inputPointType;
  }

  public OutputPointTypeVO getOutputPointType() {
    return this.outputPointType;
  }

  public TellPositionPointTypeVO getTellPositionPointType() {
    return this.tellPositionPointType;
  }

  public VariablePointTypeVO getVariablePointType() {
    return this.variablePointType;
  }

  public void setCommandPointType(CommandPointTypeVO commandPointType) {
    this.commandPointType = commandPointType;
  }

  public void setInputPointType(InputPointTypeVO inputPointType) {
    this.inputPointType = inputPointType;
  }

  public void setOutputPointType(OutputPointTypeVO outputPointType) {
    this.outputPointType = outputPointType;
  }

  public void setTellPositionPointType(TellPositionPointTypeVO tellPositionPointType) {
    this.tellPositionPointType = tellPositionPointType;
  }

  public void setVariablePointType(VariablePointTypeVO variablePointType) {
    this.variablePointType = variablePointType;
  }

  public void addProperties(List<TranslatableMessage> list)
  {
    AuditEventType.addExportCodeMessage(list, "dsEdit.galil.pointType", PointTypeVO.POINT_TYPE_CODES, this.pointTypeId);
    getPointType().addProperties(list);
  }

  public void addPropertyChanges(List<TranslatableMessage> list, Object o)
  {
    GalilPointLocatorVO from = (GalilPointLocatorVO)o;
    AuditEventType.maybeAddExportCodeChangeMessage(list, "dsEdit.galil.pointType", PointTypeVO.POINT_TYPE_CODES, from.pointTypeId, this.pointTypeId);

    if (from.pointTypeId == this.pointTypeId)
      getPointType().addPropertyChanges(list, from.getPointType());
    else
      getPointType().addProperties(list);
  }

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeInt(1);
    out.writeInt(this.pointTypeId);
    out.writeObject(this.commandPointType);
    out.writeObject(this.inputPointType);
    out.writeObject(this.outputPointType);
    out.writeObject(this.tellPositionPointType);
    out.writeObject(this.variablePointType);
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    int ver = in.readInt();

    if (ver == 1) {
      this.pointTypeId = in.readInt();
      this.commandPointType = ((CommandPointTypeVO)in.readObject());
      this.inputPointType = ((InputPointTypeVO)in.readObject());
      this.outputPointType = ((OutputPointTypeVO)in.readObject());
      this.tellPositionPointType = ((TellPositionPointTypeVO)in.readObject());
      this.variablePointType = ((VariablePointTypeVO)in.readObject());
    }
  }

  public void jsonWrite(ObjectWriter writer) throws IOException, JsonException
  {
    writer.writeEntry("pointType", getPointType());
  }

  public void jsonRead(JsonReader reader, JsonObject jsonObject) throws JsonException
  {
    JsonObject ptjson = jsonObject.getJsonObject("pointType");
    if (ptjson == null) {
      throw new TranslatableJsonException("emport.error.missingObject", new Object[] { "pointType" });
    }
    String text = ptjson.getString("type");
    if (text == null) {
      throw new TranslatableJsonException("emport.error.pointType.missing", new Object[] { "type", PointTypeVO.POINT_TYPE_CODES.getCodeList(new int[0]) });
    }

    this.pointTypeId = PointTypeVO.POINT_TYPE_CODES.getId(text, new int[0]);
    if (this.pointTypeId == -1) {
      throw new TranslatableJsonException("emport.error.pointType.invalid", new Object[] { "pointType", text, PointTypeVO.POINT_TYPE_CODES.getCodeList(new int[0]) });
    }

    reader.readInto(getPointType(), ptjson);
  }
}