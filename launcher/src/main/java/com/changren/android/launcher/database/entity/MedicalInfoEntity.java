package com.changren.android.launcher.database.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * Author: wangsy
 * Create: 2018-12-04 15:47
 * Description: TODO(描述文件做什么)
 */
public class MedicalInfoEntity implements Parcelable {

    private int id;
    private String name;

    public MedicalInfoEntity(int id, String name) {
        this.id = id;
        this.name = name;
    }

    protected MedicalInfoEntity(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MedicalInfoEntity> CREATOR = new Creator<MedicalInfoEntity>() {
        @Override
        public MedicalInfoEntity createFromParcel(Parcel in) {
            return new MedicalInfoEntity(in);
        }

        @Override
        public MedicalInfoEntity[] newArray(int size) {
            return new MedicalInfoEntity[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MedicalInfoEntity entity = (MedicalInfoEntity) o;
        return id == entity.id &&
                Objects.equals(name, entity.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "MedicalInfoEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
