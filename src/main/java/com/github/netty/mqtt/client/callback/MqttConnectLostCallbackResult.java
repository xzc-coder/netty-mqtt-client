package com.github.netty.mqtt.client.callback;

import com.github.netty.mqtt.client.constant.MqttAuthState;
import com.github.netty.mqtt.client.support.util.AssertUtils;

/**
 * @Date: 2021/12/28 16:39
 * @Description: MQTT连接丢失回调结果
 * @author: xzc-coder
 */
public class MqttConnectLostCallbackResult extends MqttCallbackResult {

    /**
     * MQTT认证状态
     */
    private final MqttAuthState mqttAuthState;


    public MqttConnectLostCallbackResult(String clientId, MqttAuthState mqttAuthState) {
        super(clientId);
        AssertUtils.notNull(mqttAuthState,"mqttAuthState is null");
        this.mqttAuthState = mqttAuthState;
    }

    public MqttAuthState getMqttAuthState() {
        return mqttAuthState;
    }

    @Override
    public String toString() {
        return "MqttConnectLostCallbackResult{" +
                "mqttAuthState=" + mqttAuthState +
                "} " + super.toString();
    }
}
