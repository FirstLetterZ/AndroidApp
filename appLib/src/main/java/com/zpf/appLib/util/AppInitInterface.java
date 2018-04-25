package com.zpf.appLib.util;

import android.app.Application;
import android.support.annotation.NonNull;

/**
 * Created by ZPF on 2018/4/24.
 */

public interface AppInitInterface {

    @NonNull
    Application getApplication();

    boolean isDebug();

    @NonNull
    String getDBFilePath();

    @NonNull
    RouteRule getRouteRule();

    String getAppTag();

}
