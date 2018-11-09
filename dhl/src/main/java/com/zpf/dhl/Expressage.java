package com.zpf.dhl;

import com.zpf.dhl.interfaces.ExpressageInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * 快递包裹
 * Created by ZPF on 2018/11/8.
 */
public class Expressage implements ExpressageInterface {
    private String receiver;//收件人
    private String sender;//发件人
    private ArrayList<Object> parts;

    public Expressage(String receiver, String sender) {
        this.receiver = receiver;
        this.sender = sender;
        parts = new ArrayList<>();
    }


    @Override
    public void put(Object object) {
        parts.add(object);
    }

    @Override
    public boolean putOnlyOne(Object object) {
        if (!parts.contains(object)) {
            parts.add(object);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getReceiver() {
        return receiver;
    }

    @Override

    public String getSender() {
        return sender;
    }

    @Override

    public List<Object> getParts() {
        return parts;
    }
}
