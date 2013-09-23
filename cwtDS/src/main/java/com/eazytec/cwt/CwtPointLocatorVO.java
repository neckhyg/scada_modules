package com.eazytec.cwt;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.spi.JsonSerializable;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.DataTypes;
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

public class CwtPointLocatorVO extends AbstractPointLocatorVO
        implements JsonSerializable {
    @JsonProperty
    private String deviceId;
    @JsonProperty
    private String dataId;
    @JsonProperty
    private double multiplier=1.0D;
    @JsonProperty
    private double additive=0.0D;

    private int dataTypeId;


    public TranslatableMessage getConfigurationDescription() {
        return new TranslatableMessage("common.noMessage");
    }

    @Override
    public boolean isSettable() {
        return false;
    }

    public PointLocatorRT createRuntime() {
        return new CwtPointLocatorRT(this);
    }

    public void validate(ProcessResult processResult) {

        if (!DataTypes.CODES.isValidId(this.dataTypeId, new int[0]))
            processResult.addContextualMessage("dataTypeId", "validate.invalidValue", new Object[0]);
    }

    public void addProperties(List<TranslatableMessage> translatableMessages) {
        AuditEventType.addPropertyMessage(translatableMessages, "dsEdit.cwt.deviceId", this.deviceId);
        AuditEventType.addPropertyMessage(translatableMessages, "dsEdit.cwt.dataId", this.dataId);
        AuditEventType.addPropertyMessage(translatableMessages, "dsEdit.cwt.multiplier", Double.valueOf(this.multiplier));
        AuditEventType.addPropertyMessage(translatableMessages, "dsEdit.cwt.additive", Double.valueOf(this.additive));
        AuditEventType.addPropertyMessage(translatableMessages, "dsEdit.pointDataType", this.dataTypeId);
    }

    public void addPropertyChanges(List<TranslatableMessage> translatableMessages, Object o) {
        CwtPointLocatorVO vo = (CwtPointLocatorVO)o;
        AuditEventType.maybeAddPropertyChangeMessage(translatableMessages,"dsEdit.cwt.deviceId",vo.deviceId,this.deviceId);
        AuditEventType.maybeAddPropertyChangeMessage(translatableMessages,"dsEdit.cwt.dataId",vo.dataId,this.dataId);
        AuditEventType.maybeAddPropertyChangeMessage(translatableMessages,"dsEdit.cwt.multiplier",Double.valueOf(vo.multiplier),Double.valueOf(this.multiplier));
        AuditEventType.maybeAddPropertyChangeMessage(translatableMessages,"dsEdit.cwt.additive",Double.valueOf(vo.additive),Double.valueOf(this.additive));
        AuditEventType.maybeAddPropertyChangeMessage(translatableMessages,"dsEdit.pointDataType",vo.dataTypeId,this.dataTypeId);
    }

    public void jsonWrite(ObjectWriter objectWriter) throws IOException, JsonException {
        writeDataType(objectWriter);
    }

    public void jsonRead(JsonReader jsonReader, JsonObject jsonObject) throws JsonException {
        Integer value = readDataType(jsonObject, new int[]{5});
        if(value != null)
            this.dataTypeId = value.intValue();
    }

    public int getDataTypeId() {
        return dataTypeId;
    }

    public void setDataTypeId(int dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    private void writeObject(ObjectOutputStream out)
            throws IOException {
        out.writeInt(1);
        SerializationHelper.writeSafeUTF(out, this.deviceId);
        SerializationHelper.writeSafeUTF(out, this.dataId);
        out.writeDouble(this.multiplier);
        out.writeDouble(this.additive);
        out.writeInt(this.dataTypeId);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        if (ver == 1) {
            this.deviceId = SerializationHelper.readSafeUTF(in);
            this.dataId = SerializationHelper.readSafeUTF(in);
            this.multiplier = in.readDouble();
            this.additive = in.readDouble();
            this.dataTypeId = in.readInt();
        }
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getAdditive() {
        return additive;
    }

    public void setAdditive(double additive) {
        this.additive = additive;
    }
}
