package com.zpf.baselib.base;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Window;

import com.zpf.baselib.interfaces.SafeWindowController;
import com.zpf.baselib.interfaces.SafeWindowInterface;

/**
 * Created by ZPF on 2018/3/22.
 */
public class BaseDialog extends Dialog implements SafeWindowInterface {
    protected SafeWindowController listener;

    public BaseDialog(@NonNull Context context) {
        super(context);
        init();
    }

    public BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    protected BaseDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            dialogWindow.requestFeature(Window.FEATURE_NO_TITLE);// 取消标题
            initWindow(dialogWindow);
        }
        initView();
    }

    protected void initView() {

    }

    protected void initWindow(@NonNull Window window) {

    }

    public void showWithController(SafeWindowController listener) {
        this.listener = listener;
        if (listener != null) {
            listener.show(this);
        }
    }

    @Override
    public void show() {
        if (listener != null) {
            if (listener.isShowing(this)) {
                super.show();
            } else {
                listener.show(this);
            }
        } else {
            super.show();
        }
    }

    @Override
    public void bindController(SafeWindowController controller) {
        this.listener = controller;
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listener != null) {
            listener.showNext();
        }
    }

}
