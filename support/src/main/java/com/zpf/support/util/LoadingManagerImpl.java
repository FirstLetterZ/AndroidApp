package com.zpf.support.util;

import android.content.Context;
import android.view.View;

import com.zpf.frame.ILoadingManager;
import com.zpf.support.defview.ProgressDialog;

/**
 * Created by ZPF on 2019/3/20.
 */

public class LoadingManagerImpl implements ILoadingManager {
    private ProgressDialog progressDialog;

    public LoadingManagerImpl(Context context) {
        progressDialog = new ProgressDialog(context);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void showLoading(String msg) {
        progressDialog.setText(msg);
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    @Override
    public boolean hideLoading() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
            return true;
        }
        return false;
    }

    @Override
    public View getLoadingView() {
        if (progressDialog.getWindow() != null) {
            return progressDialog.getWindow().getDecorView();
        } else {
            return null;
        }
    }

}
