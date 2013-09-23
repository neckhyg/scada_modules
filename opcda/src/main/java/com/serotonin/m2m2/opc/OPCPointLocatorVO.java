package com.serotonin.m2m2.opc;

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

public class OPCPointLocatorVO extends AbstractPointLocatorVO
        implements JsonSerializable {

    private static final long serialVersionUID = -1L;
    private static final int version = 1;
    @JsonProperty
    private String tag = "";
    private int dataType = 1;
    @JsonProperty
    private boolean settable;

    public PointLocatorRT createRuntime() {
        return new OPCPointLocatorRT(this);
    }

    public TranslatableMessage getConfigurationDescription() {
        return new TranslatableMessage("common.noMessage");
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getDataTypeId() {
        return this.dataType;
    }

    public void setDataTypeId(int dataType) {
        this.dataType = dataType;
    }

    public boolean isSettable() {
        return this.settable;
    }

    public void setSettable(boolean settable) {
        this.settable = settable;
    }

    public void validate(ProcessResult response) {
        if (!DataTypes.CODES.isValidId(this.dataType, new int[0]))
            response.addContextualMessage("dataType", "validate.invalidValue", new Object[0]);
    }

    public void addProperties(List<TranslatableMessage> list) {
        AuditEventType.addPropertyMessage(list, "dsedit.opc.tagName", this.tag);
        AuditEventType.addDataTypeMessage(list, "dsEdit.pointDataType", this.dataType);
        AuditEventType.addPropertyMessage(list, "dsEdit.settable", this.settable);
    }

    public void addPropertyChanges(List<TranslatableMessage> list, Object o) {
        OPCPointLocatorVO from = (OPCPointLocatorVO) o;
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsedit.opc.tagName", from.tag, this.tag);
        AuditEventType.maybeAddDataTypeChangeMessage(list, "dsEdit.pointDataType", from.dataType, this.dataType);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.settable", from.settable, this.settable);
    }

    private void writeObject(ObjectOutputStream out)
            throws IOException {
        out.writeInt(1);
        SerializationHelper.writeSafeUTF(out, this.tag);
        out.writeInt(this.dataType);
        out.writeBoolean(this.settable);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();
        if (ver == 1) {
            this.tag = SerializationHelper.readSafeUTF(in);
            this.dataType = in.readInt();
            this.settable = in.readBoolean();
        }
    }

    public void jsonWrite(ObjectWriter writer) throws IOException, JsonException {
        writeDataType(writer);
    }

    public void jsonRead(JsonReader reader, JsonObject jsonObject) throws JsonException {
        Integer value = readDataType(jsonObject, new int[]{5});
        if (value != null)
            this.dataType = value.intValue();
    }
}