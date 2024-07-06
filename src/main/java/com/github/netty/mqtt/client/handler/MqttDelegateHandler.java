package com.github.netty.mqtt.client.handler;


import com.github.netty.mqtt.client.msg.MqttDisconnectMsg;
import com.github.netty.mqtt.client.msg.MqttMsg;
import com.github.netty.mqtt.client.msg.MqttSubMsg;
import com.github.netty.mqtt.client.msg.MqttUnsubMsg;
import com.github.netty.mqtt.client.support.future.MqttFuture;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.mqtt.*;

import java.io.UnsupportedEncodingException;

/**
 * @Date: 2023/8/24 14:17
 * @Description: MQTT委托处理器接口
 * @author: xzc-coder
 */
public interface MqttDelegateHandler {

    /**
     * 接收一个TCP建立连接
     *
     * @param channel Channel
     */
    void channelConnect(Channel channel);

    /**
     * 发送一个MQTT连接
     *
     * @param channel Channel
     */
    void sendConnect(Channel channel);

    /**
     * 收到一个MQTT connack
     *
     * @param channel            Channel
     * @param mqttConnAckMessage MqttConnAckMessage
     */
    void connack(Channel channel, MqttConnAckMessage mqttConnAckMessage);

    /**
     * 收到一个MQTT auth
     *
     * @param channel         Channel
     * @param mqttAuthMessage MQTT认证消息
     */
    void auth(Channel channel, MqttMessage mqttAuthMessage);

    /**
     * 发送一个MQTT认证
     *
     * @param channel Channel
     */
    void sendAuth(Channel channel, byte reasonCode, MqttProperties mqttProperties);


    /**
     * 发送一个MQTT 断开连接
     *
     * @param channel           Channel
     * @param mqttFuture        Future
     * @param mqttDisconnectMsg 断开消息
     * @return Future
     */
    void sendDisconnect(Channel channel, MqttFuture mqttFuture, MqttDisconnectMsg mqttDisconnectMsg);

    /**
     * 接收到一个TCP断开连接
     *
     * @param channel     Channel
     * @param mqttMessage MQTT消息
     */
    void disconnect(Channel channel, MqttMessage mqttMessage);

    /**
     * 发送一个MQTT订阅
     *
     * @param channel    Channel
     * @param mqttSubMsg MQTT订阅消息
     */
    void sendSubscribe(Channel channel, MqttSubMsg mqttSubMsg);

    /**
     * 收到一个MQTT suback
     *
     * @param channel           Channel
     * @param mqttSubAckMessage MqttSubAckMessage
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
     * @param channel             Channel
     * @param mqttUnsubAckMessage MqttUnsubAckMessage
     */
    void unsuback(Channel channel, MqttUnsubAckMessage mqttUnsubAckMessage);

    /**
     * 发送一个MQTT pingreq
     *
     * @param channel Channel
     */
    void sendPingreq(Channel channel);

    /**
     * 接收到一个MQTT pingresp
     *
     * @param channel             Channel
     * @param mqttPingRespMessage MqttMessage
     */
    void pingresp(Channel channel, MqttMessage mqttPingRespMessage);

    /**
     * 发送一个MQTT publish
     *
     * @param channel   Channel
     * @param mqttMsg   MqttMsg
     * @param msgFuture MqttFuture(只有qos 为0 的消息需要)
     */
    void sendPublish(Channel channel, MqttMsg mqttMsg, MqttFuture msgFuture);

    /**
     * 接收到MQTT publish
     *
     * @param channel            Channel
     * @param mqttPublishMessage MqttPublishMessage
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
     * @param channel           Channel
     * @param mqttPubAckMessage MqttPubAckMessage
     */
    void puback(Channel channel, MqttPubAckMessage mqttPubAckMessage);

    /**
     * 发送一个MQTT pubrec
     *
     * @param channel Channel
     * @param mqttMsg MqttMsg
     */
    void sendPubrec(Channel channel, MqttMsg mqttMsg);

    /**
     * 接收到一个MQTT pubrec
     *
     * @param channel     Channel
     * @param mqttMessage MqttMessage
     */
    void pubrec(Channel channel, MqttMessage mqttMessage);

    /**
     * 发送一个MQTT pubrel
     *
     * @param channel Channel
     * @param mqttMsg MqttMsg
     */
    void sendPubrel(Channel channel, MqttMsg mqttMsg);

    /**
     * 接收一个MQTT pubrel
     *
     * @param channel     Channel
     * @param mqttMessage MqttMessage
     */
    void pubrel(Channel channel, MqttMessage mqttMessage);

    /**
     * 发送一个MQTT pubcomp
     *
     * @param channel Channel
     * @param mqttMsg MqttMsg
     */
    void sendPubcomp(Channel channel, MqttMsg mqttMsg);

    /**
     * 接收一个MQTT pubcomp
     *
     * @param channel     Channel
     * @param mqttMessage MqttMessage
     */
    void pubcomp(Channel channel, MqttMessage mqttMessage);

    /**
     * 接收到一个Channel异常
     *
     * @param channel
     * @param cause
     */
    void exceptionCaught(Channel channel, Throwable cause);

}
