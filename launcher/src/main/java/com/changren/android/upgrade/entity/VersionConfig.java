package com.changren.android.upgrade.entity;

import java.io.Serializable;

/**
 * Author: wangsy
 * Create: 2019-01-16 16:26
 * Description: 版本升级信息
 */
public class VersionConfig implements Serializable {

    //最新发布版本号
    private String version;
    //是否强制升级 1：是 0：否
    private int must_update;
    //升级提示文字
    private String update_content;
    //app下载地址
    private String url;
    //apk下载的缓存路径
    private String apkPath;
    //apk name
    private String apkName;

    public VersionConfig(String version, int must_update, String update_content, String url, String apkPath, String apkName) {
        this.version = version;
        this.must_update = must_update;
        this.update_content = update_content;
        this.url = url;
        this.apkPath = apkPath;
        this.apkName = apkName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getMust_update() {
        return must_update;
    }

    public void setMust_update(int must_update) {
        this.must_update = must_update;
    }

    public String getUpdate_content() {
        return update_content;
    }

    public void setUpdate_content(String update_content) {
        this.update_content = update_content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getApkPath() {
        return apkPath;
    }

    public void setApkPath(String apkPath) {
        this.apkPath = apkPath;
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    @Override
    public String toString() {
        return "VersionConfig{" +
                "version='" + version + '\'' +
                ", must_update=" + must_update +
                ", update_content='" + update_content + '\'' +
                ", url='" + url + '\'' +
                ", apkPath='" + apkPath + '\'' +
                ", apkName='" + apkName + '\'' +
                '}';
    }
}