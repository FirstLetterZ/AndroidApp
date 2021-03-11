package com.zpf.binding.interfaces;

import com.zpf.api.ItemListAdapter;
import com.zpf.binding.model.BaseViewModel;

/**
 * @author Created by ZPF on 2021/3/10.
 */
public interface IBindingListAdapter<T, P extends IModelProcessor> extends ItemListAdapter<T> {

    IBindingListAdapter<T, P> bindItemByType(int itemType, int itemBrId, int itemViewId,
                                             Class<? extends BaseViewModel<P>> itemModelClass);

    IBindingListAdapter<T, P> bindProcessor(P processor);
}
