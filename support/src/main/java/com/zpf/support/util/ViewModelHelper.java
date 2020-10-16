package com.zpf.support.util;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.ContextWrapper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.zpf.api.Initializer;
import com.zpf.api.OnDestroyListener;
import com.zpf.support.model.AppViewModelFactory;
import com.zpf.tool.stack.AppStackUtil;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.HashMap;

public class ViewModelHelper {
    private static final HashMap<String, WeakReference<ViewModel>> mMap = new HashMap<>();

    public static <T extends ViewModel> T createModel(@NonNull Class<T> cls) {
        return createModel(cls, null, null, null, null);
    }

    public static <T extends ViewModel> T createModel(@NonNull Class<T> cls, @Nullable String tag) {
        return createModel(cls, null, null, tag, null);
    }

    public static <T extends ViewModel> T createModel(
            @NonNull Class<T> cls, @Nullable String tag, @Nullable Initializer<T> initializer) {
        return createModel(cls, null, null, tag, initializer);
    }

    public static <T extends ViewModel> T createModel(
            @NonNull Class<T> cls, @Nullable ViewModelProvider provider) {
        return createModel(cls, null, provider, null, null);
    }

    public static <T extends ViewModel> T createModel(
            @NonNull Class<T> cls, @Nullable ViewModelProvider provider, @Nullable String tag) {
        return createModel(cls, null, provider, tag, null);
    }

    public static <T extends ViewModel> T createModel(
            @NonNull Class<T> cls, @Nullable ViewModelProvider provider, @Nullable Initializer<T> initializer) {
        return createModel(cls, null, provider, null, initializer);
    }

    public static <T extends ViewModel> T createModel(
            @NonNull Class<T> cls, @Nullable ViewModelProvider provider, @Nullable String tag,
            @Nullable Initializer<T> initializer) {
        return createModel(cls, null, provider, tag, initializer);
    }

    public static <T extends ViewModel> T createModel(
            @NonNull Class<T> cls, @Nullable ViewModelProvider.Factory factory,
            @Nullable ViewModelProvider provider, @Nullable String tag,
            @Nullable Initializer<T> initializer) {
        ViewModelProvider.Factory realFactory;
        if (factory == null) {
            realFactory = AppViewModelFactory.getInstance();
        } else {
            realFactory = factory;
        }
        String key = tag;
        if (key == null || key.length() < 1) {
            key = cls.getName();
        }
        T model;
        if (provider == null) {
            model = get(key, cls, realFactory);
        } else {
            model = provider.get(key, cls);
        }
        if (initializer != null) {
            initializer.onInit(model);
        }
        return model;
    }

    private static <T extends ViewModel> T get(String key, @NonNull Class<T> modelClass, ViewModelProvider.Factory factory) {
        WeakReference<ViewModel> weakCache = mMap.get(key);
        ViewModel viewModel = null;
        if (weakCache != null) {
            viewModel = weakCache.get();
        }
        if (viewModel != null && modelClass.isInstance(viewModel)) {
            //noinspection unchecked
            return (T) viewModel;
        }
        viewModel = factory.create(modelClass);
        WeakReference<ViewModel> weakReference = mMap.put(key, new WeakReference<>(viewModel));
        if (weakReference != null) {
            clearModel(weakReference.get());
        }
        //noinspection unchecked
        return (T) viewModel;
    }

    public static void clearAll() {
        for (WeakReference<ViewModel> wr : mMap.values()) {
            if (wr != null) {
                clearModel(wr.get());
            }
        }
        mMap.clear();
    }

    public static void clearModel(ViewModel viewModel) {
        if (viewModel != null) {
            if (viewModel instanceof OnDestroyListener) {
                ((OnDestroyListener) viewModel).onDestroy();
            } else {
                try {
                    Method clear = viewModel.getClass().getDeclaredMethod("onCleared");
                    clear.setAccessible(true);
                    clear.invoke(viewModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static ViewModelStoreOwner getViewModelStoreOwner(Context context) {
        if (context == null) {
            context = AppStackUtil.get().getTopActivity();
        }
        if (context != null) {
            if (context instanceof ContextWrapper && !(context instanceof ComponentCallbacks2)) {
                context = ((ContextWrapper) context).getBaseContext();
            }
        }
        if (context instanceof ViewModelStoreOwner) {
            return ((ViewModelStoreOwner) context);
        } else {
            return null;
        }
    }
}
