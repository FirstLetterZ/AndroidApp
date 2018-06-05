package com.zpf.middleware.lifeControl;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by ZPF on 2018/6/5.
 */
public class LifecycleDialog extends Dialog implements LifecycleWindowInterface {

    private LifecycleArrayListener listener;

    public LifecycleDialog(@NonNull Context context) {
        super(context);
    }

    public LifecycleDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void addController(LifecycleArrayListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listener != null) {
            listener.next();
        }
    }


}
