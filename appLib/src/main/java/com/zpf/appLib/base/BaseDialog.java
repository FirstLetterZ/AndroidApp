package com.zpf.appLib.base;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;


/**
 * Created by ZPF on 2018/4/16.
 */

public class BaseDialog extends Dialog {
    private OnDismissListener dismissListener;
    private OnDismissListener firstListener;

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
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 取消标题
        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            initWindow(dialogWindow);
        }
        super.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (firstListener != null) {
                    firstListener.onDismiss(dialog);
                }
                if (dismissListener != null) {
                    dismissListener.onDismiss(dialog);
                }
            }
        });
    }

    protected void initWindow(Window dialogWindow) {
        dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
        dialogWindow.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
        dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(lp);
    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        dismissListener = listener;
    }

    public void setFirstListener(@NonNull OnDismissListener listener) {
        firstListener = listener;
    }

    public void clearDismissListener() {
        firstListener = null;
        dismissListener = null;
    }
}
