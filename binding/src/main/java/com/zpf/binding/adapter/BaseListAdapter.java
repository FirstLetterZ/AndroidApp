package com.zpf.binding.adapter;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.zpf.api.IHolder;
import com.zpf.api.ItemListAdapter;
import com.zpf.api.ItemTypeManager;
import com.zpf.api.ItemViewCreator;
import com.zpf.api.OnItemClickListener;
import com.zpf.api.OnItemViewClickListener;
import com.zpf.binding.holder.BindingViewHolder;
import com.zpf.binding.interfaces.IBindingListAdapter;
import com.zpf.binding.interfaces.IBindingViewHolder;
import com.zpf.binding.interfaces.IModelProcessor;
import com.zpf.binding.model.BaseViewModel;
import com.zpf.binding.model.ItemBindingInfo;
import com.zpf.rvexpand.ItemClickHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by ZPF on 2021/3/10.
 */
public class BaseListAdapter<T, P extends IModelProcessor> extends BaseAdapter implements IBindingListAdapter<T, P> {
    private final ArrayList<T> dataList = new ArrayList<T>();
    private final ItemClickHelper clickHelper = new ItemClickHelper();
    private ItemViewCreator itemViewCreator = null;
    private ItemTypeManager itemTypeManager = null;
    private P itemProcessor = null;
    private final SparseArray<ItemBindingInfo<P>> typeInfo = new SparseArray<>();

    @Override
    public int getCount() {
        return getSize();
    }

    @Override
    public Object getItem(int position) {
        return getDataAt(position);
    }

    @Override
    public long getItemId(int position) {
        if (itemTypeManager != null) {
            return itemTypeManager.getItemId(position);
        } else {
            return position;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int itemType = getItemViewType(position);
        ItemBindingInfo<P> bindInfo = typeInfo.get(itemType);
        Object itemHolder;
        if (convertView == null) {
            itemHolder = null;
        } else {
            itemHolder = convertView.getTag();
        }
        if (itemHolder == null) {
            int bindLayoutId = bindInfo != null ? bindInfo.itemBrId : 0;
            if (bindLayoutId != 0) {
                ViewDataBinding itemBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.getContext()),
                        bindLayoutId,
                        parent,
                        false
                );
                itemHolder = new BindingViewHolder(itemBinding.getRoot(), bindLayoutId);
            } else if (itemViewCreator != null) {
                itemHolder = itemViewCreator.onCreateView(parent, position, itemType);
            }
        }
        View itemView;
        if (itemHolder instanceof IBindingViewHolder) {
            Class<? extends BaseViewModel<P>> modelClass = bindInfo != null ? bindInfo.itemModelClass : null;
            ((IBindingViewHolder) itemHolder).bindModel(modelClass, itemProcessor);
            ((IBindingViewHolder) itemHolder).bindVariable(getDataAt(position), position);
            itemView = (View) ((IBindingViewHolder) itemHolder).getRoot();
        } else if (itemHolder instanceof IHolder) {
            IHolder<View> realHolder = (IHolder<View>) itemHolder;
            itemView = (View) realHolder.getRoot();
            if (itemViewCreator != null) {
                itemViewCreator.onBindView(realHolder, position, getDataAt(position));
            }
        } else {
            itemView = convertView;
        }
        if (itemView != null) {
            itemView.setTag(itemHolder);
        }
        clickHelper.bindItemClick(itemView, position);
        return itemView;
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

    @Override
    public int getSize() {
        return dataList.size();
    }

    @Override
    public ItemListAdapter<T> setItemClickListener(@Nullable OnItemClickListener itemClickListener) {
        clickHelper.itemClickListener = itemClickListener;
        return this;
    }

    @Override
    public ItemListAdapter<T> setItemViewClickListener(@Nullable OnItemViewClickListener itemViewClickListener) {
        clickHelper.itemViewClickListener = itemViewClickListener;
        return this;
    }

    @Override
    public ItemListAdapter<T> addData(@Nullable T data) {
        if (data != null) {
            dataList.add(data);
        }
        return this;
    }

    @Nullable
    @Override
    public T getDataAt(int position) {
        if (position < 0 || position >= dataList.size()) {
            return null;
        } else {
            return dataList.get(position);
        }
    }

    @Override
    public ItemListAdapter<T> addDataList(@Nullable List<T> list) {
        if (list != null) {
            dataList.addAll(list);
        }
        return this;
    }

    @Override
    public ItemListAdapter<T> setDataList(@Nullable List<T> list) {
        dataList.clear();
        if (list != null) {
            dataList.addAll(list);
        }
        return this;
    }

    @NonNull
    @Override
    public List<T> getDataList() {
        return dataList;
    }

    @Override
    public ItemListAdapter<T> setItemTypeManager(ItemTypeManager manager) {
        itemTypeManager = manager;
        return this;
    }

    @Override
    public ItemListAdapter<T> setItemViewCreator(@Nullable ItemViewCreator creator) {
        itemViewCreator = creator;
        return this;
    }
}
