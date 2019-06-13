package com.zpf.support.single.stack;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import com.zpf.frame.INavigator;
import com.zpf.support.base.ViewProcessor;
import com.zpf.support.constant.AppConst;
import com.zpf.support.single.OnStackEmptyListener;
import com.zpf.support.single.StackElementState;
import com.zpf.support.util.FragmentHelper;
import com.zpf.tool.config.MainHandler;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ZPF on 2019/5/20.
 */
public class FragmentStackManager implements INavigator<Class<? extends ViewProcessor>> {
    private final LinkedList<FragmentElementInfo> stackList = new LinkedList<>();
    private OnStackEmptyListener emptyListener;
    private FragmentManager fragmentManager;
    private int viewId;

    public FragmentStackManager(FragmentManager fragmentManager, int viewId) {
        this.fragmentManager = fragmentManager;
        this.viewId = viewId;
    }

    public void setEmptyListener(OnStackEmptyListener emptyListener) {
        this.emptyListener = emptyListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void push(Class<? extends ViewProcessor> target, Bundle params, int requestCode) {
        String tag = target.getName();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment targetFragment = null;
        if (params == null) {
            params = new Bundle();
            params.putSerializable(AppConst.TARGET_VIEW_CLASS, target);
        }
        params.putInt(AppConst.REQUEST_CODE, requestCode);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        if (fragmentList != null && fragmentList.size() > 0) {
            for (Fragment f : fragmentList) {
                transaction.hide(f);
            }
            targetFragment = fragmentManager.findFragmentByTag(tag);
            if (targetFragment != null) {
                if (!targetFragment.isAdded()) {
                    transaction.add(viewId, targetFragment, tag);
                } else {
                    transaction.show(targetFragment);
                }
            }
        }
        if (targetFragment == null) {
            targetFragment = FragmentHelper.createFragment(params);
            transaction.add(viewId, targetFragment, tag);
        } else {
            targetFragment.setArguments(params);
        }
        synchronized (stackList) {
            boolean contain = false;
            for (FragmentElementInfo elementInfo : stackList) {
                if (TextUtils.equals(elementInfo.tag, tag)) {
                    contain = true;
                    stackList.remove(elementInfo);
                    elementInfo.requestCode = requestCode;
                    elementInfo.instance = targetFragment;
                    elementInfo.params = params;
                    elementInfo.state = StackElementState.STACK_TOP;
                    stackList.add(elementInfo);
                } else {
                    if (elementInfo.state == StackElementState.STACK_TOP) {
                        elementInfo.state = StackElementState.STACK_INSIDE;
                    }
                }
            }
            if (!contain) {
                FragmentElementInfo newElementInfo = new FragmentElementInfo();
                newElementInfo.requestCode = requestCode;
                newElementInfo.tag = tag;
                newElementInfo.instance = targetFragment;
                newElementInfo.params = params;
                newElementInfo.state = StackElementState.STACK_TOP;
                stackList.add(newElementInfo);
            }
        }
        transaction.commitNowAllowingStateLoss();
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public void push(Class<? extends ViewProcessor> target, Bundle params) {
        this.push(target, params, AppConst.DEF_REQUEST_CODE);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public void push(Class<? extends ViewProcessor> target) {
        this.push(target, null, AppConst.DEF_REQUEST_CODE);
    }

    @Override
    public void poll(int resultCode, Intent data) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        int pollTime = 0;
        FragmentElementInfo lastElementInfo;
        int requestCode = AppConst.DEF_REQUEST_CODE;
        synchronized (stackList) {
            while ((lastElementInfo = stackList.pollLast()) != null) {
                if (pollTime == 0) {
                    lastElementInfo.state = StackElementState.STACK_OUTSIDE;
                    transaction.remove(lastElementInfo.instance);
                    requestCode = lastElementInfo.requestCode;
                    pollTime++;
                } else if (lastElementInfo.state == StackElementState.STACK_REMOVING) {
                    lastElementInfo.state = StackElementState.STACK_OUTSIDE;
                    transaction.remove(lastElementInfo.instance);
                    requestCode = lastElementInfo.requestCode;
                    resultCode = lastElementInfo.resultCode;
                    data = lastElementInfo.resultData;
                    pollTime++;
                } else {
                    lastElementInfo.state = StackElementState.STACK_TOP;
                    stackList.addLast(lastElementInfo);
                    break;
                }
            }
            for (FragmentElementInfo elementInfo : stackList) {
                if (elementInfo.state == StackElementState.STACK_TOP) {
                    transaction.show(elementInfo.instance);
                } else {
                    transaction.hide(elementInfo.instance);
                }
            }
        }
        transaction.commitAllowingStateLoss();
        if (lastElementInfo != null) {
            lastElementInfo.instance.onActivityResult(requestCode, resultCode, data);
        } else {
            MainHandler.get().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (stackList.size() == 0 && emptyListener != null) {
                        emptyListener.onEmpty();
                    }
                }
            }, 16);
        }
    }

    @Override
    public void poll() {
        this.poll(AppConst.DEF_RESULT_CODE, null);
    }

    @Override
    public void pollUntil(Class<? extends ViewProcessor> target, int resultCode, Intent data) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        FragmentElementInfo lastElementInfo;
        int requestCode = AppConst.DEF_REQUEST_CODE;
        synchronized (stackList) {
            while ((lastElementInfo = stackList.pollLast()) != null) {
                if (TextUtils.equals(target.getName(), lastElementInfo.tag)) {
                    lastElementInfo.state = StackElementState.STACK_OUTSIDE;
                    requestCode = lastElementInfo.requestCode;
                    transaction.remove(lastElementInfo.instance);
                    lastElementInfo = stackList.peekLast();
                    if (lastElementInfo != null) {
                        lastElementInfo.state = StackElementState.STACK_TOP;
                        transaction.show(lastElementInfo.instance);
                    }
                    break;
                } else {
                    lastElementInfo.state = StackElementState.STACK_OUTSIDE;
                    transaction.remove(lastElementInfo.instance);
                }
            }
        }
        transaction.commitAllowingStateLoss();
        if (lastElementInfo != null) {
            lastElementInfo.instance.onActivityResult(requestCode, resultCode, data);
        } else {
            MainHandler.get().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (stackList.size() == 0 && emptyListener != null) {
                        emptyListener.onEmpty();
                    }
                }
            }, 16);
        }
    }

    @Override
    public void pollUntil(Class<? extends ViewProcessor> target) {
        this.pollUntil(target, AppConst.DEF_RESULT_CODE, null);
    }

    @Override
    public void remove(Class<? extends ViewProcessor> target, int resultCode, Intent data) {
        synchronized (stackList) {
            FragmentElementInfo lastElementInfo = stackList.peekLast();
            if (lastElementInfo != null && TextUtils.equals(lastElementInfo.tag, target.getName())) {
                poll(resultCode, data);
            } else {
                for (FragmentElementInfo elementInfo : stackList) {
                    if (TextUtils.equals(elementInfo.tag, target.getName())) {
                        elementInfo.state = StackElementState.STACK_REMOVING;
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void remove(Class<? extends ViewProcessor> target) {
        this.remove(target, AppConst.DEF_RESULT_CODE, null);
    }

    class FragmentElementInfo {
        String tag;
        Fragment instance;
        Bundle params;
        int requestCode;
        int resultCode;
        Intent resultData;
        @StackElementState
        int state;
    }
}
