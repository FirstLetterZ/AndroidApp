package com.zpf.binding.model;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.zpf.api.IGroup;
import com.zpf.api.OnDestroyListener;
import com.zpf.binding.interfaces.IModelProcessor;

import java.util.HashSet;

/**
 * @author Created by ZPF on 2021/3/10.
 */
public class BaseViewModel<T extends IModelProcessor> extends ViewModel implements IGroup<OnDestroyListener> {
    protected T mProcessor;
    private final HashSet<OnDestroyListener> mListeners = new HashSet<>();

    private volatile boolean destroy = false;

    private LiveDataHelper liveDataHelper = new LiveDataHelper(this);

    public void setProcessor(T p) {
        mProcessor = p;
    }

    @Nullable
    public T getProcessor() {
        return mProcessor;
    }

    public boolean update(Object data) {
        return false;
    }

    public <A> void observe(LiveData<A> liveData, Observer<A> observer) {
        if (destroy) {
            return;
        }
        liveDataHelper.bindObserver(liveData, observer);
    }

    @Override
    public void add(OnDestroyListener listener) {
        if (destroy) {
            return;
        }
        mListeners.add(listener);
    }

    @Override
    public void remove(OnDestroyListener listener) {
        if (destroy) {
            return;
        }
        synchronized (mListeners) {
            mListeners.remove(listener);
        }
    }


    @Override
    public int size() {
        return mListeners.size();
    }

    public void reset() {
        destroy = false;
    }


    @Override
    public void onCleared() {
        destroy = true;
        super.onCleared();
        for (OnDestroyListener mListener : mListeners) {
            mListener.onDestroy();
        }
        mListeners.clear();
        liveDataHelper.clearAll();
    }
}
