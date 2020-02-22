package com.changren.android.launcher.database;

import io.reactivex.disposables.Disposable;

/**
 * Author: wangsy
 * Create: 2018-12-13 16:09
 * Description: TODO(描述文件做什么)
 */
public interface UserDataSource {

    interface DataCallBack<T> {
        void onSucceed(Disposable d, T data);
        void onFailed(Disposable d,String msg);
    }
}
