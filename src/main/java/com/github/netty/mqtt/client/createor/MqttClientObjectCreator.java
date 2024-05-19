package com.github.netty.mqtt.client.createor;

import com.github.netty.mqtt.client.DefaultMqttClient;
import com.github.netty.mqtt.client.MqttClient;
import com.github.netty.mqtt.client.MqttConfiguration;
import com.github.netty.mqtt.client.MqttConnectParameter;

/**
 * @Date: 2023/8/24 13:17
 * @Description: 默认的MQTT客户端创建器
 * @author: xzc-coder
 */
public class MqttClientObjectCreator implements ObjectCreator<MqttClient> {

    @Override
    public MqttClient createObject(Object... constructorArgs) {
        return new DefaultMqttClient((MqttConfiguration) constructorArgs[1], (MqttConnectParameter) constructorArgs[2]);
    }
}
