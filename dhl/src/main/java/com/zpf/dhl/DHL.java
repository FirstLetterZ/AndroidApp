package com.zpf.dhl;

import android.util.SparseArray;

import com.zpf.dhl.interfaces.ExpressageInterface;
import com.zpf.dhl.interfaces.ParcelReceiverInterface;

/**
 * 快递服务商
 * Created by ZPF on 2018/11/9.
 */
public class DHL {
    private SparseArray<DeliveryLocker> deliveryLockers = new SparseArray<>();
    private final int DEF_ID;

    private DHL() {
        DEF_ID = 0;
        deliveryLockers = new SparseArray<>();
        deliveryLockers.put(DEF_ID, new DeliveryLocker());
    }

    private static volatile DHL mInstance;

    public static DHL get() {
        if (mInstance == null) {
            synchronized (DHL.class) {
                if (mInstance == null) {
                    mInstance = new DHL();
                }
            }
        }
        return mInstance;
    }

    public boolean put(ExpressageInterface expressageInterface) {
        return put(DEF_ID, expressageInterface);
    }

    public boolean put(int lockerId, ExpressageInterface expressageInterface) {
        DeliveryLocker deliveryLocker = deliveryLockers.get(lockerId);
        if (deliveryLocker != null) {
            deliveryLocker.put(expressageInterface);
            return true;
        } else {
            return false;
        }
    }

    public void send(ParcelReceiverInterface receiverInterface) {
        send(DEF_ID, receiverInterface);
    }

    public void send(int lockerId, ParcelReceiverInterface receiverInterface) {
        DeliveryLocker deliveryLocker = deliveryLockers.get(lockerId);
        if (deliveryLocker != null) {
            deliveryLocker.pick(receiverInterface);
        } else {
            receiverInterface.unpackRemnants(null);
        }
    }

    public void clear() {
        deliveryLockers.clear();
        deliveryLockers.put(DEF_ID, new DeliveryLocker());
    }
}
