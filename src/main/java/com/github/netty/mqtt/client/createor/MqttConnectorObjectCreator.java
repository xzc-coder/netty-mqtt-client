package com.github.netty.mqtt.client.createor;

import com.github.netty.mqtt.client.MqttConfiguration;
import com.github.netty.mqtt.client.MqttConnectParameter;
import com.github.netty.mqtt.client.callback.MqttCallback;
import com.github.netty.mqtt.client.connector.DefaultMqttConnector;
import com.github.netty.mqtt.client.connector.MqttConnector;

/**
 * @Date: 2023/8/24 14:09
 * @Description: 默认的MQTT连接器创建器
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
