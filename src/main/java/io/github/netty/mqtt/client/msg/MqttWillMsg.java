package io.github.netty.mqtt.client.msg;


import io.github.netty.mqtt.client.support.util.AssertUtils;
import io.github.netty.mqtt.client.support.util.MqttUtils;
import io.netty.handler.codec.mqtt.MqttProperties;
import io.netty.handler.codec.mqtt.MqttQoS;

import java.util.Arrays;

/**
 * MQTT遗嘱消息
 * @author: xzc-coder
 */
public class MqttWillMsg {

    /**
     * 遗嘱主题
     */
    private final String willTopic;
    /**
     * 遗嘱消息
     */
    private final byte[] willMessageBytes;
    /**
     * 遗嘱Qos
     */
    private final MqttQoS willQos;
    /**
     * 遗嘱消息是否保留
     */
    private final boolean isWillRetain;

    /**
     * MQTT5
     * 遗嘱延时间隔 单位 秒
     */
    private Integer willDelayIntervalSeconds;

    /**
     * MQTT5
     * 载荷格式指示
     */
    private Integer payloadFormatIndicator;

    /**
     * MQTT5
     * 消息过期间隔（秒）
     */
    private Integer messageExpiryIntervalSeconds;

    /**
     * MQTT5
     * 内容类型
     */
    private String contentType;

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
     * 用户属性
     */
    private final MqttProperties.UserProperties mqttUserProperties = new MqttProperties.UserProperties();


    public MqttWillMsg(String willTopic, byte[] willMessageBytes, MqttQoS willQos) {
        this(willTopic, willMessageBytes, willQos, false);
    }

    public MqttWillMsg(String willTopic, byte[] willMessageBytes, MqttQoS willQos, boolean isWillRetain) {
        AssertUtils.notNull(willTopic, "willTopic is null");
        AssertUtils.notNull(willMessageBytes, "willMessageBytes is null");
        AssertUtils.notNull(willQos, "willQos is null");
        MqttUtils.topicCheck(willTopic,false);
        this.willTopic = willTopic;
        this.willMessageBytes = willMessageBytes;
        this.willQos = willQos;
        this.isWillRetain = isWillRetain;
    }


    public String getWillTopic() {
        return willTopic;
    }

    public byte[] getWillMessageBytes() {
        return willMessageBytes;
    }

    public MqttQoS getWillQos() {
        return willQos;
    }

    public boolean isWillRetain() {
        return isWillRetain;
    }

    public Integer getWillDelayIntervalSeconds() {
        return willDelayIntervalSeconds;
    }

    public void setWillDelayIntervalSeconds(Integer willDelayIntervalSeconds) {
        this.willDelayIntervalSeconds = willDelayIntervalSeconds;
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

    public void setMessageExpiryIntervalSeconds(Integer messageExpiryIntervalSeconds) {
        this.messageExpiryIntervalSeconds = messageExpiryIntervalSeconds;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
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


    @Override
    public String toString() {
        return "MqttWillMsg{" +
                "willTopic='" + willTopic + '\'' +
                ", willMessageBytes=" + Arrays.toString(willMessageBytes) +
                ", willQos=" + willQos +
                ", isWillRetain=" + isWillRetain +
                ", willDelayIntervalSeconds=" + willDelayIntervalSeconds +
                ", payloadFormatIndicator=" + payloadFormatIndicator +
                ", messageExpiryIntervalSeconds=" + messageExpiryIntervalSeconds +
                ", contentType='" + contentType + '\'' +
                ", responseTopic='" + responseTopic + '\'' +
                ", correlationData=" + Arrays.toString(correlationData) +
                ", mqttUserProperties=" + mqttUserProperties +
                '}';
    }
}
