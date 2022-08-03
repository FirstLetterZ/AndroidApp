package com.zpf.binding.adapter;


import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Space;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.zpf.api.IHolder;
import com.zpf.api.Identification;
import com.zpf.api.ItemListAdapter;
import com.zpf.api.ItemTypeManager;
import com.zpf.api.ItemViewCreator;
import com.zpf.api.OnItemClickListener;
import com.zpf.rvexpand.ItemClickHelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Created by ZPF on 2021/3/10.
 */
public class BasePagerAdapter<T> extends PagerAdapter implements ItemListAdapter<T> {
    private final ArrayList<T> dataList = new ArrayList<>();
    private final ItemClickHelper clickHelper = new ItemClickHelper();
    private final SparseArray<Object> itemListeners = new SparseArray<>();
    private ItemViewCreator itemViewCreator = null;
    private ItemTypeManager itemTypeManager = null;
    protected final SparseArray<IHolder<View>> holderArray = new SparseArray<>();
    protected final SparseArray<LinkedList<IHolder<View>>> holderCache = new SparseArray<>();
    protected boolean rebuildAllView = false;

    @Override
    public int getCount() {
        return getSize();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        int itemType = getItemViewType(position);
        LinkedList<IHolder<View>> caches = holderCache.get(itemType);
        IHolder<View> itemHolder = null;
        if (caches != null) {
            itemHolder = caches.pollFirst();
        }
        if (itemHolder == null && itemViewCreator != null) {
            itemHolder = itemViewCreator.onCreateView(container, itemType);
        }
        holderArray.put(position, itemHolder);
        View itemView = onBindItemData(itemHolder, position, itemType);
        if (itemView == null) {
            itemView = new Space(container.getContext());
        }
        ViewParent vp = itemView.getParent();
        if (vp == null) {
            container.addView(itemView);
        } else if (vp != container) {
            ((ViewGroup) vp).removeView(itemView);
            container.addView(itemView);
        }
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        IHolder<View> itemHolder = holderArray.get(position);
        holderArray.remove(position);
        if (itemHolder != null) {
            int itemType = getItemViewType(position);
            LinkedList<IHolder<View>> caches = holderCache.get(itemType);
            if (caches == null) {
                caches = new LinkedList<>();
                holderCache.put(itemType, caches);
            }
            caches.add(itemHolder);
        }
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        if (rebuildAllView) {
            return POSITION_NONE;
        }
        if (object instanceof Identification) {
            return ((Identification) object).getId().hashCode();
        }
        return super.getItemPosition(object);
    }

    public void notifyDataSetChanged(boolean rebuildAllView) {
        this.rebuildAllView = rebuildAllView;
        super.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        this.rebuildAllView = false;
        super.notifyDataSetChanged();
    }

    public int getItemViewType(int position) {
        if (itemTypeManager != null) {
            return itemTypeManager.getItemType(position);
        }
        Object itemData = dataList.get(position);
        if (itemData instanceof ItemTypeManager) {
            return ((ItemTypeManager) itemData).getItemType(position);
        }
        return 0;
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

    public void notifyItemChanged() {
        for (int i = 0; i < holderArray.size(); i++) {
            int position = holderArray.keyAt(i);
            IHolder<View> itemHolder = holderArray.valueAt(i);
            onBindItemData(itemHolder, position, getItemViewType(position));
        }
    }

    public void notifyItemChanged(int position) {
        IHolder<View> itemHolder = holderArray.get(position);
        onBindItemData(itemHolder, position, getItemViewType(position));
    }

    private View onBindItemData(IHolder<View> itemHolder, int position, int itemType) {
        View itemView = null;
        if (itemHolder != null) {
            itemView = (View) itemHolder.getRoot();
            for (int i = 0; i < itemListeners.size(); i++) {
                itemHolder.onReceiveListener(itemListeners.valueAt(i), itemListeners.keyAt(i));
            }
            Object data = getDataAt(position);
            itemHolder.onBindData(data, position);
            if (itemViewCreator != null) {
                itemViewCreator.onBindView(itemHolder, position, itemType, data);
            }
        }
        if (itemView != null) {
            clickHelper.bindItemClick(itemView, position);
        }
        return itemView;
    }
}