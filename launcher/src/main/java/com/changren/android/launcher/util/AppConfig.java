package com.changren.android.launcher.util;

import android.text.TextUtils;

/**
 * Author: wangsy
 * Create: 2018-12-12 11:29
 * Description: App配置
 */
public class AppConfig {

    /**
     * 判断用户是否登录
     *
     * @return
     */
    public static boolean isLogin() {
        if (!TextUtils.isEmpty(AppConfig.getToken()) && AppConfig.getLoginFamilyId()>0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取登录后的Token
     * @return 网络请求需要的token参数
     */
    public static String getToken() {
        return SPUtils.getInstance(SPUtils.SP_USER).getString(SPUtils.SP_TOKEN);
    }

    /**
     * 获取登录的家庭ID
     * @return 用于辅助判断是否已登录
     */
    public static int getLoginFamilyId() {
        return SPUtils.getInstance(SPUtils.SP_USER).getInt(SPUtils.SP_LOGIN_FAMILY_ID);
    }

    /**
     * 清楚SP文件中所有内容
     */
    public static void logout() {
        SPUtils.getInstance(SPUtils.SP_USER).clear();
    }

    /**
     * 获取当前show_user,主要用于下次进入时，能准确定位到当前show_user
     * @return
     */
    public static int getShowUserId() {
        return SPUtils.getInstance(SPUtils.SP_USER).getInt(SPUtils.SP_SHOW_USER_ID, 0);
    }

    /**
     * 获取当前登录账号的用户，而不是show_user,主要用于切换家庭时，不会切换成show_user的家庭
     * @return
     */
    public static int getLoginUserId() {
        return SPUtils.getInstance(SPUtils.SP_USER).getInt(SPUtils.SP_LOGIN_USER_ID, 0);
    }
}
