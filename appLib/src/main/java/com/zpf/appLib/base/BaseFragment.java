package com.zpf.appLib.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.zpf.appLib.activity.PageActivity;
import com.zpf.appLib.util.DialogControlUtil;
import com.zpf.appLib.util.RouteUtil;
import com.zpf.appLib.constant.AppConst;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ZPF on 2018/4/14.
 */

public class BaseFragment extends Fragment implements BaseViewContainer {
    private boolean isCreated = false;
    private boolean isDestroy = false;
    private Intent mIntent;
    private BaseViewLayout mView;
    protected final String INTENT_TARGET_CLASS = "viewClass";
    private List<Runnable> mWaitingList;
    private Class<? extends BaseViewLayout> mViewClass;
    private LinkedList<Class<? extends BaseViewLayout>> mTargetViewList = new LinkedList<>();
    private boolean isActivityVisible = false;
    private boolean isFragmentVisible = false;
    private DialogControlUtil dialogControlUtil = new DialogControlUtil(new DialogControlUtil.LifeControl() {
        @Override
        public boolean isDestroy() {
            return isDestroy;
        }
    });

    /**
     * 静态构造方法
     *
     * @param viewClass
     * @return
     */
    public static BaseFragment create(Class<? extends BaseViewLayout> viewClass) {
        BaseFragment fragment = new BaseFragment();
        fragment.setViewClass(viewClass);
        return fragment;
    }

    private void setViewClass(Class<? extends BaseViewLayout> viewClass) {
        this.mViewClass = viewClass;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mViewClass == null) {
            return null;
        } else {
            try {
                Class[] pType = new Class[]{BaseViewContainer.class};
                Constructor constructor = mViewClass.getConstructor(pType);
                mView = (BaseViewLayout) constructor.newInstance(this);
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
            return mView.getRootLayout();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mView != null) {
            mView.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mView != null) {
            mView.onResume();
            mView.onVisibleChange(isFragmentVisible, isFragmentVisible, isActivityVisible, true);
        }
        isActivityVisible = true;

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mView != null) {
            mView.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mView != null) {
            mView.onStop();
            mView.onVisibleChange(isFragmentVisible, isFragmentVisible, isActivityVisible, false);
        }
        isActivityVisible = false;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (mView != null) {
            mView.onVisibleChange(isFragmentVisible, (!hidden), isActivityVisible, isActivityVisible);
        }
        isFragmentVisible = !hidden;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (mView != null) {
            mView.onVisibleChange(isFragmentVisible, isVisibleToUser, isActivityVisible, isActivityVisible);
        }
        isFragmentVisible = isVisibleToUser;
    }

    @Override
    public void onDestroyView() {
        isDestroy = true;
        dialogControlUtil.onDestroy();
        if (mView != null) {
            mView.onDestroy();
            mView = null;
        }
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mView != null) {
            mView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
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
            FragmentActivity fragmentActivity = getActivity();
            if (fragmentActivity != null && fragmentActivity instanceof BaseActivity) {
                ((BaseActivity) fragmentActivity).onActivityResult(requestCode, resultCode, data);
            }
        } else if (resultCode == AppConst.REQUEST_CODE_EXIT_APP) {
            exit();
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
        return false;
    }

    @Override
    public Context getContext() {
        return super.getContext();
    }

    @Override
    public Window getWindow() {
        Activity activity = getActivity();
        return activity == null ? null : activity.getWindow();
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
            finishWithResult(AppConst.REQUEST_CODE_CHECK_TARGET, mIntent);
        }
    }

    @Override
    public synchronized void pushActivity(Class<? extends BaseViewLayout> viewClass) {
        pushActivityForResult(viewClass, AppConst.REQUEST_CODE_DEFAULT);
    }

    @Override
    public synchronized void pickActivity(String name) {
        RouteUtil.instance().pickActivity(name, this);
    }

    @Override
    public synchronized void pushActivity(String name) {
        RouteUtil.instance().pushActivity(name, this);
    }

    @Override
    public void pushActivityArray(List<Class<? extends BaseViewLayout>> viewClassArray) {
        if (viewClassArray != null && viewClassArray.size() > 0 && mTargetViewList.size() == 0
                && viewClassArray.get(0) != mViewClass && viewClassArray.get(0) != null && getContext() != null) {
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
        if (viewClass != null && mTargetViewList.size() == 0 && viewClass != mViewClass && getContext() != null) {
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
    public synchronized void pushActivityForResult(String name, int requestCode) {
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
        if (getActivity() != null) {
            getActivity().setResult(resultCode, data);
            getActivity().finish();
        }
    }

    @Override
    public void exit() {
        if (getActivity() != null) {
            getActivity().setResult(AppConst.REQUEST_CODE_EXIT_APP, null);
            getActivity().finish();
        }
    }

    @Override
    public void finish() {
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

}
