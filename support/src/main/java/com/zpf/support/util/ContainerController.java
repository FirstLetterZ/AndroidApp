package com.zpf.support.util;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zpf.frame.IContainerHelper;
import com.zpf.frame.IViewContainer;
import com.zpf.frame.IViewProcessor;
import com.zpf.support.constant.AppConst;
import com.zpf.tool.config.GlobalConfigImpl;
import com.zpf.tool.expand.util.ClassLoaderImpl;

/**
 * Created by ZPF on 2019/3/1.
 */

public class ContainerController {

    public static volatile IViewContainer mInitingViewContainer;

    public static IViewProcessor createViewProcessor(@NonNull IViewContainer viewContainer, @NonNull Bundle params, @Nullable Class defViewClass) {
        //获取目标class
        Class<? extends IViewProcessor> targetViewClass = null;
        try {
            Class targetClass = null;
            try {
                targetClass = (Class) params.getSerializable(AppConst.TARGET_VIEW_CLASS);
            } catch (Exception e) {
                //
            }
            if (targetClass == null) {
                String targetClassName = params.getString(AppConst.TARGET_VIEW_CLASS_NAME);
                if (targetClassName != null) {
                    targetClass = ClassLoaderImpl.get().getClass(targetClassName);
                }
                if (targetClass == null) {
                    targetClass = defViewClass;
                }
                if (targetClass != null) {
                    params.putSerializable(AppConst.TARGET_VIEW_CLASS, targetClass);
                }
            }
            targetViewClass = (Class<? extends IViewProcessor>) targetClass;
        } catch (Exception e) {
            e.printStackTrace();
            IContainerHelper containerHelper = GlobalConfigImpl.get().getGlobalInstance(IContainerHelper.class);
            if (containerHelper != null) {
                targetViewClass = containerHelper.getErrorProcessorClass(null);
            }
        }
        //实例化
        IViewProcessor viewProcessor = null;
        if (targetViewClass != null) {
            viewProcessor = createViewProcessor(viewContainer, targetViewClass);
        }
        return viewProcessor;
    }

    public static IViewProcessor createViewProcessor(@NonNull IViewContainer viewContainer, @NonNull Class<? extends IViewProcessor> targetViewClass) {
        //实例化
        IViewProcessor viewProcessor = null;
        synchronized (ContainerController.class) {
            ContainerController.mInitingViewContainer = viewContainer;
            try {
                viewProcessor = targetViewClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            IContainerHelper containerHelper = GlobalConfigImpl.get().getGlobalInstance(IContainerHelper.class);
            if (containerHelper != null) {
                targetViewClass = containerHelper.getErrorProcessorClass(targetViewClass);
            }
            if (targetViewClass != null) {
                try {
                    viewProcessor = targetViewClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ContainerController.mInitingViewContainer = null;
        }
        return viewProcessor;
    }

}