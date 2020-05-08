package com.zpf.support.util;

import android.content.Context;

import com.zpf.frame.ILoadingManager;
import com.zpf.frame.ILoadingStateListener;
import com.zpf.support.view.ProgressDialog;

import java.util.HashSet;

/**
 * loading弹窗管理
 * Created by ZPF on 2019/3/20.
 */
public class LoadingManagerImpl implements ILoadingManager {
    private ProgressDialog progressDialog;
    private HashSet<ILoadingStateListener> listeners = new HashSet<>();

    public LoadingManagerImpl(Context context) {
        progressDialog = new ProgressDialog(context);
    }

    @Override
    public void showLoading() {
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
            for (ILoadingStateListener l : listeners) {
                l.onState(true);
            }
        }
    }

    @Override
    public boolean hideLoading() {
        if (progressDialog.isShowing()) {
            for (ILoadingStateListener l : listeners) {
                l.onState(false);
            }
            progressDialog.dismiss();
            return true;
        }
        return false;
    }

    @Override
    public void addStateListener(ILoadingStateListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeStateListener(ILoadingStateListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    @Override
    public Object getLoadingView() {
        return progressDialog;
    }

}
