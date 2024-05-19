package com.github.netty.mqtt.client.support.future;

/**
 * @Date: 2022/7/19 10:51
 * @Description: MQTTFuture的监听回调器
 * @author: xzc-coder
 */
public interface MqttFutureListener<T> {

    /**
     * 操作完成回调
     *
     * @param mqttFuture Future
     * @throws Exception 异常
     */
    void operationComplete(MqttFuture<T> mqttFuture) throws Throwable;
}
