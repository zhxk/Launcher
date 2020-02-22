package com.changren.android.launcher.util;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Author: wangsy
 * Create: 2019-02-26 9:53
 * Description: RxJava工具类
 */
public class RxUtils {

    public interface SuccessCallBack<T> {
        void onSucceed(T data);
    }

    public interface FailureCallBack {
        void onFailed(String msg);
    }

    /**
     * 倒计时60秒
     * @return
     */
    public static Disposable countDown(final SuccessCallBack<Long> successCallBack) {
        return Observable.interval(1, TimeUnit.SECONDS)
            //interval创建的I_Observable监听timer创建T_Observable，T_Observable开始发送数据，I_Observable就不再发送数据。
            .takeUntil(Observable.timer(62, TimeUnit.SECONDS))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<Long>() {
                @Override
                public void accept(Long aLong) throws Exception {
                    successCallBack.onSucceed(aLong);
                }
            });
    }
}
