package com.zpf.support.network.header;

import com.zpf.api.KVPInterface;

/**
 * Created by ZPF on 2018/9/18.
 */
public class ConstantHeader implements KVPInterface<String,String> {
    private String name;
    private String value;

    public ConstantHeader(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getKey() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
