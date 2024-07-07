package io.github.netty.mqtt.client.msg;

import io.github.netty.mqtt.client.support.util.AssertUtils;
import io.netty.handler.codec.mqtt.MqttProperties;
import io.netty.handler.codec.mqtt.MqttQoS;

import java.util.Arrays;

/**
 * MQTT消息信息（主要用于MQTT 5）
 * @author: xzc-coder
 */
public class MqttMsgInfo {

    /**
     * 主题
     */
    private final String topic;
    /**
     * qos
     */
    private final MqttQoS qos;
    /**
     * 是否保留消息
     */
    private final boolean retain;

    /**
     * 载荷
     */
    private final byte[] payload;

    /**
     * MQTT5
     * 载荷格式指示标识符
     */
    private Integer payloadFormatIndicator;

    /**
     * MQTT5
     * 消息过期间隔 单位 秒
     */
    private Integer messageExpiryIntervalSeconds;

    /**
     * MQTT5
     * 主题别名，注意；某些Broker不支持使用别名时并发的发送消息（因为并发时，可能还未完成映射后面的没有主题的消息就到了，这样会导致错误）
     */
    private Integer topicAlias;

    /**
     * MQTT5
     * 响应主题
     */
    private String responseTopic;

    /**
     * MQTT5
     * 对比数据
     */
    private byte[] correlationData;

    /**
     * MQTT5
     * 订阅标识符
     */
    private Integer subscriptionIdentifier;

    /**
     * MQTT5
     * 内容类型
     */
    private String contentType;

    /**
     * MQTT5
     * 用户属性
     */
    private final MqttProperties.UserProperties mqttUserProperties = new MqttProperties.UserProperties();


    public MqttMsgInfo(String topic, byte[] payload) {
        this(topic, payload, MqttQoS.AT_MOST_ONCE, false);
    }


    public MqttMsgInfo(String topic, byte[] payload, MqttQoS qos) {
        this(topic, payload, qos, false);
    }


    public MqttMsgInfo(String topic, byte[] payload, MqttQoS qos, boolean retain) {
        AssertUtils.notNull(topic, "topic is null");
        AssertUtils.notNull(payload, "payload is null");
        AssertUtils.notNull(qos, "qos is null");
        this.topic = topic;
        this.payload = payload;
        this.qos = qos;
        this.retain = retain;
    }

    public String getTopic() {
        return topic;
    }

    public MqttQoS getQos() {
        return qos;
    }

    public boolean isRetain() {
        return retain;
    }

    public byte[] getPayload() {
        return payload;
    }

    public Integer getPayloadFormatIndicator() {
        return payloadFormatIndicator;
    }

    public void setPayloadFormatIndicator(Integer payloadFormatIndicator) {
        this.payloadFormatIndicator = payloadFormatIndicator;
    }

    public Integer getMessageExpiryIntervalSeconds() {
        return messageExpiryIntervalSeconds;
    }

    public void setMessageExpiryIntervalSeconds(int messageExpiryIntervalSeconds) {
        if (messageExpiryIntervalSeconds > 0) {
            this.messageExpiryIntervalSeconds = messageExpiryIntervalSeconds;
        }
    }

    public Integer getTopicAlias() {
        return topicAlias;
    }

    public void setTopicAlias(int topicAlias) {
        if (topicAlias > 0) {
            this.topicAlias = topicAlias;
        }
    }

    public String getResponseTopic() {
        return responseTopic;
    }

    public void setResponseTopic(String responseTopic) {
        this.responseTopic = responseTopic;
    }

    public byte[] getCorrelationData() {
        return correlationData;
    }

    public void setCorrelationData(byte[] correlationData) {
        this.correlationData = correlationData;
    }

    public Integer getSubscriptionIdentifier() {
        return subscriptionIdentifier;
    }

    public void setSubscriptionIdentifier(Integer subscriptionIdentifier) {
        this.subscriptionIdentifier = subscriptionIdentifier;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public MqttProperties.UserProperties getMqttUserProperties() {
        return mqttUserProperties;
    }

    public void addMqttUserProperty(String key, String value) {
        if (key != null && value != null) {
            mqttUserProperties.add(key, value);
        }
    }

    public void addMqttUserProperty(MqttProperties.StringPair stringPair) {
        if (stringPair != null) {
            mqttUserProperties.add(stringPair);
        }
    }

    @Override
    public String toString() {
        return "MqttMsgInfo{" +
                "topic='" + topic + '\'' +
                ", qos=" + qos +
                ", retain=" + retain +
                ", payload=" + Arrays.toString(payload) +
                ", payloadFormatIndicator=" + payloadFormatIndicator +
                ", messageExpiryIntervalSeconds=" + messageExpiryIntervalSeconds +
                ", topicAlias=" + topicAlias +
                ", responseTopic='" + responseTopic + '\'' +
                ", correlationData=" + Arrays.toString(correlationData) +
                ", subscriptionIdentifier=" + subscriptionIdentifier +
                ", contentType='" + contentType + '\'' +
                ", mqttUserProperties=" + mqttUserProperties +
                '}';
    }
}
