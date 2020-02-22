package com.changren.android.upgrade.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.changren.android.launcher.util.LogUtils;

import java.io.File;

/**
 * Author: wangsy
 * Create: 2019-01-17 16:39
 * Description: TODO(描述文件做什么)
 */
public class FileUtils {

    public static String getDownloadApkCachePath() {
        String appCachePath = null;
        if (checkSDCard()) {
            appCachePath = Environment.getExternalStorageDirectory() + "/Launcher/";
        } else {
            appCachePath = Environment.getDataDirectory().getPath() + "/Launcher/";
        }
        File file = new File(appCachePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return appCachePath;
    }

    private static boolean checkSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取app缓存路径
     *
     * @param context
     * @return
     */
    public String getCachePath(Context context) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //外部存储可用
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            //外部存储不可用
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    /**
     *
     * @param context
     * @param downloadPath  apk的完整路径
     * @param newestVersionCode 开发者认为的最新的版本号
     * @return
     */
    public static boolean checkAPKIsExists(Context context, String downloadPath, int newestVersionCode) {
        File file = new File(downloadPath);
        boolean result = false;
        if (file.exists()) {
            try {
                PackageManager pm = context.getPackageManager();
                PackageInfo info = pm.getPackageArchiveInfo(downloadPath, PackageManager.GET_ACTIVITIES);
                int curVersionCode = DeviceUtils.getVersionCode(context);
                //判断安装包存在并且包名一样并且版本号不一样
                LogUtils.e("本地安装包版本号：" + info.versionCode + "\n 当前app版本号：" + curVersionCode);
                if (context.getPackageName().equalsIgnoreCase(info.packageName)
                        && curVersionCode != info.versionCode) {
                    //判断开发者传入的最新版本号是否大于缓存包的版本号，大于那么相当于没有缓存
                    if (newestVersionCode > 0 && info.versionCode < newestVersionCode) {
                        result = false;
                    } else {
                        result = true;
                    }
                }
            } catch (Exception e) {
                result = false;
            }
        }
        return result;

    }

    public static void deleteApk(Context context, String downloadPath) {
        if (null == downloadPath) {
            return;
        }

        File downloadFile = new File(downloadPath);
        if (downloadFile.exists()) {
            downloadFile.delete();
        }
    }

}
