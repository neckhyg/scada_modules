package com.eazy.eazySerial;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-9-23
 * Time: 下午1:43
 * To change this template use File | Settings | File Templates.
 */
public class UPSData {
    double inputVolt;
    double inputFaultVolt;
    double outputVolt;
    double loaderValue;
    double inputFreq;
    double battVolt;
    double temperature;
    boolean upsFaultStatus;
    boolean battStatus;
    boolean testStatus;
    boolean byPassStatus;
    boolean battVoltStatus;
    boolean shutdownStatus;
    boolean standbyStatus;
    double[] monitorData  = new double[7] ;
    int[] monitorStatus = new int[8];

    public double getInputVolt() {
        return inputVolt;
    }

    public void setInputVolt(double inputVolt) {
        this.inputVolt = inputVolt;
    }

    public double getInputFaultVolt() {
        return inputFaultVolt;
    }

    public void setInputFaultVolt(double inputFaultVolt) {
        this.inputFaultVolt = inputFaultVolt;
    }

    public double getOutputVolt() {
        return outputVolt;
    }

    public void setOutputVolt(double outputVolt) {
        this.outputVolt = outputVolt;
    }

    public double getLoaderValue() {
        return loaderValue;
    }

    public void setLoaderValue(double loaderValue) {
        this.loaderValue = loaderValue;
    }

    public double getInputFreq() {
        return inputFreq;
    }

    public void setInputFreq(double inputFreq) {
        this.inputFreq = inputFreq;
    }

    public double getBattVolt() {
        return battVolt;
    }

    public void setBattVolt(double battVolt) {
        this.battVolt = battVolt;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public boolean isUpsFaultStatus() {
        return upsFaultStatus;
    }

    public void setUpsFaultStatus(boolean upsFaultStatus) {
        this.upsFaultStatus = upsFaultStatus;
    }

    public boolean isBattStatus() {
        return battStatus;
    }

    public void setBattStatus(boolean battStatus) {
        this.battStatus = battStatus;
    }

    public boolean isTestStatus() {
        return testStatus;
    }

    public void setTestStatus(boolean testStatus) {
        this.testStatus = testStatus;
    }

    public boolean isByPassStatus() {
        return byPassStatus;
    }

    public void setByPassStatus(boolean byPassStatus) {
        this.byPassStatus = byPassStatus;
    }

    public boolean isBattVoltStatus() {
        return battVoltStatus;
    }

    public void setBattVoltStatus(boolean battVoltStatus) {
        this.battVoltStatus = battVoltStatus;
    }

    public boolean isShutdownStatus() {
        return shutdownStatus;
    }

    public void setShutdownStatus(boolean shutdownStatus) {
        this.shutdownStatus = shutdownStatus;
    }

    public boolean isStandbyStatus() {
        return standbyStatus;
    }

    public void setStandbyStatus(boolean standbyStatus) {
        this.standbyStatus = standbyStatus;
    }

    public double[] getMonitorData() {
        return monitorData;
    }

    public void setMonitorData(double[] monitorData) {
        this.monitorData = monitorData;
    }

    public int[] getMonitorStatus() {
        return monitorStatus;
    }

    public void setMonitorStatus(int[] monitorStatus) {
        this.monitorStatus = monitorStatus;
    }
}
