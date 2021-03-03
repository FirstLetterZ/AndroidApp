package com.zpf.support.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.api.ItemListAdapter;
import com.zpf.api.ItemTypeManager;
import com.zpf.api.ItemViewCreator;
import com.zpf.api.OnItemClickListener;
import com.zpf.api.OnItemViewClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by ZPF on 2021/3/2.
 */
public class ListAdapter<T> extends BaseAdapter implements ItemListAdapter<T> {
    private final ArrayList<T> dataList = new ArrayList<>();
//    private final ItemClickHelper clickHelper = new ItemClickHelper();
    private boolean holderRecyclable = true;
    private ItemViewCreator itemViewCreator;
    private ItemTypeManager itemTypeManager;

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public ItemListAdapter<T> setItemClickListener(@Nullable OnItemClickListener itemClickListener) {
        return null;
    }

    @Override
    public ItemListAdapter<T> setItemViewClickListener(@Nullable OnItemViewClickListener itemViewClickListener) {
        return null;
    }

    @Override
    public ItemListAdapter<T> addData(@Nullable T data) {
        return null;
    }

    @Nullable
    @Override
    public T getDataAt(int position) {
        return null;
    }

    @Override
    public ItemListAdapter<T> addDataList(@Nullable List<T> list) {
        return null;
    }

    @Override
    public ItemListAdapter<T> setDataList(@Nullable List<T> list) {
        return null;
    }

    @NonNull
    @Override
    public List<T> getDataList() {
        return null;
    }

    @Override
    public ItemListAdapter<T> setItemTypeManager(ItemTypeManager manager) {
        return null;
    }

    @Override
    public ItemListAdapter<T> setItemViewCreator(@Nullable ItemViewCreator creator) {
        return null;
    }
}
