package com.github.netty.mqtt.client.plugin;

import com.github.netty.mqtt.client.MqttConfiguration;
import com.github.netty.mqtt.client.support.util.AssertUtils;

import java.lang.reflect.Method;

/**
 * @Date: 2022/7/22 14:37
 * @Description: 拦截器的方法调用类
 * @author: xzc-coder
 */
public class Invocation {

    /**
     * 目标对象
     */
    private final Object target;
    /**
     * 当前调用的方法
     */
    private final Method method;
    /**
     * 参数
     */
    private final Object[] args;
    /**
     * 拦截链
     */
    private final InterceptorChain interceptorChain;

    public Invocation(Object target, Method method, Object[] args, InterceptorChain interceptorChain) {
        AssertUtils.notNull(target, "target is null");
        AssertUtils.notNull(method, "method is null");
        AssertUtils.notNull(interceptorChain, "interceptorChain is null");
        this.target = target;
        this.method = method;
        this.args = args;
        this.interceptorChain = interceptorChain;
    }

    public Object getTarget() {
        return target;
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getArgs() {
        return args;
    }

    /**
     * 调用方法
     *
     * @return 方法返回值
     * @throws Throwable 异常
     */
    public Object proceed() throws Throwable {
        //获取下一个拦截器
        Interceptor interceptor = this.interceptorChain.next();
        if (interceptor == null) {
            //说明拦截器调用完了
            return method.invoke(target, args);
        }
        //进行拦截器拦截调用
        return interceptor.intercept(new Invocation(target, method, args, interceptorChain));
    }
}
