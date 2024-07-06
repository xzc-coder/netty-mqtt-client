package com.github.netty.mqtt.client.callback;

import com.github.netty.mqtt.client.constant.MqttAuthState;
import com.github.netty.mqtt.client.exception.MqttException;
import com.github.netty.mqtt.client.support.util.AssertUtils;
import io.netty.handler.codec.mqtt.MqttProperties;

/**
 * @Date: 2021/12/27 14:15
 * @Description: MQTT连接回调结果
 * @author: xzc-coder
 */
public class MqttConnectCallbackResult extends MqttCallbackResult {


    /**
     * MQTT认证状态
     */
    private final MqttAuthState mqttAuthState;
    /**
     * 连接异常
     */
    private final Throwable cause;

    /**
     * 连接异常原因码，成功则不返回
     */
    private final Byte connectReturnCode;

    /**
     * Broker 是否延续之前的会话，连接失败时为null
     */
    private Boolean sessionPresent;

    public MqttConnectCallbackResult(String clientId, MqttAuthState mqttAuthState) {
        this(clientId, mqttAuthState, null);
    }

    public MqttConnectCallbackResult(String clientId, MqttAuthState mqttAuthState, Boolean sessionPresent) {
        this(clientId, mqttAuthState, sessionPresent, null, null);
    }


    public MqttConnectCallbackResult(String clientId, MqttAuthState mqttAuthState, Throwable cause, Byte connectReturnCode) {
        this(clientId, mqttAuthState, null, cause, connectReturnCode);
    }

    public MqttConnectCallbackResult(String clientId, MqttAuthState mqttAuthState, Boolean sessionPresent, Throwable cause, Byte connectReturnCode) {
        super(clientId);
        AssertUtils.notNull(mqttAuthState, "mqttAuthState is null");
        this.mqttAuthState = mqttAuthState;
        this.sessionPresent = sessionPresent;
        this.cause = cause;
        this.connectReturnCode = connectReturnCode;
    }


    public MqttAuthState getMqttAuthState() {
        return mqttAuthState;
    }

    public Throwable getCause() {
        return cause;
    }

    public Byte getConnectReturnCode() {
        return connectReturnCode;
    }

    public Boolean getSessionPresent() {
        return sessionPresent;
    }

    public void setSessionPresent(Boolean sessionPresent) {
        this.sessionPresent = sessionPresent;
    }

    /**
     * MQTT5
     * 获取消息过期时间（秒）
     *
     * @return 消息过期时间
     */
    public Integer getSessionExpiryIntervalSeconds() {
        Integer sessionExpiryIntervalSeconds = null;
        if (mqttProperties != null) {
            sessionExpiryIntervalSeconds = getIntegerMqttPropertyValue(MqttProperties.MqttPropertyType.SESSION_EXPIRY_INTERVAL);
        }
        return sessionExpiryIntervalSeconds;
    }

    /**
     * MQTT5
     * 获取接受最大值
     *
     * @return 接受最大值
     */
    public Integer getReceiveMaximum() {
        Integer receiveMaximum = null;
        if (mqttProperties != null) {
            receiveMaximum = getIntegerMqttPropertyValue(MqttProperties.MqttPropertyType.RECEIVE_MAXIMUM);
        }
        return receiveMaximum;
    }

    /**
     * MQTT5
     * 获取最大服务质量
     *
     * @return 最大服务质量
     */
    public Integer getMaximumQos() {
        Integer maximumQos = null;
        if (mqttProperties != null) {
            maximumQos = getIntegerMqttPropertyValue(MqttProperties.MqttPropertyType.MAXIMUM_QOS);
        }
        return maximumQos;
    }

    /**
     * MQTT5
     * 获取保留可用标识符
     *
     * @return 保留可用标识符
     */
    public Integer getRetainAvailable() {
        Integer retainAvailable = null;
        if (mqttProperties != null) {
            retainAvailable = getIntegerMqttPropertyValue(MqttProperties.MqttPropertyType.RETAIN_AVAILABLE);
        }
        return retainAvailable;
    }

    /**
     * MQTT5
     * 获取最大报文长度
     *
     * @return 最大报文长度
     */
    public Integer getMaximumPacketSize() {
        Integer maximumPacketSize = null;
        if (mqttProperties != null) {
            maximumPacketSize = getIntegerMqttPropertyValue(MqttProperties.MqttPropertyType.MAXIMUM_PACKET_SIZE);
        }
        return maximumPacketSize;
    }

    /**
     * MQTT5
     * 获取分配的客户端标识符
     *
     * @return 客户端标识符
     */
    public String getAssignedClientIdentifier() {
        String assignedClientIdentifier = null;
        if (mqttProperties != null) {
            assignedClientIdentifier = getStringMqttPropertyValue(MqttProperties.MqttPropertyType.ASSIGNED_CLIENT_IDENTIFIER);
        }
        return assignedClientIdentifier;
    }

    /**
     * MQTT5
     * 获取主题别名最大值
     *
     * @return 主题别名最大值
     */
    public Integer getTopicAliasMaximum() {
        Integer topicAliasMaximum = null;
        if (mqttProperties != null) {
            topicAliasMaximum = getIntegerMqttPropertyValue(MqttProperties.MqttPropertyType.TOPIC_ALIAS_MAXIMUM);
        }
        return topicAliasMaximum;
    }

    /**
     * MQTT5
     * 获取原因字符串
     *
     * @return 原因字符串
     */
    public String getReasonString() {
        String reasonString = null;
        if (mqttProperties != null) {
            reasonString = getStringMqttPropertyValue(MqttProperties.MqttPropertyType.REASON_STRING);
        }
        return reasonString;
    }

    /**
     * MQTT5
     * 获取通配符订阅可用标识符
     *
     * @return 通配符订阅可用标识符
     */
    public Integer getWildcardSubscriptionAvailable() {
        Integer wildcardSubscriptionAvailable = null;
        if (mqttProperties != null) {
            wildcardSubscriptionAvailable = getIntegerMqttPropertyValue(MqttProperties.MqttPropertyType.WILDCARD_SUBSCRIPTION_AVAILABLE);
        }
        return wildcardSubscriptionAvailable;
    }

    /**
     * MQTT5
     * 获取订阅标识符可用标识符
     *
     * @return 订阅标识符可用标识符
     */
    public Integer getSubscriptionIdentifierAvailable() {
        Integer subscriptionIdentifierAvailable = null;
        if (mqttProperties != null) {
            subscriptionIdentifierAvailable = getIntegerMqttPropertyValue(MqttProperties.MqttPropertyType.SUBSCRIPTION_IDENTIFIER_AVAILABLE);
        }
        return subscriptionIdentifierAvailable;
    }

    /**
     * MQTT5
     * 获取共享订阅可用标识符
     *
     * @return 共享订阅可用标识符
     */
    public Integer getSharedSubscriptionAvailable() {
        Integer sharedSubscriptionAvailable = null;
        if (mqttProperties != null) {
            sharedSubscriptionAvailable = getIntegerMqttPropertyValue(MqttProperties.MqttPropertyType.SHARED_SUBSCRIPTION_AVAILABLE);
        }
        return sharedSubscriptionAvailable;
    }

    /**
     * MQTT5
     * 获取服务端保持连接时间间隔（秒）
     *
     * @return 服务端保持连接时间间隔（秒）
     */
    public Integer getServerKeepAliveSeconds() {
        Integer serverKeepAlive = null;
        if (mqttProperties != null) {
            serverKeepAlive = getIntegerMqttPropertyValue(MqttProperties.MqttPropertyType.SERVER_KEEP_ALIVE);
        }
        return serverKeepAlive;
    }

    /**
     * MQTT5
     * 获取响应消息标识符
     *
     * @return 响应消息标识符
     */
    public String getResponseInformation() {
        String responseInformation = null;
        if (mqttProperties != null) {
            responseInformation = getStringMqttPropertyValue(MqttProperties.MqttPropertyType.RESPONSE_INFORMATION);
        }
        return responseInformation;
    }

    /**
     * MQTT5
     * 获取服务端参考
     *
     * @return 服务端参考
     */
    public String getServerReference() {
        String serverReference = null;
        if (mqttProperties != null) {
            serverReference = getStringMqttPropertyValue(MqttProperties.MqttPropertyType.SERVER_REFERENCE);
        }
        return serverReference;
    }

    /**
     * MQTT5
     * 获取认证方法
     *
     * @return 认证方法
     */
    public String getAuthenticationMethod() {
        String authenticationMethod = null;
        if (mqttProperties != null) {
            authenticationMethod = getStringMqttPropertyValue(MqttProperties.MqttPropertyType.AUTHENTICATION_METHOD);
        }
        return authenticationMethod;
    }

    /**
     * MQTT5
     * 获取认证数据
     *
     * @return 认证数据
     */
    public byte[] getAuthenticationData() {
        byte[] authenticationData = null;
        if (mqttProperties != null) {
            authenticationData = getBinaryMqttPropertyValue(MqttProperties.MqttPropertyType.AUTHENTICATION_DATA);
        }
        return authenticationData;
    }


    @Override
    public String toString() {
        return "MqttConnectCallbackResult{" +
                "mqttAuthState=" + mqttAuthState +
                ", cause=" + cause +
                ", connectReturnCode=" + connectReturnCode +
                ", sessionPresent=" + sessionPresent +
                "} " + super.toString();
    }
}
