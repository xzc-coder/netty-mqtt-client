package com.github.netty.mqtt.client.callback;

import com.github.netty.mqtt.client.constant.MqttAuthState;
import com.github.netty.mqtt.client.exception.MqttException;
import com.github.netty.mqtt.client.support.util.AssertUtils;

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
     * 连接异常原因码
     */
    private final Byte connectReturnCode;

    /**
     * Broker 是否延续之前的会话
     */
    private final Boolean sessionPresent;

    public MqttConnectCallbackResult(String clientId, MqttAuthState mqttAuthState) {
        this(clientId,mqttAuthState,null);
    }

    public MqttConnectCallbackResult(String clientId, MqttAuthState mqttAuthState,Boolean sessionPresent) {
        this(clientId,mqttAuthState,sessionPresent,null,null);
    }

    public MqttConnectCallbackResult(String clientId, MqttAuthState mqttAuthState, Throwable cause,Byte connectReturnCode) {
        this(clientId,mqttAuthState,null,cause,connectReturnCode);
    }
    public MqttConnectCallbackResult(String clientId, MqttAuthState mqttAuthState,Boolean sessionPresent,Throwable cause,Byte connectReturnCode) {
        super(clientId);
        AssertUtils.notNull(mqttAuthState,"mqttAuthState is null");
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
