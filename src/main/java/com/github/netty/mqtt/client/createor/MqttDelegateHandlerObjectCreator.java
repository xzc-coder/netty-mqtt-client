package com.github.netty.mqtt.client.createor;

import com.github.netty.mqtt.client.MqttConnectParameter;
import com.github.netty.mqtt.client.callback.MqttCallback;
import com.github.netty.mqtt.client.handler.DefaultMqttDelegateHandler;
import com.github.netty.mqtt.client.handler.MqttDelegateHandler;
import com.github.netty.mqtt.client.store.MqttMsgStore;

/**
 * @Date: 2023/8/24 14:19
 * @Description: 默认的MQTT消息委托处理器创建器
 * @author: xzc-coder
 */
public class MqttDelegateHandlerObjectCreator implements ObjectCreator<MqttDelegateHandler> {


    @Override
    public MqttDelegateHandler createObject(Object... constructorArgs) {
        return new DefaultMqttDelegateHandler((MqttConnectParameter) constructorArgs[0], (MqttCallback) constructorArgs[1], (MqttMsgStore) constructorArgs[2]);
    }
}
