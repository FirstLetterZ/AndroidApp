package com.zpf.dhl;

import com.zpf.dhl.interfaces.ExpressageInterface;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by ZPF on 2018/11/9.
 */
public class SinglePartExpressage<T> implements ExpressageInterface<T> {
    private String receiver;//收件人
    private String sender;//发件人
    private T part;

    public SinglePartExpressage(String receiver, String sender) {
        this.receiver = receiver;
        this.sender = sender;
    }

    @Override
    public void put(T part) {
        this.part = part;
    }

    @Override
    public boolean putOnlyOne(T part) {
        this.part = part;
        return true;
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
    public T getFirstPart() {
        return part;
    }

    @Override
    public T getLastPart() {
        return part;
    }

    @Override
    public List<T> getAllParts() {
        return Collections.singletonList(part);
    }

}
