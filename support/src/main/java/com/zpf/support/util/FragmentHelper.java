package com.zpf.support.util;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.zpf.frame.IContainerHelper;
import com.zpf.frame.IViewContainer;
import com.zpf.support.base.CompatContainerFragment;
import com.zpf.support.base.ContainerFragment;
import com.zpf.tool.config.GlobalConfigImpl;

import java.util.List;

/**
 * Created by ZPF on 2019/3/21.
 */
public class FragmentHelper {
    public static CompatContainerFragment createCompatFragment(@Nullable Bundle args) {
        IContainerHelper helper = GlobalConfigImpl.get().getGlobalInstance(IContainerHelper.class);
        if (helper != null) {
            IViewContainer container = helper.createFragmentContainer(args);
            if (container instanceof CompatContainerFragment) {
                return ((CompatContainerFragment) container);
            }
        }
        CompatContainerFragment containerFragment = new CompatContainerFragment();
        containerFragment.setArguments(args);
        return containerFragment;
    }

    public static ContainerFragment createFragment(@Nullable Bundle args) {
        IContainerHelper helper = GlobalConfigImpl.get().getGlobalInstance(IContainerHelper.class);
        if (helper != null) {
            IViewContainer container = helper.createFragmentContainer(args);
            if (container instanceof ContainerFragment) {
                return ((ContainerFragment) container);
            }
        }
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

    public static boolean checkFragmentVisible(android.app.Fragment fragment) {
        return fragment != null && fragment.getUserVisibleHint() && fragment.isAdded() && !fragment.isHidden();
    }


    public static boolean checkFragmentVisible(android.support.v4.app.Fragment fragment) {
        return fragment != null && fragment.getUserVisibleHint() && fragment.isAdded() && !fragment.isHidden();
    }

    public static boolean checkParentFragmentVisible(android.app.Fragment fragment) {
        android.app.Fragment parent = fragment.getParentFragment();
        boolean result = true;
        while (parent != null) {
            result = checkFragmentVisible(parent);
            if (result) {
                parent = parent.getParentFragment();
            } else {
                break;
            }
        }
        return result;
    }

    public static boolean checkParentFragmentVisible(android.support.v4.app.Fragment fragment) {
        android.support.v4.app.Fragment parent = fragment.getParentFragment();
        boolean result = true;
        while (parent != null) {
            result = checkFragmentVisible(parent);
            if (result) {
                parent = parent.getParentFragment();
            } else {
                break;
            }
        }
        return result;
    }

    public static void notifyChildrenFragmentVisible(android.app.Fragment parent, boolean visible) {
        android.app.FragmentManager manager = parent.getChildFragmentManager();
        List<android.app.Fragment> list = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            list = manager.getFragments();
        }
        if (list != null) {
            for (android.app.Fragment fragment : list) {
                fragment.onHiddenChanged(!visible);
            }
        }
    }

    public static void notifyChildrenFragmentVisible(android.support.v4.app.Fragment parent, boolean visible) {
        android.support.v4.app.FragmentManager manager = parent.getChildFragmentManager();
        List<android.support.v4.app.Fragment> list = manager.getFragments();
        if (list != null) {
            for (android.support.v4.app.Fragment fragment : list) {
                fragment.onHiddenChanged(!visible);
            }
        }
    }
}
