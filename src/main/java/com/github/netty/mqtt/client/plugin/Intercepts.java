package com.github.netty.mqtt.client.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Date: 2022/7/22 15:06
 * @Description: 拦截器注解
 * @author: xzc-coder
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Intercepts {

    /**
     * 拦截的类
     *
     * @return 拦截的类
     */
    Class<?>[] type();
}
