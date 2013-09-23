
package com.serotonin.m2m2.gviews.component;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.spi.JsonSerializable;
import com.serotonin.json.spi.TypeResolver;
import com.serotonin.json.type.JsonObject;
import com.serotonin.json.type.JsonValue;
import com.serotonin.m2m2.db.dao.DataPointDao;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableJsonException;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.view.ImplDefinition;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.m2m2.vo.User;
import com.serotonin.util.SerializationHelper;


abstract public class ViewComponent implements Serializable, JsonSerializable {
    private static List<ImplDefinition> DEFINITIONS;

    public static List<ImplDefinition> getImplementations() {
        if (DEFINITIONS == null) {
            List<ImplDefinition> d = new ArrayList<ImplDefinition>();
            d.add(AnalogGraphicComponent.DEFINITION);
            d.add(BinaryGraphicComponent.DEFINITION);
            d.add(DynamicGraphicComponent.DEFINITION);
            d.add(HtmlComponent.DEFINITION);
            d.add(MultistateGraphicComponent.DEFINITION);
            d.add(ScriptComponent.DEFINITION);
            d.add(SimpleImageComponent.DEFINITION);
            d.add(SimplePointComponent.DEFINITION);
            d.add(ThumbnailComponent.DEFINITION);
            d.add(SimpleCompoundComponent.DEFINITION);
            d.add(ImageChartComponent.DEFINITION);
            d.add(WirelessTempHumSensor.DEFINITION);
            DEFINITIONS = d;
        }

        return DEFINITIONS;
    }

    public static ViewComponent newInstance(String name) {
        ImplDefinition def = ImplDefinition.findByName(getImplementations(), name);
        try {
            return resolveClass(def).newInstance();
        }
        catch (Exception e) {
            throw new ShouldNeverHappenException("Error finding component with name '" + name + "': " + e.getMessage());
        }
    }

    static Class<? extends ViewComponent> resolveClass(ImplDefinition def) {
        if (def == AnalogGraphicComponent.DEFINITION)
            return AnalogGraphicComponent.class;
        if (def == BinaryGraphicComponent.DEFINITION)
            return BinaryGraphicComponent.class;
        if (def == DynamicGraphicComponent.DEFINITION)
            return DynamicGraphicComponent.class;
        if (def == HtmlComponent.DEFINITION)
            return HtmlComponent.class;
        if (def == MultistateGraphicComponent.DEFINITION)
            return MultistateGraphicComponent.class;
        if (def == ScriptComponent.DEFINITION)
            return ScriptComponent.class;
        if (def == SimpleImageComponent.DEFINITION)
            return SimpleImageComponent.class;
        if (def == SimplePointComponent.DEFINITION)
            return SimplePointComponent.class;
        if (def == ThumbnailComponent.DEFINITION)
            return ThumbnailComponent.class;
        if (def == SimpleCompoundComponent.DEFINITION)
            return SimpleCompoundComponent.class;
        if (def == ImageChartComponent.DEFINITION)
            return ImageChartComponent.class;
        if (def == WirelessTempHumSensor.DEFINITION)
            return WirelessTempHumSensor.class;
        return null;
    }

    public static List<String> getExportTypes() {
        List<ImplDefinition> definitions = getImplementations();
        List<String> result = new ArrayList<String>(definitions.size());
        for (ImplDefinition def : definitions)
            result.add(def.getExportName());
        return result;
    }

    private int index;
    private String idSuffix;
    private String style;
    @JsonProperty
    private int x;
    @JsonProperty
    private int y;

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    abstract public ImplDefinition definition();

    abstract public void validateDataPoint(User user, boolean makeReadOnly);

    abstract public boolean isVisible();

    abstract public boolean isValid();

    abstract public boolean containsValidVisibleDataPoint(int dataPointId);

    public boolean isPointComponent() {
        return false;
    }

    public boolean isCompoundComponent() {
        return false;
    }

    public String getDefName() {
        return definition().getName();
    }

    public String getId() {
        if (StringUtils.isBlank(idSuffix))
            return Integer.toString(index);
        return Integer.toString(index) + idSuffix;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getIdSuffix() {
        return idSuffix;
    }

    public void setIdSuffix(String idSuffix) {
        this.idSuffix = idSuffix;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getStyle() {
        if (style != null)
            return style;

        StringBuilder sb = new StringBuilder();
        sb.append("position:absolute;");
        sb.append("left:").append(x).append("px;");
        sb.append("top:").append(y).append("px;");
        return sb.toString();
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public void validate(ProcessResult response) {
        if (x < 0)
            response.addMessage("x", new TranslatableMessage("validate.cannotBeNegative"));
        if (y < 0)
            response.addMessage("y", new TranslatableMessage("validate.cannotBeNegative"));
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
        out.writeInt(index);
        SerializationHelper.writeSafeUTF(out, idSuffix);
        out.writeInt(x);
        out.writeInt(y);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            index = in.readInt();
            idSuffix = SerializationHelper.readSafeUTF(in);
            x = in.readInt();
            y = in.readInt();
        }
    }

    protected void writeDataPoint(ObjectOutputStream out, DataPointVO dataPoint) throws IOException {
        if (dataPoint == null)
            out.writeInt(0);
        else
            out.writeInt(dataPoint.getId());
    }

    protected DataPointVO readDataPoint(ObjectInputStream in) throws IOException {
        return new DataPointDao().getDataPoint(in.readInt());
    }

    /**
     * @throws JsonException
     */
    @Override
    public void jsonWrite(ObjectWriter writer) throws IOException, JsonException {
        writer.writeEntry("type", definition().getExportName());
    }

    @Override
    public void jsonRead(JsonReader reader, JsonObject jsonObject) throws JsonException {
        // no op
    }

    protected void jsonWriteDataPoint(ObjectWriter writer, String key, PointComponent comp) throws IOException,
            JsonException {
        DataPointVO dataPoint = comp.tgetDataPoint();
        if (dataPoint == null)
            writer.writeEntry(key, null);
        else
            writer.writeEntry(key, dataPoint.getXid());
    }

    protected void jsonWriteDataPoint(Map<String, Object> map, String key, PointComponent comp) {
        DataPointVO dataPoint = comp.tgetDataPoint();
        if (dataPoint == null)
            map.put(key, null);
        else
            map.put(key, dataPoint.getXid());
    }

    protected void jsonReadDataPoint(JsonValue jsonXid, PointComponent comp) throws JsonException {
        if (jsonXid != null) {
            String xid = jsonXid.toString();
            DataPointVO dataPoint = new DataPointDao().getDataPoint(xid);
            if (dataPoint == null)
                throw new TranslatableJsonException("emport.error.missingPoint", xid);
            if (!comp.definition().supports(dataPoint.getPointLocator().getDataTypeId()))
                throw new TranslatableJsonException("emport.error.component.incompatibleDataType", xid, definition()
                        .getExportName());
            comp.tsetDataPoint(dataPoint);
        }
    }

    public static class Resolver implements TypeResolver {
        @Override
        public Type resolve(JsonValue jsonValue) throws JsonException {
            JsonObject json = jsonValue.toJsonObject();

            String type = json.getString("type");
            if (type == null)
                throw new TranslatableJsonException("emport.error.component.missing", "type", getExportTypes());

            ImplDefinition def = ImplDefinition.findByExportName(getImplementations(), type);

            if (def == null)
                throw new TranslatableJsonException("emport.error.text.invalid", "type", type, getExportTypes());

            return resolveClass(def);
        }
    }
}
