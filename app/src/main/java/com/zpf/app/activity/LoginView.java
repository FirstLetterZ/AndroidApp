package com.zpf.app.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.zpf.api.ILayoutId;
import com.zpf.app.plugin.AsyncLoadListener;
import com.zpf.app.plugin.AsyncLoadState;
import com.zpf.app.plugin.PluginApkBean;
import com.zpf.app.plugin.PluginController;
import com.zpf.support.base.ViewProcessor;
import com.zpf.app.R;
import com.zpf.support.constant.AppConst;
import com.zpf.support.util.LogUtil;
import com.zpf.tool.ToastUtil;

/**
 * Created by ZPF on 2019/3/25.
 */
@ILayoutId(R.layout.activity_main)
public class LoginView extends ViewProcessor {
    private AsyncLoadListener loadListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitleBar.getTitle().setText("测试");
        bindAllChildren(mRootLayout.getContentLayout());
        loadListener = new AsyncLoadListener() {
            @Override
            public void onState(int sateCode, @NonNull String targetName, @Nullable PluginApkBean apkBean) {
                switch (sateCode) {
                    case AsyncLoadState.CONFIG_ERROR:
                        break;
                    case AsyncLoadState.CONFIG_NULL:
                        break;
                    case AsyncLoadState.PARAM_ERROR:
                        break;
                    case AsyncLoadState.RESULT_NULL:
                        break;
                    case AsyncLoadState.UPDATE_FORCE:
                        break;
                    case AsyncLoadState.UPDATE_NEED:
                    case AsyncLoadState.UPDATE_LOSS:
                        if (apkBean != null) {
                            Class<?> cls = PluginController.get().loadClass(apkBean, targetName);
                            if (cls != null) {
                                jumpToLayout(targetName, cls);
                            } else {
                                this.onState(AsyncLoadState.RESULT_NULL, targetName, apkBean);
                            }
                        } else {
                            this.onState(AsyncLoadState.PARAM_ERROR, targetName, apkBean);
                        }
                        break;
                    case AsyncLoadState.LOADING:
                        mContainer.showLoading();
                        break;
                }
                LogUtil.w("onState=" + sateCode);
            }

            @Override
            public void onResult(@NonNull String targetName, @NonNull Class<?> result) {
                jumpToLayout(targetName, result);
            }

        };
    }

    private void jumpToLayout(String targetName, Class<?> targetClass) {
        Bundle params = new Bundle();
        params.setClassLoader(targetClass.getClassLoader());
        params.putString(AppConst.INTENT_KEY, "123");
        params.putString(AppConst.TARGET_VIEW_CLASS_NAME, targetName);
        push(targetClass, params);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("test", "1234");
    }

    @Override
    public void onClick(View view) {
        LogUtil.e(view.getClass().getName());
        switch (view.getId()) {
            case R.id.btn_copy:
                if (PluginController.get().copyApk("plugin_test")) {
                    ToastUtil.toast("success");
                } else {
                    ToastUtil.toast("fail");
                }
                break;
            case R.id.btn_start:
                PluginController.get().getClassAsync("TestMainLayout", "plugin_test", loadListener);
                break;
            case R.id.btn_cancel:
                Bundle params = new Bundle();
                params.putString(AppConst.INTENT_KEY, "234");
                push(TestView.class, params);
                break;
        }
    }

}
