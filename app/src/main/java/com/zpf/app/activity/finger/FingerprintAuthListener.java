package com.zpf.app.activity.finger;

/**
 * Created by ZPF on 2019/2/12.
 */
public interface FingerprintAuthListener {

    void onError(int stateCode, int errCode, CharSequence errMsg);

    void onFail(int stateCode, int failCode, CharSequence failMsg, boolean willRetry);

    void onSuccess();

    void onStart();

    void onCancel();

}
