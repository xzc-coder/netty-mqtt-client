package io.github.netty.mqtt.client.handler;


import io.github.netty.mqtt.client.msg.MqttDisconnectMsg;
import io.github.netty.mqtt.client.msg.MqttMsg;
import io.github.netty.mqtt.client.msg.MqttSubMsg;
import io.github.netty.mqtt.client.msg.MqttUnsubMsg;
import io.github.netty.mqtt.client.support.future.MqttFuture;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;

/**
 * MQTT委托处理器接口
 * @author: xzc-coder
 */
public interface MqttDelegateHandler {

    /**
     * 接收一个TCP建立连接
     *
     * @param channel 通道
     */
    void channelConnect(Channel channel);

    /**
     * 发送一个MQTT连接
     *
     * @param channel 通道
     */
    void sendConnect(Channel channel);

    /**
     * 收到一个MQTT connack
     *
     * @param channel            通道
     * @param mqttConnAckMessage MQTT确认消息
     */
    void connack(Channel channel, MqttConnAckMessage mqttConnAckMessage);

    /**
     * 收到一个MQTT auth
     *
     * @param channel         通道
     * @param mqttAuthMessage MQTT认证消息
     */
    void auth(Channel channel, MqttMessage mqttAuthMessage);

    /**
     * 发送一个MQTT认证
     *
     * @param channel 通道
     * @param reasonCode 原因码
     * @param mqttProperties MQTT属性
     */
    void sendAuth(Channel channel, byte reasonCode, MqttProperties mqttProperties);


    /**
     * 发送一个MQTT 断开连接
     *
     * @param channel           通道
     * @param mqttFuture        MQTTFuture
     * @param mqttDisconnectMsg MQTT断开消息
     */
    void sendDisconnect(Channel channel, MqttFuture mqttFuture, MqttDisconnectMsg mqttDisconnectMsg);

    /**
     * 接收到一个TCP断开连接
     *
     * @param channel     通道
     * @param mqttMessage MQTT消息
     */
    void disconnect(Channel channel, MqttMessage mqttMessage);

    /**
     * 发送一个MQTT订阅
     *
     * @param channel    通道
     * @param mqttSubMsg MQTT订阅消息
     */
    void sendSubscribe(Channel channel, MqttSubMsg mqttSubMsg);

    /**
     * 收到一个MQTT suback
     *
     * @param channel           通道
     * @param mqttSubAckMessage MQTT订阅确认消息
     */
    void suback(Channel channel, MqttSubAckMessage mqttSubAckMessage);

    /**
     * 发送一个MQTT 取消订阅
     *
     * @param channel      Channel
     * @param mqttUnsubMsg MQTT取消订阅消息
     */
    void sendUnsubscribe(Channel channel, MqttUnsubMsg mqttUnsubMsg);

    /**
     * 收到一个MQTT unsuback
     *
     * @param channel             通道
     * @param mqttUnsubAckMessage MQTT取消订阅确认消息
     */
    void unsuback(Channel channel, MqttUnsubAckMessage mqttUnsubAckMessage);

    /**
     * 发送一个MQTT pingreq
     *
     * @param channel 通道
     */
    void sendPingreq(Channel channel);

    /**
     * 接收到一个MQTT pingresp
     *
     * @param channel             通道
     * @param mqttPingRespMessage MQTT消息
     */
    void pingresp(Channel channel, MqttMessage mqttPingRespMessage);

    /**
     * 发送一个MQTT publish
     *
     * @param channel   通道
     * @param mqttMsg   MQTT消息
     * @param msgFuture MqttFuture(只有qos 为0 的消息需要)
     */
    void sendPublish(Channel channel, MqttMsg mqttMsg, MqttFuture msgFuture);

    /**
     * 接收到MQTT publish
     *
     * @param channel            通道
     * @param mqttPublishMessage MQTT发布消息
     */
    void publish(Channel channel, MqttPublishMessage mqttPublishMessage);

    /**
     * 发送一个MQTT puback
     *
     * @param channel Channel
     * @param mqttMsg MqttMsg
     */
    void sendPuback(Channel channel, MqttMsg mqttMsg);

    /**
     * 接收到一个MQTT puback
     *
     * @param channel           通道
     * @param mqttPubAckMessage MQTT发布确认消息
     */
    void puback(Channel channel, MqttPubAckMessage mqttPubAckMessage);

    /**
     * 发送一个MQTT pubrec
     *
     * @param channel 通道
     * @param mqttMsg MqttMsg
     */
    void sendPubrec(Channel channel, MqttMsg mqttMsg);

    /**
     * 接收到一个MQTT pubrec
     *
     * @param channel     通道
     * @param mqttMessage MQTT消息
     */
    void pubrec(Channel channel, MqttMessage mqttMessage);

    /**
     * 发送一个MQTT pubrel
     *
     * @param channel 通道
     * @param mqttMsg MQTT消息
     */
    void sendPubrel(Channel channel, MqttMsg mqttMsg);

    /**
     * 接收一个MQTT pubrel
     *
     * @param channel     通道
     * @param mqttMessage MQTT消息
     */
    void pubrel(Channel channel, MqttMessage mqttMessage);

    /**
     * 发送一个MQTT pubcomp
     *
     * @param channel 通道
     * @param mqttMsg MQTT消息
     */
    void sendPubcomp(Channel channel, MqttMsg mqttMsg);

    /**
     * 接收一个MQTT pubcomp
     *
     * @param channel     通道
     * @param mqttMessage MQTT消息
     */
    void pubcomp(Channel channel, MqttMessage mqttMessage);

    /**
     * 接收到一个Channel异常
     *
     * @param channel 通道
     * @param cause 异常
     */
    void exceptionCaught(Channel channel, Throwable cause);

}
