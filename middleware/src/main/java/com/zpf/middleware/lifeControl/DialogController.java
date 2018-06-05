package com.zpf.middleware.lifeControl;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by ZPF on 2018/6/5.
 */
public class DialogController implements LifecycleArrayListener {
    private ArrayList<LifecycleWindowInterface> cacheList = new ArrayList<>();
    private LinkedList<LifecycleWindowInterface> waitList = new LinkedList<>();
    private LifecycleWindowInterface showingWindow;
    private volatile boolean isDestroy = false;
 
    public void show(int id) {
        if (isDestroy) {
            return;
        }
        if (id >= 0 && id < cacheList.size()) {
            if (showingWindow == null) {
                showingWindow = cacheList.get(id);
                showingWindow.show();
            } else {
                waitList.add(showingWindow);
            }
        }
    }

    public int show(LifecycleWindowInterface window) {
        if (window == null || isDestroy) {
            return -1;
        }
        int id = addCache(window);
        if (showingWindow == null) {
            showingWindow = window;
            window.show();
        } else {
            waitList.add(window);
        }
        return id;
    }

    public int addCache(LifecycleWindowInterface window) {
        if (window == null || isDestroy) {
            return -1;
        }
        int id = cacheList.indexOf(window);
        if (id < 0) {
            cacheList.add(window);
            window.addController(this);
            id = cacheList.size() - 1;
        }
        return id;
    }

    @Override
    public void beforeCreate() {

    }

    @Override
    public void afterCreate(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {
        isDestroy = true;
        waitList.clear();
        cacheList.clear();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

    }


    @Override
    public void next() {
        if (isDestroy) {
            return;
        }
        if (showingWindow == null && waitList.size() > 0) {
            showingWindow = waitList.poll();
            if (showingWindow == null) {
                next();
            } else {
                showingWindow.show();
            }
        }
    }

}
