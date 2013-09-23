
package com.serotonin.m2m2.virtual.vo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import com.serotonin.json.spi.JsonProperty;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.event.type.AuditEventType;
import com.serotonin.m2m2.virtual.rt.BrownianChangeRT;
import com.serotonin.m2m2.virtual.rt.ChangeTypeRT;

public class BrownianChangeVO extends ChangeTypeVO {
    public static final TranslatableMessage KEY = new TranslatableMessage("dsEdit.virtual.changeType.brownian");

    @JsonProperty
    private double min;
    @JsonProperty
    private double max;
    @JsonProperty
    private double maxChange;

    @Override
    public int typeId() {
        return Types.BROWNIAN;
    }

    @Override
    public TranslatableMessage getDescription() {
        return KEY;
    }

    @Override
    public ChangeTypeRT createRuntime() {
        return new BrownianChangeRT(this);
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMaxChange() {
        return maxChange;
    }

    public void setMaxChange(double maxChange) {
        this.maxChange = maxChange;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    @Override
    public void addProperties(List<TranslatableMessage> list) {
        super.addProperties(list);
        AuditEventType.addPropertyMessage(list, "dsEdit.virtual.min", min);
        AuditEventType.addPropertyMessage(list, "dsEdit.virtual.max", max);
        AuditEventType.addPropertyMessage(list, "dsEdit.virtual.maxChange", maxChange);
    }

    @Override
    public void addPropertyChanges(List<TranslatableMessage> list, Object o) {
        super.addPropertyChanges(list, o);
        BrownianChangeVO from = (BrownianChangeVO) o;
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.virtual.min", from.min, min);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.virtual.max", from.max, max);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.virtual.maxChange", from.maxChange, maxChange);
    }

    //
    // /
    // / Serialization
    // /
    //
    private static final long serialVersionUID = -1;
    private static final int version = 1;

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(version);
        out.writeDouble(min);
        out.writeDouble(max);
        out.writeDouble(maxChange);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            min = in.readDouble();
            max = in.readDouble();
            maxChange = in.readDouble();
        }
    }
}
