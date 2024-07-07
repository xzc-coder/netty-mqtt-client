package io.github.netty.mqtt.client.plugin;

/**
 * 拦截器接口
 * @author: xzc-coder
 */
public interface Interceptor {

    /**
     * 拦截某个方法
     *
     * @param invocation 方法调用器
     * @return 方法返回值
     * @throws Throwable 异常
     */
    Object intercept(Invocation invocation) throws Throwable;
}
