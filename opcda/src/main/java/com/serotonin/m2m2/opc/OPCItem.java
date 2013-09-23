package com.serotonin.m2m2.opc;

public class OPCItem {
    private String tag;
    private int dataType;
    private boolean settable;
    private boolean validate;

    public OPCItem(String tag, int dataType, boolean settable) {
        this.tag = tag;
        this.dataType = dataType;
        this.settable = settable;
    }

    public boolean isValidate() {
        return this.validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getDataType() {
        return this.dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public boolean isSettable() {
        return this.settable;
    }

    public void setSettable(boolean settable) {
        this.settable = settable;
    }
}