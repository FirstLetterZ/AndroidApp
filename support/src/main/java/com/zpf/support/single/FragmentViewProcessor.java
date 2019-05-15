package com.zpf.support.single;

import android.content.Intent;
import android.os.Bundle;

import com.zpf.frame.INavigator;
import com.zpf.support.base.ViewProcessor;

/**
 * Created by ZPF on 2019/5/15.
 */

public class FragmentViewProcessor<C> extends ViewProcessor<C> implements INavigator<Class<? extends FragmentViewProcessor>> {


    @Override
    public void push(Class<? extends FragmentViewProcessor> target, Bundle params, int requestCode) {

    }

    @Override
    public void push(Class<? extends FragmentViewProcessor> target, Bundle params) {

    }

    @Override
    public void push(Class<? extends FragmentViewProcessor> target) {

    }

    @Override
    public void poll(int resultCode, Intent data) {

    }

    @Override
    public void poll() {

    }

    @Override
    public void pollUntil(Class<? extends FragmentViewProcessor> target, int resultCode, Intent data) {

    }

    @Override
    public void pollUntil(Class<? extends FragmentViewProcessor> target) {

    }

    @Override
    public void remove(Class<? extends FragmentViewProcessor> target, int resultCode, Intent data) {

    }

    @Override
    public void remove(Class<? extends FragmentViewProcessor> target) {

    }

    public String getAlias() {
        return null;
    }
}
