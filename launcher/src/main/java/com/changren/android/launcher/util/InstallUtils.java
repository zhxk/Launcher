package com.changren.android.launcher.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * ------------------------------------------------------------------------------------
 * Copyright (C) 2017-2018, by Shanghai ChangRen Mdt InfoTech Ltd, All rights reserved.
 * ------------------------------------------------------------------------------------
 *
 * @author :  jwl
 * @ClassName :InstallUtils.java
 * @Description :
 * @CreateDate : 2019/2/20 15:01
 * Version : 1.0
 * UpdateHistory :
 */
public class InstallUtils {

    public static boolean isAvilible(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();//获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);//获取所有已安装程序的包信息
        List<String> pName = new ArrayList<String>();//用于存储所有已安装程序的包名
        //从pinfo中将包名字逐一取出，压入pName list中
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);//判断pName中是否有目标程序的包名，有TRUE，没有FALSE
    }
}
