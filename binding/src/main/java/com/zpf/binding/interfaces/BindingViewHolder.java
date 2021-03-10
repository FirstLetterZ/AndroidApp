package com.zpf.binding.interfaces;

import android.view.View;

import com.zpf.api.IHolder;
import com.zpf.binding.model.BaseViewModel;
import com.zpf.frame.IModelProcessor;

/**
 * @author Created by ZPF on 2021/3/10.
 */
public interface BindingViewHolder extends IHolder<View> {
    <T extends IModelProcessor> void bindModel(Class<? extends BaseViewModel<T>> modelClass, T processor);

    void bindVariable(Object value, int position);
}
