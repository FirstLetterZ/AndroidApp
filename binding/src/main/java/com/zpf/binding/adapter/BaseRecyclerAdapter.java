package com.zpf.binding.adapter;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.zpf.binding.holder.BaseRecyclerHolder;
import com.zpf.binding.interfaces.BindingViewHolder;
import com.zpf.binding.interfaces.IBindingListAdapter;
import com.zpf.binding.model.BaseViewModel;
import com.zpf.binding.model.ItemBindingInfo;
import com.zpf.frame.IModelProcessor;
import com.zpf.rvexpand.RecyclerViewAdapter;

/**
 * @author Created by ZPF on 2021/3/10.
 */
public class BaseRecyclerAdapter<T, P extends IModelProcessor> extends RecyclerViewAdapter<T> implements IBindingListAdapter<T, P> {
    private P itemProcessor = null;
    private final SparseArray<ItemBindingInfo<P>> typeInfo = new SparseArray<>();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        ItemBindingInfo<P> bindInfo = typeInfo.get(viewType);
        if (bindInfo != null && bindInfo.itemViewId != 0) {
            ViewDataBinding itemBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    bindInfo.itemViewId,
                    parent,
                    false
            );
            holder = new BaseRecyclerHolder(itemBinding.getRoot(), bindInfo.itemBrId);
        }
        if (holder == null) {
            holder = super.onCreateViewHolder(parent, viewType);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BindingViewHolder) {
            ItemBindingInfo<P> bindInfo = typeInfo.get(getItemViewType(position));
            Class<? extends BaseViewModel<P>> modelClass = bindInfo != null ? bindInfo.itemModelClass : null;
            ((BindingViewHolder) holder).bindModel(modelClass, itemProcessor);
            ((BindingViewHolder) holder).bindVariable(getDataAt(position), position);
        }
        super.onBindViewHolder(holder, position);
    }

    @Override
    public IBindingListAdapter<T, P> bindItemByType(int itemType, int itemBrId, int itemViewId, Class<? extends BaseViewModel<P>> itemModelClass) {
        typeInfo.put(itemType, new ItemBindingInfo<>(itemBrId, itemViewId, itemModelClass));
        return this;
    }

    @Override
    public IBindingListAdapter<T, P> bindProcessor(P processor) {
        itemProcessor = processor;
        return this;
    }
}
