package com.zpf.support.util;

import android.content.Context;
import android.view.WindowManager;

import com.zpf.api.OnAttachListener;
import com.zpf.frame.ILoadingManager;
import com.zpf.views.window.ProgressDialog;

/**
 * loading弹窗管理
 * Created by ZPF on 2019/3/20.
 */
public class LoadingManagerImpl implements ILoadingManager {
    private final ProgressDialog progressDialog;
    private OnAttachListener attachListener;

    public LoadingManagerImpl(Context context) {
        progressDialog = new ProgressDialog(context);
    }

    @Override
    public void showLoading() {
        progressDialog.getWindow().setType(WindowManager.LayoutParams.LAST_SUB_WINDOW);
        this.showLoading(null);
    }

    @Override
    public void showLoading(Object msg) {
        if (msg != null) {
            progressDialog.setText(msg.toString());
        } else {
            progressDialog.setText(null);
        }
        if (!progressDialog.isShowing()) {
            progressDialog.show();
            if (attachListener != null) {
                attachListener.onAttached();
            }
        }
    }

    @Override
    public boolean hideLoading() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
            if (attachListener != null) {
                attachListener.onDetached();
            }
            return true;
        }
        return false;
    }

    @Override
    public void setLoadingListener(OnAttachListener onAttachListener) {
        this.attachListener = onAttachListener;
    }
}
