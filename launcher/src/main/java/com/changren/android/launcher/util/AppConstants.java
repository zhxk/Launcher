package com.changren.android.launcher.util;

/**
 * Author: wangsy
 * Create: 2019-02-28 18:25
 * Description: 存放全局的静态常量
 */
public class AppConstants {

    public static final String FID = "fid";

    public static final String FID_LIST = "fid_list";

    public static final String UID = "uid";

    public static final String RESTART = "restart";

    public static final String ILLS_LIST = "ills_list";

    public static final String RESTART_LOGIN = "restart_login";

    public static final String USER_PROFILE = "profile";

    public static final String REGISTER_TYPE = "register_type";

    /**
     * 新用户注册
     */
    public static final int USER_REGISTER = 0x1001;
    /**
     * 家庭新成员注册
     */
    public static final int MEMBER_REGISTER = 0x1002;

    /** "虚拟按键(home/recent app/锁屏/长按Home键)"监听广播接收者的action值 */
    public static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    public static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    public static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    public static final String SYSTEM_DIALOG_REASON_LOCK = "lock";
    public static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";

    public static final int TYPE_REMIND_MEDICATION = 1; //服药提醒
    public static final int TYPE_DEFAULT = 6; //空白默认
    public static final int TYPE_REMIND_BP = 3;         //血压检测提醒
    public static final int TYPE_REMIND_WEIGHT = 4;     //体重检测提醒
    public static final int TYPE_REMIND_GLU = 5;        //血糖检测提醒

    public static final String PLAN_PACKAGE_NAME = "com.changren.android.healthplan2";//健康计划app包名
    public static final String PLAN_MAIN_CLASS_NAME = "com.changren.android.healthplan2.activity.MainActivity"; //主页
    public static final String REMIND_MEDICATION_CLASS_NAME = "com.changren.android.healthplan2.activity.GetRemindInfo"; //服药提醒
    public static final String REMIND_DETECT_CLASS_NAME = "com.changren.android.healthplan2.activity.RemindDetectActivity"; //检测提醒
    public static final String AITICLE_DETAIL_CLASS_NAME = "com.changren.android.healthplan2.activity.ArticleDetailActivity"; //检测提醒

    public static final String MONITOR_PACKAGE_NAME = "com.changren.android.healthmonitor";//健康检测app包名
    public static final String MONITOR_GENERAL_REPORT = "com.changren.android.healthmonitor.ui.activity.GeneralReportActivity";//综合报告页
    public static final String MONITOR_MAIN = "com.changren.android.healthmonitor.MainActivity";//app主界面
    public static final String MONITOR_EXAMINATION = "com.changren.android.healthmonitor.ui.activity.MonitorActivity";//参数检测界面
}
