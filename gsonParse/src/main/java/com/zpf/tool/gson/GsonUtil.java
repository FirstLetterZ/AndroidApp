package com.zpf.tool.gson;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import com.zpf.api.dataparser.JsonParserInterface;
import com.zpf.api.dataparser.StringParseResult;
import com.zpf.api.dataparser.StringParseType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * gson解析
 * Created by ZPF on 2018/7/21.
 */
public class GsonUtil implements JsonParserInterface {
    private final Gson mGson = new Gson();
    private final JsonParser mJsonParser = new JsonParser();
    public static GsonUtil mInstance = Instance.mInstance;

    private GsonUtil() {
    }

    private static class Instance {
        private static GsonUtil mInstance = new GsonUtil();
    }

    @Override
    public <T> T fromJson(Object object, Type classType) {
        try {
            if (object == null) {
                return null;
            } else if (object.getClass() == classType) {
                return (T) object;
            } else if (object instanceof JsonElement) {
                return mGson.fromJson((JsonElement) object, classType);
            } else if (object instanceof Reader) {
                return mGson.fromJson((Reader) object, classType);
            } else if (object instanceof JsonReader) {
                return mGson.fromJson((JsonReader) object, classType);
            } else if (classType == String.class) {
                return (T) toString(object);
            } else {
                return mGson.fromJson(toString(object), classType);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public <T> List<T> fromJsonList(Object object, Type classType) {
        List<T> list = new ArrayList<>();
        if (object != null) {
            try {
                JsonElement jsonElement;
                if (object instanceof JsonElement) {
                    jsonElement = (JsonElement) object;
                } else if (object instanceof Reader) {
                    jsonElement = mJsonParser.parse((Reader) object);
                } else if (object instanceof JsonReader) {
                    jsonElement = mJsonParser.parse((JsonReader) object);
                } else {
                    jsonElement = mJsonParser.parse(toString(object));
                }
                if (jsonElement != null && !jsonElement.isJsonNull()) {
                    T eleResult = null;
                    if (jsonElement.isJsonArray()) {
                        JsonArray array = jsonElement.getAsJsonArray();
                        for (final JsonElement elem : array) {
                            eleResult = null;
                            if (elem == null || elem.isJsonNull()) {
                                continue;
                            } else if (elem.isJsonArray()) {
                                eleResult = (T) fromJsonList(elem, classType);
                            } else if (jsonElement.isJsonObject() || jsonElement.isJsonPrimitive()) {
                                eleResult = mGson.fromJson(jsonElement, classType);
                            }
                            if (eleResult != null) {
                                list.add(eleResult);
                            }
                        }
                    } else if (jsonElement.isJsonObject() || jsonElement.isJsonPrimitive()) {
                        eleResult = mGson.fromJson(jsonElement, classType);
                    }
                    if (eleResult != null) {
                        list.add(eleResult);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    @Override
    public String toString(Object object) {
        String result = "";
        try {
            if (object == null) {
                result = "";
            } else if (object instanceof String || object instanceof JSONObject
                    || object instanceof JSONArray) {
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
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String getStringByName(Object content, String name) {
        if (content == null) {
            return "";
        }
        JsonElement jsonElement;
        if (content instanceof JsonElement) {
            jsonElement = (JsonElement) content;
        } else {
            jsonElement = fromJson(content, JsonElement.class);
        }
        if (jsonElement == null) {
            return "";
        }
        String result = "";
        if (jsonElement.isJsonObject()) {
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
                result = toString(element);
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

    @Override
    public StringParseResult parseString(String content) {
        StringParseResult result;
        JsonElement element = fromJson(content, JsonElement.class);
        if (element == null) {
            if (TextUtils.isEmpty(content)) {
                result = new StringParseResult(StringParseType.TYPE_NULL, null);
            } else {
                result = new StringParseResult(StringParseType.TYPE_STRING, content);
            }
        } else if (element.isJsonPrimitive()) {
            JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();
            if (jsonPrimitive.isBoolean()) {
                result = new StringParseResult(StringParseType.TYPE_BOOLEAN, jsonPrimitive.getAsBoolean());
            } else if (jsonPrimitive.isNumber()) {
                result = new StringParseResult(StringParseType.TYPE_NUMBER, jsonPrimitive.getAsNumber());
            } else if (jsonPrimitive.isString()) {
                result = new StringParseResult(StringParseType.TYPE_STRING, jsonPrimitive.getAsString());
            } else {
                result = new StringParseResult(StringParseType.TYPE_UNKNOWN, null);
            }
        } else if (element.isJsonObject()) {
            try {
                result = new StringParseResult(StringParseType.TYPE_JSON_OBJECT, new JSONObject(content));
            } catch (JSONException e) {
                e.printStackTrace();
                result = new StringParseResult(StringParseType.TYPE_UNKNOWN, null);
            }
        } else if (element.isJsonArray()) {
            try {
                result = new StringParseResult(StringParseType.TYPE_JSON_ARRAY, new JSONArray(content));
            } catch (JSONException e) {
                e.printStackTrace();
                result = new StringParseResult(StringParseType.TYPE_UNKNOWN, null);
            }
        } else if (element.isJsonNull()) {
            result = new StringParseResult(StringParseType.TYPE_NULL, null);
        } else {
            result = new StringParseResult(StringParseType.TYPE_UNKNOWN, null);
        }
        return result;
    }

    public JsonElement getJsonElementByName(JsonElement jsonElement, String name) {
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
