package com.serotonin.m2m2.opc;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.DataSourceRT;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;
import com.serotonin.m2m2.vo.dataSource.PointLocatorVO;
import com.serotonin.m2m2.vo.event.EventTypeVO;
import com.serotonin.util.SerializationHelper;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class OPCDataSourceVO extends DataSourceVO<OPCDataSourceVO> {
    private static final ExportCodes EVENT_CODES = new ExportCodes();
    private static final long serialVersionUID = -1L;
    private static final int version = 1;
    private int updatePeriodType = 1;
    @JsonProperty
    private int updatePeriods = 15;
    @JsonProperty
    private String host = "localhost";
    @JsonProperty
    private String domain = "";
    @JsonProperty
    private String user = "";
    @JsonProperty
    private String password = "";
    @JsonProperty
    private String server = "";

    protected void addEventTypes(List<EventTypeVO> eventTypes) {
        eventTypes.add(createEventType(1, new TranslatableMessage("event.ds.pointRead")));

        eventTypes.add(createEventType(2, new TranslatableMessage("event.ds.dataSource")));

        eventTypes.add(createEventType(3, new TranslatableMessage("event.ds.dataSource")));
    }

    public DataSourceRT createDataSourceRT() {
        return new OPCDataSourceRT(this);
    }

    public PointLocatorVO createPointLocator() {
        return new OPCPointLocatorVO();
    }

    public TranslatableMessage getConnectionDescription() {
        return new TranslatableMessage("common.noMessage");
    }

    public ExportCodes getEventCodes() {
        return EVENT_CODES;
    }

    public void validate(ProcessResult response) {
        super.validate(response);
        if (StringUtils.isBlank(this.host)) {
            response.addContextualMessage("host", "validate.required", new Object[0]);
        }
        if (StringUtils.isBlank(this.user))
            response.addContextualMessage("user", "validate.required", new Object[0]);
        if (StringUtils.isBlank(this.password))
            response.addContextualMessage("password", "validate.required", new Object[0]);
        if (StringUtils.isBlank(this.server)) {
            response.addContextualMessage("server", "validate.required", new Object[0]);
        }
        if (this.updatePeriods <= 0)
            response.addContextualMessage("updatePeriods", "validate.greaterThanZero", new Object[0]);
    }

    protected void addPropertiesImpl(List<TranslatableMessage> list) {
        AuditEventType.addPeriodMessage(list, "dsEdit.updatePeriod", this.updatePeriodType, this.updatePeriods);
        AuditEventType.addPropertyMessage(list, "dsEdit.opc.host", this.host);
        AuditEventType.addPropertyMessage(list, "dsEdit.opc.domain", this.domain);
        AuditEventType.addPropertyMessage(list, "dsEdit.opc.user", this.user);
        AuditEventType.addPropertyMessage(list, "dsEdit.opc.password", this.password);
        AuditEventType.addPropertyMessage(list, "dsEdit.opc.server", this.server);
    }

    protected void addPropertyChangesImpl(List<TranslatableMessage> list, OPCDataSourceVO from) {
        AuditEventType.maybeAddPeriodChangeMessage(list, "dsEdit.updatePeriod", from.updatePeriodType, from.updatePeriods, this.updatePeriodType, this.updatePeriods);

        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.opc.host", from.host, this.host);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.opc.domain", from.domain, this.domain);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.opc.user", from.user, this.user);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.opc.password", from.password, this.password);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.opc.server", from.server, this.server);
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

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServer() {
        return this.server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    private void writeObject(ObjectOutputStream out)
            throws IOException {
        out.writeInt(1);
        SerializationHelper.writeSafeUTF(out, this.host);
        SerializationHelper.writeSafeUTF(out, this.domain);
        SerializationHelper.writeSafeUTF(out, this.user);
        SerializationHelper.writeSafeUTF(out, this.password);
        SerializationHelper.writeSafeUTF(out, this.server);
        out.writeInt(this.updatePeriodType);
        out.writeInt(this.updatePeriods);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();
        if (ver == 1) {
            this.host = SerializationHelper.readSafeUTF(in);
            this.domain = SerializationHelper.readSafeUTF(in);
            this.user = SerializationHelper.readSafeUTF(in);
            this.password = SerializationHelper.readSafeUTF(in);
            this.server = SerializationHelper.readSafeUTF(in);
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
        EVENT_CODES.addElement(2, "DATA_SOURCE_EXCEPTION");
        EVENT_CODES.addElement(1, "POINT_READ_EXCEPTION");
        EVENT_CODES.addElement(3, "POINT_WRITE_EXCEPTION");
    }
}