package com.eazytec.cwt;

import com.serotonin.json.spi.JsonProperty;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.dataSource.DataSourceRT;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.vo.dataSource.DataSourceVO;
import com.serotonin.m2m2.vo.dataSource.PointLocatorVO;
import com.serotonin.m2m2.vo.event.EventTypeVO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class CwtDataSourceVO extends DataSourceVO<CwtDataSourceVO> {
    private static final ExportCodes EVENT_CODES = new ExportCodes();

    @JsonProperty
    private int port=502;

    @JsonProperty
    private int bufferSize=2048;

    @JsonProperty
    private int idleTime=10;

    public TranslatableMessage getConnectionDescription() {
        return new TranslatableMessage("dsEdit.cwt.dsconn", new Object[]{Integer.valueOf(this.port)});
    }

    public PointLocatorVO createPointLocator() {
        return new CwtPointLocatorVO();
    }

    public DataSourceRT createDataSourceRT() {
        return new CwtDataSourceRT(this);
    }

    public ExportCodes getEventCodes() {
        return EVENT_CODES;
    }

    protected void addEventTypes(List<EventTypeVO> eventTypes) {
        eventTypes.add(createEventType(1, new TranslatableMessage("event.ds.dataSource")));
    }

    protected void addPropertiesImpl(List<TranslatableMessage> list) {
        AuditEventType.addPropertyMessage(list, "dsEdit.cwt.port", Integer.valueOf(this.port));
        AuditEventType.addPropertyMessage(list, "dsEdit.cwt.bufferSize", Integer.valueOf(this.bufferSize));
        AuditEventType.addPropertyMessage(list, "dsEdit.cwt.idleTime", Integer.valueOf(this.idleTime));

    }

    protected void addPropertyChangesImpl(List<TranslatableMessage> list, CwtDataSourceVO from) {
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.cwt.port", from, Integer.valueOf(this.port));
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.cwt.bufferSize", from, Integer.valueOf(this.bufferSize));
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.cwt.idleTime", from, Integer.valueOf(this.idleTime));
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public int getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(int idleTime) {
        this.idleTime = idleTime;
    }

    public void validate(ProcessResult result) {
        super.validate(result);
        if ((this.port <= 0) || (this.port > 65535)) {
            result.addContextualMessage("port", "validate.invalidValue", new Object[0]);
        }
    }

    private void writeObject(ObjectOutputStream out)
            throws IOException {
        out.writeInt(2);
        out.writeInt(this.port);
        out.writeInt(this.bufferSize);
        out.writeInt(this.idleTime);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        if (ver == 1) {
            this.port = in.readInt();
            this.bufferSize = in.readInt();
            this.idleTime = in.readInt();
        } else if (ver == 2) {
            this.port = in.readInt();
            this.bufferSize = in.readInt();
            this.idleTime = in.readInt();
        }
    }

    static {
        EVENT_CODES.addElement(1, "DATA_SOURCE_EXCEPTION_EVENT");
    }
}
