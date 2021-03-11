package com.zpf.binding.holder;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.zpf.binding.interfaces.IBindingViewHolder;
import com.zpf.binding.interfaces.IModelProcessor;
import com.zpf.binding.model.BaseViewModel;

/**
 * @author Created by ZPF on 2021/3/10.
 */
public class BindingRecyclerHolder extends RecyclerView.ViewHolder implements IBindingViewHolder {
    protected BindingViewHolder realHolder;

    public BindingRecyclerHolder(View itemView, int variableId) {
        super(itemView);
        realHolder = new BindingViewHolder(itemView, variableId);
    }

    @Override
    public <T extends IModelProcessor> void bindModel(Class<? extends BaseViewModel<T>> modelClass, T processor) {
        realHolder.bindModel(modelClass, processor);
    }

    @Override
    public void bindVariable(Object value, int position) {
        realHolder.bindVariable(value, position);
    }

    @Override
    public View getRoot() {
        return itemView;
    }

    @Override
    public View findById(int id) {
        return realHolder.findById(id);
    }

    @Override
    public View findByTag(String tag) {
        return realHolder.findByTag(tag);
    }
}
