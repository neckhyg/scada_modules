package com.eazytec.cwt;

import java.lang.String;
import java.util.Iterator;
import java.util.List;

public class CwtData {

    //设备号
    String deviceNo;
    //包类型
    PackageType packageType;
    //数字区分的更细的包类型
    int packageNum;
    //时间
    String timeStr;
    //接收到的数据字符串
    String dataStr;
    //是否布防
    boolean arming;
    //是否供电
    boolean power;
    //信号强度
    String signal;
    //内部温度
    double innerTemp;
    //4个AD数据
    double [] adData = new double[4];
    //4个AD数据的报警信息
    short[] adAlarm = new short[4];
    //8个DI数据
    boolean[] diData = new boolean[8];
    //8个DO数据
    boolean[] doData = new boolean[8];
    //读入数据的位数或者是读入数据的序号
    int number;

    public short[] getAdAlarm() {
        return adAlarm;
    }

    public void setAdAlarm(short[] adAlarm) {
        this.adAlarm = adAlarm;
    }

    public double getInnerTemp() {
        return innerTemp;
    }

    public void setInnerTemp(double innerTemp) {
        this.innerTemp = innerTemp;
    }

    public boolean isArming() {
        return arming;
    }

    public void setArming(boolean arming) {
        this.arming = arming;
    }

    public boolean isPower() {
        return power;
    }

    public void setPower(boolean power) {
        this.power = power;
    }

    public String getSignal() {
        return signal;
    }

    public void setSignal(String signal) {
        this.signal = signal;
    }

    public double[] getAdData() {
        return adData;
    }

    public void setAdData(double[] adData) {
        this.adData = adData;
    }

    public boolean[] getDiData() {
        return diData;
    }

    public void setDiData(boolean[] diData) {
        this.diData = diData;
    }

    public boolean[] getDoData() {
        return doData;
    }

    public void setDoData(boolean[] doData) {
        this.doData = doData;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public PackageType getPackageType() {
        return packageType;
    }

    public void setPackageType(PackageType packageType) {
        this.packageType = packageType;
    }

    public String getTimeStr() {
        return timeStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }

    public void print(){
        if(deviceNo != null)
            System.out.println("Device No: " + deviceNo);
        System.out.println("Package Type: " + packageType.toString());
        System.out.println("Package Number: " + packageNum);
        if(timeStr != null)
            System.out.println("Time String: " + timeStr);
        System.out.println("Arming:" + arming);
        System.out.println("Inner Temperatur:" + innerTemp);
        if(signal != null)
            System.out.println("Signal:" + signal);
        System.out.println("Power:" + power);
        for(boolean b : diData){
            System.out.print(b + " ");
        }
        System.out.println();
        for(boolean b : doData){
            System.out.print(b + " " );
        }
        System.out.println();
        for(short b : adAlarm){
            System.out.print(b + " " );
        }
        System.out.println();
        for(double d : adData){
            System.out.print(d + " " );
        }
        System.out.println();
    }

    public String getDataStr() {
        return dataStr;
    }

    public void setDataStr(String dataStr) {
        this.dataStr = dataStr;
    }

    public int getPackageNum() {
        return packageNum;
    }

    public void setPackageNum(int packageNum) {
        this.packageNum = packageNum;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
