package com.zpf.binding.interfaces;

import android.view.View;

import com.zpf.api.IHolder;
import com.zpf.binding.model.BaseViewModel;

/**
 * @author Created by ZPF on 2021/3/10.
 */
public interface IBindingViewHolder extends IHolder<View> {
    <T extends IModelProcessor> void bindModel(Class<? extends BaseViewModel<T>> modelClass, T processor);

    void bindVariable(Object value, int position);
}
