package com.changren.android.launcher.database.typeconverter;

import android.arch.persistence.room.TypeConverter;
import android.text.TextUtils;

import com.changren.android.launcher.database.entity.MedicalInfoEntity;
import com.changren.android.launcher.util.LogUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Author: wangsy
 * Create: 2018-12-14 15:06
 * Description: TODO(描述文件做什么)
 */
public class MedicalInfoConverter {

    private static Gson gson = new Gson();
    /**
     * 将数据库保存的值转换成List<MedicalInfoEntity>
     * @param value Json格式的字符串
     * @return List
     */
    @TypeConverter
    public static ArrayList<MedicalInfoEntity> revertDate(String value) {
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        //Json数组 转为 List
        ArrayList<MedicalInfoEntity> entities = gson.fromJson(value, new TypeToken<ArrayList<MedicalInfoEntity>>(){}.getType());
        LogUtils.i("Json数组 转为 List", entities.get(0).toString(), entities.size());
        return entities;
    }

    /**
     * 将List<MedicalInfoEntity>转换成数据库可以保存的类型
     * @param values List列表
     * @return String
     */
    @TypeConverter
    public static String converterDate(ArrayList<MedicalInfoEntity> values) {
        if (values == null || values.size() == 0) {
            return "";
        }

        //List 转为 Json数组
        String jsonArray = gson.toJson(values, new TypeToken<ArrayList<MedicalInfoEntity>>(){}.getType());
        LogUtils.i("List<MedicalInfoEntity>转换成Json", jsonArray);

        return jsonArray;
    }

}
