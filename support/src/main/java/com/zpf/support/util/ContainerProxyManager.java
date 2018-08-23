package com.zpf.support.util;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;

import com.zpf.support.defview.ProxyCompatContainer;
import com.zpf.support.defview.ProxyContainer;
import com.zpf.support.interfaces.ViewContainerInterface;

/**
 * Created by ZPF on 2018/7/25.
 */

public class ContainerProxyManager {
    private static final String TAG = "Proxy_";

    public static ViewContainerInterface create(Activity activity) {
        if (activity instanceof FragmentActivity) {
            ProxyCompatContainer proxyViewContainer;
            android.support.v4.app.FragmentManager manager = ((FragmentActivity) activity).getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
            android.support.v4.app.Fragment fragment = manager.findFragmentByTag(TAG + activity.getClass().getName());
            if (fragment != null && fragment instanceof ProxyCompatContainer) {
                proxyViewContainer = (ProxyCompatContainer) fragment;
                if (proxyViewContainer.isHidden()) {
                    transaction.show(fragment);
                }
            } else {
                proxyViewContainer = new ProxyCompatContainer();
                proxyViewContainer.onConditionsCompleted(((FragmentActivity) activity));
                transaction.add(proxyViewContainer, TAG + activity.getClass().getName());
            }
            transaction.commitAllowingStateLoss();
            return proxyViewContainer;
        } else {
            ProxyContainer proxyViewContainer;
            android.app.FragmentManager manager = activity.getFragmentManager();
            android.app.FragmentTransaction transaction = manager.beginTransaction();
            android.app.Fragment fragment = manager.findFragmentByTag(TAG + activity.getClass().getName());
            if (fragment != null && fragment instanceof ProxyContainer) {
                proxyViewContainer = (ProxyContainer) fragment;
                if (proxyViewContainer.isHidden()) {
                    transaction.show(fragment);
                }
            } else {
                proxyViewContainer = new ProxyContainer();
                proxyViewContainer.onConditionsCompleted(activity);
                transaction.add(proxyViewContainer, TAG + activity.getClass().getName());
            }
            transaction.commitAllowingStateLoss();
            return proxyViewContainer;
        }
    }

    public static ViewContainerInterface create(android.app.Fragment fragment) {
        ProxyContainer proxyViewContainer;
        android.app.FragmentManager manager = fragment.getChildFragmentManager();
        android.app.FragmentTransaction transaction = manager.beginTransaction();
        android.app.Fragment childFragment = manager.findFragmentByTag(TAG + fragment.getClass().getName());
        if (childFragment != null && childFragment instanceof ProxyContainer) {
            proxyViewContainer = (ProxyContainer) childFragment;
            if (proxyViewContainer.isHidden()) {
                transaction.show(childFragment);
            }
        } else {
            proxyViewContainer = new ProxyContainer();
            proxyViewContainer.onConditionsCompleted(fragment);
            transaction.add(proxyViewContainer, TAG + fragment.getClass().getName());
        }
        transaction.commitAllowingStateLoss();
        return proxyViewContainer;
    }

    public static ViewContainerInterface create(android.support.v4.app.Fragment fragment) {
        ProxyCompatContainer proxyViewContainer;
        android.support.v4.app.FragmentManager manager = fragment.getChildFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
        android.support.v4.app.Fragment childFragment = manager.findFragmentByTag(TAG + fragment.getClass().getName());
        if (childFragment != null && childFragment instanceof ProxyCompatContainer) {
            proxyViewContainer = (ProxyCompatContainer) childFragment;
            if (proxyViewContainer.isHidden()) {
                transaction.show(childFragment);
            }
        } else {
            proxyViewContainer = new ProxyCompatContainer();
            proxyViewContainer.onConditionsCompleted(fragment);
            transaction.add(proxyViewContainer, TAG + fragment.getClass().getName());
        }
        transaction.commitAllowingStateLoss();
        return proxyViewContainer;
    }
}
