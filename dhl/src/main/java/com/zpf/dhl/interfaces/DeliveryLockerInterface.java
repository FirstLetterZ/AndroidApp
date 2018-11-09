package com.zpf.dhl.interfaces;

/**
 * Created by ZPF on 2018/11/9.
 */
public interface DeliveryLockerInterface {
    void put(ExpressageInterface expressageInterface);

    void pick(ParcelReceiverInterface receiverInterface);

    void clear();
}
