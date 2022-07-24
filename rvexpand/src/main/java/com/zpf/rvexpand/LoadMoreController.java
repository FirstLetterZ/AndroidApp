package com.zpf.rvexpand;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LoadMoreController {
    private long lastScrollBottomTime;
    private boolean enable = false;
    private boolean loading = false;
    private int currentPage = -1;
    private final LoadMoreViewHolder viewHolder;
    private BottomLoadingListener listener;
    private final RecyclerView.OnScrollListener scrollEndListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            if (!enable) {
                changeState(loading, false);
                return;
            }
            if (listener == null || loading || System.currentTimeMillis() - lastScrollBottomTime < 500) {
                return;
            }
            for (int i = recyclerView.getChildCount() - 1; i >= 0; i--) {
                if (recyclerView.getChildAt(i) == viewHolder.getItemView()) {
                    lastScrollBottomTime = System.currentTimeMillis();
                    changeState(true, true);
                    break;
                }
            }
        }
    };

    public LoadMoreController(@NonNull LoadMoreViewHolder holder) {
        this.viewHolder = holder;
        changeState(false, false);
    }

    void attachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(scrollEndListener);
    }

    void detachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.removeOnScrollListener(scrollEndListener);
    }

    public void changeState(boolean loadMore, boolean enableLoad) {
        if (loading != loadMore || enable != enableLoad) {
            loading = enableLoad && loadMore;
            enable = enableLoad;
            if (loading) {
                viewHolder.onLoading();
                if (listener != null) {
                    listener.onLoading();
                }
            } else {
                viewHolder.onComplete(enable, currentPage);
            }
        }
    }

    public void stopLoad() {
        changeState(false, enable);
    }

    public void finishLoad(boolean enableLoadMore, int pageNumber) {
        currentPage = pageNumber;
        changeState(false, enableLoadMore);
    }

    public void setBottomHolderListener(BottomLoadingListener listener) {
        this.listener = listener;
    }

    @NonNull
    public LoadMoreViewHolder getViewHolder() {
        return viewHolder;
    }

}
