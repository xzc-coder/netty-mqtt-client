package io.github.netty.mqtt.client.callback;

import io.github.netty.mqtt.client.constant.MqttAuthState;

/**
 * MQTT异常回调结果
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


    /**
     * 获取认证状态
     * @return 用于判断当前MQTT的连接状态
     */
    public MqttAuthState getAuthState() {
        return authState;
    }

    /**
     * 获取异常原因
     * @return 异常原因
     */
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
