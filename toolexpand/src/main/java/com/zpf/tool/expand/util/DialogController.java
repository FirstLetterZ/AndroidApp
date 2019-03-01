package com.zpf.tool.expand.util;

import android.util.Pair;

import com.zpf.api.ICustomWindow;
import com.zpf.api.IManager;
import com.zpf.api.OnDestroyListener;

import java.util.LinkedList;

/**
 * Created by ZPF on 2018/6/5.
 */
public class DialogController implements IManager<ICustomWindow>, OnDestroyListener {
    private LinkedList<Pair<Long, ICustomWindow>> cacheList = new LinkedList<>();
    private ICustomWindow showingWindow;
    private long showingWindowId;
    private volatile boolean isDestroy = false;

    @Override
    public long bind(ICustomWindow safeWindow) {
        if (isDestroy || safeWindow == null) {
            return 0;
        }
        long id = -1;
        if (showingWindow == safeWindow) {
            id = showingWindowId;
        }else {
            for (Pair<Long, ICustomWindow> cache : cacheList) {
                if (cache.second == safeWindow) {
                    id = cache.first;
                    break;
                }
            }
            if (id < 0) {
                id = System.currentTimeMillis();
                cacheList.add(new Pair<Long, ICustomWindow>(id, safeWindow));
            }
        }
        show(safeWindow);
        return id;
    }

    @Override
    public boolean execute(long id) {
        if (isDestroy) {
            return false;
        }
        if (id <= 0) {
            showNext();
            return false;
        } else {
            return !checkShowing();
        }
    }

    @Override
    public void remove(long id) {
        synchronized (DialogController.class) {
            for (Pair<Long, ICustomWindow> cache : cacheList) {
                if (id == cache.first) {
                    cacheList.remove(cache);
                    break;
                }
            }
        }
    }

    @Override
    public void cancel(long id) {
        if (showingWindowId == id && checkShowing()) {
            showingWindow.dismiss();
        }
        remove(id);
    }

    @Override
    public void cancelAll() {
        isDestroy = true;
        if (showingWindow != null && showingWindow.isShowing()) {
            showingWindow.dismiss();
        }
        showingWindow = null;
        showingWindowId = -1;
        cacheList.clear();
        isDestroy = false;
    }

    @Override
    public void onDestroy() {
        isDestroy = true;
        showingWindow = null;
        showingWindowId = -1;
        cacheList.clear();
    }

    private boolean checkShowing() {
        return (showingWindow != null && showingWindow.isShowing());
    }

    public void show(ICustomWindow window) {
        if (isDestroy || window == null || checkShowing()) {
            return;
        }
        showingWindow = window;
        try {
            showingWindow.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showNext() {
        if (cacheList.size() > 0) {
            Pair<Long, ICustomWindow> pair = cacheList.pollFirst();
            if (pair == null || pair.second == null) {
                showNext();
            } else {
                show(pair.second);
            }
        }
    }

}
