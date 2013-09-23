package com.serotonin.m2m2.http.rt;

import com.serotonin.m2m2.http.vo.HttpRetrieverPointLocatorVO;
import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class HttpRetrieverPointLocatorRT extends PointLocatorRT
{
  private final Pattern valuePattern;
  private final boolean ignoreIfMissing;
  private final int dataTypeId;
  private String binary0Value;
  private DecimalFormat valueFormat;
  private final Pattern timePattern;
  private final SimpleDateFormat timeFormat;
  private final boolean settable;
  private final String setPointName;

  public HttpRetrieverPointLocatorRT(HttpRetrieverPointLocatorVO vo)
  {
    if (StringUtils.isBlank(vo.getValueRegex()))
      this.valuePattern = null;
    else {
      this.valuePattern = Pattern.compile(vo.getValueRegex());
    }
    this.ignoreIfMissing = vo.isIgnoreIfMissing();
    this.dataTypeId = vo.getDataTypeId();

    if (this.dataTypeId == 1)
      this.binary0Value = vo.getValueFormat();
    else if ((this.dataTypeId == 3) && (!StringUtils.isBlank(vo.getValueFormat()))) {
      this.valueFormat = new DecimalFormat(vo.getValueFormat());
    }
    if (!StringUtils.isBlank(vo.getTimeRegex())) {
      this.timePattern = Pattern.compile(vo.getTimeRegex());
      this.timeFormat = new SimpleDateFormat(vo.getTimeFormat());
    }
    else {
      this.timePattern = null;
      this.timeFormat = null;
    }

    this.settable = vo.isSettable();
    this.setPointName = vo.getSetPointName();
  }

  public boolean isSettable()
  {
    return this.settable;
  }

  public Pattern getValuePattern() {
    return this.valuePattern;
  }

  public boolean isIgnoreIfMissing() {
    return this.ignoreIfMissing;
  }

  public DecimalFormat getValueFormat() {
    return this.valueFormat;
  }

  public int getDataTypeId() {
    return this.dataTypeId;
  }

  public String getBinary0Value() {
    return this.binary0Value;
  }

  public Pattern getTimePattern() {
    return this.timePattern;
  }

  public SimpleDateFormat getTimeFormat() {
    return this.timeFormat;
  }

  public String getSetPointName() {
    return this.setPointName;
  }
}