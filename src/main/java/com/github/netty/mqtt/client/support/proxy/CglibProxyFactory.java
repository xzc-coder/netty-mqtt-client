package com.github.netty.mqtt.client.support.proxy;

import com.github.netty.mqtt.client.constant.MqttConstant;
import com.github.netty.mqtt.client.plugin.CglibMethodInterceptor;
import com.github.netty.mqtt.client.plugin.CglibTargetHelper;
import com.github.netty.mqtt.client.plugin.Interceptor;
import com.github.netty.mqtt.client.support.util.EmptyUtils;
import net.sf.cglib.proxy.Enhancer;

import java.util.List;

/**
 * @Date: 2024/6/24 20:15
 * @Description: 拦截器接口
 * @author: xzc-coder
 */
public class CglibProxyFactory implements ProxyFactory {
    @Override
    public Object getProxy(Object target, List<Interceptor> interceptors) {
        Object obj = target;
        if (target == null || EmptyUtils.isEmpty(interceptors)) {
            return obj;
        }else {
            //创建一个Enhancer
            Enhancer enhancer = new Enhancer();
            //设置父类的class
            enhancer.setSuperclass(CglibTargetHelper.createCglibTarget(target).getClass());
            //设置回调时拦截器
            enhancer.setCallback(new CglibMethodInterceptor(interceptors, target));
            //创建代理对象
            obj = enhancer.create();
        }
        return obj;
    }

    @Override
    public String getProxyType() {
        return MqttConstant.PROXY_TYPE_CGLIB;
    }
}
