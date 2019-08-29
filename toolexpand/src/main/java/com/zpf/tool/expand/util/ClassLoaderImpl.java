package com.zpf.tool.expand.util;

import android.support.annotation.NonNull;

import com.zpf.api.IClassLoader;
import com.zpf.api.IGroup;
import com.zpf.api.OnDestroyListener;

import java.util.HashSet;

public class ClassLoaderImpl implements IClassLoader, IGroup<IClassLoader>, OnDestroyListener {
    private ClassLoaderImpl() {
    }

    private static class Instance {
        private static final ClassLoaderImpl mInstance = new ClassLoaderImpl();
    }

    public static ClassLoaderImpl get() {
        return Instance.mInstance;
    }

    private HashSet<IClassLoader> mLoaderSet = new HashSet<>();

    @Override
    public void remove(@NonNull IClassLoader classLoader) {
        mLoaderSet.remove(classLoader);
    }

    @Override
    public void add(@NonNull IClassLoader classLoader) {
        mLoaderSet.add(classLoader);
    }

    @Override
    public int size() {
        return mLoaderSet.size();
    }

    @Override
    public void onDestroy() {
        mLoaderSet.clear();
    }


    @Override
    public Object newInstance(String name, Object... args) {
        Object result = null;
        for (IClassLoader loader : mLoaderSet) {
            if (loader != null) {
                result = loader.newInstance(name, args);
            }
            if (result != null) {
                break;
            }
        }
        return result;
    }

    @Override
    public Class<?> getClass(String name) {
        Class<?> result = null;
        for (IClassLoader loader : mLoaderSet) {
            if (loader != null) {
                result = loader.getClass(name);
            }
            if (result != null) {
                break;
            }
        }
        return result;
    }

}
