package com.github.netty.mqtt.client.support.proxy;


import com.github.netty.mqtt.client.constant.MqttConstant;
import com.github.netty.mqtt.client.plugin.Interceptor;
import com.github.netty.mqtt.client.support.util.AssertUtils;

import java.util.List;

/**
 * @Date: 2022/8/16 14:07
 * @Description: 代理工厂接口
 * @author: xzc-coder
 */
public interface ProxyFactory {

    /**
     * 获取一个代理对象
     *
     * @param target       目标对象
     * @param interceptors 拦截器集合
     * @return 代理对象
     */
    Object getProxy(Object target, List<Interceptor> interceptors);

    /**
     * 获取代理类别
     *
     * @return 代理类别
     */
    String getProxyType();

    /**
     * 判断对象是否是代理对象
     *
     * @param object 对象
     * @return 是否是代理对象
     */
    static boolean isProxyObject(Object object) {
        AssertUtils.notNull(object, "object is null");
        return object.getClass().getName().contains(MqttConstant.CGLIB_CONTAIN_CONTENT) || object.getClass().getName().contains(MqttConstant.JDK_PROXY_CONTAIN_CONTENT);
    }
}
