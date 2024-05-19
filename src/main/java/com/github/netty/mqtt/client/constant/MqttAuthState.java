package com.github.netty.mqtt.client.constant;

/**
 * @Date: 2021/12/24 10:58
 * @Description: MQTT认证状态
 * @author: xzc-coder
 */
public enum MqttAuthState {

    /**
     * 未认证，初始值
     */
    NOT_AUTH,
    /**
     * 认证失败
     */
    AUTH_FAIL,
    /**
     * 认证成功
     */
    AUTH_SUCCESS
}
