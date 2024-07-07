package io.github.netty.mqtt.client;

import io.github.netty.mqtt.client.callback.MqttCallback;
import io.github.netty.mqtt.client.connector.MqttConnector;
import io.github.netty.mqtt.client.constant.MqttMsgDirection;
import io.github.netty.mqtt.client.exception.MqttStateCheckException;
import io.github.netty.mqtt.client.handler.MqttDelegateHandler;
import io.github.netty.mqtt.client.msg.MqttMsg;
import io.github.netty.mqtt.client.store.MqttMsgIdCache;
import io.github.netty.mqtt.client.store.MqttMsgStore;
import io.github.netty.mqtt.client.support.util.AssertUtils;
import io.github.netty.mqtt.client.support.util.EmptyUtils;
import io.github.netty.mqtt.client.support.util.LogUtils;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 抽象的MQTT客户端
 * @author: xzc-coder
 */
public abstract class AbstractMqttClient implements MqttClient {

    /**
     * 客户端ID
     */
    protected final String clientId;
    /**
     * 客户端工厂
     */
    protected final MqttClientFactory mqttClientFactory;
    /**
     * MQTT消息委托器
     */
    protected final MqttDelegateHandler mqttDelegateHandler;
    /**
     * MQTT消息存储器
     */
    protected final MqttMsgStore mqttMsgStore;
    /**
     * MQTT连接器
     */
    protected final MqttConnector mqttConnector;
    /**
     * MQTT全局配置
     */
    protected final MqttConfiguration mqttConfiguration;
    /**
     * MQTT的连接参数
     */
    protected final MqttConnectParameter mqttConnectParameter;

    /**
     * 当前的Netty Channel，如果未连接则为null，只有当当前Channel关闭后，才能进行新的一次连接
     */
    protected volatile Channel currentChannel;
    /**
     * MQTT回调器集合，读多写少，适合用 CopyOnWriteArraySet
     */
    protected final Set<MqttCallback> mqttCallbackSet = new CopyOnWriteArraySet<>();
    /**
     * 客户端是否关闭
     */
    private final AtomicBoolean isClose = new AtomicBoolean(false);

    public AbstractMqttClient(MqttConfiguration mqttConfiguration, MqttConnectParameter mqttConnectParameter, Object... connectorCreateArgs) {
        AssertUtils.notNull(mqttConfiguration, "mqttConfiguration is null");
        AssertUtils.notNull(mqttConnectParameter, "mqttConnectParameter is null");
        this.mqttConfiguration = mqttConfiguration;
        this.mqttConnectParameter = mqttConnectParameter;
        MqttClientFactory mqttClientFactory = mqttConfiguration.getMqttClientFactory();
        AssertUtils.notNull(mqttClientFactory, "mqttClientFactory is null");
        this.mqttClientFactory = mqttClientFactory;
        this.clientId = mqttConnectParameter.getClientId();
        MqttConnector mqttConnector = createMqttConnector(connectorCreateArgs);
        AssertUtils.notNull(mqttConnector, "mqttConnector is null");
        this.mqttConnector = mqttConnector;
        MqttDelegateHandler mqttMsgHandler = mqttConnector.getMqttDelegateHandler();
        AssertUtils.notNull(mqttMsgHandler, "mqttMsgHandler is null");
        this.mqttDelegateHandler = mqttMsgHandler;
        this.mqttMsgStore = mqttConfiguration.getMqttMsgStore();
        if (!mqttConnectParameter.isCleanSession()) {
            occupyMsgId();
        }
    }

    /**
     * 不清理会话时，占用消息ID（因为旧的还未释放）
     */
    private void occupyMsgId() {
        List<MqttMsg> msgList = this.mqttMsgStore.getMsgList(MqttMsgDirection.SEND, clientId);
        if (EmptyUtils.isNotEmpty(msgList)) {
            Set<Integer> msgIdSet = msgList.stream().map(MqttMsg::getMsgId).collect(Collectors.toSet());
            MqttMsgIdCache.occupyMsgId(clientId, msgIdSet);
        }
    }


    /**
     * 创建一个MQTT连接器
     *
     * @param connectorCreateArgs 创建参数
     * @return MQTT连接器
     */
    protected abstract MqttConnector createMqttConnector(Object... connectorCreateArgs);

    @Override
    public InetSocketAddress getLocalAddress() {
        Channel channel = currentChannel;
        InetSocketAddress localAddress = null;
        if (channel != null) {
            localAddress = (InetSocketAddress) channel.localAddress();
        }
        return localAddress;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        Channel channel = currentChannel;
        InetSocketAddress remoteAddress = null;
        if (channel != null) {
            remoteAddress = (InetSocketAddress) channel.remoteAddress();
        }
        return remoteAddress;
    }

    @Override
    public boolean isClose() {
        return isClose.get();
    }

    @Override
    public void close() {
        // cas设值，保证只执行一次关闭
        if (isClose.compareAndSet(false, true)) {
            try {
                LogUtils.info(AbstractMqttClient.class,"client:" + getClientId() + " is shutting down");
                doClose();
            } finally {
                //释放客户端ID，以便之后还可以继续创建
                mqttClientFactory.releaseMqttClientId(clientId);
            }
        }
    }

    /**
     * 执行关闭操作，留给子类实现
     */
    protected void doClose() {

    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public MqttConnectParameter getMqttConnectParameter() {
        return this.mqttConnectParameter;
    }

    protected Channel getChannel() {
        return currentChannel;
    }

    @Override
    public void addMqttCallback(MqttCallback mqttCallback) {
        if (mqttCallback != null) {
            mqttCallbackSet.add(mqttCallback);
        }
    }

    @Override
    public void addMqttCallbacks(Collection<MqttCallback> mqttCallbacks) {
        if (mqttCallbacks != null && mqttCallbacks.size() > 0) {
            mqttCallbackSet.addAll(mqttCallbacks);
        }
    }

    protected void closeCheck() {
        if (isClose()) {
            throw new MqttStateCheckException("client: " + getClientId() + " already closed");
        }
    }
}
