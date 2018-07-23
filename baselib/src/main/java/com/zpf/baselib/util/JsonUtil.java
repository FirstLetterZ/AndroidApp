package com.zpf.baselib.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ZPF on 2018/7/21.
 */
public class JsonUtil {
    private static final Gson mGson = new Gson();
    private static final JsonParser mJsonParser = new JsonParser();

    public static <T> T fromJson(Object object, Type classType) {
        try {
            if (object == null) {
                return null;
            } else if (object instanceof JsonElement) {
                return mGson.fromJson((JsonElement) object, classType);
            } else if (object instanceof Reader) {
                return mGson.fromJson((Reader) object, classType);
            } else {
                return mGson.fromJson(toString(object), classType);
            }
        } catch (Exception e) {
            LogUtil.w("JsonUtil fromJson fail:" + e.toString());
            e.printStackTrace();
            return null;
        }
    }

    public static <T> List<T> fromJsonList(Object object, Type classType) {
        List<T> list = new ArrayList<>();
        if (object != null) {
            try {
                JsonElement jsonElement;
                if (object instanceof JsonElement) {
                    jsonElement = (JsonElement) object;
                } else if (object instanceof Reader) {
                    jsonElement = mJsonParser.parse((Reader) object);
                } else {
                    jsonElement = mJsonParser.parse(toString(object));
                }
                if (jsonElement != null && jsonElement.isJsonArray()) {
                    JsonArray array = jsonElement.getAsJsonArray();
                    for (final JsonElement elem : array) {
                        T result = mGson.fromJson(elem, classType);
                        if (result != null) {
                            list.add(result);
                        }
                    }
                }
            } catch (Exception e) {
                LogUtil.w("JsonUtil fromJsonList fail:" + e.toString());
                e.printStackTrace();
            }
        }
        return list;
    }

    public static String toString(Object object) {
        String result = "";
        try {
            if (object == null) {
                result = "";
            } else if (object instanceof String) {
                result = object.toString();
            } else if (object instanceof JsonElement) {
                if (((JsonElement) object).isJsonNull()) {
                    result = "";
                } else if (((JsonElement) object).isJsonPrimitive()) {
                    result = ((JsonElement) object).getAsJsonPrimitive().getAsString();
                } else {
                    result = mGson.toJson((JsonElement) object);
                }
            } else {
                result = mGson.toJson(object);
            }
        } catch (Exception e) {
            LogUtil.w("JsonUtil toString fail:" + e.toString());
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 根据name获取内部指定部分字符串
     */
    public static String getStringByName(JsonElement jsonElement, String name) {
        String result = "";
        if (jsonElement == null) {
            result = "";
        } else if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonElement element = jsonObject.get(name);
            if (element == null) {
                for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    JsonElement otherElement = entry.getValue();
                    result = getStringByName(otherElement, name);
                    if (!TextUtils.isEmpty(result)) {
                        break;
                    }
                }
            } else {
                result = JsonUtil.toString(element);
            }
        } else if (jsonElement.isJsonArray()) {
            JsonArray jsonElements = jsonElement.getAsJsonArray();
            for (JsonElement element : jsonElements) {
                result = getStringByName(element, name);
                if (!TextUtils.isEmpty(result)) {
                    break;
                }
            }
        }
        return result;
    }

    public static JsonElement getJsonElementByName(JsonElement jsonElement, String name) {
        JsonElement result = new JsonNull();
        if (jsonElement != null) {
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                JsonElement element = jsonObject.get(name);
                if (element == null) {
                    for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                        JsonElement otherElement = entry.getValue();
                        result = getJsonElementByName(otherElement, name);
                        if (result != null && !result.isJsonNull()) {
                            break;
                        }
                    }
                } else {
                    result = element;
                }
            } else if (jsonElement.isJsonArray()) {
                JsonArray jsonElements = jsonElement.getAsJsonArray();
                for (JsonElement element : jsonElements) {
                    result = getJsonElementByName(element, name);
                    if (result != null && !result.isJsonNull()) {
                        break;
                    }
                }
            }
        }
        return result;
    }
}
