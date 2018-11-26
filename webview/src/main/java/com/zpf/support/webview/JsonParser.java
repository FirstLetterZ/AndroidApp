package com.zpf.support.webview;

import com.zpf.api.dataparser.JsonParserInterface;
import com.zpf.api.dataparser.StringParseResult;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by ZPF on 2018/11/26.
 */

public class JsonParser implements JsonParserInterface {

    @Override
    public <T> T fromJson(Object object, Type classType) {
        return null;
    }

    @Override
    public <T> List<T> fromJsonList(Object object, Type classType) {
        return null;
    }

    @Override
    public String toString(Object object) {
        return null;
    }

    @Override
    public String getStringByName(Object content, String name) {
        return null;
    }

    @Override
    public StringParseResult parseString(String content) {
        return null;
    }
}
