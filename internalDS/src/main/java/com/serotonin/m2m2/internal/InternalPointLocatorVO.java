
package com.serotonin.m2m2.internal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonEntity;
import com.serotonin.json.spi.JsonSerializable;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.DataTypes;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableJsonException;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.vo.dataSource.AbstractPointLocatorVO;


@JsonEntity
public class InternalPointLocatorVO extends AbstractPointLocatorVO implements JsonSerializable {
    public interface Attributes {
        int BATCH_ENTRIES = 1;
        int BATCH_INSTANCES = 2;
        int MONITOR_HIGH = 3;
        int MONITOR_MEDIUM = 4;
        int MONITOR_SCHEDULED = 5;
        int MONITOR_STACK_HEIGHT = 6;
        int MONITOR_THREAD_COUNT = 7;
        int MONITOR_DB_ACTIVE_CONNECTIONS = 8;
        int MONITOR_DB_IDLE_CONNECTIONS = 9;
    }

    // Values in this array correspond to the attribute ids above.
    public static String[] MONITOR_NAMES = { "", //
            "com.serotonin.m2m2.db.dao.PointValueDao$BatchWriteBehind.ENTRIES_MONITOR", //
            "com.serotonin.m2m2.db.dao.PointValueDao$BatchWriteBehind.INSTANCES_MONITOR", //
            "com.serotonin.m2m2.rt.maint.WorkItemMonitor.highPriorityServiceQueueSize", //
            "com.serotonin.m2m2.rt.maint.WorkItemMonitor.mediumPriorityServiceQueueSize", //
            "com.serotonin.m2m2.rt.maint.WorkItemMonitor.scheduledTimerTaskCount", //
            "com.serotonin.m2m2.rt.maint.WorkItemMonitor.maxStackHeight", //
            "com.serotonin.m2m2.rt.maint.WorkItemMonitor.threadCount", //
            "com.serotonin.m2m2.rt.maint.WorkItemMonitor.dbActiveConnections", //
            "com.serotonin.m2m2.rt.maint.WorkItemMonitor.dbIdleConnections", //
    };

    public static ExportCodes ATTRIBUTE_CODES = new ExportCodes();
    static {
        ATTRIBUTE_CODES.addElement(Attributes.BATCH_ENTRIES, "BATCH_ENTRIES", "internal.monitor.BATCH_ENTRIES");
        ATTRIBUTE_CODES.addElement(Attributes.BATCH_INSTANCES, "BATCH_INSTANCES", "internal.monitor.BATCH_INSTANCES");
        ATTRIBUTE_CODES.addElement(Attributes.MONITOR_HIGH, "MONITOR_HIGH", "internal.monitor.MONITOR_HIGH");
        ATTRIBUTE_CODES.addElement(Attributes.MONITOR_MEDIUM, "MONITOR_MEDIUM", "internal.monitor.MONITOR_MEDIUM");
        ATTRIBUTE_CODES.addElement(Attributes.MONITOR_SCHEDULED, "MONITOR_SCHEDULED",
                "internal.monitor.MONITOR_SCHEDULED");
        ATTRIBUTE_CODES.addElement(Attributes.MONITOR_STACK_HEIGHT, "MONITOR_STACK_HEIGHT",
                "internal.monitor.MONITOR_STACK_HEIGHT");
        ATTRIBUTE_CODES.addElement(Attributes.MONITOR_THREAD_COUNT, "MONITOR_THREAD_COUNT",
                "internal.monitor.MONITOR_THREAD_COUNT");
        ATTRIBUTE_CODES.addElement(Attributes.MONITOR_DB_ACTIVE_CONNECTIONS, "DB_ACTIVE_CONNECTIONS",
                "internal.monitor.DB_ACTIVE_CONNECTIONS");
        ATTRIBUTE_CODES.addElement(Attributes.MONITOR_DB_IDLE_CONNECTIONS, "DB_IDLE_CONNECTIONS",
                "internal.monitor.DB_IDLE_CONNECTIONS");
    };

    private int attributeId = Attributes.BATCH_ENTRIES;

    @Override
    public boolean isSettable() {
        return false;
    }

    @Override
    public PointLocatorRT createRuntime() {
        return new InternalPointLocatorRT(this);
    }

    @Override
    public TranslatableMessage getConfigurationDescription() {
        if (ATTRIBUTE_CODES.isValidId(attributeId))
            return new TranslatableMessage(ATTRIBUTE_CODES.getKey(attributeId));
        return new TranslatableMessage("common.unknown");
    }

    @Override
    public int getDataTypeId() {
        return DataTypes.NUMERIC;
    }

    public int getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(int attributeId) {
        this.attributeId = attributeId;
    }

    @Override
    public void validate(ProcessResult response) {
        if (!ATTRIBUTE_CODES.isValidId(attributeId))
            response.addContextualMessage("attributeId", "validate.invalidValue");
    }

    @Override
    public void addProperties(List<TranslatableMessage> list) {
        AuditEventType.addExportCodeMessage(list, "dsEdit.vmstat.attribute", ATTRIBUTE_CODES, attributeId);
    }

    @Override
    public void addPropertyChanges(List<TranslatableMessage> list, Object o) {
        InternalPointLocatorVO from = (InternalPointLocatorVO) o;
        AuditEventType.maybeAddExportCodeChangeMessage(list, "dsEdit.vmstat.attribute", ATTRIBUTE_CODES,
                from.attributeId, attributeId);
    }

    //
    //
    // Serialization
    //
    private static final long serialVersionUID = -1;
    private static final int version = 1;

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(version);
        out.writeInt(attributeId);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1)
            attributeId = in.readInt();
    }

    @Override
    public void jsonWrite(ObjectWriter writer) throws IOException, JsonException {
        writer.writeEntry("attributeId", ATTRIBUTE_CODES.getCode(attributeId));
    }

    @Override
    public void jsonRead(JsonReader reader, JsonObject jsonObject) throws JsonException {
        String text = jsonObject.getString("attributeId");
        if (text == null)
            throw new TranslatableJsonException("emport.error.missing", "attributeId", ATTRIBUTE_CODES.getCodeList());
        attributeId = ATTRIBUTE_CODES.getId(text);
        if (!ATTRIBUTE_CODES.isValidId(attributeId))
            throw new TranslatableJsonException("emport.error.invalid", "attributeId", text,
                    ATTRIBUTE_CODES.getCodeList());
    }
}
