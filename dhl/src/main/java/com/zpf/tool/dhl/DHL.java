package com.zpf.tool.dhl;

import android.util.SparseArray;

import com.zpf.tool.dhl.interfaces.ExpressageInterface;
import com.zpf.tool.dhl.interfaces.ParcelReceiverInterface;

/**
 * 快递服务商
 * Created by ZPF on 2018/11/9.
 */
public class DHL {
    private SparseArray<DeliveryLocker> deliveryLockers = new SparseArray<>();
    private DeliveryLocker defDeliveryLockers = new DeliveryLocker();

    private DHL() {
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
        if (expressageInterface != null) {
            defDeliveryLockers.put(expressageInterface);
            return true;
        }
        return false;
    }

    public boolean put(int lockerId, ExpressageInterface expressageInterface) {
        DeliveryLocker deliveryLocker = deliveryLockers.get(lockerId);
        if (expressageInterface != null && deliveryLocker != null) {
            deliveryLocker.put(expressageInterface);
            return true;
        } else {
            return false;
        }
    }

    public void send(ParcelReceiverInterface receiverInterface) {
        if (receiverInterface != null) {
            defDeliveryLockers.pick(receiverInterface);
        }
    }

    public void send(int lockerId, ParcelReceiverInterface receiverInterface) {
        if (receiverInterface != null) {
            DeliveryLocker deliveryLocker = deliveryLockers.get(lockerId);
            if (deliveryLocker != null) {
                deliveryLocker.pick(receiverInterface);
            } else {
                receiverInterface.unpackRemnants(null);
            }
        }
    }

    public void addDeliveryLocker(int lockerId, DeliveryLocker deliveryLocker) {
        if (deliveryLocker != null) {
            deliveryLockers.put(lockerId, deliveryLocker);
        }
    }

    public void clear(int lockerId) {
        deliveryLockers.remove(lockerId);
    }

    public void clear() {
        defDeliveryLockers.clear();
    }

    public void clearAll() {
        defDeliveryLockers.clear();
        deliveryLockers.clear();
    }
}
