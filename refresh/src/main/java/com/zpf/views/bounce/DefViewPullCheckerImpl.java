package com.zpf.views.bounce;

import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;

import java.util.HashMap;

public class DefViewPullCheckerImpl implements IViewPullChecker {
    private static class Instance {
        static DefViewPullCheckerImpl Instance = new DefViewPullCheckerImpl();
    }

    public static DefViewPullCheckerImpl get() {
        return Instance.Instance;
    }

    private final HashMap<Class<?>, IViewPullChecker> handlerHashMap = new HashMap<>();

    public void addViewStateHandler(Class<?> cls, IViewPullChecker handler) {
        handlerHashMap.put(cls, handler);
    }

    public IViewPullChecker removeViewStateHandler(Class<?> cls) {
        return handlerHashMap.remove(cls);
    }

    @Override
    public boolean checkPullUp(View view) {
        if (view == null) {
            return false;
        }
        IViewPullChecker handler = handlerHashMap.get(view.getClass());
        if (handler != null) {
            return handler.checkPullUp(view);
        }
        if (view instanceof IViewPullChecker) {
            return ((IViewPullChecker) view).checkPullUp(view);
        }
        if (view instanceof WebView) {
            return ViewBorderUtil.isWebViewToBottom((WebView) view);
        }
        if (view instanceof AbsListView) {
            return ViewBorderUtil.isAbsListViewToBottom((AbsListView) view);
        }
        if (view instanceof ViewGroup) {
            return ViewBorderUtil.isViewGroupToBottom((ViewGroup) view);
        }
        return ViewBorderUtil.isViewToBottom(view);

    }

    @Override
    public boolean checkPullDown(View view) {
        if (view == null) {
            return false;
        }
        IViewPullChecker handler = handlerHashMap.get(view.getClass());
        if (handler != null) {
            return handler.checkPullDown(view);
        }
        if (view instanceof IViewPullChecker) {
            return ((IViewPullChecker) view).checkPullDown(view);
        }
        if (view instanceof AbsListView) {
            return ViewBorderUtil.isAbsListViewToTop((AbsListView) view);
        }
        return ViewBorderUtil.isViewToTop(view);

    }
}