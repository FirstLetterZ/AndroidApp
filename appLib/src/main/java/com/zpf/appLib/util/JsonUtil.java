package com.zpf.appLib.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZPF on 2018/4/16.
 */

public class JsonUtil {
    private static Gson mGson = new Gson();

    public static <T> T fromJson(String json, Type type) {
        try {
            return mGson.fromJson(json, type);
        } catch (Exception e) {
            LogUtil.w("JsonUtil fromJson fail:"+ e.toString());
        }
        return null;
    }

    public static <T> T fromJson(JsonElement json, Type type) {
        try {
            return mGson.fromJson(json, type);
        } catch (Exception e) {
            LogUtil.w("JsonUtil fromJson fail:"+ e.toString());
        }
        return null;
    }

    public static <T> T fromJson(String json, Class<T> clz) {
        try {
            return mGson.fromJson(json, clz);
        } catch (Exception e) {
            LogUtil.w("JsonUtil fromJson fail:"+ e.toString());
        }
        return null;
    }

    public static <T> T fromJson(JsonElement json, Class<T> clz) {
        if (json == null || json.isJsonNull()) {
            return null;
        }
        try {
            return mGson.fromJson(json, clz);
        } catch (Exception e) {
            LogUtil.w("JsonUtil fromJson fail:"+ e.toString());
        }
        return null;
    }

    public static <T> List<T> fromJsonList(JsonElement jsonElement, Class<T> clz) {
        List<T> list = new ArrayList<>();
        if (jsonElement.isJsonArray()) {
            try {
                JsonArray array = jsonElement.getAsJsonArray();
                for (final JsonElement elem : array) {
                    list.add(mGson.fromJson(elem, clz));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static <T> List<T> fromJsonList(JsonElement jsonElement, Type type) {
        List<T> list = new ArrayList<>();
        if (jsonElement.isJsonArray()) {
            try {
                JsonArray array = jsonElement.getAsJsonArray();
                for (final JsonElement elem : array) {
                    list.add((T) mGson.fromJson(elem, type));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }


    public static <T> List<T> fromJsonList(String json, Class<T> clz) {
        List<T> list = new ArrayList<T>();
        try {
            JsonArray array = new JsonParser().parse(json).getAsJsonArray();
            for (final JsonElement elem : array) {
                list.add(mGson.fromJson(elem, clz));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static String toString(Object object) {
        return mGson.toJson(object);
    }

    public static String toString(JsonElement jsonElement) {
        if (jsonElement.isJsonNull()) {
            return "";
        } else if (jsonElement.isJsonPrimitive()) {
            return jsonElement.getAsJsonPrimitive().getAsString();
        } else {
            return mGson.toJson(jsonElement);
        }
    }

}
