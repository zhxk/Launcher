package com.changren.android.launcher.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UrlUtils {

    private static Gson gson;

    /**
     * 将Map集合拼接成url的格式
     * @param params Map<String, String>
     * @return string
     */
    public static String map2QueryStr(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }

        StringBuilder paramStr = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            paramStr.append(entry.getKey()).append("=")
                    .append(entry.getValue()).append("&");
        }

        return paramStr.substring(0, paramStr.length() - 1);
    }

    /**
     * 将Map集合拼接成url的格式
     * @param paramsObj
     * @return string Map<String, String>
     */
    public static Map<String, String> JsonStr2Map(String paramsObj) throws JsonSyntaxException {
        if (paramsObj == null || paramsObj.length() == 0) {
            return null;
        }

        if (gson == null) {
            gson = new Gson();
        }

        return gson.fromJson(paramsObj, new TypeToken<HashMap<String, String>>(){}.getType());
    }
}
