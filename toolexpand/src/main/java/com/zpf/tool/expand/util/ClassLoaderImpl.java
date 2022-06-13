package com.zpf.tool.expand.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.api.IClassLoader;
import com.zpf.api.IGroup;

import java.lang.reflect.Type;
import java.util.HashSet;

public class ClassLoaderImpl implements IClassLoader, IGroup {
    private ClassLoaderImpl() {
    }

    private static class Instance {
        private static final ClassLoaderImpl mInstance = new ClassLoaderImpl();
    }

    public static ClassLoaderImpl get() {
        return Instance.mInstance;
    }

    private final HashSet<IClassLoader> mLoaderSet = new HashSet<>();

    @Override
    public boolean remove(@NonNull Object obj, @Nullable Type asType) {
        if (obj instanceof IClassLoader) {
            return mLoaderSet.remove(((IClassLoader) obj));
        }
        return false;
    }

    @Override
    public boolean add(@NonNull Object obj, @Nullable Type asType) {
        if (obj instanceof IClassLoader) {
            return mLoaderSet.add(((IClassLoader) obj));
        }
        return false;
    }

    @Override
    public int size(@Nullable Type asType) {
        return mLoaderSet.size();
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
