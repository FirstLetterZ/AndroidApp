package com.zpf.app.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.zpf.api.ILayoutId;
import com.zpf.app.plugin.MainClassLoader;
import com.zpf.support.base.ViewProcessor;
import com.zpf.app.R;
import com.zpf.support.constant.AppConst;
import com.zpf.support.util.LogUtil;
import com.zpf.tool.FileUtil;
import com.zpf.tool.ToastUtil;
import com.zpf.tool.config.AppContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by ZPF on 2019/3/25.
 */
@ILayoutId(R.layout.activity_main)
public class LoginView extends ViewProcessor {
    private MainClassLoader mainClassLoader = new MainClassLoader();
    private MainClassLoader.AsyncLoadListener loadListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitleBar.getTitle().setText("测试");
        bindAllChildren(mRootLayout.getContentLayout());
        loadListener = new MainClassLoader.AsyncLoadListener() {
            @Override
            public void onResult(@Nullable ClassLoader classLoader, @Nullable Class<?> result) {
                if (result == null) {
                    ToastUtil.toast("result == null");
                } else {
                    try {
                        Bundle params = new Bundle();
                        params.setClassLoader(result.getClassLoader());
                        params.putString(AppConst.INTENT_KEY, "123");
                        params.putString(AppConst.TARGET_VIEW_CLASS_NAME, "TestMainLayout");
                        push(result, params);
                    } catch (Exception e) {
                        ToastUtil.toast("push fail");
                    }
                }
            }
        };
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
                if (copyApk("plugin-test.apk", FileUtil.getAppDataPath() + File.separator + "plugin-test.apk")) {
                    ToastUtil.toast("success");
                } else {
                    ToastUtil.toast("fail");
                }
                break;
            case R.id.btn_start:
                mainClassLoader.getClassAsync("TestMainLayout", loadListener);
                break;
            case R.id.btn_cancel:
                Bundle params = new Bundle();
                params.putString(AppConst.INTENT_KEY, "234");
                push(TestView.class, params);
                break;
        }
    }

    private boolean copyApk(String assetFileName, String targetFilePath) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = AppContext.get().getAssets().open(assetFileName);
            out = new FileOutputStream(targetFilePath);
            byte[] bytes = new byte[1024];
            int i;
            while ((i = in.read(bytes)) != -1)
                out.write(bytes, 0, i);
        } catch (IOException e) {
            LogUtil.e(e.toString());
            return false;
        } finally {
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            } catch (IOException e) {
                LogUtil.e(e.toString());
            }

        }
        return true;
    }
}
