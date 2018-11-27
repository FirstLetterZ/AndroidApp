package com.zpf.support.util;

import com.google.gson.JsonElement;
import com.zpf.tool.gsonparse.GsonUtil;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by ZPF on 2018/7/21.
 */
public class JsonUtil {

    public static <T> T fromJson(Object object, Type classType) {
        return GsonUtil.get().fromJson(object, classType);
    }

    public static <T> List<T> fromJsonList(Object object, Type classType) {
        return GsonUtil.get().fromJsonList(object, classType);
    }

    public static String toString(Object object) {
        return GsonUtil.get().toString(object);
    }

    public static String getStringByName(JsonElement jsonElement, String name) {
        return GsonUtil.get().getStringByName(jsonElement, name);
    }

    public static JsonElement getJsonElementByName(JsonElement jsonElement, String name) {
        return GsonUtil.get().getJsonElementByName(jsonElement, name);
    }
}
