package com.github.netty.mqtt.client.callback;

import com.github.netty.mqtt.client.constant.MqttAuthState;

/**
 * @Date: 2023/8/29 09:38
 * @Description: MQTT异常回调结果
 * @author: xzc-coder
 */
public class MqttChannelExceptionCallbackResult extends MqttCallbackResult {

    /**
     * MQTT认证状态
     */
    private final MqttAuthState authState;
    /**
     * 异常原因
     */
    private final Throwable cause;

    public MqttChannelExceptionCallbackResult(String clientId,MqttAuthState authState,Throwable cause) {
        super(clientId);
        this.authState = authState;
        this.cause = cause;
    }


    public MqttAuthState getAuthState() {
        return authState;
    }

    public Throwable getCause() {
        return cause;
    }

    @Override
    public String toString() {
        return "MqttChannelExceptionCallbackResult{" +
                "authState=" + authState +
                ", cause=" + cause +
                "} " + super.toString();
    }
}
