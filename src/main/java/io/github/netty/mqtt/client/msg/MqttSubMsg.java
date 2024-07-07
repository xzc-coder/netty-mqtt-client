package io.github.netty.mqtt.client.msg;


import io.github.netty.mqtt.client.support.util.AssertUtils;
import io.netty.handler.codec.mqtt.MqttProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * MQTT订阅消息
 * @author: xzc-coder
 */
public class MqttSubMsg {

    /**
     * 消息ID
     */
    private final int msgId;
    /**
     * 订阅的主题列表
     */
    private final List<MqttSubInfo> mqttSubInfoList = new ArrayList<>();

    /**
     * MQTT5
     * 订阅标识符
     */
    private Integer subscriptionIdentifier;

    /**
     * MQTT5
     * 用户属性
     */
    private final MqttProperties.UserProperties mqttUserProperties = new MqttProperties.UserProperties();


    public MqttSubMsg(int msgId, List<MqttSubInfo> mqttSubInfoList) {
        AssertUtils.notEmpty(mqttSubInfoList, "mqttSubInfoList is empty");
        this.msgId = msgId;
        this.mqttSubInfoList.addAll(mqttSubInfoList);
    }

    public MqttSubMsg(int msgId, List<MqttSubInfo> mqttSubInfoList, Integer subscriptionIdentifier, MqttProperties.UserProperties mqttUserProperties) {
        AssertUtils.notEmpty(mqttSubInfoList, "mqttSubInfoList is empty");
        this.msgId = msgId;
        this.mqttSubInfoList.addAll(mqttSubInfoList);
        this.setSubscriptionIdentifier(subscriptionIdentifier);
        this.addMqttUserProperties(mqttUserProperties);
    }

    public MqttSubMsg(int msgId, MqttSubInfo mqttSubInfo) {
        AssertUtils.notNull(mqttSubInfo, "mqttSubInfo is null");
        this.msgId = msgId;
        mqttSubInfoList.add(mqttSubInfo);
    }

    public MqttSubMsg(int msgId, MqttSubInfo mqttSubInfo, Integer subscriptionIdentifier, MqttProperties.UserProperties mqttUserProperties) {
        AssertUtils.notNull(mqttSubInfo, "mqttSubInfo is null");
        this.msgId = msgId;
        this.mqttSubInfoList.add(mqttSubInfo);
        this.setSubscriptionIdentifier(subscriptionIdentifier);
        this.addMqttUserProperties(mqttUserProperties);
    }

    public List<MqttSubInfo> getMqttSubInfoList() {
        return mqttSubInfoList;
    }

    public int getMsgId() {
        return msgId;
    }

    public MqttProperties.UserProperties getMqttUserProperties() {
        return mqttUserProperties;
    }

    /**
     * MQTT5
     * 添加一个MQTT用户属性
     * @param key key
     * @param value value
     */
    public void addMqttUserProperty(String key, String value) {
        if (key != null && value != null) {
            mqttUserProperties.add(key, value);
        }
    }

    /**
     * MQTT5
     * 添加一个MQTT用户属性
     * @param stringPair key value对象
     */
    public void addMqttUserProperty(MqttProperties.StringPair stringPair) {
        if (stringPair != null) {
            mqttUserProperties.add(stringPair);
        }
    }

    /**
     * MQTT5
     * 添加一个MQTT用户属性
     * @param mqttUserProperties MQTT用户属性
     */
    private void addMqttUserProperties(MqttProperties.UserProperties mqttUserProperties) {
        if (mqttUserProperties != null) {
            for (MqttProperties.StringPair stringPair : mqttUserProperties.value()) {
                this.mqttUserProperties.add(stringPair);
            }
        }
    }

    public Integer getSubscriptionIdentifier() {
        return subscriptionIdentifier;
    }

    public void setSubscriptionIdentifier(Integer subscriptionIdentifier) {
        this.subscriptionIdentifier = subscriptionIdentifier;
    }


    @Override
    public String toString() {
        return "MqttSubMsg{" +
                "msgId=" + msgId +
                ", mqttSubInfoList=" + mqttSubInfoList +
                ", subscriptionIdentifier=" + subscriptionIdentifier +
                ", mqttUserProperties=" + mqttUserProperties +
                '}';
    }
}
