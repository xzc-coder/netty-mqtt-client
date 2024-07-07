package io.github.netty.mqtt.client.support.future;

/**
 * MQTTFuture的监听器
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
