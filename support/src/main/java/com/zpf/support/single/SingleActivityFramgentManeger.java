package com.zpf.support.single;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.zpf.api.IBackPressInterceptor;
import com.zpf.frame.INavigator;
import com.zpf.support.base.CompatContainerFragment;
import com.zpf.support.base.ViewProcessor;
import com.zpf.support.util.FragmentHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ZPF on 2019/5/13.
 */

public class SingleActivityFramgentManeger implements INavigator<Class<? extends ViewProcessor>>, IBackPressInterceptor {
    private LinkedList<String> fragmentStack = new LinkedList<>();
    private FragmentManager fragmentManager;
    private int viewid;

    public SingleActivityFramgentManeger(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void pushNewActivity(Class target) {

    }

    public void pushNewActivity(Fragment form, Class target) {

    }

    @Override
    public void navigate(Class<? extends ViewProcessor> target) {

    }

    @Override
    public void navigate(Class<? extends ViewProcessor> target, Bundle params) {

    }

    @Override
    public void navigate(Class<? extends ViewProcessor> target, Bundle params, int requestCode) {
        String tag = target.getName();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment targetFragment = null;
        List<Fragment> fragmentList = fragmentManager.getFragments();
        if (fragmentList != null && fragmentList.size() > 0) {
            for (Fragment f : fragmentList) {
                transaction.hide(f);
            }
            targetFragment = fragmentManager.findFragmentByTag(tag);
            if (targetFragment != null) {
                if (!targetFragment.isAdded()) {
                    transaction.add(viewid, targetFragment, tag);
                    fragmentStack.add(tag);
                } else {
                    transaction.show(targetFragment);
                }

            }
        }
        if (targetFragment == null) {
            targetFragment = FragmentHelper.createCompatFragment(params);
            transaction.add(viewid, targetFragment, tag);
            fragmentStack.add(tag);
        }else {
            targetFragment.setArguments(params);
            targetFragment.setTargetFragment();

        }
        transaction.commitNowAllowingStateLoss();

    }


    @Override
    public boolean onInterceptBackPress() {
        return false;
    }
}
