package com.zpf.support.single.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.zpf.frame.INavigator;
import com.zpf.frame.IViewProcessor;
import com.zpf.support.base.CompatContainerActivity;
import com.zpf.support.base.ViewProcessor;
import com.zpf.support.constant.AppConst;
import com.zpf.support.constant.ContainerType;
import com.zpf.support.single.stack.CompatFragmentStackManager;
import com.zpf.support.single.OnStackEmptyListener;

/**
 * Created by ZPF on 2019/5/20.
 */
public class CompatSinglePageActivity extends CompatContainerActivity {
    private CompatFragmentStackManager fragmentStackManager;

    @Override
    protected IViewProcessor initViewProcessor() {
        return null;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        int viewId = View.generateViewId();
        frameLayout.setId(viewId);
        setContentView(frameLayout);
        fragmentStackManager = new CompatFragmentStackManager(getSupportFragmentManager(), viewId);
        fragmentStackManager.setEmptyListener(new OnStackEmptyListener() {
            @Override
            public void onEmpty(int requestCode, int resultCode, Intent data) {
                finishWithResult(resultCode, data);
            }
        });
        Class<? extends ViewProcessor> targetViewClass = null;
        try {
            targetViewClass = (Class<? extends ViewProcessor>) getParams().getSerializable(AppConst.TARGET_VIEW_CLASS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (targetViewClass != null) {
            fragmentStackManager.push(targetViewClass);
        }
    }

    @Override
    public void onBackPressed() {
        if (!mController.onInterceptBackPress() && !close()) {
            fragmentStackManager.poll();
        }
    }

    @Override
    public int getContainerType() {
        return ContainerType.CONTAINER_SINGLE_COMPAT_ACTIVITY;
    }

    @Override
    public INavigator<Class<? extends IViewProcessor>> getNavigator() {
        return fragmentStackManager;
    }

}
