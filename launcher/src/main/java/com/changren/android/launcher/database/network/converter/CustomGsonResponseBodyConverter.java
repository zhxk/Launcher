package com.changren.android.launcher.database.network.converter;

import android.text.TextUtils;

import com.changren.android.launcher.database.entity.Response;
import com.changren.android.launcher.database.network.api.ApiException;
import com.changren.android.launcher.util.LogUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Author: wangsy
 * Create: 2018-11-27 10:53
 * Description: 修改ResponseBody解析方式
 */
public class CustomGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private final Gson gson;
//    private final TypeAdapter<T> adapter;
    private Type type;

//    CustomGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
//        this.gson = gson;
//        this.adapter = adapter;
//    }

    CustomGsonResponseBodyConverter(Gson gson, Type type) {
        this.gson = gson;
        this.type = type;
    }

    @Override public T convert(ResponseBody value) throws IOException {
//        JsonReader jsonReader = gson.newJsonReader(value.charStream());
//        try {
//            T result = adapter.read(jsonReader);
//            if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
//                throw new JsonIOException("JSON document was not fully consumed.");
//            }
//            return result;
//        } finally {
//            value.close();
//        }

        String responseStr = value.string();
        try {
            Response response = gson.fromJson(responseStr, Response.class);
            if (!response.isSuccess()) {
                throw new ApiException(response.getCode(), response.getMsg());
            }
            try {
                JSONObject jsonObject = new JSONObject(responseStr);
                String data = jsonObject.get("data").toString();
                LogUtils.i("网络请求响应返回的Json中data内容", data);
                if (TextUtils.isEmpty(data) || "null".equals(data)) {
                    data = "{}";
                }
                return gson.fromJson(data, type);
            } catch (JSONException e) {
                throw new ApiException(response.getCode(), responseStr);
            }
        } finally {
            value.close();
        }
    }
}
