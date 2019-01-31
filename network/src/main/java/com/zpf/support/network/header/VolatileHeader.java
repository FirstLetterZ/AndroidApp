package com.zpf.support.network.header;

import com.zpf.api.KVPInterface;
import com.zpf.api.VariableParameterInterface;

/**
 * Created by ZPF on 2018/9/18.
 */
public class VolatileHeader implements KVPInterface<String,String> {
    private String name;
    private VariableParameterInterface variableParameter;

    public VolatileHeader(String name, VariableParameterInterface variableParameter) {
        this.name = name;
        this.variableParameter = variableParameter;
    }

    @Override
    public String getKey() {
        return name;
    }

    @Override
    public String getValue() {
        if (variableParameter != null) {
            Object currentValue = variableParameter.getCurrentValue();
            if (currentValue != null && currentValue instanceof String) {
                return (String) currentValue;
            }
        }
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVariableParameter(VariableParameterInterface variableParameter) {
        this.variableParameter = variableParameter;
    }

}
