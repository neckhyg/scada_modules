package com.serotonin.ma.bacnet;

import com.serotonin.bacnet4j.type.enumerated.LifeSafetyState;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import java.util.ArrayList;
import java.util.List;

public class BACnetObjectBean
{
  private int objectTypeId;
  private int instanceNumber;
  private String objectTypeDescription;
  private String objectName;
  private String presentValue;
  private boolean cov;
  private int dataTypeId;
  private List<String> unitsDescription = new ArrayList();

  public String getPrettyPresentValue() {
    if ((this.objectTypeId == ObjectType.binaryInput.intValue()) || (this.objectTypeId == ObjectType.binaryOutput.intValue()) || (this.objectTypeId == ObjectType.binaryValue.intValue()))
    {
      if (("0".equals(this.presentValue)) && (this.unitsDescription.size() > 0))
        return (String)this.unitsDescription.get(0);
      if (("1".equals(this.presentValue)) && (this.unitsDescription.size() > 1))
        return (String)this.unitsDescription.get(1);
    }
    else if ((this.objectTypeId == ObjectType.multiStateInput.intValue()) || (this.objectTypeId == ObjectType.multiStateOutput.intValue()) || (this.objectTypeId == ObjectType.multiStateValue.intValue()))
    {
      try
      {
        int index = Integer.parseInt(this.presentValue) - 1;
        if ((index >= 0) && (index < this.unitsDescription.size()))
          return (String)this.unitsDescription.get(index);
      }
      catch (NumberFormatException e)
      {
      }
    }
    else if ((this.objectTypeId == ObjectType.lifeSafetyPoint.intValue()) || (this.objectTypeId == ObjectType.lifeSafetyZone.intValue()))
    {
      try {
        int index = Integer.parseInt(this.presentValue);
        return new LifeSafetyState(index).toString();
      }
      catch (NumberFormatException e)
      {
      }
    }
    else if (this.unitsDescription.size() > 0) {
      return this.presentValue + " " + (String)this.unitsDescription.get(0);
    }
    return this.presentValue;
  }

  public int getObjectTypeId() {
    return this.objectTypeId;
  }

  public void setObjectTypeId(int objectTypeId) {
    this.objectTypeId = objectTypeId;
  }

  public int getInstanceNumber() {
    return this.instanceNumber;
  }

  public void setInstanceNumber(int instanceNumber) {
    this.instanceNumber = instanceNumber;
  }

  public String getObjectTypeDescription() {
    return this.objectTypeDescription;
  }

  public void setObjectTypeDescription(String objectTypeDescription) {
    this.objectTypeDescription = objectTypeDescription;
  }

  public String getObjectName() {
    return this.objectName;
  }

  public void setObjectName(String objectName) {
    this.objectName = objectName;
  }

  public int getDataTypeId() {
    return this.dataTypeId;
  }

  public void setDataTypeId(int dataTypeId) {
    this.dataTypeId = dataTypeId;
  }

  public List<String> getUnitsDescription() {
    return this.unitsDescription;
  }

  public void setUnitsDescription(List<String> unitsDescription) {
    this.unitsDescription = unitsDescription;
  }

  public String getPresentValue() {
    return this.presentValue;
  }

  public void setPresentValue(String presentValue) {
    this.presentValue = presentValue;
  }

  public boolean isCov() {
    return this.cov;
  }

  public void setCov(boolean cov) {
    this.cov = cov;
  }
}