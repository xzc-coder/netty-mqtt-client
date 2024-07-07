package io.github.netty.mqtt.client.callback;

import io.github.netty.mqtt.client.MqttConnectParameter;

/**
 * MQTT对调接口
 */
public interface MqttCallback {

    /**
     * 订阅完成回调
     *
     * @param mqttSubscribeCallbackResult 订阅结果
     */
    default void subscribeCallback(MqttSubscribeCallbackResult mqttSubscribeCallbackResult) {

    }

    /**
     * 取消订阅完成回调
     *
     * @param mqttUnSubscribeCallbackResult 取消订阅结果
     */
    default void unsubscribeCallback(MqttUnSubscribeCallbackResult mqttUnSubscribeCallbackResult) {

    }


    /**
     * 当发送的消息，完成时回调
     *
     * @param mqttSendCallbackResult 发送消息结果
     */
    default void messageSendCallback(MqttSendCallbackResult mqttSendCallbackResult) {

    }

    /**
     * 接收消息完成时回调
     *
     * @param receiveCallbackResult 接收消息结果
     */
    default void messageReceiveCallback(MqttReceiveCallbackResult receiveCallbackResult) {

    }

    /**
     * TCP的连接成功时回调
     *
     * @param mqttConnectCallbackResult TCP的连接成功结果
     */
    default void channelConnectCallback(MqttConnectCallbackResult mqttConnectCallbackResult) {

    }

    /**
     * MQTT连接完成时回调
     *
     * @param mqttConnectCallbackResult 连接完成结果
     */
    default void connectCompleteCallback(MqttConnectCallbackResult mqttConnectCallbackResult) {

    }

    /**
     * 连接丢失时回调
     *
     * @param mqttConnectLostCallbackResult 连接丢失结果
     */
    default void connectLostCallback(MqttConnectLostCallbackResult mqttConnectLostCallbackResult) {

    }

    /**
     * 收到心跳响应时回调
     *
     * @param mqttHeartbeatCallbackResult 心跳响应结果
     */
    default void heartbeatCallback(MqttHeartbeatCallbackResult mqttHeartbeatCallbackResult) {

    }

    /**
     * Netty的Channel发生异常时回调
     *
     * @param mqttConnectParameter               连接时的参数
     * @param mqttChannelExceptionCallbackResult Channel异常结果
     */
    default void channelExceptionCaught(MqttConnectParameter mqttConnectParameter, MqttChannelExceptionCallbackResult mqttChannelExceptionCallbackResult) {

    }
}
