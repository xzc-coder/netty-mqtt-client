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

    public MqttConnectCallbackResult(String clientId, MqttAuthState mqttAuthState) {
        this(clientId,mqttAuthState,null);
    }

    public MqttConnectCallbackResult(String clientId, MqttAuthState mqttAuthState, Throwable cause) {
        super(clientId);
        AssertUtils.notNull(mqttAuthState,"mqttAuthState is null");
        this.mqttAuthState = mqttAuthState;
        this.cause = cause;
    }

    public MqttAuthState getMqttAuthState() {
        return mqttAuthState;
    }

    public Throwable getCause() {
        return cause;
    }


    @Override
    public String toString() {
        return "MqttConnectCallbackResult{" +
                "mqttAuthState=" + mqttAuthState +
                ", cause=" + cause +
                "} " + super.toString();
    }
}
