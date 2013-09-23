package com.serotonin.m2m2.persistent.common;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonReader;
import com.serotonin.json.ObjectWriter;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.json.spi.JsonSerializable;
import com.serotonin.json.type.JsonObject;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.DataTypes;
import com.serotonin.m2m2.persistent.ds.PersistentPointLocatorVO;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.view.chart.ChartRenderer;
import com.serotonin.m2m2.view.text.TextRenderer;
import com.serotonin.m2m2.vo.DataPointVO;
import com.serotonin.m2m2.vo.dataSource.PointLocatorVO;
import java.io.IOException;
import java.util.ArrayList;

public class DataPointInfo
  implements JsonSerializable
{

  @JsonProperty
  private String name;

  @JsonProperty
  private String deviceName;
  private int intervalLoggingPeriodType;

  @JsonProperty
  private int intervalLoggingPeriod;
  private int intervalLoggingType;

  @JsonProperty
  private double tolerance;

  @JsonProperty
  private boolean purgeOverride;
  private int purgeType;

  @JsonProperty
  private int purgePeriod;

  @JsonProperty
  private TextRenderer textRenderer;

  @JsonProperty
  private ChartRenderer chartRenderer;

  @JsonProperty
  private int defaultCacheSize;

  @JsonProperty
  private boolean discardExtremeValues;

  @JsonProperty
  private double discardLowLimit;

  @JsonProperty
  private double discardHighLimit;
  private int engineeringUnits;

  @JsonProperty
  private String chartColour;
  private int dataTypeId;

  public DataPointInfo()
  {
  }

  public DataPointInfo(DataPointVO dpvo)
  {
    this.name = dpvo.getName();
    this.deviceName = dpvo.getDeviceName();
    this.intervalLoggingPeriodType = dpvo.getIntervalLoggingPeriodType();
    this.intervalLoggingPeriod = dpvo.getIntervalLoggingPeriod();
    this.intervalLoggingType = dpvo.getIntervalLoggingType();
    this.tolerance = dpvo.getTolerance();
    this.purgeOverride = dpvo.isPurgeOverride();
    this.purgeType = dpvo.getPurgeType();
    this.purgePeriod = dpvo.getPurgePeriod();
    this.textRenderer = dpvo.getTextRenderer();
    this.chartRenderer = dpvo.getChartRenderer();
    this.defaultCacheSize = dpvo.getDefaultCacheSize();
    this.discardExtremeValues = dpvo.isDiscardExtremeValues();
    this.discardLowLimit = dpvo.getDiscardLowLimit();
    this.discardHighLimit = dpvo.getDiscardHighLimit();
    this.engineeringUnits = dpvo.getEngineeringUnits();
    this.chartColour = dpvo.getChartColour();
    this.dataTypeId = dpvo.getPointLocator().getDataTypeId();
  }

  public void updateDpvo(DataPointVO dpvo) {
    dpvo.setName(this.name);
    dpvo.setDeviceName(this.deviceName);
    dpvo.setEngineeringUnits(this.engineeringUnits);
    dpvo.setTextRenderer(this.textRenderer);
    dpvo.setChartRenderer(this.chartRenderer);
    dpvo.setChartColour(this.chartColour);
  }

  public DataPointVO createDpvo(String xid, int dataSourceId) {
    DataPointVO dpvo = new DataPointVO();
    dpvo.setId(-1);
    dpvo.setXid(xid);
    dpvo.setDataSourceId(dataSourceId);
    dpvo.setEnabled(true);
    dpvo.setPointFolderId(0);
    dpvo.setEventDetectors(new ArrayList());
    dpvo.setLoggingType(2);

    PersistentPointLocatorVO locator = new PersistentPointLocatorVO();
    locator.setDataTypeId(this.dataTypeId);
    dpvo.setPointLocator(locator);

    updateDpvo(dpvo);

    return dpvo;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDeviceName() {
    return this.deviceName;
  }

  public void setDeviceName(String deviceName) {
    this.deviceName = deviceName;
  }

  public int getIntervalLoggingPeriodType() {
    return this.intervalLoggingPeriodType;
  }

  public void setIntervalLoggingPeriodType(int intervalLoggingPeriodType) {
    this.intervalLoggingPeriodType = intervalLoggingPeriodType;
  }

  public int getIntervalLoggingPeriod() {
    return this.intervalLoggingPeriod;
  }

  public void setIntervalLoggingPeriod(int intervalLoggingPeriod) {
    this.intervalLoggingPeriod = intervalLoggingPeriod;
  }

  public int getIntervalLoggingType() {
    return this.intervalLoggingType;
  }

  public void setIntervalLoggingType(int intervalLoggingType) {
    this.intervalLoggingType = intervalLoggingType;
  }

  public double getTolerance() {
    return this.tolerance;
  }

  public void setTolerance(double tolerance) {
    this.tolerance = tolerance;
  }

  public boolean isPurgeOverride() {
    return this.purgeOverride;
  }

  public void setPurgeOverride(boolean purgeOverride) {
    this.purgeOverride = purgeOverride;
  }

  public int getPurgeType() {
    return this.purgeType;
  }

  public void setPurgeType(int purgeType) {
    this.purgeType = purgeType;
  }

  public int getPurgePeriod() {
    return this.purgePeriod;
  }

  public void setPurgePeriod(int purgePeriod) {
    this.purgePeriod = purgePeriod;
  }

  public TextRenderer getTextRenderer() {
    return this.textRenderer;
  }

  public void setTextRenderer(TextRenderer textRenderer) {
    this.textRenderer = textRenderer;
  }

  public ChartRenderer getChartRenderer() {
    return this.chartRenderer;
  }

  public void setChartRenderer(ChartRenderer chartRenderer) {
    this.chartRenderer = chartRenderer;
  }

  public int getDefaultCacheSize() {
    return this.defaultCacheSize;
  }

  public void setDefaultCacheSize(int defaultCacheSize) {
    this.defaultCacheSize = defaultCacheSize;
  }

  public boolean isDiscardExtremeValues() {
    return this.discardExtremeValues;
  }

  public void setDiscardExtremeValues(boolean discardExtremeValues) {
    this.discardExtremeValues = discardExtremeValues;
  }

  public double getDiscardLowLimit() {
    return this.discardLowLimit;
  }

  public void setDiscardLowLimit(double discardLowLimit) {
    this.discardLowLimit = discardLowLimit;
  }

  public double getDiscardHighLimit() {
    return this.discardHighLimit;
  }

  public void setDiscardHighLimit(double discardHighLimit) {
    this.discardHighLimit = discardHighLimit;
  }

  public int getEngineeringUnits() {
    return this.engineeringUnits;
  }

  public void setEngineeringUnits(int engineeringUnits) {
    this.engineeringUnits = engineeringUnits;
  }

  public String getChartColour() {
    return this.chartColour;
  }

  public void setChartColour(String chartColour) {
    this.chartColour = chartColour;
  }

  public int getDataTypeId() {
    return this.dataTypeId;
  }

  public void setDataTypeId(int dataTypeId) {
    this.dataTypeId = dataTypeId;
  }

  public void jsonWrite(ObjectWriter writer) throws JsonException, IOException
  {
    writer.writeEntry("intervalLoggingPeriodType", Common.TIME_PERIOD_CODES.getCode(this.intervalLoggingPeriodType));
    writer.writeEntry("intervalLoggingType", DataPointVO.INTERVAL_LOGGING_TYPE_CODES.getCode(this.intervalLoggingType));
    writer.writeEntry("purgeType", Common.TIME_PERIOD_CODES.getCode(this.purgeType));
    writer.writeEntry("engineeringUnits", DataPointVO.ENGINEERING_UNITS_CODES.getCode(this.engineeringUnits));
    writer.writeEntry("dataTypeId", DataTypes.CODES.getCode(this.dataTypeId));
  }

  public void jsonRead(JsonReader reader, JsonObject json) throws JsonException
  {
    this.intervalLoggingPeriodType = Common.TIME_PERIOD_CODES.getId(json.getString("intervalLoggingPeriodType"), new int[0]);
    this.intervalLoggingType = DataPointVO.INTERVAL_LOGGING_TYPE_CODES.getId(json.getString("intervalLoggingType"), new int[0]);
    this.purgeType = Common.TIME_PERIOD_CODES.getId(json.getString("purgeType"), new int[0]);
    this.engineeringUnits = DataPointVO.ENGINEERING_UNITS_CODES.getId(json.getString("engineeringUnits"), new int[0]);
    this.dataTypeId = DataTypes.CODES.getId(json.getString("dataTypeId"), new int[0]);
  }
}