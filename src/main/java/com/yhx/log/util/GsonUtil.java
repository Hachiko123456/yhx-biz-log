package com.yhx.log.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author yanghuaxu
 * @date 2022/6/20 15:45
 */
public class GsonUtil {

    private static Gson gson = null;

    static {
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }

    public static String toJson(Object bean) {
        return gson.toJson(bean);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

}
