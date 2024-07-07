package io.github.netty.mqtt.client.createor;

import io.github.netty.mqtt.client.DefaultMqttClient;
import io.github.netty.mqtt.client.MqttClient;
import io.github.netty.mqtt.client.MqttConfiguration;
import io.github.netty.mqtt.client.MqttConnectParameter;

/**
 * 默认的MQTT客户端创建器
 * @author: xzc-coder
 */
public class MqttClientObjectCreator implements ObjectCreator<MqttClient> {

    @Override
    public MqttClient createObject(Object... constructorArgs) {
        MqttConfiguration mqttConfiguration = (MqttConfiguration) constructorArgs[1];
        MqttConnectParameter mqttConnectParameter = (MqttConnectParameter) constructorArgs[2];
        return new DefaultMqttClient(mqttConfiguration,mqttConnectParameter);
    }
}
