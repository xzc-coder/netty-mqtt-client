package com.github.netty.mqtt.client.plugin;


import com.github.netty.mqtt.client.MqttConfiguration;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @Date: 2022/8/16 15:23
 * @Description: 默认的JDK拦截器实现
 * @author: xzc-coder
 */
public class JdkMethodInterceptor extends BaseMethodInterceptor implements InvocationHandler {

    public JdkMethodInterceptor(List<Interceptor> interceptors, Object target) {
        super(interceptors, target);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //是否拦截该方法
        boolean invoke = invokeInterceptMethod(method);
        if (invoke) {
            //创建方法调用器
            Invocation invocation = new Invocation(this.target, method, args, new InterceptorChain(this.interceptors));
            //执行方法
            return invocation.proceed();
        } else {
            //不拦截则调用目标对象方法继续执行
            return method.invoke(this.target, args);
        }
    }

    @Override
    protected void filterInterceptor(List<Interceptor> interceptors, Object target) {
        //翻转拦截器
        Collections.reverse(interceptors);
        Iterator<Interceptor> iterator = interceptors.iterator();
        //遍历拦截器
        while (iterator.hasNext()) {
            //获取拦截器上的注解Intercepts信息
            Interceptor interceptor = iterator.next();
            Intercepts intercepts = interceptor.getClass().getAnnotation(Intercepts.class);
            //是否删除该拦截器（无效的拦截器会被删除）
            boolean delete = true;
            if (intercepts != null) {
                //获取Intercepts上的class信息集合
                Class<?>[] classes = intercepts.type();
                //遍历
                for (Class<?> annotationClass : classes) {
                    //如果class不是接口则抛出异常,因为jdk只支持接口代理
                    if (!annotationClass.isInterface()) {
                        throw new IllegalArgumentException("jdk proxy type " + annotationClass.getName() + " must is interface.");
                    }
                    //判断注解上的要拦截的类是否是目标对象的超类或相同类
                    if (annotationClass.isAssignableFrom(target.getClass())) {
                        //有一个符合条件则不删除该拦截器
                        delete = false;
                        this.interceptClassSet.add(annotationClass);
                    }
                }
            }
            if (delete) {
                iterator.remove();
            }
        }
    }
}
