package com.github.netty.mqtt.client.exception;

/**
 * @Date: 2023/8/30 09:44
 * @Description: MQTT状态检测异常
 * @author: xzc-coder
 */
public class MqttStateCheckException extends MqttException {

    public MqttStateCheckException(String message) {
        super(message);
    }

    public MqttStateCheckException(String message, Throwable cause) {
        super(message, cause);
    }

    public MqttStateCheckException(Throwable cause) {
        super(cause);
    }
}
