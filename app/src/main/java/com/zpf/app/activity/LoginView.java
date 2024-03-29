package com.zpf.app.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpf.app.plugin.AsyncLoadListener;
import com.zpf.app.plugin.AsyncLoadState;
import com.zpf.app.plugin.PluginApkBean;
import com.zpf.app.plugin.PluginController;
import com.zpf.frame.IViewProcessor;
import com.zpf.support.base.ViewProcessor;
import com.zpf.app.R;
import com.zpf.support.constant.AppConst;
import com.zpf.support.util.Navigation;
import com.zpf.support.view.banner.BannerPagerView;
import com.zpf.support.view.banner.BannerViewCreator;
import com.zpf.support.view.banner.StretchableIndicator;
import com.zpf.tool.expand.util.Logger;
import com.zpf.tool.toast.ToastUtil;

/**
 * Created by ZPF on 2019/3/25.
 */
public class LoginView extends ViewProcessor {
    private AsyncLoadListener loadListener;
    private BannerPagerView bpv = (BannerPagerView) find(R.id.bpv);
    private StretchableIndicator indicator = (StretchableIndicator) bind(R.id.indicator);
    private int pageSize = 8;
    private int d = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindAllChildren(mRootLayout.getContentView(), Button.class);
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
                Logger.w("onState=" + sateCode);
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
                    ((TextView) one).setText(("position=" + position));
                } catch (Exception e) {
                    //
                }
            }

        });

        float[] pp = new float[]{0.6f, 0.7f, 0.8f, 0.6f, 0.8f};
//        sdl.setItemViewCreator(new ItemViewCreator() {
//            @Override
//            public View onCreateView(Context context, int type) {
//                return LayoutInflater.from(context).inflate(R.layout.item_test, sdl, false);
//            }
//
//            @Override
//            public void onBindView(View view, int position) {
//                TextView tv = view.findViewById(R.id.tv_title);
//                tv.setText(position + "->" + pp[position]);
//            }
//        });
//        sdl.setPointPercents(pp);

    }

    private void jumpToLayout(String targetName, Class<?> targetClass) {
        Intent params = new Intent();
        params.setExtrasClassLoader(targetClass.getClassLoader());
        params.putExtra(AppConst.INTENT_KEY, "123");
        params.putExtra(AppConst.TARGET_VIEW_CLASS_NAME, targetName);
        Navigation.get(mContainer).push((Class<? extends IViewProcessor>) targetClass, params);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("test", "1234");
    }

    @Override
    public void onClick(View view) {
        Logger.e(view.getClass().getName());
        switch (view.getId()) {
            case R.id.btn_copy:
                if (PluginController.get().copyApk("plugin_test")) {
                    ToastUtil.toast("success");
                } else {
                    ToastUtil.toast("fail");
                }
                break;
            case R.id.btn_cancel:
                Intent params = new Intent();
                params.putExtra(AppConst.INTENT_KEY, "234");
                Navigation.get(mContainer).push(TestView.class, params);
                break;
            case R.id.btn_custom:
                Navigation.get(mContainer).push(NetView.class);
                break;
        }
    }

}
