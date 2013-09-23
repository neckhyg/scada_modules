package com.serotonin.m2m2.jviews;

import com.serotonin.ShouldNeverHappenException;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

public class JspComponentState
        implements Cloneable {
    private int id;
    private String value;
    private Long time;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getTime() {
        return this.time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public JspComponentState clone() {
        try {
            return (JspComponentState) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new ShouldNeverHappenException(e);
        }
    }

    public void removeEqualValue(JspComponentState that) {
        if (StringUtils.equals(this.value, that.value))
            this.value = null;
        if (ObjectUtils.equals(this.time, that.time))
            this.time = null;
    }

    public boolean isEmpty() {
        return (this.value == null) && (this.time == null);
    }
}