package com.zpf.tool.expand.util;

import com.zpf.api.SafeWindowController;
import com.zpf.api.SafeWindowInterface;

import java.util.LinkedList;

/**
 * Created by ZPF on 2018/6/5.
 */
public class DialogController implements SafeWindowController {
    private LinkedList<SafeWindowInterface> cacheList = new LinkedList<>();
    private SafeWindowInterface showingWindow;
    private volatile boolean isDestroy = false;

    @Override
    public void show(SafeWindowInterface safeWindow) {
        if (isDestroy || safeWindow == null) {
            return;
        }
        safeWindow.bindController(this);
        if (showingWindow == null || !showingWindow.isShowing()) {
            showingWindow = safeWindow;
            showingWindow.show();
        } else {
            if (!cacheList.contains(safeWindow)) {
                cacheList.add(safeWindow);
            }
        }
    }

    @Override
    public boolean isShowing(SafeWindowInterface windowInterface) {
        return windowInterface != null && windowInterface == showingWindow;
    }


    @Override
    public void onDestroy() {
        isDestroy = true;
        showingWindow = null;
        cacheList.clear();
    }

    @Override
    public boolean showNext() {
        if (isDestroy) {
            return false;
        }
        if (cacheList.size() > 0) {
            showingWindow = cacheList.poll();
            if (showingWindow == null) {
                return showNext();
            } else {
                showingWindow.show();
                return true;
            }
        } else {
            showingWindow = null;
            return false;
        }
    }

    @Override
    public boolean dismiss() {
        if (showingWindow != null && showingWindow.isShowing()) {
            try {
                showingWindow.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        } else {
            return false;
        }

    }

}
