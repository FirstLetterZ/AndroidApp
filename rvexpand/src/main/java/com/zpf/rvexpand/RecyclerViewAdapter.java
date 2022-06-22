package com.zpf.rvexpand;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.zpf.api.IHolder;
import com.zpf.api.ItemListAdapter;
import com.zpf.api.ItemTypeManager;
import com.zpf.api.ItemViewCreator;
import com.zpf.api.OnAttachListener;
import com.zpf.api.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemListAdapter<T> {
    private final ArrayList<T> dataList = new ArrayList<>();
    private final ItemClickHelper clickHelper = new ItemClickHelper();
    private final SparseArray<Object> itemListeners = new SparseArray<>();
    private boolean holderRecyclable = true;
    private ItemViewCreator itemViewCreator;
    private ItemTypeManager itemTypeManager;
    private LoadMoreController loadMoreController;
    private View emptyView = null;
    private boolean showEmpty = false;

    public RecyclerViewAdapter() {
        setHasStableIds(true);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (loadMoreController != null) {
            loadMoreController.attachedToRecyclerView(recyclerView);
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (loadMoreController != null) {
            loadMoreController.detachedFromRecyclerView(recyclerView);
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof OnAttachListener) {
            ((OnAttachListener) holder).onAttached();
        }
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof OnAttachListener) {
            ((OnAttachListener) holder).onDetached();
        }
        super.onViewDetachedFromWindow(holder);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (showEmpty) {
            return new EmptyHolder(emptyView, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        if (viewType == LoadMoreViewHolder.HOLDER_TYPE) {
            View itemView = loadMoreController.getViewHolder().getItemView();
            ViewGroup.LayoutParams itemLp = itemView.getLayoutParams();
            if (itemLp != null) {
                new EmptyHolder(itemView, new ViewGroup.LayoutParams(
                        itemLp.width, itemLp.height));
            }
            return new EmptyHolder(itemView, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        RecyclerView.ViewHolder holder = null;
        if (itemViewCreator != null) {
            IHolder<View> item = itemViewCreator.onCreateView(parent, viewType);
            if (item instanceof RecyclerView.ViewHolder) {
                holder = (RecyclerView.ViewHolder) item;
            } else if (item != null) {
                holder = new ItemHolder(item);
            }
        }
        if (holder == null) {
            holder = new EmptyHolder(parent.getContext());
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EmptyHolder) {
            ((EmptyHolder) holder).checkSizeChanged();
            return;
        }
        if (holder instanceof ItemHolder) {
            ItemHolder itemHolder = ((ItemHolder) holder);
            Object data = getDataAt(position);
            for (int i = 0; i < itemListeners.size(); i++) {
                itemHolder.onReceiveListener(itemListeners.valueAt(i), itemListeners.keyAt(i));
            }
            itemHolder.onBindData(data, position);
            if (itemViewCreator != null) {
                itemViewCreator.onBindView(itemHolder, getItemViewType(position), position, data);
            }
        }
        if (holder.isRecyclable() != holderRecyclable) {
            holder.setIsRecyclable(holderRecyclable);
        }
        clickHelper.bindItemClick(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        int size = dataList.size();
        showEmpty = size == 0 && emptyView != null;
        if (size > 0) {
            if (loadMoreController != null) {
                return size + 1;
            } else {
                return size;
            }
        } else {
            return showEmpty ? 1 : 0;
        }
    }

    public RecyclerViewAdapter<T> setItemClickListener(@Nullable OnItemClickListener itemClickListener) {
        clickHelper.itemClickListener = itemClickListener;
        return this;
    }

    @Override
    public ItemListAdapter<T> addItemListener(int type, @Nullable Object listener) {
        itemListeners.put(type, listener);
        return this;
    }

    public void setLoadMoreController(LoadMoreController loadMoreController) {
        this.loadMoreController = loadMoreController;
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    public RecyclerViewAdapter<T> addData(@Nullable T data) {
        if (data != null) {
            dataList.add(data);
        }
        return this;
    }

    public RecyclerViewAdapter<T> addDataList(@Nullable List<T> list) {
        if (list != null) {
            dataList.addAll(list);
        }
        return this;
    }

    public RecyclerViewAdapter<T> setDataList(@Nullable List<T> list) {
        dataList.clear();
        if (list != null) {
            dataList.addAll(list);
        }
        return this;
    }

    public RecyclerViewAdapter<T> setItemViewCreator(@Nullable ItemViewCreator creator) {
        itemViewCreator = creator;
        return this;
    }

    @Nullable
    public T getDataAt(int position) {
        if (position < 0 || position > dataList.size() - 1) {
            return null;
        }
        return dataList.get(position);
    }

    @NonNull
    public List<T> getDataList() {
        return dataList;
    }

    @Override
    public RecyclerViewAdapter<T> setItemTypeManager(ItemTypeManager manager) {
        itemTypeManager = manager;
        return this;
    }

    @Override
    public int getItemViewType(int position) {
        if (loadMoreController != null && position == getItemCount() - 1) {
            return LoadMoreViewHolder.HOLDER_TYPE;
        }
        if (itemTypeManager != null) {
            return itemTypeManager.getItemType(position);
        }
        Object itemData = dataList.get(position);
        if (itemData instanceof ItemTypeManager) {
            return ((ItemTypeManager) itemData).getItemType(position);
        }
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        if (itemTypeManager != null) {
            return itemTypeManager.getItemId(position);
        }
        Object itemData = dataList.get(position);
        if (itemData instanceof ItemTypeManager) {
            return ((ItemTypeManager) itemData).getItemId(position);
        }
        if (hasStableIds()) {
            return position;
        }
        return super.getItemId(position);
    }

    @Override
    public int getSize() {
        return getItemCount();
    }

    public RecyclerViewAdapter<T> setHolderRecyclable(boolean recyclable) {
        holderRecyclable = recyclable;
        return this;
    }

    protected boolean isHolderRecyclable() {
        return holderRecyclable;
    }
}