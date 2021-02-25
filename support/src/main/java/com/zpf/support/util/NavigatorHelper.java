package com.zpf.support.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.zpf.frame.IContainerHelper;
import com.zpf.frame.IViewProcessor;
import com.zpf.support.base.CompatContainerActivity;
import com.zpf.support.constant.AppConst;
import com.zpf.tool.config.GlobalConfigImpl;

/**
 * @author Created by ZPF on 2021/2/25.
 */
public class NavigatorHelper {

    public Intent push(Class<? extends IViewProcessor> target, Context context, Bundle params, int containerType) {
        Intent intent = new Intent();
        Class<?> defContainerClass = null;
        IContainerHelper helper = GlobalConfigImpl.get().getGlobalInstance(IContainerHelper.class);
        if (helper != null) {
            defContainerClass = helper.getDefContainerClassByType(containerType);
        }
        if (defContainerClass == null) {
            defContainerClass = CompatContainerActivity.class;
        }
        if (params == null) {
            intent.setClass(context, defContainerClass);
            intent.putExtra(AppConst.TARGET_VIEW_CLASS_NAME, target.getName());
        } else {
            Class<?> containerClass = null;
            try {
                containerClass = (Class<?>) params.getSerializable(AppConst.TARGET_CONTAINER_CLASS);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (containerClass == null) {
                intent.setClass(context, defContainerClass);
            } else {
                intent.setClass(context, containerClass);
            }
            String containerAction = params.getString(AppConst.TARGET_CONTAINER_ACTION, null);
            if (!TextUtils.isEmpty(containerAction)) {
                intent.setAction(containerAction);

            }
            params.remove(AppConst.TARGET_CONTAINER_CLASS);
            params.remove(AppConst.TARGET_CONTAINER_ACTION);
            intent.putExtras(params);
            intent.setExtrasClassLoader(params.getClassLoader());
        }
        intent.putExtra(AppConst.TARGET_VIEW_CLASS, target);
        return intent;
    }
}
