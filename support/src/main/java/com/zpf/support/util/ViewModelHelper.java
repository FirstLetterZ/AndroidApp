package com.zpf.support.util;

import android.app.Dialog;
import android.content.ContextWrapper;
import android.view.View;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.zpf.api.Initializer;
import com.zpf.api.OnDestroyListener;
import com.zpf.support.model.AppViewModelFactory;
import com.zpf.support.model.AppViewModelProvider;
import com.zpf.tool.stack.AppStackUtil;

import java.lang.reflect.Method;

public class ViewModelHelper {

    public static <T extends ViewModel> T createModel(@NonNull Class<T> cls) {
        return createModel(cls, null, null, null);
    }

    public static <T extends ViewModel> T createModel(@NonNull Class<T> cls, @Nullable String tag) {
        return createModel(cls, tag, null, null);
    }

    public static <T extends ViewModel> T createModel(
            @NonNull Class<T> cls, @Nullable String tag, @Nullable Initializer<T> initializer) {
        return createModel(cls, tag, null, initializer);
    }

    public static <T extends ViewModel> T createModel(
            @NonNull Class<T> cls, @Nullable String tag, @Nullable ViewModelProvider provider,
            @Nullable Initializer<T> initializer) {
        T model;
        if (provider == null) {
            provider = AppViewModelProvider.get();
        }
        if (tag == null) {
            model = provider.get(cls);
        } else {
            model = provider.get(tag, cls);
        }
        if (initializer != null) {
            initializer.onInit(model);
        }
        return model;
    }

    public static void clearModel(ViewModel viewModel) {
        if (viewModel != null) {
            try {
                Method clear = viewModel.getClass().getDeclaredMethod("onCleared");
                clear.setAccessible(true);
                clear.invoke(viewModel);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (viewModel instanceof OnDestroyListener) {
                ((OnDestroyListener) viewModel).onDestroy();
            }
        }
    }

    public static ViewModelProvider createViewModelProvider(@Nullable Object obj, @Nullable ViewModelProvider.Factory factory) {
        ViewModelStoreOwner storeOwner = getViewModelStoreOwner(obj);
        if (storeOwner == null) {
            return null;
        }
        if (factory == null) {
            return new ViewModelProvider(storeOwner, AppViewModelFactory.getInstance());
        } else {
            return new ViewModelProvider(storeOwner, factory);
        }
    }

    public static ViewModelStoreOwner getViewModelStoreOwner(@Nullable Object obj) {
        ViewModelStoreOwner result = null;
        if (obj == null) {
            obj = AppStackUtil.get().getTopActivity();
        }
        while (obj != null) {
            if (obj instanceof ViewModelStoreOwner) {
                result = ((ViewModelStoreOwner) obj);
                break;
            } else if (obj instanceof ContextWrapper) {
                obj = ((ContextWrapper) obj).getBaseContext();
            } else if (obj instanceof View) {
                obj = ((View) obj).getContext();
            } else if (obj instanceof Dialog) {
                obj = ((Dialog) obj).getContext();
            } else if (obj instanceof PopupWindow) {
                obj = ((PopupWindow) obj).getContentView();
            } else {
                break;
            }
        }
        return result;
    }
}
