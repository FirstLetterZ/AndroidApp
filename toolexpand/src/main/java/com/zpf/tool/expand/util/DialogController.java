package com.zpf.tool.expand.util;

import android.util.Pair;

import com.zpf.api.ICustomWindow;
import com.zpf.api.IManager;

import java.util.LinkedList;

/**
 * Created by ZPF on 2018/6/5.
 */
public class DialogController implements IManager<ICustomWindow> {
    private LinkedList<Pair<Long, ICustomWindow>> cacheList = new LinkedList<>();
    private ICustomWindow showingWindow;
    private long showingWindowId;
    private volatile boolean isDestroy = false;

    @Override
    public long bind(final ICustomWindow safeWindow) {
        if (isDestroy || safeWindow == null) {
            return 0;
        }
        long id = -1;
        if (showingWindow == safeWindow) {
            id = showingWindowId;

        } else {
            for (Pair<Long, ICustomWindow> cache : cacheList) {
                if (cache.second == safeWindow) {
                    id = cache.first;
                    break;
                }
            }
            if (id < 0) {
                id = System.currentTimeMillis();
                if (checkShowing()) {
                    cacheList.add(new Pair<Long, ICustomWindow>(id, safeWindow));
                } else {
                    showingWindow = safeWindow;
                }
            }
        }
        if (!checkShowing()) {
            try {
                showingWindow.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return id;
    }

    @Override
    public boolean execute(long id) {
        if (isDestroy) {
            return false;
        }
        if (id <= 0) {
            return showNext();
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
    public void reset() {
        cancelAll();
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
        bind(window);
    }

    private boolean showNext() {
        boolean handled = false;
        if (showingWindow != null) {
            showingWindow.dismiss();
            showingWindow = null;
            handled = true;
        }
        if (cacheList.size() > 0) {
            Pair<Long, ICustomWindow> pair = cacheList.pollFirst();
            if (pair == null || pair.second == null) {
                return showNext();
            } else {
                show(pair.second);
                handled = true;
            }
        }
        return handled;
    }

}
