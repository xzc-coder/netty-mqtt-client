package com.github.netty.mqtt.client.msg;

import com.github.netty.mqtt.client.support.util.AssertUtils;
import io.netty.handler.codec.mqtt.MqttQoS;

/**
 * @Date: 2023/8/24 14:19
 * @Description: MQTT单个订阅信息
 * @author: xzc-coder
 */
public class MqttSubInfo {

    /**
     * 主题
     */
    private final String topic;
    /**
     * qos
     */
    private final MqttQoS qos;

    public MqttSubInfo(String topic, MqttQoS qos) {
        AssertUtils.notNull(topic, "topic is null");
        AssertUtils.notNull(qos, "qos is null");
        this.topic = topic;
        this.qos = qos;
    }

    public String getTopic() {
        return topic;
    }


    public MqttQoS getQos() {
        return qos;
    }
}
