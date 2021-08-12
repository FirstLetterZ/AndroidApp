package com.zpf.tool.expand.event;


/**
 * 事件协议
 * Created by ZPF on 2019/7/18.
 */
public interface IEvent {

    int id();

    String sender();

    String message();

    Object attachment();

    int type();

    boolean shouldRetry();
}
