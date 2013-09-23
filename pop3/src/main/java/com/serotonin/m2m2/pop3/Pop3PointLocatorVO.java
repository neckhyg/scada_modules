package com.serotonin.m2m2.pop3;

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
import com.serotonin.web.taglib.Functions;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Pop3PointLocatorVO extends AbstractPointLocatorVO
        implements JsonSerializable {

    @JsonProperty
    private boolean findInSubject;

    @JsonProperty
    private String valueRegex;

    @JsonProperty
    private boolean ignoreIfMissing;

    @JsonProperty
    private String valueFormat;
    private int dataTypeId;

    @JsonProperty
    private boolean useReceivedTime;

    @JsonProperty
    private String timeRegex;

    @JsonProperty
    private String timeFormat;
    private static final long serialVersionUID = -1L;
    private static final int version = 2;

    public boolean isSettable() {
        return false;
    }

    public PointLocatorRT createRuntime() {
        return new Pop3PointLocatorRT(this);
    }

    public TranslatableMessage getConfigurationDescription() {
        return new TranslatableMessage("dsEdit.pop3.dpconn", new Object[]{Functions.escapeLessThan(this.valueRegex)});
    }

    public boolean isFindInSubject() {
        return this.findInSubject;
    }

    public void setFindInSubject(boolean findInSubject) {
        this.findInSubject = findInSubject;
    }

    public String getValueRegex() {
        return this.valueRegex;
    }

    public void setValueRegex(String valueRegex) {
        this.valueRegex = valueRegex;
    }

    public boolean isIgnoreIfMissing() {
        return this.ignoreIfMissing;
    }

    public void setIgnoreIfMissing(boolean ignoreIfMissing) {
        this.ignoreIfMissing = ignoreIfMissing;
    }

    public String getValueFormat() {
        return this.valueFormat;
    }

    public void setValueFormat(String valueFormat) {
        this.valueFormat = valueFormat;
    }

    public int getDataTypeId() {
        return this.dataTypeId;
    }

    public void setDataTypeId(int dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    public boolean isUseReceivedTime() {
        return this.useReceivedTime;
    }

    public void setUseReceivedTime(boolean useReceivedTime) {
        this.useReceivedTime = useReceivedTime;
    }

    public String getTimeRegex() {
        return this.timeRegex;
    }

    public void setTimeRegex(String timeRegex) {
        this.timeRegex = timeRegex;
    }

    public String getTimeFormat() {
        return this.timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public void validate(ProcessResult response) {
        if (StringUtils.isBlank(this.valueRegex))
            response.addContextualMessage("valueRegex", "validate.required", new Object[0]);
        else {
            try {
                Pattern pattern = Pattern.compile(this.valueRegex);
                if (pattern.matcher("").groupCount() < 1)
                    response.addContextualMessage("valueRegex", "validate.captureGroup", new Object[0]);
            } catch (PatternSyntaxException e) {
                response.addContextualMessage("valueRegex", "common.default", new Object[]{e.getMessage()});
            }
        }

        if ((this.dataTypeId == 3) && (!StringUtils.isBlank(this.valueFormat))) {
            try {
                new DecimalFormat(this.valueFormat);
            } catch (IllegalArgumentException e) {
                response.addContextualMessage("valueFormat", "common.default", new Object[]{e.getMessage()});
            }
        }

        if (!DataTypes.CODES.isValidId(this.dataTypeId, new int[0])) {
            response.addContextualMessage("dataTypeId", "validate.invalidValue", new Object[0]);
        }
        if (!StringUtils.isBlank(this.timeRegex)) {
            try {
                Pattern pattern = Pattern.compile(this.timeRegex);
                if (pattern.matcher("").groupCount() < 1)
                    response.addContextualMessage("timeRegex", "validate.captureGroup", new Object[0]);
            } catch (PatternSyntaxException e) {
                response.addContextualMessage("timeRegex", "common.default", new Object[]{e.getMessage()});
            }

            if (StringUtils.isBlank(this.timeFormat))
                response.addContextualMessage("timeFormat", "validate.required", new Object[0]);
            else
                try {
                    new SimpleDateFormat(this.timeFormat);
                } catch (IllegalArgumentException e) {
                    response.addContextualMessage("timeFormat", "common.default", new Object[]{e.getMessage()});
                }
        }
    }

    public void addProperties(List<TranslatableMessage> list) {
        AuditEventType.addDataTypeMessage(list, "dsEdit.pointDataType", this.dataTypeId);
        AuditEventType.addPropertyMessage(list, "dsEdit.pop3.findInSubject", this.findInSubject);
        AuditEventType.addPropertyMessage(list, "dsEdit.pop3.valueRegex", this.valueRegex);
        AuditEventType.addPropertyMessage(list, "dsEdit.pop3.ignoreIfMissing", this.ignoreIfMissing);
        AuditEventType.addPropertyMessage(list, "dsEdit.pop3.numberFormat", this.valueFormat);
        AuditEventType.addPropertyMessage(list, "dsEdit.pop3.useMessageTime", this.useReceivedTime);
        AuditEventType.addPropertyMessage(list, "dsEdit.pop3.timeRegex", this.timeRegex);
        AuditEventType.addPropertyMessage(list, "dsEdit.pop3.timeFormat", this.timeFormat);
    }

    public void addPropertyChanges(List<TranslatableMessage> list, Object o) {
        Pop3PointLocatorVO from = (Pop3PointLocatorVO) o;
        AuditEventType.maybeAddDataTypeChangeMessage(list, "dsEdit.pointDataType", from.dataTypeId, this.dataTypeId);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.pop3.findInSubject", from.findInSubject, this.findInSubject);

        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.pop3.valueRegex", from.valueRegex, this.valueRegex);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.pop3.ignoreIfMissing", from.ignoreIfMissing, this.ignoreIfMissing);

        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.pop3.numberFormat", from.valueFormat, this.valueFormat);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.pop3.useMessageTime", from.useReceivedTime, this.useReceivedTime);

        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.pop3.timeRegex", from.timeRegex, this.timeRegex);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.pop3.timeFormat", from.timeFormat, this.timeFormat);
    }

    private void writeObject(ObjectOutputStream out)
            throws IOException {
        out.writeInt(2);
        out.writeBoolean(this.findInSubject);
        SerializationHelper.writeSafeUTF(out, this.valueRegex);
        out.writeBoolean(this.ignoreIfMissing);
        out.writeInt(this.dataTypeId);
        SerializationHelper.writeSafeUTF(out, this.valueFormat);
        out.writeBoolean(this.useReceivedTime);
        SerializationHelper.writeSafeUTF(out, this.timeRegex);
        SerializationHelper.writeSafeUTF(out, this.timeFormat);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        if (ver == 1) {
            this.findInSubject = false;
            this.valueRegex = SerializationHelper.readSafeUTF(in);
            this.ignoreIfMissing = in.readBoolean();
            this.dataTypeId = in.readInt();
            this.valueFormat = SerializationHelper.readSafeUTF(in);
            this.useReceivedTime = in.readBoolean();
            this.timeRegex = SerializationHelper.readSafeUTF(in);
            this.timeFormat = SerializationHelper.readSafeUTF(in);
        } else if (ver == 2) {
            this.findInSubject = in.readBoolean();
            this.valueRegex = SerializationHelper.readSafeUTF(in);
            this.ignoreIfMissing = in.readBoolean();
            this.dataTypeId = in.readInt();
            this.valueFormat = SerializationHelper.readSafeUTF(in);
            this.useReceivedTime = in.readBoolean();
            this.timeRegex = SerializationHelper.readSafeUTF(in);
            this.timeFormat = SerializationHelper.readSafeUTF(in);
        }
    }

    public void jsonWrite(ObjectWriter writer) throws IOException, JsonException {
        writeDataType(writer);
    }

    public void jsonRead(JsonReader reader, JsonObject jsonObject) throws JsonException {
        Integer value = readDataType(jsonObject, new int[]{5});
        if (value != null)
            this.dataTypeId = value.intValue();
    }
}