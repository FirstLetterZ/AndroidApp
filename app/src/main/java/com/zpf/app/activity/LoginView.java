package com.zpf.app.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpf.api.ILayoutId;
import com.zpf.app.plugin.AsyncLoadListener;
import com.zpf.app.plugin.AsyncLoadState;
import com.zpf.app.plugin.PluginApkBean;
import com.zpf.app.plugin.PluginController;
import com.zpf.support.base.ViewProcessor;
import com.zpf.app.R;
import com.zpf.support.constant.AppConst;
import com.zpf.support.util.LogUtil;
import com.zpf.support.view.TriangleView;
import com.zpf.support.view.banner.BannerPagerView;
import com.zpf.support.view.banner.BannerViewCreator;
import com.zpf.support.view.banner.StretchableIndicator;
import com.zpf.tool.ToastUtil;

/**
 * Created by ZPF on 2019/3/25.
 */
@ILayoutId(R.layout.activity_main)
public class LoginView extends ViewProcessor {
    private AsyncLoadListener loadListener;
    private BannerPagerView bpv = (BannerPagerView) $(R.id.bpv);
    private TriangleView triangle = (TriangleView) $(R.id.triangle);
    private StretchableIndicator indicator = (StretchableIndicator) $(R.id.indicator);
    private int pageSize = 2;
    private int d = 1;

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

        final float d = getContext().getResources().getDisplayMetrics().density;
        bpv.setPageMargin((int) (-25 * d));
        bpv.setBannerIndicator(indicator);
        bpv.init(new BannerViewCreator() {

            @Override
            public View onCreateView(Context context, int position) {
                LinearLayout layout = new LinearLayout(getContext());
                layout.setGravity(Gravity.CENTER);
                layout.setPadding((int) (20 * d), (int) (20 * d), (int) (20 * d), (int) (20 * d));
                TextView view = new TextView(getContext());
                view.setGravity(Gravity.CENTER);
                view.setTextColor(Color.BLACK);
                view.setBackgroundColor(Color.BLUE);
                layout.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                return layout;
            }

            @Override
            public int getSize() {
                return pageSize;
            }

            @Override
            public void onBindView(View view, int position) {
                try {
                    View one = ((LinearLayout) view).getChildAt(0);
                    ((TextView) one).setText(("tag=" + view.getTag()));
                } catch (Exception e) {
                    //
                }
            }

        });
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
                d = triangle.getTriangleDirection() + 1;
                triangle.setTriangleDirection(d);
                triangle.invalidate();
//                PluginController.get().getClassAsync("TestMainLayout", "plugin_test", loadListener);
                break;
            case R.id.btn_cancel:
                Bundle params = new Bundle();
                params.putString(AppConst.INTENT_KEY, "234");
                push(TestView.class, params);
                break;
        }
    }

}
