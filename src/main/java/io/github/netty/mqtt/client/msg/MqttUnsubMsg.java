package io.github.netty.mqtt.client.msg;

import io.github.netty.mqtt.client.support.util.AssertUtils;
import io.netty.handler.codec.mqtt.MqttProperties;

import java.util.ArrayList;
import java.util.List;


/**
 * MQTT取消订阅消息
 * @author: xzc-coder
 */
public class MqttUnsubMsg {

    /**
     * 消息ID
     */
    private final int msgId;
    /**
     * 取消订阅列表
     */
    private final List<String> topicList = new ArrayList<>();

    /**
     * MQTT 5.0.0参数
     * 用户属性
     */
    private final MqttProperties.UserProperties mqttUserProperties = new MqttProperties.UserProperties();


    public MqttUnsubMsg(int msgId, List<String> topicList) {
        this(msgId,topicList,null);
    }

    public MqttUnsubMsg(int msgId, String topic) {
        AssertUtils.notNull(topic, "topic is null");
        this.msgId = msgId;
        topicList.add(topic);
    }



    public MqttUnsubMsg(int msgId, List<String> topicList,MqttProperties.UserProperties mqttUserProperties) {
        AssertUtils.notEmpty(topicList, "topicList is empty");
        this.msgId = msgId;
        this.topicList.addAll(topicList);
        this.addMqttUserProperties(mqttUserProperties);
    }

    public List<String> getTopicList() {
        return topicList;
    }

    public int getMsgId() {
        return msgId;
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

    public MqttProperties.UserProperties getMqttUserProperties() {
        return mqttUserProperties;
    }

    @Override
    public String toString() {
        return "MqttUnsubMsg{" +
                "msgId=" + msgId +
                ", topicList=" + topicList +
                ", mqttUserProperties=" + mqttUserProperties +
                '}';
    }
}
