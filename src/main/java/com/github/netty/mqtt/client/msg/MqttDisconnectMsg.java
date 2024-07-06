package com.github.netty.mqtt.client.msg;

import com.github.netty.mqtt.client.constant.MqttConstant;
import io.netty.handler.codec.mqtt.MqttProperties;

/**
 * MQTT5
 */
public class MqttDisconnectMsg {


    /**
     * MQTT5
     * 原因码
     */
    private byte reasonCode = MqttConstant.DISCONNECT_SUCCESS_REASON_CODE;

    /**
     * MQTT5
     * 会话过期间隔(秒)
     */
    private Integer sessionExpiryIntervalSeconds;

    /**
     * MQTT5
     * 原因字符串
     */
    private String reasonString;


    /**
     * MQTT5
     * MQTT用户属性
     */
    private final MqttProperties.UserProperties mqttUserProperties = new MqttProperties.UserProperties();


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

    public byte getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(byte reasonCode) {
        this.reasonCode = reasonCode;
    }

    public Integer getSessionExpiryIntervalSeconds() {
        return sessionExpiryIntervalSeconds;
    }

    public void setSessionExpiryIntervalSeconds(Integer sessionExpiryIntervalSeconds) {
        this.sessionExpiryIntervalSeconds = sessionExpiryIntervalSeconds;
    }

    public String getReasonString() {
        return reasonString;
    }

    public void setReasonString(String reasonString) {
        this.reasonString = reasonString;
    }


    @Override
    public String toString() {
        return "MqttDisconnectMsg{" +
                "reasonCode=" + reasonCode +
                ", sessionExpiryIntervalSeconds=" + sessionExpiryIntervalSeconds +
                ", reasonString='" + reasonString + '\'' +
                ", mqttUserProperties=" + mqttUserProperties +
                '}';
    }
}
