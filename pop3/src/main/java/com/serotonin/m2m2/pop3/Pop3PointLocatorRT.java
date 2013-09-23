package com.serotonin.m2m2.pop3;

import com.serotonin.m2m2.rt.dataSource.PointLocatorRT;
import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class Pop3PointLocatorRT extends PointLocatorRT {
    private final boolean findInSubject;
    private final Pattern valuePattern;
    private final boolean ignoreIfMissing;
    private final int dataTypeId;
    private String binary0Value;
    private DecimalFormat valueFormat;
    private final Pattern timePattern;
    private final boolean useReceivedTime;
    private final SimpleDateFormat timeFormat;
    private final boolean settable;

    public Pop3PointLocatorRT(Pop3PointLocatorVO vo) {
        this.findInSubject = vo.isFindInSubject();
        this.valuePattern = Pattern.compile(vo.getValueRegex());
        this.ignoreIfMissing = vo.isIgnoreIfMissing();
        this.dataTypeId = vo.getDataTypeId();

        if (this.dataTypeId == 1)
            this.binary0Value = vo.getValueFormat();
        else if ((this.dataTypeId == 3) && (!StringUtils.isBlank(vo.getValueFormat()))) {
            this.valueFormat = new DecimalFormat(vo.getValueFormat());
        }
        this.useReceivedTime = vo.isUseReceivedTime();
        if ((!this.useReceivedTime) && (!StringUtils.isBlank(vo.getTimeRegex()))) {
            this.timePattern = Pattern.compile(vo.getTimeRegex());
            this.timeFormat = new SimpleDateFormat(vo.getTimeFormat());
        } else {
            this.timePattern = null;
            this.timeFormat = null;
        }

        this.settable = vo.isSettable();
    }

    public boolean isSettable() {
        return this.settable;
    }

    public boolean isFindInSubject() {
        return this.findInSubject;
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

    public boolean isUseReceivedTime() {
        return this.useReceivedTime;
    }

    public Pattern getTimePattern() {
        return this.timePattern;
    }

    public SimpleDateFormat getTimeFormat() {
        return this.timeFormat;
    }
}