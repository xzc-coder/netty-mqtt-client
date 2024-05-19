package com.github.netty.mqtt.client.createor;

/**
 * @Date: 2023/8/24 13:14
 * @Description: 对象创建器接口
 * @author: xzc-coder
 */
public interface ObjectCreator<T> {

    /**
     * 对象创建器
     *
     * @param constructorArgs 构建参数
     * @return 对象实例
     * @param <T> T
     */
    <T>T createObject(Object... constructorArgs);
}
