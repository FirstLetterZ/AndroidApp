package com.zpf.support.util;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;

import com.zpf.support.view.ProxySupportViewContainer;
import com.zpf.support.view.ProxyViewContainer;
import com.zpf.support.interfaces.ViewContainerInterface;

/**
 * Created by ZPF on 2018/7/25.
 */

public class ContainerProxyManager {
    private static final String TAG = "Proxy_";

    public static ViewContainerInterface create(Activity activity) {
        if (activity instanceof FragmentActivity) {
            ProxySupportViewContainer proxyViewContainer;
            android.support.v4.app.FragmentManager manager = ((FragmentActivity) activity).getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
            android.support.v4.app.Fragment fragment = manager.findFragmentByTag(TAG + activity.getClass().getName());
            if (fragment != null && fragment instanceof ProxySupportViewContainer) {
                proxyViewContainer = (ProxySupportViewContainer) fragment;
                if (proxyViewContainer.isHidden()) {
                    transaction.show(fragment);
                }
            } else {
                proxyViewContainer = new ProxySupportViewContainer();
                proxyViewContainer.onConditionsCompleted(((FragmentActivity) activity));
                transaction.add(proxyViewContainer, TAG + activity.getClass().getName());
            }
            transaction.commitAllowingStateLoss();
            return proxyViewContainer;
        } else {
            ProxyViewContainer proxyViewContainer;
            android.app.FragmentManager manager = activity.getFragmentManager();
            android.app.FragmentTransaction transaction = manager.beginTransaction();
            android.app.Fragment fragment = manager.findFragmentByTag(TAG + activity.getClass().getName());
            if (fragment != null && fragment instanceof ProxyViewContainer) {
                proxyViewContainer = (ProxyViewContainer) fragment;
                if (proxyViewContainer.isHidden()) {
                    transaction.show(fragment);
                }
            } else {
                proxyViewContainer = new ProxyViewContainer();
                proxyViewContainer.onConditionsCompleted(activity);
                transaction.add(proxyViewContainer, TAG + activity.getClass().getName());
            }
            transaction.commitAllowingStateLoss();
            return proxyViewContainer;
        }
    }

    public static ViewContainerInterface create(android.app.Fragment fragment) {
        ProxyViewContainer proxyViewContainer;
        android.app.FragmentManager manager = fragment.getChildFragmentManager();
        android.app.FragmentTransaction transaction = manager.beginTransaction();
        android.app.Fragment childFragment = manager.findFragmentByTag(TAG + fragment.getClass().getName());
        if (childFragment != null && childFragment instanceof ProxyViewContainer) {
            proxyViewContainer = (ProxyViewContainer) childFragment;
            if (proxyViewContainer.isHidden()) {
                transaction.show(childFragment);
            }
        } else {
            proxyViewContainer = new ProxyViewContainer();
            proxyViewContainer.onConditionsCompleted(fragment);
            transaction.add(proxyViewContainer, TAG + fragment.getClass().getName());
        }
        transaction.commitAllowingStateLoss();
        return proxyViewContainer;
    }

    public static ViewContainerInterface create(android.support.v4.app.Fragment fragment) {
        ProxySupportViewContainer proxyViewContainer;
        android.support.v4.app.FragmentManager manager = fragment.getChildFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
        android.support.v4.app.Fragment childFragment = manager.findFragmentByTag(TAG + fragment.getClass().getName());
        if (childFragment != null && childFragment instanceof ProxySupportViewContainer) {
            proxyViewContainer = (ProxySupportViewContainer) childFragment;
            if (proxyViewContainer.isHidden()) {
                transaction.show(childFragment);
            }
        } else {
            proxyViewContainer = new ProxySupportViewContainer();
            proxyViewContainer.onConditionsCompleted(fragment);
            transaction.add(proxyViewContainer, TAG + fragment.getClass().getName());
        }
        transaction.commitAllowingStateLoss();
        return proxyViewContainer;
    }
}
