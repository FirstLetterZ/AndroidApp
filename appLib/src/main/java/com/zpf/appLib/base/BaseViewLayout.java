package com.zpf.appLib.base;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.zpf.appLib.activity.PageActivity;
import com.zpf.appLib.util.PermissionHelper;
import com.zpf.appLib.util.SafeClickListener;
import com.zpf.appLib.view.RootLayout;
import com.zpf.appLib.view.TitleBarLayout;
import com.zpf.appLib.view.interfaces.LifecycleView;

/**
 * 实际视图管理文件
 * Created by ZPF on 2018/4/13.
 */
public abstract class BaseViewLayout implements LifecycleView {

    private BaseViewContainer mContainer;
    private RootLayout mRootLayout;
    private SafeClickListener mClickListener = new SafeClickListener() {
        @Override
        public void click(View v) {
            OnClick(v);
        }
    };
    private PermissionHelper mPermissionHelper;

    public BaseViewLayout(BaseViewContainer container) {
        this.mContainer = container;
        TitleBarLayout titleBarView = getTitleBarView();
        if (titleBarView != null) {
            mRootLayout = new RootLayout(titleBarView);
        } else {
            mRootLayout = new RootLayout(container.getContext());
        }
        mRootLayout.getTitleBarLayout().setVisibility(container.isActivity() ? View.VISIBLE : View.GONE);
        mRootLayout.getStatusBar().setVisibility(container.isActivity() ? View.VISIBLE : View.GONE);
        View view = getContentView();
        if (view == null) {
            mRootLayout.setContentView(container.getViewInflater(), getLayoutId());
        } else {
            mRootLayout.setContentView(view);
        }
    }

    protected View getContentView() {
        return null;
    }

    protected abstract int getLayoutId();

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (mPermissionHelper != null) {
            mPermissionHelper. onRequestPermissionsResult(requestCode,permissions,grantResults)      ;
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        //Activity独有方法
    }

    @Override
    public void onVisibleChange(boolean oldFragmentVisible, boolean newFragmentVisible,
                                boolean oldActivityVisible, boolean newActivityVisible) {
        //Fragment独有方法
    }

    public TitleBarLayout getTitleBarView() {
        return null;
    }

    //activity配置
    public void initActivityConfig(@NonNull Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).setStatusBarTranslucent(true);
        }
    }

    public Class<? extends Activity> getActivityClass(Class<? extends BaseViewLayout> targetViewClass) {
        return PageActivity.class;
    }

    protected BaseViewContainer getContainer() {
        return mContainer;
    }

    public RootLayout getRootLayout() {
        return mRootLayout;
    }

    public TitleBarLayout getTitleBarLayout() {
        return mRootLayout.getTitleBarLayout();
    }

    public void setClickListener(@NonNull View view) {
        view.setOnClickListener(mClickListener);
    }

    public void setClickListener(int viewId) {
        View view = $(viewId);
        if (view != null) {
            view.setOnClickListener(mClickListener);
        }
    }

    /**
     * 点击事件统一管理
     *
     * @param view
     */
    public void OnClick(View view) {

    }

    public void setPermissionHelper(PermissionHelper mPermissionHelper) {
        this.mPermissionHelper = mPermissionHelper;
    }

    public <T extends View> T $(int viewId) {
        return mRootLayout.findViewById(viewId);
    }

    public void setText(int viewId, CharSequence content) {
        TextView textView = $(viewId);
        if (textView != null) {
            textView.setText(content);
            textView.setVisibility(TextUtils.isEmpty(content) ? View.GONE : View.VISIBLE);
        }
    }

}
