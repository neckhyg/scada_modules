package com.serotonin.m2m2.pop3;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.DataSourceRT;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;
import com.serotonin.m2m2.vo.event.EventTypeVO;
import com.serotonin.util.SerializationHelper;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class Pop3DataSourceVO extends DataSourceVO<Pop3DataSourceVO> {
    private static final ExportCodes EVENT_CODES = new ExportCodes();

    @JsonProperty
    private String pop3Server;

    @JsonProperty
    private String username;

    @JsonProperty
    private String password;
    private int updatePeriodType = 2;

    @JsonProperty
    private int updatePeriods = 5;
    private static final long serialVersionUID = -1L;
    private static final int version = 1;

    protected void addEventTypes(List<EventTypeVO> ets) {
        ets.add(createEventType(1, new TranslatableMessage("event.ds.emailInbox")));
        ets.add(createEventType(2, new TranslatableMessage("event.ds.emailRead")));

        ets.add(createEventType(3, new TranslatableMessage("event.ds.emailParse")));
    }

    public ExportCodes getEventCodes() {
        return EVENT_CODES;
    }

    public TranslatableMessage getConnectionDescription() {
        return new TranslatableMessage("common.default", new Object[]{this.username});
    }

    public DataSourceRT createDataSourceRT() {
        return new Pop3DataSourceRT(this);
    }

    public Pop3PointLocatorVO createPointLocator() {
        return new Pop3PointLocatorVO();
    }

    public String getPop3Server() {
        return this.pop3Server;
    }

    public void setPop3Server(String pop3Server) {
        this.pop3Server = pop3Server;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getUpdatePeriodType() {
        return this.updatePeriodType;
    }

    public void setUpdatePeriodType(int updatePeriodType) {
        this.updatePeriodType = updatePeriodType;
    }

    public int getUpdatePeriods() {
        return this.updatePeriods;
    }

    public void setUpdatePeriods(int updatePeriods) {
        this.updatePeriods = updatePeriods;
    }

    public void validate(ProcessResult response) {
        super.validate(response);
        if (StringUtils.isBlank(this.pop3Server))
            response.addContextualMessage("pop3Server", "validate.required", new Object[0]);
        if (StringUtils.isBlank(this.username))
            response.addContextualMessage("username", "validate.required", new Object[0]);
        if (StringUtils.isBlank(this.password))
            response.addContextualMessage("password", "validate.required", new Object[0]);
        if (!Common.TIME_PERIOD_CODES.isValidId(this.updatePeriodType, new int[0]))
            response.addContextualMessage("updatePeriodType", "validate.invalidValue", new Object[0]);
        if (this.updatePeriods <= 0)
            response.addContextualMessage("updatePeriods", "validate.greaterThanZero", new Object[0]);
    }

    protected void addPropertiesImpl(List<TranslatableMessage> list) {
        AuditEventType.addPeriodMessage(list, "dsEdit.pop3.checkPeriod", this.updatePeriodType, this.updatePeriods);
        AuditEventType.addPropertyMessage(list, "dsEdit.pop3.server", this.pop3Server);
        AuditEventType.addPropertyMessage(list, "dsEdit.pop3.username", this.username);
        AuditEventType.addPropertyMessage(list, "dsEdit.pop3.password", this.password);
    }

    protected void addPropertyChangesImpl(List<TranslatableMessage> list, Pop3DataSourceVO from) {
        AuditEventType.maybeAddPeriodChangeMessage(list, "dsEdit.pop3.checkPeriod", from.updatePeriodType, from.updatePeriods, this.updatePeriodType, this.updatePeriods);

        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.pop3.server", from.pop3Server, this.pop3Server);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.pop3.username", from.username, this.username);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.pop3.password", from.password, this.password);
    }

    private void writeObject(ObjectOutputStream out)
            throws IOException {
        out.writeInt(1);
        SerializationHelper.writeSafeUTF(out, this.pop3Server);
        SerializationHelper.writeSafeUTF(out, this.username);
        SerializationHelper.writeSafeUTF(out, this.password);
        out.writeInt(this.updatePeriodType);
        out.writeInt(this.updatePeriods);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        if (ver == 1) {
            this.pop3Server = SerializationHelper.readSafeUTF(in);
            this.username = SerializationHelper.readSafeUTF(in);
            this.password = SerializationHelper.readSafeUTF(in);
            this.updatePeriodType = in.readInt();
            this.updatePeriods = in.readInt();
        }
    }

    public void jsonWrite(ObjectWriter writer) throws IOException, JsonException {
        super.jsonWrite(writer);
        writeUpdatePeriodType(writer, this.updatePeriodType);
    }

    public void jsonRead(JsonReader reader, JsonObject jsonObject) throws JsonException {
        super.jsonRead(reader, jsonObject);
        Integer value = readUpdatePeriodType(jsonObject);
        if (value != null)
            this.updatePeriodType = value.intValue();
    }

    static {
        EVENT_CODES.addElement(1, "INBOX_EXCEPTION");
        EVENT_CODES.addElement(2, "MESSAGE_READ_EXCEPTION");
        EVENT_CODES.addElement(3, "PARSE_EXCEPTION");
    }
}