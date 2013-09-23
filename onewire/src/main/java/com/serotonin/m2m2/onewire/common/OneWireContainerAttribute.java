package com.serotonin.m2m2.onewire.common;

import com.serotonin.m2m2.onewire.vo.OneWirePointLocatorVO;

public class OneWireContainerAttribute
{
  private int id;
  private String description;
  private int startIndex;
  private int length;

  public OneWireContainerAttribute(int id)
  {
    this.id = id;
    this.description = OneWirePointLocatorVO.getAttributeDescription(id);
  }

  public OneWireContainerAttribute(int id, int startIndex, int length) {
    this(id);
    this.startIndex = startIndex;
    this.length = length;
  }

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getStartIndex() {
    return this.startIndex;
  }

  public void setStartIndex(int startIndex) {
    this.startIndex = startIndex;
  }

  public int getLength() {
    return this.length;
  }

  public void setLength(int length) {
    this.length = length;
  }
}