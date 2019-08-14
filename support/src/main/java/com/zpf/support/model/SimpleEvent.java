package com.zpf.support.model;

import com.zpf.api.IEvent;

public class SimpleEvent<T> implements IEvent<T> {

    private int eventCode;
    private String eventName;
    private String eventMessage;
    private T eventData;

    public SimpleEvent() {
    }

    public SimpleEvent(String eventName) {
        this.eventName = eventName;
    }

    public SimpleEvent(int eventCode) {
        this.eventCode = eventCode;
    }

    public SimpleEvent(String eventName, int eventCode) {
        this.eventName = eventName;
        this.eventCode = eventCode;
    }

    public SimpleEvent(int eventCode, T eventData) {
        this.eventCode = eventCode;
        this.eventData = eventData;
    }

    public SimpleEvent(String eventName, T eventData) {
        this.eventName = eventName;
        this.eventData = eventData;
    }

    public SimpleEvent(int eventCode, String eventName, String eventMessage, T eventData) {
        this.eventCode = eventCode;
        this.eventName = eventName;
        this.eventMessage = eventMessage;
        this.eventData = eventData;
    }

    @Override
    public int getEventCode() {
        return eventCode;
    }

    @Override
    public String getEventName() {
        return eventName;
    }

    public void setEventData(T data) {
        this.eventData = data;
    }

    @Override
    public T getEventData() {
        return eventData;
    }

    public void setEventMessage(String eventMessage) {
        this.eventMessage = eventMessage;
    }

    @Override
    public String getEventMessage() {
        return eventMessage;
    }


}
