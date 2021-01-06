package com.zpf.support.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import com.zpf.support.util.ViewModelHelper;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class AppViewModelProvider extends ViewModelProvider {

    private final HashMap<String, WeakReference<ViewModel>> mMap = new HashMap<>();
    private final ViewModelProvider.Factory factory = AppViewModelFactory.getInstance();
    private static final ViewModelStore viewModelStore = new ViewModelStore();

    public static AppViewModelProvider get() {
        return AppViewModelProvider.Instance.provider;
    }

    static class Instance {
        static AppViewModelProvider provider = new AppViewModelProvider();
    }

    public AppViewModelProvider() {
        super(viewModelStore, AppViewModelFactory.getInstance());
    }

    @NonNull
    @Override
    public <T extends ViewModel> T get(@NonNull String key, @NonNull Class<T> modelClass) {
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
        mMap.put(key, new WeakReference<>(viewModel));
        //noinspection unchecked
        return (T) viewModel;
    }

    public void clearAll() {
        for (WeakReference<ViewModel> wr : mMap.values()) {
            if (wr != null) {
                ViewModelHelper.clearModel(wr.get());
            }
        }
        mMap.clear();
    }
}
