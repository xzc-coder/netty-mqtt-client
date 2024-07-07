package io.github.netty.mqtt.client.connector;

import io.github.netty.mqtt.client.MqttConfiguration;
import io.github.netty.mqtt.client.handler.MqttDelegateHandler;
import io.github.netty.mqtt.client.support.future.MqttFuture;
import io.netty.channel.Channel;

/**
 * MQTT连接器接口
 * @author: xzc-coder
 */
public interface MqttConnector {

    /**
     * 进行MQTT连接
     *
     * @return Future
     */
    MqttFuture<Channel> connect();

    /**
     * 获取消息委托处理器
     *
     * @return MqttDelegateHandler
     */
    MqttDelegateHandler getMqttDelegateHandler();

    /**
     * 获取MQTT全局配置
     *
     * @return MqttConfiguration
     */
    MqttConfiguration getMqttConfiguration();

}
