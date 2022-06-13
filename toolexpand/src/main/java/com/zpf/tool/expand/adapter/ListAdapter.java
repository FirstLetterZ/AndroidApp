package com.zpf.tool.expand.adapter;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.api.IHolder;
import com.zpf.api.ItemListAdapter;
import com.zpf.api.ItemTypeManager;
import com.zpf.api.ItemViewCreator;
import com.zpf.api.OnItemClickListener;
import com.zpf.tool.expand.util.ItemClickHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by ZPF on 2021/3/2.
 */
public class ListAdapter<T> extends BaseAdapter implements ItemListAdapter<T> {
    private final ArrayList<T> dataList = new ArrayList<>();
    private final ItemClickHelper clickHelper = new ItemClickHelper();
    private final SparseArray<Object> itemListeners = new SparseArray<>();
    private ItemViewCreator itemViewCreator;
    private ItemTypeManager itemTypeManager;

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
        Object itemHolder = null;
        if (convertView != null) {
            itemHolder = convertView.getTag();
        }
        if (itemHolder == null && itemViewCreator != null) {
            itemHolder = itemViewCreator.onCreateView(parent, itemType);
        }
        View itemView = null;
        if (itemHolder instanceof IHolder) {
            IHolder<View> realHolder = (IHolder<View>) itemHolder;
            Object data = getDataAt(position);
            itemView = (View) realHolder.getRoot();
            for (int i = 0; i < itemListeners.size(); i++) {
                realHolder.onReceiveListener(itemListeners.valueAt(i), itemListeners.keyAt(i));
            }
            realHolder.onBindData(data, position);
            if (itemViewCreator != null) {
                itemViewCreator.onBindView(realHolder, getItemViewType(position), position, data);
            }
        }
        if (itemView == null) {
            itemView = convertView;
        }
        if (itemView != null) {
            itemView.setTag(itemHolder);
        }
        clickHelper.bindItemClick(itemView, position);
        return itemView;
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
    public ItemListAdapter<T> addItemListener(int type, @Nullable Object listener) {
        itemListeners.put(type, listener);
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
