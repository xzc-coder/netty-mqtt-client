package com.github.netty.mqtt.client.plugin;

import com.github.netty.mqtt.client.MqttConfiguration;
import com.github.netty.mqtt.client.support.util.AssertUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Date: 2022/8/16 15:25
 * @Description: 拦截器基础类
 * @author: xzc-coder
 */
public class BaseMethodInterceptor {

    /**
     * 拦截器列表
     */
    protected final List<Interceptor> interceptors = Collections.synchronizedList(new ArrayList<>());
    /**
     * 目标对象
     */
    protected final Object target;
    /**
     * 要拦截的类信息集合 做缓存
     */
    protected final Set<Class<?>> interceptClassSet;
    /**
     * 方法调用缓存，返回null，则表示该方法没有被判断过是否拦截，true则表示拦截，false 则表示跳过
     */
    protected final Map<Method, Boolean> invokeInterceptCache = new ConcurrentHashMap<>();


    public BaseMethodInterceptor(List<Interceptor> interceptors, Object target) {
        if (interceptors != null) {
            this.interceptors.addAll(interceptors);
        }
        AssertUtils.notNull(target, "target is null");
        this.interceptClassSet = new CopyOnWriteArraySet<>();
        filterInterceptor(this.interceptors, target);
        this.target = target;
    }

    /**
     * 过滤拦截器
     *
     * @param interceptors 拦截器
     * @param target       目标对象
     */
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

    /**
     * 判断某个方法是否需要被调用拦截
     *
     * @param method 方法
     * @return 是否要调用拦截方法
     */
    protected boolean invokeInterceptMethod(Method method) {
        //先从缓存中获取
        Boolean invoke = this.invokeInterceptCache.get(method);
        if (invoke == null) {
            invoke = false;
            //判断是否是Object方法
            if (!this.isObjectMethod(method)) {
                //不是Object方法则遍历拦截类的集合
                for (Class<?> clazz : this.interceptClassSet) {
                    Method[] methods = clazz.getDeclaredMethods();
                    for (Method m : methods) {
                        //如果方法名，参数个数都相同，则表示要拦截当前方法
                        if (m.getName().equals(method.getName()) && m.getParameterTypes().length == method.getParameterTypes().length) {
                            invoke = true;
                            break;
                        }
                    }
                }
            }
        }
        //添加到缓存
        this.invokeInterceptCache.putIfAbsent(method, invoke);
        return invoke;
    }

    /**
     * 判断是否是Object方法
     *
     * @param method 方法
     * @return 是否是Object方法
     */
    protected boolean isObjectMethod(Method method) {
        Method[] methods = Object.class.getDeclaredMethods();
        for (Method m : methods) {
            if (m.getName().equals(method.getName()) && m.getParameterTypes().length == method.getParameterTypes().length) {
                return true;
            }
        }
        return false;
    }

}
