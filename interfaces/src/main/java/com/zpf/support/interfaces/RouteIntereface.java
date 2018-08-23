package com.zpf.support.interfaces;

import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by ZPF on 2018/8/22.
 */
public interface RouteIntereface {

    void startActivity(Context context, int targetNumber, @Nullable Intent intent);

    void startActivity(Context context, int targetNumber, @Nullable Intent intent, @Nullable Bundle options);

    //Activity,Service,Fragment都继承ComponentCallbacks
    void startActivityForResult(ComponentCallbacks componentCallbacks, int targetNumber,
                                @Nullable Intent intent, int requestCode);

    void startActivityForResult(ComponentCallbacks componentCallbacks, int targetNumber,
                                @Nullable Intent intent, int requestCode, @Nullable Bundle options);

}
