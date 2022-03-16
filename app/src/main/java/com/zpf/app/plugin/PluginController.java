package com.zpf.app.plugin;

import android.text.TextUtils;
import android.util.ArrayMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.api.IClassLoader;
import com.zpf.api.dataparser.JsonParserInterface;
import com.zpf.file.FileUtil;
import com.zpf.tool.expand.cache.SpStorageWorker;
import com.zpf.tool.expand.cache.SpUtil;
import com.zpf.tool.expand.util.ClassLoaderImpl;
import com.zpf.tool.expand.util.LogUtil;
import com.zpf.tool.global.CentralManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

public final class PluginController {
    private final ArrayMap<String, PluginApkBean> pluginInfoMap = new ArrayMap<>();
    private final ArrayMap<String, ClassLoader> pluginClassLoaderMap = new ArrayMap<>();
    private final SpStorageWorker spWorker = SpUtil.get("sp_plugin_config_file");

    private static class Instance {
        static final PluginController mInstance = new PluginController();
    }

    private PluginController() {
    }

    public static PluginController get() {
        return Instance.mInstance;
    }

    public PluginApkBean checkInit(String pluginApkName) {
        PluginApkBean pluginApkBean = null;
        if (pluginInfoMap.size() > 0) {
            pluginApkBean = pluginInfoMap.get(pluginApkName);
        }
        if (pluginApkBean == null) {
            JsonParserInterface jsonParser = CentralManager.getInstance(JsonParserInterface.class);
            if (jsonParser != null) {
                String fileConfig = spWorker.getString(pluginApkName);
                if (!TextUtils.isEmpty(fileConfig)) {
                    pluginApkBean = jsonParser.fromJson(fileConfig, PluginApkBean.class);
                }
                if (pluginApkBean == null && copyApk(pluginApkName)) {
                    fileConfig = FileUtil.readAssetFile(CentralManager.getAppContext(), "plugin/" + pluginApkName + "_config.json");
                    pluginApkBean = jsonParser.fromJson(fileConfig, PluginApkBean.class);
                    if (pluginApkBean != null) {
                        ApkInfo apkInfo = new ApkInfo();
                        apkInfo.path = FileUtil.getAppDataPath() + "/plugin/" + pluginApkName + ".apk";
                        pluginApkBean.localApkInfo = apkInfo;
                        spWorker.put(pluginApkName, fileConfig);
                    }
                }
            }
            pluginInfoMap.put(pluginApkName, pluginApkBean);
        }
        return pluginApkBean;
    }

    public void updateCacheInfo(String pluginApkName, PluginApkBean pluginApkBean) {
        pluginInfoMap.put(pluginApkName, pluginApkBean);
        spWorker.put(pluginApkName, pluginApkBean);
    }

    public void getClassAsync(String name, String apkName, AsyncLoadListener listener) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(apkName)) {
            //参数异常
            onResult(listener, AsyncLoadState.PARAM_ERROR, name, null, null);
            return;
        }
        PluginApkBean pluginApkBean = checkInit(apkName);
        int paramsState = checkParams(pluginApkBean);
        if (paramsState == 0) {
            Class result = loadClass(pluginApkBean, name);
            if (result == null) {
                onResult(listener, AsyncLoadState.RESULT_NULL, name, pluginApkBean, null);
            } else {
                onResult(listener, AsyncLoadState.RESULT_SUCCESS, name, pluginApkBean, result);
            }
        } else {
            onResult(listener, paramsState, name, pluginApkBean, null);
        }
    }

    public Class<?> loadClass(PluginApkBean pluginApkBean, String name) {
        String pluginPath = pluginApkBean.localApkInfo.path;
        ClassLoader classLoader = pluginClassLoaderMap.get(pluginPath);
        Class result = null;
        if (classLoader != null) {
            //尝试直接加载
            result = ClassLoaderImpl.get().getClass(name);
        } else {
            //初始化加载
            classLoader = PluginUtil.loadPlugin(CentralManager.getAppContext(), pluginPath,
                    FileUtil.getAppCachePath(), CentralManager.getAppContext().getClassLoader());
            pluginClassLoaderMap.put(pluginPath, classLoader);
        }
        if (result == null && classLoader != null) {
            IClassLoader pluginClassLoader = null;
            try {
                Class<?> pluginClass = classLoader.loadClass(pluginApkBean.loaderName);
                if (!TextUtils.isEmpty(pluginApkBean.invokeFunction)) {
                    try {
                        Method method = pluginClass.getMethod(pluginApkBean.invokeFunction);
                        pluginClassLoader = (IClassLoader) method.invoke(null);
                    } catch (Exception e) {
                        //
                    }
                }
                if (pluginClassLoader == null) {
                    pluginClassLoader = (IClassLoader) pluginClass.newInstance();
                }
            } catch (Exception e) {
                //
            }
            if (pluginClassLoader != null) {
                ClassLoaderImpl.get().add(pluginClassLoader);
                result = pluginClassLoader.getClass(name);
            }
        }
        return result;
    }

    private int checkParams(PluginApkBean pluginApkBean) {
        if (pluginApkBean == null) {
            //没有对应的配置
            return AsyncLoadState.CONFIG_NULL;
        } else if (pluginApkBean.localApkInfo == null || pluginApkBean.localApkInfo.path == null) {
            //配置异常
            return AsyncLoadState.CONFIG_ERROR;
        } else if (pluginApkBean.apkInfo == null || pluginApkBean.apkInfo.path == null) {
            //没有升级信息
            return AsyncLoadState.UPDATE_LOSS;
        } else if (pluginApkBean.apkInfo.versionCode > pluginApkBean.localApkInfo.versionCode) {
            if (pluginApkBean.apkInfo.forceUpdate) {
                //强制升级
                return AsyncLoadState.UPDATE_FORCE;
            } else {
                //非强制升级
                return AsyncLoadState.UPDATE_NEED;
            }
        } else {
            return 0;
        }
    }

    private void onResult(AsyncLoadListener listener, @AsyncLoadState int code, @NonNull String targetName, @Nullable PluginApkBean apkBean, @Nullable Class<?> result) {
        if (listener != null) {
            if (result == null) {
                listener.onState(code, targetName, apkBean);
            } else {
                listener.onResult(targetName, result);
            }
        }
    }

    public boolean copyApk(String apkName) {
        File targetFile = FileUtil.getFileOrCreate(FileUtil.getAppDataPath() + "/plugin", apkName + ".apk");
        return copyApk("plugin/" + apkName + ".apk", targetFile.getAbsolutePath());
    }

    private boolean copyApk(String assetFileName, String targetFilePath) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = CentralManager.getAppContext().getAssets().open(assetFileName);
            out = new FileOutputStream(targetFilePath);
            byte[] bytes = new byte[1024];
            int i;
            while ((i = in.read(bytes)) != -1)
                out.write(bytes, 0, i);
        } catch (IOException e) {
            LogUtil.e(e.toString());
            return false;
        } finally {
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            } catch (IOException e) {
                LogUtil.e(e.toString());
            }

        }
        return true;
    }
}