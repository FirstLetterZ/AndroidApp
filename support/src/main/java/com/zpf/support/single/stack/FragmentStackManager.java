package com.zpf.support.single.stack;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.zpf.api.INavigator;
import com.zpf.support.base.ViewProcessor;
import com.zpf.support.constant.AppConst;
import com.zpf.support.single.OnStackEmptyListener;
import com.zpf.support.util.FragmentHelper;
import com.zpf.tool.global.CentralManager;
import com.zpf.tool.stack.StackElementState;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ZPF on 2019/5/20.
 */
public class FragmentStackManager implements INavigator<Class<? extends ViewProcessor>> {
    private final LinkedList<FragmentElementInfo> stackList = new LinkedList<>();
    private OnStackEmptyListener emptyListener;
    private final FragmentManager fragmentManager;
    private final int viewId;

    public FragmentStackManager(FragmentManager fragmentManager, int viewId) {
        this.fragmentManager = fragmentManager;
        this.viewId = viewId;
    }

    public void setEmptyListener(OnStackEmptyListener emptyListener) {
        this.emptyListener = emptyListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void push(Class<? extends ViewProcessor> target, Intent params, int requestCode) {
        String tag = target.getName();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment targetFragment = null;
        if (params == null) {
            params = new Intent();
            params.putExtra(AppConst.TARGET_VIEW_CLASS, target);
        }
        params.putExtra(AppConst.REQUEST_CODE, requestCode);
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
            targetFragment = FragmentHelper.createFragment(params.getExtras());
            transaction.add(viewId, targetFragment, tag);
        } else {
            targetFragment.setArguments(params.getExtras());
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
    public void push(@NonNull Class<? extends ViewProcessor> target, Intent params) {
        this.push(target, params, AppConst.DEF_REQUEST_CODE);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public void push(@NonNull Class<? extends ViewProcessor> target) {
        this.push(target, null, AppConst.DEF_REQUEST_CODE);
    }

    @Override
    public void pop(int resultCode, Intent data) {
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
            CentralManager.runDelayed(new Runnable() {
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
    public void pop() {
        this.pop(AppConst.DEF_RESULT_CODE, null);
    }

    @Override
    public void popToRoot(@Nullable Intent data) {
        if (stackList.size() <= 1) {
            return;
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        FragmentElementInfo lastElementInfo;
        synchronized (stackList) {
            while ((lastElementInfo = stackList.pollLast()) != null) {
                if (stackList.size() == 0) {
                    lastElementInfo.instance.onActivityResult(AppConst.POLL_BACK_REQUEST_CODE, AppConst.POLL_BACK_RESULT_CODE, data);
                    lastElementInfo.state = StackElementState.STACK_TOP;
                    transaction.show(lastElementInfo.instance);
                    stackList.add(lastElementInfo);
                    break;
                }
                lastElementInfo.state = StackElementState.STACK_OUTSIDE;
                transaction.remove(lastElementInfo.instance);
            }
        }
        transaction.commitAllowingStateLoss();
    }

    @Override
    public boolean popTo(@NonNull Class<? extends ViewProcessor> target, @Nullable Intent data) {
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
            CentralManager.runDelayed(new Runnable() {
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
    public void replace(@NonNull Class<? extends ViewProcessor> target, @Nullable Intent params) {
        this.pop();
        push(target, params);
    }

    @Override
    public boolean remove(@NonNull Class<? extends ViewProcessor> target) {
        boolean result = false;
        synchronized (stackList) {
            FragmentElementInfo lastElementInfo = stackList.peekLast();
            if (lastElementInfo != null && TextUtils.equals(lastElementInfo.tag, target.getName())) {
                pop();
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
        Intent params;
        int requestCode;
        int resultCode;
        Intent resultData;
        int state;
    }
}
