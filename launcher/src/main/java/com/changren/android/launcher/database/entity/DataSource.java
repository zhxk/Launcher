package com.changren.android.launcher.database.entity;

/**
 * ------------------------------------------------------------------------------------
 * Copyright (C) 2017-2018, by Shanghai ChangRen Mdt InfoTech Ltd, All rights reserved.
 * ------------------------------------------------------------------------------------
 *
 * @author :  jwl
 * @ClassName :DataSource.java
 * @Description :
 * @CreateDate : 2019/2/27 15:19
 * Version : 1.0
 * UpdateHistory :
 */
public abstract class DataSource {

    private String item_type;

    public String getItem_type() {
        return item_type;
    }

    public void setItem_type(String item_type) {
        this.item_type = item_type;
    }

    public abstract void clear();
}
