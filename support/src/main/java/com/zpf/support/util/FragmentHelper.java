package com.zpf.support.util;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.zpf.frame.IViewContainer;
import com.zpf.support.base.CompatContainerFragment;
import com.zpf.support.base.ContainerFragment;

/**
 * Created by ZPF on 2019/3/21.
 */
public class FragmentHelper {
    public static CompatContainerFragment createCompatFragment(@Nullable Bundle args) {
        CompatContainerFragment containerFragment = new CompatContainerFragment();
        containerFragment.setArguments(args);
        return containerFragment;
    }

    public static ContainerFragment createFragment(@Nullable Bundle args) {
        ContainerFragment containerFragment = new ContainerFragment();
        containerFragment.setArguments(args);
        return containerFragment;
    }

    public static android.app.FragmentManager getParentManager(IViewContainer container) {
        if (container != null && container instanceof android.app.Fragment) {
            return ((android.app.Fragment) container).getFragmentManager();
        }
        return null;
    }

    public static android.support.v4.app.FragmentManager getComptParentManager(IViewContainer container) {
        if (container != null && container instanceof android.support.v4.app.Fragment) {
            return ((android.support.v4.app.Fragment) container).getFragmentManager();
        }
        return null;
    }

    public static android.app.FragmentManager getChildManager(IViewContainer container) {
        if (container == null) {
            return null;
        } else if (container instanceof android.app.Fragment) {
            return ((android.app.Fragment) container).getChildFragmentManager();
        } else if (container instanceof Activity) {
            return ((Activity) container).getFragmentManager();
        }
        return null;
    }

    public static android.support.v4.app.FragmentManager getComptChildManager(IViewContainer container) {
        if (container == null) {
            return null;
        } else if (container instanceof android.support.v4.app.Fragment) {
            return ((android.support.v4.app.Fragment) container).getChildFragmentManager();
        } else if (container instanceof FragmentActivity) {
            return ((FragmentActivity) container).getSupportFragmentManager();
        }
        return null;
    }
}
