package com.zpf.support.model;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.view.View;
import android.widget.PopupWindow;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zpf.api.IDataCallback;
import com.zpf.api.INavigator;
import com.zpf.api.OnActivityResultListener;
import com.zpf.frame.IContainerHelper;
import com.zpf.frame.IViewContainer;
import com.zpf.frame.IViewProcessor;
import com.zpf.support.base.CompatContainerActivity;
import com.zpf.support.constant.AppConst;
import com.zpf.tool.ShakeInterceptor;
import com.zpf.tool.expand.util.Logger;
import com.zpf.tool.global.CentralManager;
import com.zpf.tool.global.MainHandler;
import com.zpf.tool.stack.AppStackUtil;

import java.lang.ref.WeakReference;

/**
 * @author Created by ZPF on 2021/4/2.
 */
public class SmartNavigator implements INavigator<Class<? extends IViewProcessor>, Intent, Intent> {
    private WeakReference<Object> executorReference;
    private final ShakeInterceptor interceptor = new ShakeInterceptor(50);

    public void setExecutor(Object executor) {
        this.executorReference = new WeakReference<>(executor);
    }

    @Override
    public void push(@NonNull Class<? extends IViewProcessor> target) {
        this.push(target, null);
    }

    @Override
    public void push(@NonNull Class<? extends IViewProcessor> target, @Nullable Intent params) {
        if (!interceptor.checkInterval()) {
            return;
        }
        Object executor = executorReference.get();
        if (executor == null) {
            Logger.e("The resources have been released.");
            return;
        }
        Context context = getContext();
        if (context == null) {
            Logger.e("Unable to find the appropriate context.");
            return;
        }
        interceptor.record();
        Intent intent = buildIntent(target, params, executor, context);
        if (!(context instanceof Activity)) {
            Logger.w("The context is not an Activity, should be added flag:Intent.FLAG_ACTIVITY_NEW_TASK");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    public void push(@NonNull Class<? extends IViewProcessor> target, @Nullable Intent params, @Nullable final IDataCallback<Intent> callback) {
        if (!interceptor.checkInterval()) {
            return;
        }
        Object executor = executorReference.get();
        if (executor == null) {
            Logger.e("The resources have been released.");
            return;
        }
        Context context = getContext();
        if (context == null) {
            Logger.e("Unable to find the appropriate context.");
            return;
        }
        interceptor.record();
        Intent intent = buildIntent(target, params, executor, context);
        final int requestCode = intent.getIntExtra(AppConst.REQUEST_CODE, AppConst.DEF_REQUEST_CODE);
        IViewContainer container = null;
        if (executor instanceof IViewContainer) {
            container = ((IViewContainer) executor);
        } else if (context instanceof IViewContainer) {
            container = ((IViewContainer) context);
        }
        if (container != null) {
            if (callback == null) {
                container.startActivityForResult(intent, requestCode);
            } else {
                container.startActivityForResult(intent, new OnActivityResultListener() {
                    @Override
                    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                        callback.onResult(resultCode, data);
                    }
                });
            }
            return;
        }
        if (callback != null) {
            if (executor instanceof ComponentActivity) {
                final SimpleActivityResultContract resultContract = new SimpleActivityResultContract();
                ((AppCompatActivity) executor).registerForActivityResult(resultContract, new ActivityResultCallback<Intent>() {
                    @Override
                    public void onActivityResult(Intent result) {
                        callback.onResult(resultContract.getResultCode(), result);
                    }
                });
                return;
            } else if (executor instanceof androidx.fragment.app.Fragment) {
                final SimpleActivityResultContract resultContract = new SimpleActivityResultContract();
                ((androidx.fragment.app.Fragment) executor).registerForActivityResult(resultContract, new ActivityResultCallback<Intent>() {
                    @Override
                    public void onActivityResult(Intent result) {
                        callback.onResult(requestCode, result);
                    }
                });
                return;
            } else if (context instanceof ComponentActivity) {
                final SimpleActivityResultContract resultContract = new SimpleActivityResultContract();
                ((ComponentActivity) context).registerForActivityResult(resultContract, new ActivityResultCallback<Intent>() {
                    @Override
                    public void onActivityResult(Intent result) {
                        callback.onResult(requestCode, result);
                    }
                });
                return;
            }
        }
        Logger.w("It is not support to register the result listener.");
        if (executor instanceof Activity) {
            ((Activity) executor).startActivityForResult(intent, requestCode);
        } else if (executor instanceof android.app.Fragment) {
            ((android.app.Fragment) executor).startActivityForResult(intent, requestCode);
        } else if (executor instanceof androidx.fragment.app.Fragment) {
            ((androidx.fragment.app.Fragment) executor).startActivityForResult(intent, requestCode);
        } else if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, requestCode);
        } else {
            Logger.e("Will open Activity on a new task without requestCode.It's better not to call this method like this.");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    @Override
    public void replace(@NonNull Class<? extends IViewProcessor> target, @Nullable Intent params) {
        if (!interceptor.checkInterval()) {
            return;
        }
        interceptor.record();
        this.pop();
        push(target, params);
    }

    @Override
    public void pop() {
        this.pop(AppConst.DEF_RESULT_CODE, null);
    }

    @Override
    public void pop(int resultCode, @Nullable Intent data) {
        if (!interceptor.checkInterval()) {
            return;
        }
        Activity activity = getActivity();
        if (activity == null) {
            Logger.w("Unable to find the appropriate Activity.");
            return;
        }
        interceptor.record();
        activity.setResult(resultCode, data);
        activity.finish();
    }

    @Override
    public void popToRoot(@Nullable Intent data) {
        if (!interceptor.checkInterval()) {
            return;
        }
        interceptor.record();
        AppStackUtil.finishToRoot();
        if (data == null) {
            return;
        }
        postData(data, "");
    }

    @Override
    public boolean popTo(@NonNull Class<? extends IViewProcessor> target, @Nullable Intent data) {
        if (!interceptor.checkInterval()) {
            return false;
        }
        interceptor.record();
        final String targetName = target.getName();
        if (!AppStackUtil.finishUntil(targetName)) {
            push(target);
        }
        postData(data, targetName);
        return true;
    }

    @Override
    public boolean remove(@NonNull Class<? extends IViewProcessor> target) {
        if (!interceptor.checkInterval()) {
            return false;
        }
        interceptor.record();
        return AppStackUtil.finishByName(target.getName());
    }

    private Intent buildIntent(@NonNull Class<? extends IViewProcessor> target, @Nullable Intent params,
                               @NonNull Object executor, @NonNull Context context) {
        Intent intent = new Intent();
        Class<?> containerClass = null;
        if (params == null) {
            IContainerHelper helper = CentralManager.getInstance(IContainerHelper.class);
            if (helper != null && executor instanceof IViewContainer) {
                containerClass = helper.getDefContainerClassByType(((IViewContainer) executor).getContainerType());
            }
        } else {
            intent = params;
            try {
                containerClass = (Class<?>) params.getSerializableExtra(AppConst.TARGET_CONTAINER_CLASS);
                params.removeExtra(AppConst.TARGET_CONTAINER_CLASS);
            } catch (Exception e) {
                e.printStackTrace();
            }
            intent.replaceExtras(params);
        }
        if (containerClass == null) {
            containerClass = CompatContainerActivity.class;
        }
        intent.setClass(context, containerClass);
        intent.putExtra(AppStackUtil.STACK_ITEM_NAME, target.getName());
        intent.putExtra(AppConst.TARGET_VIEW_CLASS_NAME, target.getName());
        intent.putExtra(AppConst.TARGET_VIEW_CLASS, target);
        return intent;
    }

    private void postData(final Intent data, final String targetName) {
        if (data == null || targetName == null) {
            return;
        }
        MainHandler.get().postDelayed(new Runnable() {
            @Override
            public void run() {
                Activity activity = AppStackUtil.getTopActivity();
                if (activity instanceof OnActivityResultListener) {
                    String itemName = activity.getIntent().getStringExtra(AppStackUtil.STACK_ITEM_NAME);
                    if (targetName.length() == 0 || targetName.equals(itemName)) {
                        ((OnActivityResultListener) activity).onActivityResult(AppConst.POLL_BACK_REQUEST_CODE, AppConst.POLL_BACK_RESULT_CODE, data);
                    }
                }
            }
        }, 100);
    }

    private Activity getActivity() {
        Context context = getContext();
        if (context instanceof Activity) {
            return ((Activity) context);
        }
        return null;
    }

    private Context getContext() {
        if (executorReference == null) {
            return null;
        }
        Object executor = executorReference.get();
        while (executor != null) {
            if (executor instanceof Activity) {
                return ((Activity) executor);
            }
            if (executor instanceof IViewContainer) {
                return ((IViewContainer) executor).getCurrentActivity();
            }
            if (executor instanceof IViewProcessor) {
                return ((IViewProcessor) executor).getCurrentActivity();
            }
            if (executor instanceof android.app.Fragment) {
                return ((android.app.Fragment) executor).getActivity();
            }
            if (executor instanceof androidx.fragment.app.Fragment) {
                return ((androidx.fragment.app.Fragment) executor).getActivity();
            }
            if (executor instanceof Application) {
                return ((Application) executor);
            } else if (executor instanceof Service) {
                return ((Service) executor);
            } else if (executor instanceof View) {
                executor = ((View) executor).getContext();
            } else if (executor instanceof Dialog) {
                executor = ((Dialog) executor).getContext();
            } else if (executor instanceof PopupWindow) {
                executor = ((PopupWindow) executor).getContentView();
            } else if (executor instanceof ContextWrapper) {
                executor = ((ContextWrapper) executor).getBaseContext();
            } else if (executor instanceof Context) {
                return ((Context) executor);
            } else {
                break;
            }
        }
        return null;
    }
}