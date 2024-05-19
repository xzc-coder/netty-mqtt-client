package com.github.netty.mqtt.client.callback;

import io.netty.handler.codec.mqtt.MqttQoS;

/**
 * @Date: 2022/1/6 15:44
 * @Description: mqtt订阅回调结果信息
 * @author: xzc-coder
 */
public class MqttSubscribeCallbackInfo {

    /**
     * 服务器返回的qos
     */
    private MqttQoS serverQos;
    /**
     * 该条主题是否订阅成功
     */
    private boolean isSubscribed;
    /**
     * 订阅时的qos
     */
    private MqttQoS subscribeQos;
    /**
     * 订阅时的主题
     */
    private String subscribeTopic;

    public MqttQoS getServerQos() {
        return serverQos;
    }

    public void setServerQos(MqttQoS serverQos) {
        this.serverQos = serverQos;
    }

    public boolean isSubscribed() {
        return isSubscribed;
    }

    public void setSubscribed(boolean subscribed) {
        isSubscribed = subscribed;
    }

    public MqttQoS getSubscribeQos() {
        return subscribeQos;
    }

    public void setSubscribeQos(MqttQoS subscribeQos) {
        this.subscribeQos = subscribeQos;
    }

    public String getSubscribeTopic() {
        return subscribeTopic;
    }

    public void setSubscribeTopic(String subscribeTopic) {
        this.subscribeTopic = subscribeTopic;
    }

    @Override
    public String toString() {
        return "MqttSubscribeCallbackInfo{" +
                "serverQos=" + serverQos +
                ", isSubscribed=" + isSubscribed +
                ", subscribeQos=" + subscribeQos +
                ", subscribeTopic='" + subscribeTopic + '\'' +
                '}';
    }
}
