package io.github.netty.mqtt.client;

import io.github.netty.mqtt.client.connector.MqttConnector;
import io.github.netty.mqtt.client.createor.ObjectCreator;
import io.github.netty.mqtt.client.handler.MqttDelegateHandler;
import io.github.netty.mqtt.client.plugin.Interceptor;
import io.github.netty.mqtt.client.store.MqttMsgStore;
import io.github.netty.mqtt.client.support.proxy.ProxyFactory;
import io.netty.channel.ChannelOption;


/**
 * MQTT客户端工厂接口
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
     * @param proxyFactory 代理工厂
     */
    void setProxyFactory(ProxyFactory proxyFactory);

    /**
     * 添加一个拦截器
     *
     * @param interceptor 拦截器
     */
    void addInterceptor(Interceptor interceptor);

    /**
     * 设置MQTT客户端对象创建器
     *
     * @param mqttClientObjectCreator MQTT客户端对象创建器
     */
    void setMqttClientObjectCreator(ObjectCreator<MqttClient> mqttClientObjectCreator);

    /**
     * 设置MQTT连接器对象创建器
     *
     * @param mqttConnectorObjectCreator MQTT连接器对象创建器
     */
    void setMqttConnectorObjectCreator(ObjectCreator<MqttConnector> mqttConnectorObjectCreator);

    /**
     * 设置MQTT委托处理器对象创建器
     *
     * @param mqttDelegateHandlerObjectCreator MQTT委托处理器对象创建器
     */
    void setMqttDelegateHandlerObjectCreator(ObjectCreator<MqttDelegateHandler> mqttDelegateHandlerObjectCreator);

    /**
     * 设置一个MQTT消息存储器
     *
     * @param mqttMsgStore MQTT消息存储器
     */
    void setMqttMsgStore(MqttMsgStore mqttMsgStore);

    /**
     * 获取MQTT全局配置
     *
     * @return MQTT全局配置
     */
    MqttConfiguration getMqttConfiguration();

    /**
     * 添加或删除一个Netty的TCP配置项（value为null时为删除）
     *
     * @param option 连接参数项
     * @param value 连接参数值
     */
    void option(ChannelOption option, Object value);

    /**
     * 关闭MQTT客户端工厂，会释放线程资源
     */
    void close();
}
