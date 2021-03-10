package com.zpf.binding.adapter;


import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Space;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.viewpager.widget.PagerAdapter;

import com.zpf.api.IHolder;
import com.zpf.api.ItemListAdapter;
import com.zpf.api.ItemTypeManager;
import com.zpf.api.ItemViewCreator;
import com.zpf.api.OnItemClickListener;
import com.zpf.api.OnItemViewClickListener;
import com.zpf.binding.holder.BaseViewHolder;
import com.zpf.binding.interfaces.BindingViewHolder;
import com.zpf.binding.interfaces.IBindingListAdapter;
import com.zpf.binding.model.BaseViewModel;
import com.zpf.binding.model.ItemBindingInfo;
import com.zpf.frame.IModelProcessor;
import com.zpf.rvexpand.ItemClickHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by ZPF on 2021/3/10.
 */
public class BasePagerAdapter<T, P extends IModelProcessor> extends PagerAdapter implements IBindingListAdapter<T, P> {
    private final ArrayList<T> dataList = new ArrayList<T>();
    private final ItemClickHelper clickHelper = new ItemClickHelper();
    private ItemViewCreator itemViewCreator = null;
    private ItemTypeManager itemTypeManager = null;
    private P itemProcessor = null;
    private final SparseArray<ItemBindingInfo<P>> typeInfo = new SparseArray<>();
    protected final SparseArray<IHolder<?>> holderCache =new SparseArray<>();
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
        ItemBindingInfo<P> bindInfo = typeInfo.get(itemType);
        Object itemHolder = null;
        int bindLayoutId = bindInfo != null ? bindInfo.itemBrId : 0;
        if (bindLayoutId != 0) {
            ViewDataBinding itemBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(container.getContext()),
                    bindLayoutId,
                    container,
                    false
            );
            itemHolder = new BaseViewHolder(itemBinding.getRoot(), bindLayoutId);
        } else if (itemViewCreator != null) {
            itemHolder = itemViewCreator.onCreateView(container, position, itemType);
        }
        View itemView = null;
        if (itemHolder instanceof BindingViewHolder) {
            Class<? extends BaseViewModel<P>> modelClass = bindInfo != null ? bindInfo.itemModelClass : null;
            ((BindingViewHolder) itemHolder).bindModel(modelClass, itemProcessor);
            ((BindingViewHolder) itemHolder).bindVariable(getDataAt(position), position);
            itemView = (View) ((BindingViewHolder) itemHolder).getRoot();
        } else if (itemHolder instanceof IHolder) {
            IHolder<View> realHolder = (IHolder<View>) itemHolder;
            itemView = (View) realHolder.getRoot();
            if (itemViewCreator != null) {
                itemViewCreator.onBindView(realHolder, position, getDataAt(position));
            }
        }
        if (itemView == null) {
            itemView = new Space(container.getContext());
        } else {
            itemView.setTag(position);
            clickHelper.bindItemClick(itemView, position);
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
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
//        return if (itemTypeManager != null) {
//            itemTypeManager !!.getItemId(position)
//        } else position.toLong()
        return super.getItemPosition(object);
    }

    public void notifyDataSetChanged(boolean rebuildAllView) {
        this.rebuildAllView=rebuildAllView;
        super.notifyDataSetChanged();
    }

    public int getItemViewType(int position) {
        if (itemTypeManager != null) {
            return itemTypeManager.getItemType(position);
        } else {
            return 0;
        }
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
