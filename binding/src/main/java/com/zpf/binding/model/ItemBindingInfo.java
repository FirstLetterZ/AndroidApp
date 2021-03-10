package com.zpf.binding.model;

import com.zpf.frame.IModelProcessor;

/**
 * @author Created by ZPF on 2021/3/10.
 */
public class ItemBindingInfo<T extends IModelProcessor> {
    public final int itemBrId;
    public final int itemViewId;
    public final Class<? extends BaseViewModel<T>> itemModelClass;

    public ItemBindingInfo(int itemBrId, int itemViewId, Class<? extends BaseViewModel<T>> itemModelClass) {
        this.itemBrId = itemBrId;
        this.itemViewId = itemViewId;
        this.itemModelClass = itemModelClass;
    }
}
