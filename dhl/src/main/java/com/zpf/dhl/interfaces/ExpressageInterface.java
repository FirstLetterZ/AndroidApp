package com.zpf.dhl.interfaces;

import java.util.List;

/**
 * Created by ZPF on 2018/11/8.
 */
public interface ExpressageInterface {
    void put(Object object);

    boolean putOnlyOne(Object object);

    String getReceiver();

    String getSender();

    List<Object> getParts();
}
