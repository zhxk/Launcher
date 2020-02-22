package com.changren.android.launcher.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Author: wangsy
 * Create: 2018-10-22 14:37
 * Description: family表对应的实体类
 */
@Entity(tableName = "family")
public class Family {

    /** 家庭id */
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "family_id")
    private int fid;

    /** 家庭名称 */
    @ColumnInfo(name = "family_name")
    private String family_name;

    /** 创建家庭的用户id,用于默认为家庭登录后的显示成员 */
    @ColumnInfo(name = "create_user_id")
    private int uid;

    /** 标记为已登录的家庭，对应的0->false，1->true */
    @ColumnInfo(name = "is_login")
    private boolean isLogin;

    /** 区分是 药店家族 还是 普通家庭 */
    @ColumnInfo(name = "type")
    private int type;

    @ColumnInfo(name = "avatar")
    private String avatar;

    /** 家庭成员列表 */
    @Ignore
    private List<User> member_list;

    public Family(@NonNull int fid, String family_name, int uid, boolean isLogin, int type, String avatar) {
        this.fid = fid;
        this.family_name = family_name;
        this.uid = uid;
        this.isLogin = isLogin;
        this.type = type;
        this.avatar = avatar;
    }

    @NonNull
    public int getFid() {
        return fid;
    }

    public void setFid(@NonNull int fid) {
        this.fid = fid;
    }

    public String getFamily_name() {
        return family_name;
    }

    public void setFamily_name(String family_name) {
        this.family_name = family_name;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<User> getMember_list() {
        return member_list;
    }

    public void setMember_list(List<User> member_list) {
        this.member_list = member_list;
    }

    @Override
    public String toString() {
        return "Family{" +
                "fid=" + fid +
                ", family_name='" + family_name + '\'' +
                ", uid=" + uid +
                ", isLogin=" + isLogin +
                ", type=" + type +
                ", avatar='" + avatar + '\'' +
                ", member_list=" + member_list +
                '}';
    }
}
