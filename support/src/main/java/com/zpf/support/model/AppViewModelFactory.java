package com.zpf.support.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.lang.reflect.Constructor;

public class AppViewModelFactory implements ViewModelProvider.Factory {

    public static AppViewModelFactory getInstance() {
        return Instance.factory;
    }

    static class Instance {
        static AppViewModelFactory factory = new AppViewModelFactory();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        T result = null;
        try {
            result = modelClass.newInstance();
        } catch (Exception e) {
            //
        }
        if (result == null) {
            Constructor<?>[] constructors = modelClass.getConstructors();
            if (constructors.length > 0) {
                for (Constructor<?> constructor : constructors) {
                    try {
                        result = (T) constructor.newInstance((Object[]) null);
                    } catch (Exception e) {
                        //
                    }
                    if (result != null) {
                        break;
                    }
                }
            }
        }
        if (result == null) {
            throw new RuntimeException("Cannot create an instance of " + modelClass);
        }
        return result;
    }
}