package com.zpf.frame;

import com.zpf.api.IBackPressInterceptor;
import com.zpf.api.IFullLifecycle;
import com.zpf.api.OnActivityResultListener;
import com.zpf.api.OnPermissionResultListener;
import com.zpf.api.OnTouchKeyListener;
import com.zpf.api.OnViewStateChangedListener;

public interface IListenerSet extends IFullLifecycle, OnActivityResultListener, OnViewStateChangedListener,
        OnPermissionResultListener, IBackPressInterceptor, OnTouchKeyListener {
}
