package com.github.netty.mqtt.client.callback;

import com.github.netty.mqtt.client.msg.MqttMsg;
import com.github.netty.mqtt.client.support.util.AssertUtils;

/**
 * @Date: 2022/1/6 15:44
 * @Description: mqtt发送完成回调结果
 * @author: xzc-coder
 */
public class MqttSendCallbackResult extends MqttCallbackResult {

    /**
     * 发送时的MQTT消息
     */
    private final MqttMsg mqttMsg;

    public MqttSendCallbackResult(String clientId, MqttMsg mqttMsg) {
        super(clientId);
        AssertUtils.notNull(mqttMsg,"mqttMsg is null");
        this.mqttMsg = mqttMsg;
    }

    public MqttMsg getMqttMsg() {
        return mqttMsg;
    }

    @Override
    public String toString() {
        return "MqttSendCallbackResult{" +
                "mqttMsg=" + mqttMsg +
                "} " + super.toString();
    }
}
