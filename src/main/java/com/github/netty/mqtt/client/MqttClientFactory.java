package com.github.netty.mqtt.client;

import com.github.netty.mqtt.client.connector.MqttConnector;
import com.github.netty.mqtt.client.createor.ObjectCreator;
import com.github.netty.mqtt.client.handler.MqttDelegateHandler;
import com.github.netty.mqtt.client.plugin.Interceptor;
import com.github.netty.mqtt.client.store.MqttMsgStore;
import com.github.netty.mqtt.client.support.proxy.ProxyFactory;
import io.netty.channel.ChannelOption;

import java.util.List;


/**
 * @Date: 2023/8/25 10:11
 * @Description: MQTT客户端工厂
 * @author: xzc-coder
 */
public interface MqttClientFactory {


    /**
     * 创建一个MQTT客户端
     *
     * @param mqttConnectParameter MQTT连接参数
     * @return MQTT客户端
     */
    MqttClient createMqttClient(MqttConnectParameter mqttConnectParameter);

    /**
     * 关闭一个MQTT客户端
     *
     * @param clientId 客户端ID
     */
    void closeMqttClient(String clientId);

    /**
     * 释放掉一个MQTT的客户端ID
     *
     * @param clientId 客户端ID
     */
    void releaseMqttClientId(String clientId);

    /**
     * 设置客户端工厂
     *
     * @param proxyFactory
     */
    void setProxyFactory(ProxyFactory proxyFactory);

    /**
     * 添加一个拦截器
     *
     * @param interceptor
     */
    void addInterceptor(Interceptor interceptor);

    /**
     * 设置MQTT客户端对象创建器
     *
     * @param mqttClientObjectCreator
     */
    void setMqttClientObjectCreator(ObjectCreator<MqttClient> mqttClientObjectCreator);

    /**
     * 设置MQTT连接器对象创建器
     *
     * @param mqttConnectorObjectCreator
     */
    void setMqttConnectorObjectCreator(ObjectCreator<MqttConnector> mqttConnectorObjectCreator);

    /**
     * 设置MQTT委托处理器对象创建器
     *
     * @param mqttDelegateHandlerObjectCreator
     */
    void setMqttDelegateHandlerObjectCreator(ObjectCreator<MqttDelegateHandler> mqttDelegateHandlerObjectCreator);

    /**
     * 设置一个MQTT消息存储器
     *
     * @param mqttMsgStore
     */
    void setMqttMsgStore(MqttMsgStore mqttMsgStore);

    /**
     * 获取MQTT全局配置
     *
     * @return
     */
    MqttConfiguration getMqttConfiguration();

    /**
     * 添加或删除一个Netty的TCP配置项（value为null时为删除）
     *
     * @param option
     * @param value
     */
    void option(ChannelOption option, Object value);

    /**
     * 关闭MQTT客户端工厂，会释放线程资源
     */
    void close();
}
