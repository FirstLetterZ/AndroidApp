package com.zpf.rvexpand;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LoadMoreController {
    private long lastScrollBottomTime;
    private boolean enable = false;
    private boolean loading = false;
    private int totalLoadCount = 0;
    private final LoadMoreViewHolder viewHolder;
    private BottomLoadingListener listener;
    private RecyclerView attachView;
    private final Runnable delayCheckRunnable = new Runnable() {
        @Override
        public void run() {
            checkLoadMore(attachView);
        }
    };
    private final RecyclerView.OnScrollListener scrollEndListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            checkLoadMore(recyclerView);
        }
    };

    public LoadMoreController(@NonNull LoadMoreViewHolder holder) {
        this.viewHolder = holder;
        holder.onInit();
    }

    void attachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        attachView = recyclerView;
        recyclerView.addOnScrollListener(scrollEndListener);
    }

    void detachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.removeOnScrollListener(scrollEndListener);
        recyclerView.removeCallbacks(delayCheckRunnable);
        attachView = null;
    }

    private void changeState(boolean loadMore, boolean enableLoad) {
        if (loading != loadMore || enable != enableLoad) {
            loading = enableLoad && loadMore;
            enable = enableLoad;
            if (loading) {
                viewHolder.onLoading();
                if (listener != null) {
                    listener.onLoading();
                }
            } else {
                viewHolder.onComplete(enable);
            }
        }
    }

    public void stopLoad() {
        loading = false;
        if (totalLoadCount == 0) {
            viewHolder.onInit();
        } else {
            viewHolder.onComplete(enable);
        }
    }

    public void finishLoad(int totalLoad, boolean hasMore) {
        totalLoadCount = totalLoad;
        loading = false;
        enable = hasMore;
        if (totalLoad == 0) {
            viewHolder.onInit();
        } else {
            viewHolder.onComplete(hasMore);
        }
    }

    public void setBottomHolderListener(BottomLoadingListener listener) {
        this.listener = listener;
    }

    @NonNull
    public LoadMoreViewHolder getViewHolder() {
        return viewHolder;
    }

    private void checkLoadMore(RecyclerView recyclerView) {
        if (recyclerView == null) {
            return;
        }
        recyclerView.removeCallbacks(delayCheckRunnable);
        if (!enable) {
            changeState(loading, false);
            return;
        }
        if (listener == null || loading) {
            return;
        }
        if (System.currentTimeMillis() - lastScrollBottomTime < 200) {
            recyclerView.postDelayed(delayCheckRunnable, 200);
            return;
        }
        int lastPosition = recyclerView.getChildCount() - 1;
        if (lastPosition >= 0 && recyclerView.getChildAt(lastPosition) == viewHolder.getItemView()) {
            lastScrollBottomTime = System.currentTimeMillis();
            changeState(true, true);
        }
    }

}
