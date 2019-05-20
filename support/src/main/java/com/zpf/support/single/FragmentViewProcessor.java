package com.zpf.support.single;

import android.content.Intent;
import android.os.Bundle;

import com.zpf.frame.INavigator;
import com.zpf.support.base.ViewProcessor;
import com.zpf.support.constant.AppConst;

/**
 * Created by ZPF on 2019/5/15.
 */
public class FragmentViewProcessor<C> extends ViewProcessor<C> implements INavigator<Class<? extends FragmentViewProcessor>> {
    private INavigator<Class<? extends ViewProcessor>> realNavigator;

    public FragmentViewProcessor() {
        super();
        realNavigator = ShareBox.getRealNavigator(
                getParams().getString(AppConst.TARGET_VIEW_EXPANSION));
    }

    @Override
    public void push(Class<? extends FragmentViewProcessor> target, Bundle params, int requestCode) {
        realNavigator.push(target, params, requestCode);
    }

    @Override
    public void push(Class<? extends FragmentViewProcessor> target, Bundle params) {
        realNavigator.push(target, params);
    }

    @Override
    public void push(Class<? extends FragmentViewProcessor> target) {
        realNavigator.push(target);
    }

    @Override
    public void poll(int resultCode, Intent data) {
        realNavigator.poll(resultCode, data);
    }

    @Override
    public void poll() {
        realNavigator.poll();
    }

    @Override
    public void pollUntil(Class<? extends FragmentViewProcessor> target, int resultCode, Intent data) {
        realNavigator.pollUntil(target, resultCode, data);
    }

    @Override
    public void pollUntil(Class<? extends FragmentViewProcessor> target) {
        realNavigator.pollUntil(target);
    }

    @Override
    public void remove(Class<? extends FragmentViewProcessor> target, int resultCode, Intent data) {
        realNavigator.remove(target, resultCode, data);
    }

    @Override
    public void remove(Class<? extends FragmentViewProcessor> target) {
        realNavigator.remove(target);
    }

    @Override
    public boolean onInterceptBackPress() {
        if (dismiss()) {
            realNavigator.remove(getClass());
        }
        return true;
    }
}
