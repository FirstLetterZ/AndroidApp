package com.zpf.appLib.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import com.zpf.appLib.activity.PageActivity;
import com.zpf.appLib.util.DialogControlUtil;
import com.zpf.appLib.util.RouteUtil;
import com.zpf.appLib.constant.AppConst;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ZPF on 2018/4/13.
 */
@SuppressLint("Registered")
public class BaseActivity extends FragmentActivity implements BaseViewContainer {
    private boolean isCreated = false;
    private boolean isDestroy = false;
    private Intent mIntent;
    private BaseViewLayout mView;
    protected final String INTENT_TARGET_CLASS = "viewClass";
    private List<Runnable> mWaitingList;
    private volatile Class<? extends BaseViewLayout> mViewClass;
    private LinkedList<Class<? extends BaseViewLayout>> mTargetViewList = new LinkedList<>();
    private DialogControlUtil dialogControlUtil = new DialogControlUtil(new DialogControlUtil.LifeControl() {
        @Override
        public boolean isDestroy() {
            return isDestroy;
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isRootActivity()) {
            mViewClass = getViewClass(getIntent());
            if (mViewClass != null) {
                try {
                    Class[] pType = new Class[]{BaseViewContainer.class};
                    Constructor constructor = mViewClass.getConstructor(pType);
                    mView = (BaseViewLayout) constructor.newInstance(this);
                    mView.initActivityConfig(this);
                    setContentView(mView.getRootLayout());
                    isCreated = true;
                    mView.afterCreate(savedInstanceState);
                    if (mWaitingList != null && mWaitingList.size() > 0) {
                        for (Runnable run : mWaitingList) {
                            run.run();
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException("create activity failed:" + e.toString());
                }
            } else {
                onViewClassNotFound(savedInstanceState);
            }
        }
    }

    protected void setStatusBarTranslucent(boolean halfTransparent) {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//判断版本是5.0以上
            if (halfTransparent) {
                //半透明状态栏
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//判断版本是4.4以上
            //半透明状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    protected boolean isRootActivity() {
        return false;
    }

    protected void onViewClassNotFound(@Nullable Bundle savedInstanceState) {
        throw new RuntimeException("not found target view class");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mView != null) {
            mView.onNewIntent(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mView != null) {
            mView.onStart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mView != null) {
            mView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mView != null) {
            mView.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mView != null) {
            mView.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        isDestroy = true;
        dialogControlUtil.onDestroy();
        if (mView != null) {
            mView.onDestroy();
            mView = null;
        }
        super.onDestroy();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null && mView != null) {
            mView.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && mView != null) {
            mView.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mTargetViewList.poll();
        if (mTargetViewList.size() > 0) {
            pushActivityArray(mTargetViewList);
            return;
        }
        if (resultCode == AppConst.REQUEST_CODE_CHECK_TARGET) {
            Class<? extends BaseViewLayout> viewClass = getViewClass(data);
            if (viewClass != null) {
                if (mViewClass == viewClass) {
                    onNewIntent(data);
                } else if (isRootActivity()) {
                    obtainIntent(data);
                    pushActivity(viewClass);
                } else {
                    setResult(resultCode, data);
                    finish();
                }
            } else if (isRootActivity()) {
                onNewIntent(data);
            }
        } else if (resultCode == AppConst.REQUEST_CODE_EXIT_APP) {
            exit();
        } else if (resultCode == Activity.RESULT_CANCELED && isRootActivity()) {
            finish();
        } else {
            if (mView != null) {
                mView.onActivityResult(requestCode, resultCode, data);
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (mView != null) {
            mView.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean isCreated() {
        return isCreated;
    }

    @Override
    public boolean isDestroy() {
        return isDestroy;
    }

    @Override
    public boolean isActivity() {
        return true;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public Window getWindow() {
        return super.getWindow();
    }

    @Override
    public LayoutInflater getViewInflater() {
        return getLayoutInflater();
    }

    @Override
    public Intent obtainIntent(@Nullable Intent intent) {
        if (intent != null) {
            mIntent = intent;
        }
        if (mIntent == null) {
            mIntent = new Intent();
        }
        return mIntent;
    }

    @Override
    public synchronized void pickActivity(Class<? extends BaseViewLayout> viewClass) {
        if (viewClass != null && viewClass != mViewClass && mTargetViewList.size() == 0 && viewClass != mViewClass) {
            mTargetViewList.add(viewClass);
            obtainIntent(null).putExtra(INTENT_TARGET_CLASS, viewClass);
            setResult(AppConst.REQUEST_CODE_CHECK_TARGET, mIntent);
            finish();
        }
    }

    @Override
    public synchronized void pushActivity(Class<? extends BaseViewLayout> viewClass) {
        pushActivityForResult(viewClass, AppConst.REQUEST_CODE_DEFAULT);
    }

    @Override
    public void pickActivity(String name) {
        RouteUtil.instance().pickActivity(name, this);
    }

    @Override
    public void pushActivity(String name) {
        RouteUtil.instance().pushActivity(name, this);
    }

    @Override
    public void pushActivityArray(List<Class<? extends BaseViewLayout>> viewClassArray) {
        if (viewClassArray != null && viewClassArray.size() > 0 && mTargetViewList.size() == 0
                && viewClassArray.get(0) != mViewClass && viewClassArray.get(0) != null) {
            mTargetViewList.clear();
            mTargetViewList.addAll(viewClassArray);
            Class targetActivityClass = null;
            if (mView != null) {
                targetActivityClass = mView.getActivityClass(viewClassArray.get(0));
            }
            if (targetActivityClass == null) {
                targetActivityClass = PageActivity.class;
            }
            obtainIntent(null).setClass(getContext(), targetActivityClass);
            mIntent.putExtra(INTENT_TARGET_CLASS, viewClassArray.get(0));
            startActivityForResult(mIntent, AppConst.REQUEST_CODE_DEFAULT);
        }
    }

    @Override
    public synchronized void pushActivityForResult(Class<? extends BaseViewLayout> viewClass, int requestCode) {
        if (viewClass != null && mTargetViewList.size() == 0 && viewClass != mViewClass) {
            mTargetViewList.add(viewClass);
            Class targetActivityClass = null;
            if (mView != null) {
                targetActivityClass = mView.getActivityClass(viewClass);
            }
            if (targetActivityClass == null) {
                targetActivityClass = PageActivity.class;
            }
            obtainIntent(null).setClass(getContext(), targetActivityClass);
            mIntent.putExtra(INTENT_TARGET_CLASS, viewClass);
            startActivityForResult(mIntent, requestCode);
        }
    }

    @Override
    public void pushActivityForResult(String name, int requestCode) {
        RouteUtil.instance().pushActivityForResult(name, this, requestCode);
    }

    @Override
    public void showDialog(BaseDialog dialog) {
        dialogControlUtil.showDialog(dialog);
    }

    @Override
    public void sendMessage(String name, Object value) {
        if (isCreated) {
            handleMessage(name, value);
        } else {
            if (mWaitingList == null) {
                mWaitingList = new ArrayList<>();
            }
            final String msgName = name;
            final Object msgValue = value;
            mWaitingList.add(new Runnable() {
                @Override
                public void run() {
                    handleMessage(msgName, msgValue);
                }
            });
        }
    }

    @Override
    public void handleMessage(String name, Object value) {

    }

    @Override
    public void finishWithResult(int resultCode, Intent data) {
        setResult(resultCode, data);
        finish();
    }

    @Override
    public void exit() {
        setResult(AppConst.REQUEST_CODE_EXIT_APP, null);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
    }

    private Class<? extends BaseViewLayout> getViewClass(Intent intent) {
        Class<? extends BaseViewLayout> viewClass = null;
        if (intent != null) {
            Serializable serializable = intent.getSerializableExtra(INTENT_TARGET_CLASS);
            if (serializable != null) {
                try {
                    viewClass = (Class<? extends BaseViewLayout>) serializable;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return viewClass;
    }
}
