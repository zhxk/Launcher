package com.changren.android.launcher.database.entity;

/**
 * ------------------------------------------------------------------------------------
 * Copyright (C) 2017-2018, by Shanghai ChangRen Mdt InfoTech Ltd, All rights reserved.
 * ------------------------------------------------------------------------------------
 *
 * @author :  jwl
 * @ClassName :HealthScore.java
 * @Description :健康指数实体类
 * @CreateDate : 2019/2/22 16:44
 * Version : 1.0
 * UpdateHistory :
 */
public class HealthScore {


    private int index;
    private int score;
    private String tip;

    @Override
    public String toString() {
        return "HealthScore{" +
                "index=" + index +
                ", score=" + score +
                ", tip='" + tip + '\'' +
                '}';
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }


}
