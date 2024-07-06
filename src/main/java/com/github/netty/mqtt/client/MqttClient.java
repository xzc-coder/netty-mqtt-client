package com.github.netty.mqtt.client;


import com.github.netty.mqtt.client.callback.MqttCallback;
import com.github.netty.mqtt.client.msg.MqttDisconnectMsg;
import com.github.netty.mqtt.client.msg.MqttMsg;
import com.github.netty.mqtt.client.msg.MqttMsgInfo;
import com.github.netty.mqtt.client.msg.MqttSubInfo;
import com.github.netty.mqtt.client.support.future.MqttFuture;
import com.github.netty.mqtt.client.support.future.MqttFutureListener;
import com.github.netty.mqtt.client.support.future.MqttFutureWrapper;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttProperties;
import io.netty.handler.codec.mqtt.MqttQoS;

import java.util.Collection;
import java.util.List;

/**
 * @Date: 2022/12/30 14:44
 * @Description: MQTT客户端接口
 * @author: xzc-coder
 */
public interface MqttClient extends Endpoint {

    /**
     * 获取客户端ID
     *
     * @return 客户端ID
     */
    String getClientId();

    /**
     * 获取MQTT的连接参数
     *
     * @return MQTT的连接参数
     */
    MqttConnectParameter getMqttConnectParameter();

    /**
     * 进行连接，不会阻塞
     *
     * @return MqttFutureWrapper
     */
    MqttFutureWrapper connectFuture();

    /**
     * 进行连接，会阻塞至超时或者连接成功
     */
    void connect();


    /**
     * 断开连接，会阻塞至TCP断开
     */
    void disconnect();


    /**
     * 断开连接
     *
     * @return Future
     */
    MqttFutureWrapper disconnectFuture();


    /**
     * 断开连接
     *
     * @param mqttDisconnectMsg 断开消息
     * @return Future
     */
    MqttFutureWrapper disconnectFuture(MqttDisconnectMsg mqttDisconnectMsg);


    /**
     * 断开连接
     *
     * @param mqttDisconnectMsg 断开消息
     */
    void disconnect(MqttDisconnectMsg mqttDisconnectMsg);

    /**
     * 发送一个消息，不会阻塞（MQTT 5 带额外属性使用）
     *
     * @param mqttMsgInfo mqtt消息
     * @return MqttFutureWrapper
     */
    MqttFutureWrapper publishFuture(MqttMsgInfo mqttMsgInfo);


    /**
     * 发送一个消息，不会阻塞
     *
     * @param payload 载荷
     * @param topic   主题
     * @param qos     服务质量
     * @param retain  是否保留消息
     * @return MqttFutureWrapper
     */
    MqttFutureWrapper publishFuture(byte[] payload, String topic, MqttQoS qos, boolean retain);

    /**
     * 发送一个消息，不会阻塞，retain 为 false
     *
     * @param payload 载荷
     * @param topic   主题
     * @param qos     服务质量
     * @return MqttFutureWrapper
     */
    MqttFutureWrapper publishFuture(byte[] payload, String topic, MqttQoS qos);

    /**
     * 发送一个消息，不会阻塞，retain 为 false，qos 为 0
     *
     * @param payload 载荷
     * @param topic   主题
     * @return MqttFutureWrapper
     */
    MqttFutureWrapper publishFuture(byte[] payload, String topic);


    /**
     * 发送一个消息，会阻塞至发送完成（MQTT 5 带额外属性使用）
     *
     * @param mqttMsgInfo mqtt消息
     */
    void publish(MqttMsgInfo mqttMsgInfo);

    /**
     * 发送一个消息，会阻塞至发送完成
     *
     * @param payload 载荷
     * @param topic   主题
     * @param qos     服务质量
     * @param retain  是否保留消息
     */
    void publish(byte[] payload, String topic, MqttQoS qos, boolean retain);

    /**
     * 发送一个消息，会阻塞至发送完成，retain 为 false
     *
     * @param payload 载荷
     * @param topic   主题
     * @param qos     服务质量
     */
    void publish(byte[] payload, String topic, MqttQoS qos);

    /**
     * 发送一个消息，会阻塞至发送完成,retain 为 false，qos 为 0
     *
     * @param payload 载荷
     * @param topic   主题
     */
    void publish(byte[] payload, String topic);


    /**
     * 发送一个订阅消息，会阻塞至发送完成
     *
     * @param topic 订阅的主题
     * @param qos   订阅的qos
     */
    void subscribe(String topic, MqttQoS qos);


    /**
     * 发送一个订阅消息，会阻塞至发送完成（MQTT 5）
     *
     * @param mqttSubInfo 订阅消息
     */
    void subscribe(MqttSubInfo mqttSubInfo);

    /**
     * 发送一个订阅消息，会阻塞至发送完成（MQTT 5）
     *
     * @param mqttSubInfo            订阅消息
     * @param subscriptionIdentifier 订阅标识符
     * @param mqttUserProperties     用户属性
     */
    void subscribe(MqttSubInfo mqttSubInfo, Integer subscriptionIdentifier, MqttProperties.UserProperties mqttUserProperties);

    /**
     * 发送一个订阅消息，会阻塞至发送完成
     *
     * @param mqttSubInfoList 订阅消息集合
     */
    void subscribes(List<MqttSubInfo> mqttSubInfoList);


    /**
     * 发送一个订阅消息，会阻塞至发送完成（MQTT 5）
     *
     * @param mqttSubInfoList        订阅消息集合
     * @param subscriptionIdentifier 订阅标识符
     * @param mqttUserProperties     用户属性
     */
    void subscribes(List<MqttSubInfo> mqttSubInfoList, Integer subscriptionIdentifier, MqttProperties.UserProperties mqttUserProperties);

    /**
     * 发送一个订阅消息，会阻塞至发送完成
     *
     * @param topicList 订阅主题集合
     * @param qos       订阅的qos
     */
    void subscribes(List<String> topicList, MqttQoS qos);


    /**
     * 发送一个订阅消息，不会阻塞
     *
     * @param topicList 订阅主题集合
     * @param qos       订阅的qos
     * @return MqttFutureWrapper
     */
    MqttFutureWrapper subscribesFuture(List<String> topicList, MqttQoS qos);

    /**
     * 发送一个订阅消息，不会阻塞
     *
     * @param topic 订阅的主题
     * @param qos   订阅的qos
     * @return MqttFutureWrapper
     */
    MqttFutureWrapper subscribeFuture(String topic, MqttQoS qos);

    /**
     * 发送一个订阅消息，不会阻塞（MQTT 5）
     *
     * @param mqttSubInfo 订阅消息
     * @return MqttFutureWrapper
     */
    MqttFutureWrapper subscribeFuture(MqttSubInfo mqttSubInfo);


    /**
     * 发送一个订阅消息，不会阻塞（MQTT 5）
     *
     * @param mqttSubInfo            订阅消息
     * @param subscriptionIdentifier 订阅标识符
     * @param mqttUserProperties     订阅用户属性
     * @return MqttFutureWrapper
     */
    MqttFutureWrapper subscribeFuture(MqttSubInfo mqttSubInfo, Integer subscriptionIdentifier, MqttProperties.UserProperties mqttUserProperties);

    /**
     * 发送一个订阅消息，不会阻塞
     *
     * @param mqttSubInfoList        订阅消息集合（MQTT 5）
     * @param subscriptionIdentifier 订阅标识符
     * @param mqttUserProperties     用户属性
     * @return MqttFutureWrapper
     */
    MqttFutureWrapper subscribesFuture(List<MqttSubInfo> mqttSubInfoList, Integer subscriptionIdentifier, MqttProperties.UserProperties mqttUserProperties);

    /**
     * 发送一个订阅消息，不会阻塞
     *
     * @param mqttSubInfoList 订阅集合
     * @return MqttFutureWrapper
     */
    MqttFutureWrapper subscribesFuture(List<MqttSubInfo> mqttSubInfoList);

    /**
     * 取消订阅，会阻塞至消息发送完成
     *
     * @param topicList          取消订阅的主题集合
     * @param mqttUserProperties 用户属性
     */
    void unsubscribes(List<String> topicList, MqttProperties.UserProperties mqttUserProperties);

    /**
     * 取消订阅，会阻塞至消息发送完成
     *
     * @param topicList 取消订阅的主题集合
     */
    void unsubscribes(List<String> topicList);

    /**
     * 取消订阅，会阻塞至消息发送完成
     *
     * @param topic              取消订阅的主题
     * @param mqttUserProperties 用户属性
     */
    void unsubscribe(String topic, MqttProperties.UserProperties mqttUserProperties);

    /**
     * 取消订阅，会阻塞至消息发送完成
     *
     * @param topic 取消订阅的主题
     */
    void unsubscribe(String topic);

    /**
     * 取消订阅，不会阻塞
     *
     * @param topic              取消订阅的主题
     * @param mqttUserProperties 用户属性
     * @return MqttFutureWrapper
     */
    MqttFutureWrapper unsubscribeFuture(String topic, MqttProperties.UserProperties mqttUserProperties);

    /**
     * 取消订阅，不会阻塞
     *
     * @param topic 取消订阅的主题
     * @return MqttFutureWrapper
     */
    MqttFutureWrapper unsubscribeFuture(String topic);


    /**
     * 取消订阅，不会阻塞
     *
     * @param topicList 取消订阅的主题集合
     * @return MqttFutureWrapper
     */
    MqttFutureWrapper unsubscribesFuture(List<String> topicList);

    /**
     * 取消订阅，不会阻塞
     *
     * @param topicList          取消订阅的主题集合
     * @param mqttUserProperties 用户属性
     * @return MqttFutureWrapper
     */
    MqttFutureWrapper unsubscribesFuture(List<String> topicList, MqttProperties.UserProperties mqttUserProperties);


    /**
     * 添加一个MQTT回调器
     *
     * @param mqttCallback 回调器
     */
    void addMqttCallback(MqttCallback mqttCallback);

    /**
     * 添加MQTT回调器集合
     *
     * @param mqttCallbacks 回调器集合
     */
    void addMqttCallbacks(Collection<MqttCallback> mqttCallbacks);

    /**
     * 客户端是否在线（完成认证的才算在线）
     *
     * @return 是否在线
     */
    boolean isOnline();

    /**
     * 客户端是否活跃（指TCP连接是否是ESTABLISHED状态）
     *
     * @return 是否活跃
     */
    boolean isActive();

    /**
     * 客户端是否关闭
     *
     * @return 是否关闭
     */
    boolean isClose();

    /**
     * 关闭客户端，关闭后，无法再进行连接、发送消息、订阅、取消订阅等操作
     */
    void close();

}
