package com.changren.android.launcher.database.entity;

import com.changren.android.launcher.database.network.HttpConfig;

/**
 * Author: wangsy
 * Create: 2018-11-23 15:31
 * Description: 响应报文实体类，用于框架Gson解析
 */
public class Response {

//    "code":0,
//    "msg":"请求成功",
//    "data":{"key":"value"} || "data":""

    private int code;
    private String msg;
//    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSuccess() {
        return code == HttpConfig.HTTP_OK;
    }

    //    public T getData() {
//        return data;
//    }
//
//    public void setData(T data) {
//        this.data = data;
//    }
}
