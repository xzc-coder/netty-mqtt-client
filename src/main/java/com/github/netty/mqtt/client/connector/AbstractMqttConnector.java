package com.github.netty.mqtt.client.connector;

import com.github.netty.mqtt.client.MqttConfiguration;
import com.github.netty.mqtt.client.MqttConnectParameter;
import com.github.netty.mqtt.client.constant.MqttConstant;
import com.github.netty.mqtt.client.handler.MqttDelegateHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Date: 2023/8/24 15:32
 * @Description: 抽象的MQTT连接器
 * @author: xzc-coder
 */
public abstract class AbstractMqttConnector implements MqttConnector {

    protected final MqttConfiguration configuration;
    protected final MqttConnectParameter mqttConnectParameter;
    protected final MqttDelegateHandler mqttDelegateHandler;

    public AbstractMqttConnector(MqttConfiguration configuration, MqttConnectParameter mqttConnectParameter, Object... handlerCreateArgs) {
        this.configuration = configuration;
        this.mqttConnectParameter = mqttConnectParameter;
        this.mqttDelegateHandler = createDelegateHandle(handlerCreateArgs);
    }

    /**
     * 创建一个MQTT委托处理器，子类实现
     *
     * @param handlerCreateArgs 创建的参数
     * @return MqttDelegateHandler
     */
    protected abstract MqttDelegateHandler createDelegateHandle(Object... handlerCreateArgs);

    protected void addOptions(Bootstrap bootstrap, Map<ChannelOption, Object> optionMap) {
        Iterator<Map.Entry<ChannelOption, Object>> optionIterator = optionMap.entrySet().iterator();
        while (optionIterator.hasNext()) {
            Map.Entry<ChannelOption, Object> option = optionIterator.next();
            bootstrap.option(option.getKey(), option.getValue());
        }
    }


    @Override
    public MqttDelegateHandler getMqttDelegateHandler() {
        return this.mqttDelegateHandler;
    }

    @Override
    public MqttConfiguration getMqttConfiguration() {
        return this.configuration;
    }

    protected SslHandler getSslHandler(ByteBufAllocator allocator) throws SSLException {
        final SslContext sslCtx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
        return sslCtx.newHandler(allocator, mqttConnectParameter.getHost(), mqttConnectParameter.getPort());

    }


}
