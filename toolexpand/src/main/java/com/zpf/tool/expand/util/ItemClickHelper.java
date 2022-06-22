package com.zpf.tool.expand.util;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.zpf.api.OnItemClickListener;

import java.lang.ref.SoftReference;

public class ItemClickHelper {
    public OnItemClickListener itemClickListener;
    private final SparseArray<SoftReference<View.OnClickListener>> clickCache = new SparseArray<>();

    public void bindItemClick(View view, final int position) {
        View targetView = view;
        while (targetView instanceof ViewGroup) {
            if (((ViewGroup) targetView).getChildCount() == 1) {
                targetView = ((ViewGroup) targetView).getChildAt(0);
            } else {
                break;
            }
        }
        if (targetView == null) {
            return;
        }
        if (itemClickListener == null) {
            targetView.setOnClickListener(null);
        } else {
            View.OnClickListener clickListener = null;
            SoftReference<View.OnClickListener> weakCache = clickCache.get(position);
            if (weakCache != null) {
                clickListener = weakCache.get();
            }
            if (clickListener == null) {
                clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (itemClickListener != null) {
                            itemClickListener.onItemClick(position, v);
                        }
                    }
                };
                weakCache = new SoftReference<>(clickListener);
                clickCache.put(position, weakCache);
            }
            targetView.setOnClickListener(clickListener);
        }
    }
}
