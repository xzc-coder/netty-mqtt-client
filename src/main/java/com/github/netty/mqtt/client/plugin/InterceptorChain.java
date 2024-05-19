package com.github.netty.mqtt.client.plugin;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @Date: 2022/7/22 16:44
 * @Description: 拦截器链
 * @author: xzc-coder
 */
public class InterceptorChain {

    /**
     * 拦截集合的迭代器
     */
    private Iterator<Interceptor> interceptorIterator;

    public InterceptorChain(List<Interceptor> interceptors) {
        if (interceptors != null) {
            this.interceptorIterator = new LinkedList<>(interceptors).iterator();
        }

    }

    /**
     * 返回下一个拦截器
     *
     * @return 拦截器
     */
    Interceptor next() {
        Interceptor interceptor = null;
        if (this.interceptorIterator.hasNext()) {
            interceptor = this.interceptorIterator.next();
        }
        return interceptor;
    }
}
