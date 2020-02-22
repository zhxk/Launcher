package com.changren.android.launcher.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: wangsy
 * Create: 2018-10-22 15:00
 * Description: user表的实体类
 */
@Entity(tableName = "user", primaryKeys = {"user_id", "family_id"})
public class User implements Parcelable {

    /** 用户ID */
    @NonNull
    @ColumnInfo(name = "user_id")
    private int uid;

    /** 家庭ID */
    @NonNull
    @ColumnInfo(name = "family_id")
    private int fid;

    /** 姓名 */
    @ColumnInfo(name = "user_name")
    private String username;

    /** 昵称 */
    @ColumnInfo(name = "nick_name")
    private String nickname;

    /** 手机号码 */
    @ColumnInfo(name = "phone_number")
    private String phone;

    /** md5加密后密码 */
    @ColumnInfo(name = "md5_password")
    private String password;

    /** 头像地址 */
    @ColumnInfo(name = "user_avatar")
    private String avatar;

    /** 性别 */
    @ColumnInfo(name = "sex")
    private int sex;

    /** 出生日期 */
    @ColumnInfo(name = "birthday")
    private String birth;

    /** 身高 */
    @ColumnInfo(name = "height")
    private int stature;

    /** 体重 */
    @ColumnInfo(name = "weight")
    private float weight;

    /** 注册状态：1待提交基本信息，2待创建/加入家庭，3已完成注册 */
    @ColumnInfo(name = "status")
    private int status;

    /** 是否在当前家庭中被锁定为查看成员,暂不使用该字段 */
    @ColumnInfo(name = "is_selected")
    private boolean selected;

    @ColumnInfo(name = "update_time")
    private String mUpdateTime;

    @ColumnInfo(name = "create_time")
    private String mCreateTime;

    @ColumnInfo(name = "ills")
    private ArrayList<MedicalInfoEntity> ills;

    /** 该用户的网易云账号 */
    @NonNull
    @ColumnInfo(name = "accid")
    private String accid;
    @NonNull
    /** 该用户的网易云密码 */
    @ColumnInfo(name = "token")
    private String token;

    @Ignore
    public User() {}

    public User(@NonNull int uid, @NonNull int fid, String username, String nickname, String phone, String password, String avatar, int sex, String birth, int stature, float weight, int status, boolean selected, String mUpdateTime, String mCreateTime,@NonNull String accid,@NonNull String token) {
        this.uid = uid;
        this.fid = fid;
        this.username = username;
        this.nickname = nickname;
        this.phone = phone;
        this.password = password;
        this.avatar = avatar;
        this.sex = sex;
        this.birth = birth;
        this.stature = stature;
        this.weight = weight;
        this.status = status;
        this.selected = selected;
        this.mUpdateTime = mUpdateTime;
        this.mCreateTime = mCreateTime;
        this.accid = accid;
        this.token = token;
    }

    protected User(Parcel in) {
        uid = in.readInt();
        fid = in.readInt();
        username = in.readString();
        nickname = in.readString();
        phone = in.readString();
        password = in.readString();
        avatar = in.readString();
        sex = in.readInt();
        birth = in.readString();
        stature = in.readInt();
        weight = in.readFloat();
        status = in.readInt();
        selected = in.readByte() != 0;
        mUpdateTime = in.readString();
        mCreateTime = in.readString();
        ills = in.createTypedArrayList(MedicalInfoEntity.CREATOR);
        accid = in.readString();
        token = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(uid);
        dest.writeInt(fid);
        dest.writeString(username);
        dest.writeString(nickname);
        dest.writeString(phone);
        dest.writeString(password);
        dest.writeString(avatar);
        dest.writeInt(sex);
        dest.writeString(birth);
        dest.writeInt(stature);
        dest.writeFloat(weight);
        dest.writeInt(status);
        dest.writeByte((byte) (selected ? 1 : 0));
        dest.writeString(mUpdateTime);
        dest.writeString(mCreateTime);
        dest.writeTypedList(ills);
        dest.writeString(accid);
        dest.writeString(token);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @NonNull
    public int getUid() {
        return uid;
    }

    public void setUid(@NonNull int uid) {
        this.uid = uid;
    }

    @NonNull
    public int getFid() {
        return fid;
    }

    public void setFid(@NonNull int fid) {
        this.fid = fid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public int getStature() {
        return stature;
    }

    public void setStature(int stature) {
        this.stature = stature;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getUpdateTime() {
        return mUpdateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.mUpdateTime = updateTime;
    }

    public String getCreateTime() {
        return mCreateTime;
    }

    public void setCreateTime(String createTime) {
        this.mCreateTime = createTime;
    }

    public ArrayList<MedicalInfoEntity> getIlls() {
        return ills;
    }

    public void setIlls(ArrayList<MedicalInfoEntity> ills) {
        this.ills = ills;
    }

    public String getName() {
        return TextUtils.isEmpty(nickname) ? username : nickname;
    }

    public void setAccid(String accid) {
        this.accid = accid;
    }

    @NonNull
    public String getAccid() {
        return accid;
    }

    @NonNull
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", fid=" + fid +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", avatar='" + avatar + '\'' +
                ", sex=" + sex +
                ", birth='" + birth + '\'' +
                ", stature=" + stature +
                ", weight=" + weight +
                ", status=" + status +
                ", selected=" + selected +
                ", mUpdateTime='" + mUpdateTime + '\'' +
                ", mCreateTime='" + mCreateTime + '\'' +
                ", ills=" + ills +
                ", accid='" + accid + '\'' +
                ", token='" + token + '\'' +
                '}';
    }

}
