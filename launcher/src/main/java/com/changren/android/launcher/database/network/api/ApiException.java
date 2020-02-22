package com.changren.android.launcher.database.network.api;

import com.changren.android.launcher.database.network.HttpConfig;

import java.io.IOException;

/**
 * Author: wangsy
 * Create: 2018-11-27 11:43
 * Description: TODO(描述文件做什么)
 */
public class ApiException extends IOException {

    private int mErrorCode;

    public ApiException(int errorCode, String errorMessage) {
        super(errorMessage);
        mErrorCode = errorCode;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    /**
     * 判断是否已注册
     *
     * @return 失效返回true, 否则返回false;
     */
    public boolean isRegistered() {
        return mErrorCode == HttpConfig.HTTP_PHONE_REGISTERED;
    }
}
