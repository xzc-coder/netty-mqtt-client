package io.github.netty.mqtt.client.callback;


import io.github.netty.mqtt.client.constant.MqttVersion;
import io.github.netty.mqtt.client.support.util.AssertUtils;
import io.github.netty.mqtt.client.support.util.MqttUtils;
import io.netty.handler.codec.mqtt.MqttProperties;

import java.util.List;

/**
 * MQTT回调的结果基类
 * @author: xzc-coder
 */
public class MqttCallbackResult {


    /**
     * 客户端ID
     */
    protected final String clientId;

    /**
     * MQTT 版本
     */
    protected MqttVersion mqttVersion;

    /**
     * MQTT5
     * MQTT属性
     */
    protected MqttProperties mqttProperties;

    /**
     * 创建时间戳
     */
    protected final long createTimestamp;


    public MqttCallbackResult(String clientId) {
        AssertUtils.notNull(clientId, "clientId is null");
        this.mqttVersion = MqttVersion.MQTT_3_1_1;
        this.clientId = clientId;
        this.createTimestamp = System.currentTimeMillis();
    }

    public MqttCallbackResult(MqttVersion mqttVersion, String clientId) {
        AssertUtils.notNull(mqttVersion, "mqttVersion is null");
        AssertUtils.notNull(clientId, "clientId is null");
        this.mqttVersion = mqttVersion;
        this.clientId = clientId;
        this.createTimestamp = System.currentTimeMillis();
    }

    public long getCreateTimestamp() {
        return createTimestamp;
    }

    public String getClientId() {
        return clientId;
    }

    public MqttVersion getMqttVersion() {
        return mqttVersion;
    }

    public void setMqttVersion(MqttVersion mqttVersion) {
        this.mqttVersion = mqttVersion;
    }

    /**
     * 获取MQTT属性
     *
     * @param mqttPropertyType 属性类型
     * @return MQTT属性
     */
    public MqttProperties.MqttProperty getMqttProperty(MqttProperties.MqttPropertyType mqttPropertyType) {
        MqttProperties.MqttProperty mqttProperty = null;
        if (mqttProperties != null && mqttPropertyType != null) {
            mqttProperty = mqttProperties.getProperty(mqttPropertyType.value());
        }
        return mqttProperty;
    }

    /**
     * 获取MQTT属性集合
     *
     * @param mqttPropertyType 属性类型
     * @return MQTT属性集合
     */
    public List<MqttProperties.MqttProperty> getProperties(MqttProperties.MqttPropertyType mqttPropertyType) {
        List<MqttProperties.MqttProperty> mqttPropertyList = null;
        if (mqttProperties != null && mqttPropertyType != null) {
            mqttPropertyList = (List<MqttProperties.MqttProperty>) mqttProperties.getProperties(mqttPropertyType.value());
        }
        return mqttPropertyList;
    }

    /**
     * 获取MQTT用户属性
     *
     * @return MQTT用户属性
     */
    public MqttProperties.UserProperties getUserProperties() {
        MqttProperties.UserProperties userProperties = null;
        if (mqttProperties != null) {
            userProperties = (MqttProperties.UserProperties) mqttProperties.getProperty(MqttProperties.MqttPropertyType.USER_PROPERTY.value());
        }
        return userProperties;
    }

    /**
     * 获取MQTT用户属性中的某个Key的值
     *
     * @param key key
     * @return Key对应的值
     */
    public String getUserMqttPropertyValue(String key) {
        return MqttUtils.getUserMqttPropertyValue(mqttProperties, key);
    }

    /**
     * 获取MQTT用户属性中的某个Key的值集合
     *
     * @param key key
     * @return Key对应的值的集合
     */
    public List<String> getUserMqttPropertyValues(String key) {
        return MqttUtils.getUserMqttPropertyValues(mqttProperties, key);
    }

    /**
     * 获取MQTT用户属性中的某个Key的值
     *
     * @param key          key
     * @param defaultValue 获取不到时的默认值
     * @return Key对应的值
     */

    public String getUserMqttPropertyValue(String key, String defaultValue) {
        return MqttUtils.getUserMqttPropertyValue(mqttProperties, key, defaultValue);
    }

    /**
     * 获取MQTT属性中的二进制值
     *
     * @param mqttPropertyType MQTT属性类型
     * @return 二进制
     */
    public byte[] getBinaryMqttPropertyValue(MqttProperties.MqttPropertyType mqttPropertyType) {
        return MqttUtils.getBinaryMqttPropertyValue(mqttProperties, mqttPropertyType);
    }

    /**
     * 获取MQTT属性中的字符串值
     *
     * @param mqttPropertyType MQTT属性类型
     * @return 字符串
     */
    public String getStringMqttPropertyValue(MqttProperties.MqttPropertyType mqttPropertyType) {
        return MqttUtils.getStringMqttPropertyValue(mqttProperties, mqttPropertyType);
    }


    /**
     * 获取MQTT属性中的整型值
     *
     * @param mqttPropertyType MQTT属性类型
     * @return 整型
     */
    public Integer getIntegerMqttPropertyValue(MqttProperties.MqttPropertyType mqttPropertyType) {
        return MqttUtils.getIntegerMqttPropertyValue(mqttProperties, mqttPropertyType);
    }

    /**
     * 获取MQTT属性
     *
     * @return MQTT属性
     */
    public MqttProperties getMqttProperties() {
        return mqttProperties;
    }

    public void setMqttProperties(MqttProperties mqttProperties) {
        this.mqttProperties = mqttProperties;
    }


    @Override
    public String toString() {
        return "MqttCallbackResult{" +
                "clientId='" + clientId + '\'' +
                ", mqttVersion=" + mqttVersion +
                ", mqttProperties=" + mqttProperties +
                ", createTimestamp=" + createTimestamp +
                '}';
    }
}
