package com.zpf.support.single.stack;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.zpf.frame.INavigator;
import com.zpf.frame.IViewProcessor;
import com.zpf.support.constant.AppConst;
import com.zpf.support.single.OnStackEmptyListener;
import com.zpf.support.util.FragmentHelper;
import com.zpf.tool.config.MainHandler;
import com.zpf.tool.stack.StackElementState;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ZPF on 2019/5/20.
 */
public class CompatFragmentStackManager implements INavigator<Class<? extends IViewProcessor>> {
    private final LinkedList<FragmentElementInfo> stackList = new LinkedList<>();
    private OnStackEmptyListener emptyListener;
    private final FragmentManager fragmentManager;
    private final int viewId;

    public CompatFragmentStackManager(FragmentManager fragmentManager, int viewId) {
        this.fragmentManager = fragmentManager;
        this.viewId = viewId;
    }

    public void setEmptyListener(OnStackEmptyListener emptyListener) {
        this.emptyListener = emptyListener;
    }

    @Override
    public void push(Class<? extends IViewProcessor> target, Bundle params, int requestCode) {
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
            targetFragment = FragmentHelper.createCompatFragment(params);
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

    @Override
    public void push(@NonNull Class<? extends IViewProcessor> target, Bundle params) {
        this.push(target, params, AppConst.DEF_REQUEST_CODE);
    }

    @Override
    public void push(@NonNull Class<? extends IViewProcessor> target) {
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
            final Intent c = data;
            MainHandler.get().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (stackList.size() == 0 && emptyListener != null) {
                        emptyListener.onEmpty(c);
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
    public boolean pollUntil(@NonNull Class<? extends IViewProcessor> target, Intent data) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        FragmentElementInfo lastElementInfo;
        synchronized (stackList) {
            while ((lastElementInfo = stackList.pollLast()) != null) {
                if (TextUtils.equals(target.getName(), lastElementInfo.tag)) {
                    lastElementInfo.state = StackElementState.STACK_OUTSIDE;
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
            lastElementInfo.instance.onActivityResult(AppConst.POLL_BACK_REQUEST_CODE, AppConst.POLL_BACK_RESULT_CODE, data);
        } else {
            final Intent c = data;
            MainHandler.get().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (stackList.size() == 0 && emptyListener != null) {
                        emptyListener.onEmpty(c);
                    }
                }
            }, 16);
        }
        return true;
    }

    @Override
    public boolean pollUntil(@NonNull Class<? extends IViewProcessor> target) {
        return this.pollUntil(target, null);
    }

    @Override
    public boolean remove(@NonNull Class<? extends IViewProcessor> target) {
        boolean result = false;
        synchronized (stackList) {
            FragmentElementInfo lastElementInfo = stackList.peekLast();
            if (lastElementInfo != null && TextUtils.equals(lastElementInfo.tag, target.getName())) {
                poll();
                result = true;
            } else {
                for (FragmentElementInfo elementInfo : stackList) {
                    if (TextUtils.equals(elementInfo.tag, target.getName())) {
                        elementInfo.state = StackElementState.STACK_REMOVING;
                        result = true;
                        break;
                    }
                }
            }
        }
        return result;
    }

    static class FragmentElementInfo {
        String tag;
        Fragment instance;
        Bundle params;
        int requestCode;
        int resultCode;
        Intent resultData;
        int state;
    }
}
