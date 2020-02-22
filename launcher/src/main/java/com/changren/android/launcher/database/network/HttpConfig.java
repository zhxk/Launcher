package com.changren.android.launcher.database.network;

public class HttpConfig {

    public static final String BASE_URL = "https://test.changrentech.com/";
//    public static final String BASE_URL = "http://192.168.2.10/";
    public static final String REQUEST_METHOD_GET = "GET";
    public static final String REQUEST_METHOD_POST = "POST";
    public static final String REQUEST_METHOD_DELETE = "DELETE";
    public static final String REQUEST_METHOD_PUT = "PUT";

    public static final String TOKEN = "token";

    public static final String AVATAR_PATH_MALE = "/avatar/user/default_male_middle.png";
    public static final String AVATAR_PATH_FEMALE = "/avatar/user/default_female_middle.png";

    public static final String API_SMS_SEND = "v1/sms/send";
    public static final String API_SMS_CHECK = "v1/sms/check";

    public static final String API_USER_LOGIN_UID = "v1/token/uid";
    public static final String API_USER_REGISTER =  "v1/user/register";
    public static final String API_USER_LOGIN =     "v1/user/login";
    public static final String API_USER_SET_INFO =  "v1/user/set-info";
    public static final String API_USER_UPDATE_INFO =       "v1/user/update-info";
    public static final String API_USER_GET_MEMBER_LIST =   "v1/member/list";
    public static final String API_USER_LOGOUT =            "v1/user/logout";
    public static final String API_USER_MEMBER_SWITCH =     "v1/member/switch";
    public static final String API_USER_BIND_PHONE =        "v1/user/phone-bind";
    public static final String API_USER_MEMBER_REGISTER =   "v1/member/register";
    public static final String API_USER_MEMBER_SET_INFO =  "v1/member/set-info";
    public static final String API_USER_MEMBER_UPDATE_INFO =  "v1/member/update-info";

    public static final String API_FAMILY_CREATE = "v1/family/add";
    public static final String API_FAMILY_LIST = "v1/family/list";

    public static final String API_DISEASE_LIST = "v1/disease/list";

    public static final String API_CHECK_VERSION = "v1/version/detection";
    // add for plan line by shibo.zheng 20190222 start
    public static final String API_GET_USER_SETTINGS = "v1/plan/getUserSettings";
    public static final String API_GET_ARTICLE_LIST = "v1/getArticleListByType";

    // add for plan line by shibo.zheng 20190222 end

    public static final String API_HEALTH_ALL_SCORE = "/v1/getCheckHealthIndexByUser";
    public static final String API_HEALTH_USER_SCORE = "/v1/getLastCheckIndexByUser";

    public static final int DEFAULT_TIMEOUT = 20;

    public static final int HTTP_OK = 0;
    public static final int HTTP_PHONE_REGISTERED = 47001;
    public static final int TOKEN_INVALID = 40001;//token失效
}
