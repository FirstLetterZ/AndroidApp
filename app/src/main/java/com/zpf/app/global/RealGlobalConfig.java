package com.zpf.app.global;

import com.zpf.api.IClassLoader;
import com.zpf.api.dataparser.JsonParserInterface;
import com.zpf.support.network.base.IResponseHandler;
import com.zpf.tool.expand.util.ClassLoaderImpl;
import com.zpf.tool.global.ICentralOperator;
import com.zpf.tool.gson.GsonUtil;

import java.util.UUID;

/**
 * Created by ZPF on 2019/6/13.
 */
public class RealGlobalConfig implements ICentralOperator {
    private UUID uuid = UUID.randomUUID();

    @Override
    public String getId() {
        return uuid.toString();
    }

    @Override
    public void onObjectInit(Object object) {
    }

    @Override
    public Object invokeMethod(Object object, String methodName, Object... args) {
        return null;
    }

    /**
     * @param target    目标对应的Class
     * @param qualifier 限定符，用于获取相同class的不同实现
     * @return 对应的全局单例
     */
    @Override
    public <T> T getInstance(Class<T> target, String qualifier) {
        return null;
    }

    /**
     * 上面方法中 qualifier 为null时的默认实现
     *
     * @param target
     */
    @Override
    public <T> T getInstance(Class<T> target) {
        if (target == JsonParserInterface.class) {
            return (T) GsonUtil.get();
        } else if (target == IResponseHandler.class) {
            return (T) ResponseHandleImpl.get();
        } else if (target == IClassLoader.class) {
            return (T) ClassLoaderImpl.get();
        }
        return null;
    }
}
