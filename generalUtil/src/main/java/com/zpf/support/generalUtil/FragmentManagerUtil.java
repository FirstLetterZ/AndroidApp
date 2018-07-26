package com.zpf.support.generalUtil;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.HashMap;

/**
 * Created by ZPF on 2018/6/14.
 */
public class FragmentManagerUtil {

    private FragmentManager mFragmentManager;
    private MyFragmentCreator mCreator;
    private int mContainerViewId;
    private String[] mTagArray;
    private int mCurrentIndex = 0;
    private HashMap<String, Fragment> fragmentCache = new HashMap<>();

    public FragmentManagerUtil(FragmentManager mFragmentManager, int mContainerViewId,
                               String[] tagArray, MyFragmentCreator mCreator) {
        this.mFragmentManager = mFragmentManager;
        this.mCreator = mCreator;
        this.mTagArray = tagArray;
        this.mContainerViewId = mContainerViewId;
    }

    public void showFragment(int index) {
        if (mTagArray == null || mTagArray.length - 1 < index) {
            return;
        }
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        for (int i = 0; i < mTagArray.length; i++) {
            Fragment fragment = fragmentCache.get(mTagArray[i]);
            if (fragment == null) {
                fragment = mFragmentManager.findFragmentByTag(mTagArray[i]);
            }
            if (i == index) {
                if (fragment == null) {
                    fragment = mCreator.create(index);
                    if (fragment != null) {
                        transaction.add(mContainerViewId, fragment, mTagArray[i]);
                        fragmentCache.put(mTagArray[i], fragment);
                    }
                } else {
                    transaction.show(fragment);
                }
            } else if (fragment != null) {
                transaction.hide(fragment);
            }
        }
        mCurrentIndex = index;
        transaction.commitAllowingStateLoss();
    }

    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    public Fragment getFragment(int index) {
        Fragment fragment = null;
        if (mTagArray != null && index < mTagArray.length) {
            fragment = fragmentCache.get(mTagArray[index]);
            if (fragment == null) {
                fragment = mFragmentManager.findFragmentByTag(mTagArray[index]);
            }
        }
        return fragment;
    }

    public interface MyFragmentCreator {
        Fragment create(int index);
    }

}
