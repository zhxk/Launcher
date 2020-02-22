package com.changren.android.upgrade;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.changren.android.launcher.R;
import com.changren.android.launcher.database.network.FileDownLoadObserver;
import com.changren.android.launcher.database.network.RetrofitClient;
import com.changren.android.launcher.database.network.api.ApiService;
import com.changren.android.launcher.util.LogUtils;
import com.changren.android.launcher.util.ToastUtils;
import com.changren.android.upgrade.entity.VersionConfig;
import com.changren.android.upgrade.util.DeviceUtils;
import com.changren.android.upgrade.util.FileUtils;

import java.io.File;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Author: wangsy
 * Create: 2019-01-16 19:00
 * Description: 下载apk工具类
 */
public class DownloadManager {

    private static final int APP_ID = 10002;
    private static DownloadManager INSTANCE = null;

    private ApiService apiService;
    private Context mContext;
    private boolean isUpdate = true;
    private boolean isShowMsg = false;

    interface DownloadListener<T> {
        void onSucceed(Disposable d, T data);
        void onFailed(Disposable d, String msg);
    }

    public DownloadManager() {
        apiService = RetrofitClient.getInstance().getApiService();
    }

    public static DownloadManager getInstance() {
        if (INSTANCE == null) {
            synchronized (DownloadManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DownloadManager();
                }
            }
        }
        return INSTANCE;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }

    public boolean isShowMsg() {
        return isShowMsg;
    }

    public void setShowMsg(boolean showMsg) {
        isShowMsg = showMsg;
    }

    public void doUpgrade() {
        if (mContext == null) {
            throw new NullPointerException("DownloadManager Context must be not null");
        }

        if (!isUpdate()) {
            return;
        }

        if (DeviceUtils.checkNetwork(mContext)) {
            LogUtils.d("开始检查新版本信息...");
            checkVersion(DeviceUtils.getVersionName(mContext), APP_ID,
                new DownloadManager.DownloadListener<VersionConfig>() {
                    @Override
                    public void onSucceed(Disposable d, VersionConfig data) {
                        if (data != null && !TextUtils.isEmpty(data.getUrl())) {
                            processCheckResult(data);
                        } else {
                            FileUtils.deleteApk(mContext, FileUtils.getDownloadApkCachePath() + "launcher.apk");
                            if (isShowMsg()) {
                                ToastUtils.showLong(R.string.upgrade_latest);
                                setShowMsg(false);
                            }
                        }
                        d.dispose();
                    }

                    @Override
                    public void onFailed(Disposable d, String msg) {
                        LogUtils.e("版本校验失败" + msg);
                        ToastUtils.showLong("版本校验失败" + msg);
                        d.dispose();
                    }
                });
        } else {
            ToastUtils.showLong("网络异常");
        }
    }

    public void checkVersion(String curVersion, int appId, final DownloadListener<VersionConfig> listener) {
        apiService.checkVersion(curVersion, appId)
            .subscribe(new Observer<VersionConfig>() {
                private Disposable disposable;

                @Override
                public void onSubscribe(Disposable d) {
                    disposable = d;
                }

                @Override
                public void onNext(VersionConfig result) {
                    listener.onSucceed(disposable, result);
                }

                @Override
                public void onError(Throwable e) {
                    listener.onFailed(disposable, e.getMessage());
                }

                @Override
                public void onComplete() {
                    LogUtils.i("完成");
                }
            });
    }

    public void processCheckResult(@NonNull VersionConfig result) {
        result.setApkPath(FileUtils.getDownloadApkCachePath());
        result.setApkName("launcher.apk");

        //弹窗提示有新版本
        Intent intent = new Intent(mContext, UpdatePromptsActivity.class);
        intent.putExtra(UpdatePromptsActivity.VERSION_DATA, result);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);

        //检查本地是否存在服务器上的最新apk
    }

    public Disposable downloadApk(VersionConfig versionConfig, FileDownLoadObserver<File> observer) {
        if (DeviceUtils.checkNetwork(mContext)) {
            LogUtils.d("开始下载Apk...", versionConfig.getApkPath());
            return downloadFile(versionConfig.getUrl(), versionConfig.getApkPath(), versionConfig.getApkName(), observer);
        } else {
            ToastUtils.showLong("下载文件时，网络异常");
            return null;
        }
    }

    /**
     * 下载单文件，该方法不支持断点下载
     *
     * @param url       文件地址
     * @param destDir   存储文件夹
     * @param fileName  存储文件名
     * @param callback  监听回调
     */
    private Disposable downloadFile(@NonNull String url, final String destDir, final String fileName,
                             final FileDownLoadObserver<File> callback) {
        return apiService.downLoadFile(url)
            .subscribeOn(Schedulers.io())//subscribeOn和observeOn必须在io线程，如果在主线程会出错
            .observeOn(Schedulers.io())
            .observeOn(Schedulers.computation())//需要
            .map(new Function<ResponseBody, File>() {
                @Override
                public File apply(@NonNull ResponseBody responseBody) throws Exception {
                    return callback.saveFile(responseBody, destDir, fileName);
                }
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<File>() {
                @Override
                public void accept(File file) throws Exception {
                    callback.onDownLoadSuccess(file);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    callback.onDownLoadFail(throwable);
                }
            }, new Action() {
                @Override
                public void run() throws Exception {
                    callback.onComplete();
                }
            });
    }

}