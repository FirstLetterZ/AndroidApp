package com.zpf.binding.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.zpf.api.IGroup;
import com.zpf.api.OnDestroyListener;
import com.zpf.binding.interfaces.IModelProcessor;

import java.lang.reflect.Type;
import java.util.HashSet;

/**
 * @author Created by ZPF on 2021/3/10.
 */
public class BaseViewModel<T extends IModelProcessor> extends ViewModel implements IGroup {
    protected T mProcessor;
    private final HashSet<OnDestroyListener> mListeners = new HashSet<>();
    private volatile boolean destroy = false;
    private final LiveDataHelper liveDataHelper = new LiveDataHelper(this);

    public void unsafeSetProcessor(IModelProcessor p) throws ClassCastException {
        T unsafe = ((T) p);
        setProcessor(unsafe);
    }

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

    public void reset() {
        destroy = false;
    }

    @Override
    public void onCleared() {
        destroy = true;
        mProcessor = null;
        super.onCleared();
        for (OnDestroyListener mListener : mListeners) {
            mListener.onDestroy();
        }
        mListeners.clear();
        liveDataHelper.clearAll();
    }

    @Override
    public boolean remove(@NonNull Object obj, @Nullable Type asType) {
        if (destroy) {
            return false;
        }
        if (asType == OnDestroyListener.class && obj instanceof OnDestroyListener) {
            synchronized (mListeners) {
                mListeners.remove((OnDestroyListener) obj);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean add(@NonNull Object obj, @Nullable Type asType) {
        if (destroy) {
            return false;
        }
        if (asType == OnDestroyListener.class && obj instanceof OnDestroyListener) {
            synchronized (mListeners) {
                mListeners.add((OnDestroyListener) obj);
            }
            return true;
        }
        return false;
    }

    @Override
    public int size(@Nullable Type asType) {
        return mListeners.size();
    }
}
