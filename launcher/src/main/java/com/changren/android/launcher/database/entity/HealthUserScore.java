package com.changren.android.launcher.database.entity;

/**
 * ------------------------------------------------------------------------------------
 * Copyright (C) 2017-2018, by Shanghai ChangRen Mdt InfoTech Ltd, All rights reserved.
 * ------------------------------------------------------------------------------------
 *
 * @author :  jwl
 * @ClassName :HealthUserScore.java
 * @Description :健康数据页面的实体类
 * @CreateDate : 2019/2/23 15:42
 * Version : 1.0
 * UpdateHistory :
 */
public class HealthUserScore extends DataSource {
    /**
     * systolic_blood_pressure : 130
     * diastolic_blood_pressure : 110
     * blood_pressure_short_tip : 正常
     * glu : 122.0
     * blood_sugar_short_tip : 偏高
     * spo2 : 200.0
     * blood_oxygen_short_tip : 正常
     * temp : 36.0
     * temp_short_tip : 正常
     */


    private int systolic_blood_pressure;
    private int diastolic_blood_pressure;
    private String blood_pressure_short_tip;
    private String glu;
    private String blood_sugar_short_tip;
    private String spo2;
    private String blood_oxygen_short_tip;
    private String temp;
    private String temp_short_tip;

    public HealthUserScore() {
        setItem_type("M");
    }

    public HealthUserScore(int systolic_blood_pressure, int diastolic_blood_pressure,
                           String blood_pressure_short_tip, String glu, String blood_sugar_short_tip, String spo2,
                           String blood_oxygen_short_tip, String temp, String temp_short_tip) {
        this.systolic_blood_pressure = systolic_blood_pressure;
        this.diastolic_blood_pressure = diastolic_blood_pressure;
        this.blood_pressure_short_tip = blood_pressure_short_tip;
        this.glu = glu;
        this.blood_sugar_short_tip = blood_sugar_short_tip;
        this.spo2 = spo2;
        this.blood_oxygen_short_tip = blood_oxygen_short_tip;
        this.temp = temp;
        this.temp_short_tip = temp_short_tip;

        setItem_type("M");
    }

    public int getSystolic_blood_pressure() {
        return systolic_blood_pressure;
    }

    public void setSystolic_blood_pressure(int systolic_blood_pressure) {
        this.systolic_blood_pressure = systolic_blood_pressure;
    }

    public int getDiastolic_blood_pressure() {
        return diastolic_blood_pressure;
    }

    public void setDiastolic_blood_pressure(int diastolic_blood_pressure) {
        this.diastolic_blood_pressure = diastolic_blood_pressure;
    }

    public String getBlood_pressure_short_tip() {
        return blood_pressure_short_tip;
    }

    public void setBlood_pressure_short_tip(String blood_pressure_short_tip) {
        this.blood_pressure_short_tip = blood_pressure_short_tip;
    }

    public String getGlu() {
        return glu;
    }

    public void setGlu(String glu) {
        this.glu = glu;
    }

    public String getBlood_sugar_short_tip() {
        return blood_sugar_short_tip;
    }

    public void setBlood_sugar_short_tip(String blood_sugar_short_tip) {
        this.blood_sugar_short_tip = blood_sugar_short_tip;
    }

    public String getSpo2() {
        return spo2;
    }

    public void setSpo2(String spo2) {
        this.spo2 = spo2;
    }

    public String getBlood_oxygen_short_tip() {
        return blood_oxygen_short_tip;
    }

    public void setBlood_oxygen_short_tip(String blood_oxygen_short_tip) {
        this.blood_oxygen_short_tip = blood_oxygen_short_tip;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getTemp_short_tip() {
        return temp_short_tip;
    }

    public void setTemp_short_tip(String temp_short_tip) {
        this.temp_short_tip = temp_short_tip;
    }

    @Override
    public String toString() {
        return "HealthUserScore{" +
                "systolic_blood_pressure=" + systolic_blood_pressure +
                ", diastolic_blood_pressure=" + diastolic_blood_pressure +
                ", blood_pressure_short_tip='" + blood_pressure_short_tip + '\'' +
                ", glu='" + glu + '\'' +
                ", blood_sugar_short_tip='" + blood_sugar_short_tip + '\'' +
                ", spo2='" + spo2 + '\'' +
                ", blood_oxygen_short_tip='" + blood_oxygen_short_tip + '\'' +
                ", temp='" + temp + '\'' +
                ", temp_short_tip='" + temp_short_tip + '\'' +
                '}';
    }

    @Override
    public void clear() {
        setBlood_oxygen_short_tip(null);
        setBlood_pressure_short_tip(null);
        setBlood_sugar_short_tip(null);
        setDiastolic_blood_pressure(0);
        setSystolic_blood_pressure(0);
        setGlu(null);
        setSpo2(null);
        setTemp(null);
        setTemp_short_tip(null);
    }
}





