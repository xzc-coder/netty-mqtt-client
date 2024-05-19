package com.github.netty.mqtt.client;

import com.github.netty.mqtt.client.connector.MqttConnector;
import com.github.netty.mqtt.client.createor.ObjectCreator;
import com.github.netty.mqtt.client.handler.MqttDelegateHandler;
import com.github.netty.mqtt.client.plugin.Interceptor;
import com.github.netty.mqtt.client.store.MqttMsgStore;
import com.github.netty.mqtt.client.support.proxy.ProxyFactory;
import com.github.netty.mqtt.client.support.util.AssertUtils;
import io.netty.channel.ChannelOption;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Date: 2023/8/25 10:18
 * @Description: 默认的MQTT客户端工厂
 * @author: xzc-coder
 */
public class DefaultMqttClientFactory implements MqttClientFactory {

    /**
     * 全局的MQTT配置，此工厂下创建的所有MQTT客户端都应该使用该配置
     */
    private final MqttConfiguration mqttConfiguration;
    /**
     * MQTT客户端MAP，用来避免重复创建，同时创建相同ClientId的MqttClient会导致不可预测的问题
     */
    private static final Map<String, MqttClient> MQTT_CLIENT_MAP = new ConcurrentHashMap<>();

    public DefaultMqttClientFactory() {
        this(1);
    }

    public DefaultMqttClientFactory(int maxThreadNumber) {
        this(new MqttConfiguration(maxThreadNumber));
    }

    public DefaultMqttClientFactory(MqttConfiguration mqttConfiguration) {
        AssertUtils.notNull(mqttConfiguration, "mqttConfiguration is null ");
        this.mqttConfiguration = mqttConfiguration;
        this.mqttConfiguration.setMqttClientFactory(this);
    }

    @Override
    public MqttClient createMqttClient(MqttConnectParameter mqttConnectParameter) {
        AssertUtils.notNull(mqttConnectParameter, "mqttConnectParameter is null");
        MqttClient mqttClient = mqttConfiguration.newMqttClient(this, mqttConfiguration, mqttConnectParameter);
        MQTT_CLIENT_MAP.put(mqttClient.getClientId(), mqttClient);
        return mqttClient;
    }

    @Override
    public void closeMqttClient(String clientId) {
        MqttClient mqttClient = MQTT_CLIENT_MAP.get(clientId);
        if (mqttClient != null) {
            mqttClient.close();
        }
    }

    @Override
    public void releaseMqttClientId(String clientId) {
        MQTT_CLIENT_MAP.remove(clientId);
    }

    @Override
    public void setProxyFactory(ProxyFactory proxyFactory) {
        this.mqttConfiguration.setProxyFactory(proxyFactory);
    }

    @Override
    public void addInterceptor(Interceptor interceptor) {
        this.mqttConfiguration.addInterceptor(interceptor);
    }

    @Override
    public void setMqttClientObjectCreator(ObjectCreator<MqttClient> mqttClientObjectCreator) {
        this.mqttConfiguration.setMqttClientObjectCreator(mqttClientObjectCreator);
    }

    @Override
    public void setMqttConnectorObjectCreator(ObjectCreator<MqttConnector> mqttConnectorObjectCreator) {
        this.mqttConfiguration.setMqttConnectorObjectCreator(mqttConnectorObjectCreator);
    }

    @Override
    public void setMqttDelegateHandlerObjectCreator(ObjectCreator<MqttDelegateHandler> mqttDelegateHandlerObjectCreator) {
        this.mqttConfiguration.setMqttDelegateHandlerObjectCreator(mqttDelegateHandlerObjectCreator);
    }

    @Override
    public void setMqttMsgStore(MqttMsgStore mqttMsgStore) {
        this.mqttConfiguration.setMqttMsgStore(mqttMsgStore);
    }

    @Override
    public MqttConfiguration getMqttConfiguration() {
        return this.mqttConfiguration;
    }

    @Override
    public void option(ChannelOption option, Object value) {
        this.mqttConfiguration.option(option, value);
    }

    @Override
    public void close() {
        this.mqttConfiguration.close();
    }

}
