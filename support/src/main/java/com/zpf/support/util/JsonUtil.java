package com.zpf.support.util;

import com.google.gson.JsonElement;
import com.zpf.api.dataparser.JsonParserInterface;
import com.zpf.tool.expand.util.GlobalConfigImpl;
import com.zpf.tool.gsonparse.GsonUtil;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by ZPF on 2018/7/21.
 */
public class JsonUtil {
    private static JsonParserInterface jsonParser;

    public static <T> T fromJson(Object object, Type classType) {
        if (checkGlobalJsonParser()) {
            return jsonParser.fromJson(object, classType);
        } else {
            return GsonUtil.get().fromJson(object, classType);
        }
    }

    public static <T> List<T> fromJsonList(Object object, Type classType) {
        if (checkGlobalJsonParser()) {
            return jsonParser.fromJsonList(object, classType);
        } else {
            return GsonUtil.get().fromJsonList(object, classType);
        }
    }

    public static String toString(Object object) {
        if (checkGlobalJsonParser()) {
            return jsonParser.toString(object);
        } else {
            return GsonUtil.get().toString(object);
        }
    }

    public static String getStringByName(Object json, String name) {
        if (checkGlobalJsonParser()) {
            return jsonParser.getStringByName(json, name);
        } else {
            return GsonUtil.get().getStringByName(json, name);
        }
    }

    public static JsonElement getJsonElementByName(JsonElement jsonElement, String name) {
        return GsonUtil.get().getJsonElementByName(jsonElement, name);
    }

    private synchronized static boolean checkGlobalJsonParser() {
        if (jsonParser == null) {
            try {
                jsonParser = GlobalConfigImpl.get().getGlobalInstance(JsonParserInterface.class);
            } catch (Exception e) {
                //
            }
        }
        return jsonParser != null;
    }
}
