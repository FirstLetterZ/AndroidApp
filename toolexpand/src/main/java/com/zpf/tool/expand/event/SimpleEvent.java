package com.zpf.tool.expand.event;

/**
 * @author Created by ZPF on 2021/6/7.
 */
public class SimpleEvent implements IEvent {
    private final int id;
    private int type;
    private boolean retry;
    private String sender;
    private Object data;
    private String message;

    public SimpleEvent(int id, Object data, String message) {
        this.id = id;
        this.data = data;
        this.message = message;
    }

    public SimpleEvent(int id) {
        this.id = id;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setRetry(boolean retry) {
        this.retry = retry;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public String sender() {
        return sender;
    }


    @Override
    public String message() {
        return message;
    }

    @Override
    public Object attachment() {
        return data;
    }

    @Override
    public int type() {
        return type;
    }

    @Override
    public boolean shouldRetry() {
        return retry;
    }
}
