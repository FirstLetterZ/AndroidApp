package com.zpf.binding.holder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LifecycleOwner;

import com.zpf.api.IHolder;
import com.zpf.binding.interfaces.IModelProcessor;
import com.zpf.binding.model.BaseViewModel;
import com.zpf.binding.model.ViewModelHelper;

/**
 * @author Created by ZPF on 2021/3/10.
 */
public class BindingViewHolder implements IHolder<View> {
    private final View itemView;
    private final int variableId;
    private boolean lastBindSuccess = false;
    private final ViewDataBinding itemBinder;
    protected BaseViewModel<?> itemViewModel;

    //should be override!
    public BindingViewHolder(@NonNull ViewGroup parent) {
        this(parent, 0, 0, null);
    }

    public BindingViewHolder(@NonNull ViewGroup parent, @LayoutRes int layoutId, int variableId,
                             Class<? extends BaseViewModel<?>> modelClass) {
        itemBinder = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), layoutId, parent, false);
        this.itemView = itemBinder.getRoot();
        this.variableId = variableId;
        LifecycleOwner lifecycleOwner = ViewModelHelper.getLifecycleOwner(itemView);
        itemBinder.setLifecycleOwner(lifecycleOwner);
        itemViewModel = createModel(modelClass);
    }

    protected BaseViewModel<?> createModel(Class<? extends BaseViewModel<?>> modelClass) {
        if (modelClass == null) {
            return null;
        }
        try {
            return ViewModelHelper.createModel(
                    modelClass,
                    getClass().getName() + "-" + this.hashCode(),
                    ViewModelHelper.createViewModelProvider(itemView, null),
                    null
            );
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void onBindData(@Nullable Object data, int position) {
        if (variableId != 0) {
            BaseViewModel<?> model = itemViewModel;
            if (model == null) {
                lastBindSuccess = setVariable(variableId, data);
            } else if (!model.update(data) || !lastBindSuccess) {
                lastBindSuccess = setVariable(variableId, model);
            }
        }
    }

    @Override
    public void onReceiveListener(@Nullable Object listener, int type) {
        if (itemViewModel != null && listener instanceof IModelProcessor) {
            try {
                itemViewModel.unsafeSetProcessor(((IModelProcessor) listener));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View getRoot() {
        return itemView;
    }

    @Override
    public View findById(int id) {
        return itemView.findViewById(id);
    }

    @Override
    public View findByTag(String tag) {
        return itemView.findViewWithTag(tag);
    }

    private boolean setVariable(int variableId, Object value) {
        if (itemBinder != null) {
            try {
                if (itemViewModel == null) {
                    return itemBinder.setVariable(variableId, value);
                } else {
                    return itemBinder.setVariable(variableId, itemViewModel);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}