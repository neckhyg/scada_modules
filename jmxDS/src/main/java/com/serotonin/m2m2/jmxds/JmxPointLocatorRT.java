package com.serotonin.m2m2.jmxds;

import com.serotonin.m2m2.rt.dataImage.types.DataValue;
import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;
import javax.management.ObjectName;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class JmxPointLocatorRT extends PointLocatorRT
{
  private final JmxPointLocatorVO vo;
  private ObjectName objectName;
  private String type;
  static String[] validTypes = { "int", "java.lang.Integer", "long", "java.lang.Long", "java.lang.String", "double", "boolean" };

  public JmxPointLocatorRT(JmxPointLocatorVO vo)
  {
    this.vo = vo;
  }

  public boolean isSettable()
  {
    return this.vo.isSettable();
  }

  public JmxPointLocatorVO getPointLocatorVO() {
    return this.vo;
  }

  public ObjectName getObjectName() {
    return this.objectName;
  }

  public void setObjectName(ObjectName objectName) {
    this.objectName = objectName;
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public boolean isComposite() {
    return !StringUtils.isBlank(this.vo.getCompositeItemName());
  }

  public static boolean isValidType(String type) {
    return ArrayUtils.contains(validTypes, type);
  }

  public Object mangoValueToManagementValue(DataValue value) {
    if (value == null) {
      return null;
    }
    if (("int".equals(this.type)) || ("java.lang.Integer".equals(this.type)))
      return Integer.valueOf(value.getIntegerValue());
    if (("long".equals(this.type)) || ("java.lang.Long".equals(this.type)))
      return Long.valueOf(value.getIntegerValue());
    if ("java.lang.String".equals(this.type))
      return value.getStringValue();
    if ("double".equals(this.type))
      return Double.valueOf(value.getDoubleValue());
    if ("boolean".equals(this.type)) {
      return Boolean.valueOf(value.getBooleanValue());
    }
    return null;
  }

  public DataValue managementValueToMangoValue(Object value) {
    String s = null;
    if (value != null)
      s = value.toString();
    return DataValue.stringToValue(s, this.vo.getDataTypeId());
  }
}