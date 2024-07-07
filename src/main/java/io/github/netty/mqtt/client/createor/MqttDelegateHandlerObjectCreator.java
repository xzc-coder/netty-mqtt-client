package io.github.netty.mqtt.client.createor;

import io.github.netty.mqtt.client.MqttConnectParameter;
import io.github.netty.mqtt.client.callback.MqttCallback;
import io.github.netty.mqtt.client.handler.DefaultMqttDelegateHandler;
import io.github.netty.mqtt.client.handler.MqttDelegateHandler;
import io.github.netty.mqtt.client.store.MqttMsgStore;

/**
 * 默认的MQTT消息委托处理器创建器
 * @author: xzc-coder
 */
public class MqttDelegateHandlerObjectCreator implements ObjectCreator<MqttDelegateHandler> {


    @Override
    public MqttDelegateHandler createObject(Object... constructorArgs) {
        MqttConnectParameter mqttConnectParameter = (MqttConnectParameter) constructorArgs[0];
        MqttCallback mqttCallback = (MqttCallback) constructorArgs[1];
        MqttMsgStore mqttMsgStore = (MqttMsgStore) constructorArgs[2];
        return new DefaultMqttDelegateHandler(mqttConnectParameter,mqttCallback,mqttMsgStore);
    }
}
