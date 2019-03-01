package com.zpf.tool.expand.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Window;

import com.zpf.api.ICustomWindow;
import com.zpf.api.IManager;

/**
 * Created by ZPF on 2018/3/22.
 */
public class SafeDialog extends Dialog implements ICustomWindow {
    protected IManager<ICustomWindow> listener;
    protected long bindId = -1;

    public SafeDialog(@NonNull Context context) {
        super(context);
        init();
    }

    public SafeDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    protected SafeDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
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

    @Override
    public void show() {
        if (listener != null) {
            if (listener.execute(bindId)) {
                super.show();
            }
        } else {
            super.show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listener != null) {
            listener.execute(-1);
        }
    }

    @Override
    public SafeDialog toBind(IManager<ICustomWindow> manager) {
        bindId = manager.bind(this);
        return this;
    }
}
