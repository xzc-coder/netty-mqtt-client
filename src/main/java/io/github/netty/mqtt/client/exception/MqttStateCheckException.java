package io.github.netty.mqtt.client.exception;

/**
 * MQTT状态检测异常
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
