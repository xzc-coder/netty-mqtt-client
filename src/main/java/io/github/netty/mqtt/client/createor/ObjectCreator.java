package io.github.netty.mqtt.client.createor;

/**
 * 对象创建器接口
 * @param <T> 对象创建器创建的类
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
