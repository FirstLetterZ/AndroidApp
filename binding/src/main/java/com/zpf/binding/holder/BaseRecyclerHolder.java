package com.zpf.binding.holder;

import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.zpf.binding.interfaces.BindingViewHolder;
import com.zpf.binding.model.BaseViewModel;
import com.zpf.binding.model.ViewModelHelper;
import com.zpf.frame.IModelProcessor;

/**
 * @author Created by ZPF on 2021/3/10.
 */
public class BaseRecyclerHolder extends RecyclerView.ViewHolder implements BindingViewHolder {
    private final int variableId;
    private boolean lastBindSuccess = false;
    private final ViewDataBinding itemBinder;
    protected BaseViewModel<?> itemViewModel;
    protected IModelProcessor itemProcessor;

    public BaseRecyclerHolder(View itemView, int variableId) {
        super(itemView);
        this.variableId = variableId;
        itemBinder = DataBindingUtil.getBinding(itemView);
        LifecycleOwner lifecycleOwner;
        if (itemBinder != null && (lifecycleOwner = ViewModelHelper.getLifecycleOwner(itemView)) != null) {
            itemBinder.setLifecycleOwner(lifecycleOwner);
        }
    }

    @Override
    public <T extends IModelProcessor> void bindModel(Class<? extends BaseViewModel<T>> modelClass, T processor) {
        itemProcessor = processor;
        if (modelClass != null && (itemViewModel == null || modelClass != itemViewModel.getClass())) {
            BaseViewModel<T> model = null;
            try {
                model = ViewModelHelper.createModel(
                        modelClass,
                        getClass().getName() + "-" + this.hashCode(),
                        ViewModelHelper.createViewModelProvider(itemView, null),
                        null
                );
                model.setProcessor(processor);
            } catch (Exception e) {
                //
            }
            if (itemViewModel != model) {
                lastBindSuccess = false;
            }
            itemViewModel = model;
        }
    }

    @Override
    public void bindVariable(Object value, int position) {
        if (variableId != 0) {
            if (lastBindSuccess) {
                if (itemViewModel == null || !itemViewModel.update(value)) {
                    lastBindSuccess = setVariable(variableId, value);
                }
            } else {
                lastBindSuccess = setVariable(variableId, value);
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
