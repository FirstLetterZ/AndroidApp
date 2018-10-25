package com.zpf.support.api;


/**
 * Created by ZPF on 2018/6/14.
 */

public interface CallBackManagerInterface extends OnDestroyListener {
    long addCallBack(CallBackInterface callBack);

    void removeCallBack(long id);

    void cancelCallBack(long id);

    void cancelAll();

}
