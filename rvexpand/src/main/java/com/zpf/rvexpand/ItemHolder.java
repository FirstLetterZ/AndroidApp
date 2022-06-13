package com.zpf.rvexpand;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.zpf.api.IHolder;

/**
 * @author Created by ZPF on 2021/2/25.
 */
public class ItemHolder extends RecyclerView.ViewHolder implements IHolder<View> {
    private final IHolder<View> realHolder;

    public ItemHolder(@NonNull IHolder<View> holder) {
        super((View) holder.getRoot());
        realHolder = holder;
        initView();
    }

    public ItemHolder(@NonNull View itemView) {
        super(itemView);
        realHolder = null;
        initView();
    }

    public ItemHolder(@NonNull ViewGroup parent, @LayoutRes int layoutId) {
        super(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
        realHolder = null;
        initView();
    }

    protected void initView() {

    }

    @Override
    public void onBindData(@Nullable Object data, int position) {
        if (realHolder != null) {
            realHolder.onBindData(data, position);
        }
    }

    @Override
    public void onReceiveListener(@Nullable Object listener, int type) {
        if (realHolder != null) {
            realHolder.onReceiveListener(listener, type);
        }
    }

    @Override
    public View getRoot() {
        return itemView;
    }

    @Override
    public View findById(int id) {
        if (realHolder != null) {
            return realHolder.findById(id);
        } else {
            return itemView.findViewById(id);
        }
    }

    @Override
    public View findByTag(String tag) {
        if (realHolder != null) {
            return realHolder.findByTag(tag);
        } else {
            return itemView.findViewWithTag(tag);
        }
    }
}