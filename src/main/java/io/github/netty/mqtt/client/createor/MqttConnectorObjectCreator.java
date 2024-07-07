package io.github.netty.mqtt.client.createor;

import io.github.netty.mqtt.client.MqttConfiguration;
import io.github.netty.mqtt.client.MqttConnectParameter;
import io.github.netty.mqtt.client.callback.MqttCallback;
import io.github.netty.mqtt.client.connector.DefaultMqttConnector;
import io.github.netty.mqtt.client.connector.MqttConnector;

/**
 * 默认的MQTT连接器创建器
 * @author: xzc-coder
 */
public class MqttConnectorObjectCreator implements ObjectCreator<MqttConnector> {


    @Override
    public MqttConnector createObject(Object... constructorArgs) {
        MqttConfiguration mqttConfiguration = (MqttConfiguration) constructorArgs[0];
        MqttConnectParameter mqttConnectParameter = (MqttConnectParameter) constructorArgs[1];
        MqttCallback mqttCallback = (MqttCallback) constructorArgs[2];
        return new DefaultMqttConnector(mqttConfiguration,mqttConnectParameter,mqttCallback);
    }
}
