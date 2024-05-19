package com.github.netty.mqtt.client.callback;

import com.github.netty.mqtt.client.constant.MqttMsgState;
import com.github.netty.mqtt.client.msg.MqttMsg;
import com.github.netty.mqtt.client.support.util.AssertUtils;
import io.netty.handler.codec.mqtt.MqttQoS;

import java.util.Arrays;

/**
 * @Date: 2022/1/7 13:48
 * @Description: MQTT接受到消息回调结果
 * @author: xzc-coder
 */
public class MqttReceiveCallbackResult extends MqttCallbackResult {

    /**
     * 接收到的MQTT消息
     */
    private final MqttMsg mqttMsg;

    public MqttReceiveCallbackResult(String clientId, MqttMsg mqttMsg) {
        super(clientId);
        AssertUtils.notNull(mqttMsg,"mqttMsg is null");
        this.mqttMsg = mqttMsg;
    }


    public MqttMsg getMqttMsg() {
        return mqttMsg;
    }

    @Override
    public String toString() {
        return "MqttReceiveCallbackResult{" +
                "mqttMsg=" + mqttMsg +
                "} " + super.toString();
    }
}
