package com.github.netty.mqtt.client;

import com.github.netty.mqtt.client.callback.*;
import com.github.netty.mqtt.client.connector.MqttConnector;
import com.github.netty.mqtt.client.constant.*;
import com.github.netty.mqtt.client.exception.MqttStateCheckException;
import com.github.netty.mqtt.client.exception.MqttException;
import com.github.netty.mqtt.client.msg.*;
import com.github.netty.mqtt.client.retry.MqttRetrier;
import com.github.netty.mqtt.client.store.MqttMsgIdCache;
import com.github.netty.mqtt.client.support.future.DefaultMqttFuture;
import com.github.netty.mqtt.client.support.future.MqttFutureListener;
import com.github.netty.mqtt.client.support.future.MqttFuture;
import com.github.netty.mqtt.client.support.future.MqttFutureWrapper;
import com.github.netty.mqtt.client.support.util.AssertUtils;
import com.github.netty.mqtt.client.support.util.EmptyUtils;
import com.github.netty.mqtt.client.support.util.LogUtils;
import com.github.netty.mqtt.client.support.util.MqttUtils;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.mqtt.MqttProperties;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @Date: 2023/8/25 10:18
 * @Description: 默认的MQTT客户端实现
 * @author: xzc-coder
 */
public class DefaultMqttClient extends AbstractMqttClient implements MqttCallback {

    /**
     * 是否是第一次连接，用来启动自动重连的定时任务
     */
    private final AtomicBoolean firstConnect = new AtomicBoolean(true);
    /**
     * 是否是第一次连接成功，用来启动 当不清理会话时，历史消息的重试
     */
    private final AtomicBoolean firstConnectSuccess = new AtomicBoolean(true);
    /**
     * 是否是手动关闭，当调用MqttClient的 disconnect 方法时，则为手动关闭，
     * 手动关闭后，如果开启了自动重连，则自动重连任务会停止
     */
    private volatile boolean manualDisconnect = false;

    /**
     * 重连的定时任务ScheduledFuture
     */
    private volatile ScheduledFuture reconnectScheduledFuture;

    /**
     * 读写锁，发送、订阅、取消订阅消息时使用读锁，涉及到连接相关的，则使用写锁
     */
    private final ReadWriteLock LOCK = new ReentrantReadWriteLock();

    /**
     * Mqtt重试器
     */
    private final MqttRetrier mqttRetrier;

    public DefaultMqttClient(MqttConfiguration configuration, MqttConnectParameter mqttConnectParameter) {
        super(configuration, mqttConnectParameter);
        mqttRetrier = new MqttRetrier(mqttConnectParameter,configuration.getEventLoopGroup());
    }

    @Override
    protected MqttConnector createMqttConnector(Object... connectorCreateArgs) {
        return mqttConfiguration.newMqttConnector(mqttConfiguration, mqttConnectParameter, this);
    }


    @Override
    public MqttFutureWrapper connectFuture() {
        MqttFuture mqttFuture = doConnect();
        return new MqttFutureWrapper(mqttFuture);
    }

    @Override
    public void connect() {
        MqttFutureWrapper mqttFutureWrapper = connectFuture();
        try {
            mqttFutureWrapper.sync(mqttConnectParameter.getConnectTimeoutSeconds() * 1000);
        } catch (InterruptedException | TimeoutException e) {
            throw new MqttException(e, clientId);
        }
    }


    @Override
    public void disconnect() {
        disconnectFuture().syncUninterruptibly();
    }


    @Override
    public MqttFutureWrapper disconnectFuture() {
        return disconnectFuture(new MqttDisconnectMsg());
    }

    @Override
    public MqttFutureWrapper disconnectFuture(MqttDisconnectMsg mqttDisconnectMsg) {
        AssertUtils.notNull(mqttDisconnectMsg, "mqttDisconnectMsg is null");
        LOCK.writeLock().lock();
        try {
            closeCheck();
            manualDisconnect = true;
            if (reconnectScheduledFuture != null) {
                reconnectScheduledFuture.cancel(true);
            }
            Channel channel = getChannel();
            MqttFuture mqttFuture = new DefaultMqttFuture(clientId, new Object());
            if (isOnline(channel)) {
                //在线，发送MQTT断开包，正常断开
                mqttDelegateHandler.sendDisconnect(channel, mqttFuture, mqttDisconnectMsg);
            } else {
                //正在连接中，直接关闭
                isConnected(channel);
                channel.close().addListener(closeFuture -> {
                    if (closeFuture.isSuccess()) {
                        mqttFuture.setSuccess(null);
                    } else {
                        mqttFuture.setFailure(closeFuture.cause());
                    }
                });
            }
            return new MqttFutureWrapper(mqttFuture);
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    @Override
    public void disconnect(MqttDisconnectMsg mqttDisconnectMsg) {
        disconnectFuture(mqttDisconnectMsg).syncUninterruptibly();
    }

    @Override
    public MqttFutureWrapper publishFuture(MqttMsgInfo mqttMsgInfo) {
        LOCK.readLock().lock();
        try {
            Channel channel = currentChannel;
            //发送发布消息之前进行检查
            sendMsgCheck(channel, mqttMsgInfo.getQos());
            //执行发布消息
            MqttFuture msgFuture = doPublish(channel, mqttMsgInfo);
            return new MqttFutureWrapper(msgFuture);
        } finally {
            LOCK.readLock().unlock();
        }
    }

    @Override
    public MqttFutureWrapper publishFuture(byte[] payload, String topic, MqttQoS qos, boolean retain) {
        return publishFuture(new MqttMsgInfo(topic, payload, qos, retain));
    }

    @Override
    public MqttFutureWrapper publishFuture(byte[] payload, String topic, MqttQoS qos) {
        return publishFuture(payload, topic, qos, false);
    }

    @Override
    public MqttFutureWrapper publishFuture(byte[] payload, String topic) {
        return publishFuture(payload, topic, MqttQoS.AT_MOST_ONCE);
    }

    @Override
    public void publish(MqttMsgInfo mqttMsgInfo) {
        publishFuture(mqttMsgInfo).syncUninterruptibly();
    }

    @Override
    public void publish(byte[] payload, String topic, MqttQoS qos, boolean retain) {
        publishFuture(payload, topic, qos, retain).syncUninterruptibly();
    }

    @Override
    public void publish(byte[] payload, String topic, MqttQoS qos) {
        publish(payload, topic, qos, false);
    }

    @Override
    public void publish(byte[] payload, String topic) {
        publish(payload, topic, MqttQoS.AT_MOST_ONCE);
    }

    @Override
    public MqttFutureWrapper subscribesFuture(List<MqttSubInfo> mqttSubInfoList) {
        return this.subscribesFuture(mqttSubInfoList, null, null);
    }


    @Override
    public MqttFutureWrapper subscribesFuture(List<String> topicList, MqttQoS qos) {
        AssertUtils.notEmpty(topicList, "topicList is empty");
        AssertUtils.notNull(qos, "qos is null");
        List<MqttSubInfo> mqttSubInfoList = toSubInfoList(topicList, qos);
        MqttFutureWrapper subscribeFutureWrapper = subscribesFuture(mqttSubInfoList);
        return subscribeFutureWrapper;
    }

    @Override
    public MqttFutureWrapper subscribeFuture(String topic, MqttQoS qos) {
        return subscribeFuture(new MqttSubInfo(topic, qos), null, null);
    }

    @Override
    public MqttFutureWrapper subscribeFuture(MqttSubInfo mqttSubInfo) {
        return subscribeFuture(mqttSubInfo, null, null);
    }

    @Override
    public MqttFutureWrapper subscribeFuture(MqttSubInfo mqttSubInfo, Integer subscriptionIdentifier, MqttProperties.UserProperties mqttUserProperties) {
        AssertUtils.notNull(mqttSubInfo, "mqttSubInfo is null");
        List<MqttSubInfo> mqttSubInfoList = new ArrayList<>(1);
        mqttSubInfoList.add(mqttSubInfo);
        return subscribesFuture(mqttSubInfoList, subscriptionIdentifier, mqttUserProperties);
    }

    @Override
    public MqttFutureWrapper subscribesFuture(List<MqttSubInfo> mqttSubInfoList, Integer subscriptionIdentifier, MqttProperties.UserProperties mqttUserProperties) {
        AssertUtils.notEmpty(mqttSubInfoList, "mqttSubInfoList is empty");
        LOCK.readLock().lock();
        try {
            Channel channel = currentChannel;
            subscribeCheck(channel, mqttSubInfoList);
            MqttFuture subscribeFuture = doSubscribeFuture(channel, mqttSubInfoList, subscriptionIdentifier, mqttUserProperties);
            return new MqttFutureWrapper(subscribeFuture);
        } finally {
            LOCK.readLock().unlock();
        }
    }


    @Override
    public void subscribes(List<MqttSubInfo> mqttSubInfoList) {
        subscribesFuture(mqttSubInfoList).syncUninterruptibly();
    }

    @Override
    public void subscribes(List<MqttSubInfo> mqttSubInfoList, Integer subscriptionIdentifier, MqttProperties.UserProperties mqttUserProperties) {
        subscribesFuture(mqttSubInfoList, subscriptionIdentifier, mqttUserProperties).syncUninterruptibly();
    }


    @Override
    public void subscribes(List<String> topicList, MqttQoS qos) {
        subscribesFuture(topicList, qos).syncUninterruptibly();
    }


    @Override
    public void subscribe(String topic, MqttQoS qos) {
        subscribeFuture(topic, qos).syncUninterruptibly();
    }

    @Override
    public void subscribe(MqttSubInfo mqttSubInfo) {
        subscribe(mqttSubInfo, null, null);
    }

    @Override
    public void subscribe(MqttSubInfo mqttSubInfo, Integer subscriptionIdentifier, MqttProperties.UserProperties mqttUserProperties) {
        subscribeFuture(mqttSubInfo, subscriptionIdentifier, mqttUserProperties).syncUninterruptibly();
    }

    @Override
    public MqttFutureWrapper unsubscribesFuture(List<String> topicList) {
        return this.unsubscribesFuture(topicList, null);
    }

    @Override
    public MqttFutureWrapper unsubscribesFuture(List<String> topicList, MqttProperties.UserProperties mqttUserProperties) {
        AssertUtils.notEmpty(topicList, "topicList is empty");
        LOCK.readLock().lock();
        try {
            Channel channel = currentChannel;
            //关闭检查
            closeCheck();
            //在线检查，必须在线才能进行下一步
            onlineCheck(channel);
            //进行取消订阅操作
            MqttFuture unsubscribeFuture = doUnsubscribeFuture(channel, topicList, mqttUserProperties);
            return new MqttFutureWrapper(unsubscribeFuture);
        } finally {
            LOCK.readLock().unlock();
        }
    }

    @Override
    public void unsubscribes(List<String> topicList, MqttProperties.UserProperties mqttUserProperties) {
        unsubscribesFuture(topicList, mqttUserProperties).syncUninterruptibly();
    }

    @Override
    public MqttFutureWrapper unsubscribeFuture(String topic) {
        return unsubscribeFuture(topic, null);
    }


    @Override
    public void unsubscribes(List<String> topicList) {
        unsubscribesFuture(topicList).syncUninterruptibly();
    }

    @Override
    public void unsubscribe(String topic, MqttProperties.UserProperties mqttUserProperties) {
        unsubscribeFuture(topic, mqttUserProperties).syncUninterruptibly();
    }

    @Override
    public void unsubscribe(String topic) {
        unsubscribeFuture(topic).syncUninterruptibly();
    }

    @Override
    public MqttFutureWrapper unsubscribeFuture(String topic, MqttProperties.UserProperties mqttUserProperties) {
        AssertUtils.notEmpty(topic, "topic is empty");
        List<String> topicList = new ArrayList<>(1);
        topicList.add(topic);
        MqttFutureWrapper unsubscribeFutureWrapper = unsubscribesFuture(topicList, mqttUserProperties);
        return unsubscribeFutureWrapper;
    }


    /**
     * 进行MQTT的连接
     *
     * @return Future
     */
    private MqttFuture doConnect() {
        //写锁，保证同一时间只有一个连接
        LOCK.writeLock().lock();
        try {
            Channel channel = currentChannel;
            //进行连接（客户端在线、客户端关闭、客户端正在连接中都不能继续）检查，因为连接是异步的，需要保证只有一个Channel在线
            connectCheck(channel);
            //使用连接器进行异步连接
            MqttFuture<Channel> connectFuture = mqttConnector.connect();
            Channel newChannel = (Channel) connectFuture.getParameter();
            //只有此处为客户端设置新的Channel，别的地方不能设置，保证来源只有一处
            this.currentChannel = newChannel;
            //添加一个TCP断开连接监听，当连接断开时，唤醒还在等待中的 Future
            newChannel.closeFuture().addListener((future) -> {
                //获取所有连接上的发送消息
                Map<Integer, Object> incompleteMsgMap = newChannel.attr(MqttConstant.SEND_MSG_MAP_ATTRIBUTE_KEY).get();
                incompleteMsgMap.forEach((msgId, msg) -> {
                    //进行失败唤醒
                    MqttFuture notifyMqttFuture = MqttFuture.getFuture(clientId, msgId);
                    if (notifyMqttFuture != null) {
                        notifyMqttFuture.setFailure(new MqttException("connect has been lost", clientId));
                    }
                });
            });
            //添加一个MQTT的连接监听，因为需要开启自动重连和消息重试
            connectFuture.addListener(mqttFuture -> {
                //是首次连接且开启了自动重连，不管连接是否成功，都需要开启自动重连定时任务
                if (firstConnect.compareAndSet(true, false) && mqttConnectParameter.isAutoReconnect()) {
                    startReconnectTask();
                }
                //当MQTT连接成功时（包括认证完成）
                if (mqttFuture.isSuccess()) {
                    //是首次连接成功并且不清理会话，开启旧任务重试（不清理会话的情况下，才存在旧任务）
                    if (firstConnectSuccess.compareAndSet(true, false) && !mqttConnectParameter.isCleanSession()) {
                        oldMsgListRetry();
                    }
                }
            });
            return connectFuture;
        } finally {
            LOCK.writeLock().unlock();
        }
    }


    /**
     * 调用MQTT的消息委托器进行发送
     *
     * @param channel     Channel
     * @param mqttMsgInfo MQTT消息信息
     * @return Future
     */
    private MqttFuture doPublish(Channel channel, MqttMsgInfo mqttMsgInfo) {
        //创建发布消息
        MqttMsg mqttMsg = createMsgAndMsgId(channel, true, mqttMsgInfo.getQos(), (msgId) -> new MqttMsg(msgId, mqttMsgInfo.getPayload(), mqttMsgInfo.getTopic(), mqttMsgInfo.getQos(), mqttMsgInfo.isRetain()));
        //对于qos为0的消息，创建一个Object作为Future的key，qos 1 和 2的 则用消息ID作为Key
        Object futureKey = (mqttMsg.getMsgId() == MqttConstant.INVALID_MSG_ID ? new Object() : mqttMsg.getMsgId());
        boolean isHighQos = isHighQos(mqttMsg.getQos());
        MqttFuture msgFuture = new DefaultMqttFuture(clientId, futureKey, mqttMsg);
        //添加一个兜底监听，释放消息和消息ID
        msgFuture.addListener(mqttFuture -> {
            if (isHighQos) {
                releaseMsgIdAndRemoveMsg(channel, msgFuture, mqttMsg.getMsgId(), true);
            }
        });
        //高版本额外设置
        if (mqttConnectParameter.getMqttVersion() == MqttVersion.MQTT_5_0_0) {
            MqttProperties mqttProperties = MqttUtils.getPublishMqttProperties(mqttMsgInfo);
            mqttMsg.setMqttProperties(mqttProperties);
        }
        //真正发布消息
        mqttDelegateHandler.sendPublish(channel, mqttMsg, msgFuture);
        //如果是高qos，添加重试任务
        if (isHighQos) {
            Supplier<Channel> channelSupplier;
            if (mqttConnectParameter.isCleanSession()) {
                channelSupplier = () -> channel;
            } else {
                channelSupplier = this::getChannel;
            }
            MqttMsgRetryTask mqttMsgRetryTask = new MqttMsgRetryTask(channelSupplier, mqttMsg.getMsgId(), msgFuture);
            mqttRetrier.retry(msgFuture, MqttConstant.MSG_RETRY_MILLS, mqttMsgRetryTask, false);
        }
        return msgFuture;
    }


    /**
     * 进行MQTT的订阅
     *
     * @param channel                Channel
     * @param mqttSubInfoList        订阅的列表
     * @param subscriptionIdentifier 订阅标识符
     * @param mqttUserProperties     用户属性
     * @return Future
     */
    private MqttFuture doSubscribeFuture(Channel channel, List<MqttSubInfo> mqttSubInfoList, Integer subscriptionIdentifier, MqttProperties.UserProperties mqttUserProperties) {
        MqttSubMsg mqttSubMsg = createMsgAndMsgId(channel, false, (msgId) -> new MqttSubMsg(msgId, mqttSubInfoList, subscriptionIdentifier, mqttUserProperties));
        MqttFuture subscribeFuture = new DefaultMqttFuture<>(clientId, mqttSubMsg.getMsgId(), mqttSubMsg);
        subscribeFuture.addListener(mqttFuture -> releaseMsgIdAndRemoveMsg(channel, subscribeFuture, mqttSubMsg.getMsgId(), false));
        mqttDelegateHandler.sendSubscribe(channel, mqttSubMsg);
        return subscribeFuture;
    }

    /**
     * 进行MQTT的取消订阅
     *
     * @param channel            Channel
     * @param topicList          取消订阅主题列表
     * @param mqttUserProperties 用户属性
     * @return Future
     */
    private MqttFuture doUnsubscribeFuture(Channel channel, List<String> topicList, MqttProperties.UserProperties mqttUserProperties) {
        //创建一个取消订阅消息
        MqttUnsubMsg mqttUnsubMsg = createMsgAndMsgId(channel, false, (msgId) -> new MqttUnsubMsg(msgId, topicList, mqttUserProperties));
        //取消订阅Future
        MqttFuture unsubscribeFuture = new DefaultMqttFuture<>(clientId, mqttUnsubMsg.getMsgId(), mqttUnsubMsg);
        //添加一个兜底监听，对于取消订阅，不管成功与否，都需要释放消息ID
        unsubscribeFuture.addListener(mqttFuture -> releaseMsgIdAndRemoveMsg(channel, unsubscribeFuture, mqttUnsubMsg.getMsgId(), false));
        mqttDelegateHandler.sendUnsubscribe(channel, mqttUnsubMsg);
        return unsubscribeFuture;
    }


    /**
     * 创建一个消息和消息ID
     *
     * @param channel        Channel
     * @param publishMqttMsg 是否是发布消息（三种消息，发布消息，订阅消息，取消订阅消息）
     * @param function       创建消息的方式
     * @param <T>            消息类型
     * @return 创建的消息
     */
    private <T> T createMsgAndMsgId(Channel channel, boolean publishMqttMsg, Function<Integer, T> function) {
        return createMsgAndMsgId(channel, publishMqttMsg, null, function);
    }

    /**
     * 创建一个消息和消息ID
     *
     * @param channel        Channel
     * @param publishMqttMsg 是否是发布消息（三种消息，发布消息，订阅消息，取消订阅消息）
     * @param qos            消息的qos（只有发布消息存在）
     * @param function       创建消息的方式
     * @param <T>            消息类型
     * @return 创建的消息
     */
    private <T> T createMsgAndMsgId(Channel channel, boolean publishMqttMsg, MqttQoS qos, Function<Integer, T> function) {
        //qos是0的发送消息是不需要消息ID的，直接创建即可
        if (publishMqttMsg && qos == MqttQoS.AT_MOST_ONCE) {
            return function.apply(MqttConstant.INVALID_MSG_ID);
        }
        //消息
        T result;
        //获取一个消息ID
        int msgId = MqttMsgIdCache.nextMsgId(clientId);
        //是否清理会话，对于清理会话，所有的消息都存储在Channel中
        if (mqttConnectParameter.isCleanSession()) {
            //创建一个消息
            result = function.apply(msgId);
            //添加到Channel的发送消息中（包含发布消息，订阅消息，取消订阅消息）
            channel.attr(MqttConstant.SEND_MSG_MAP_ATTRIBUTE_KEY).get().put(msgId, result);
        } else {
            //不清理会话时，发布消息存储在消息存储器中，需要进行重试
            if (publishMqttMsg) {
                result = function.apply(msgId);
                mqttMsgStore.putMsg(MqttMsgDirection.SEND, clientId, (MqttMsg) result);
            } else {
                //不清理会话，订阅消息，取消订阅消息也存在Channel中
                result = function.apply(msgId);
                channel.attr(MqttConstant.SEND_MSG_MAP_ATTRIBUTE_KEY).get().put(msgId, result);
            }
        }
        return result;
    }


    /**
     * 释放消息ID和移除消息
     *
     * @param channel        Channel
     * @param mqttFuture     对应的Future
     * @param msgId          消息ID
     * @param publishMqttMsg 是否是发布消息（三种消息，发布消息，订阅消息，取消订阅消息）
     */
    private void releaseMsgIdAndRemoveMsg(Channel channel, MqttFuture mqttFuture, int msgId, boolean publishMqttMsg) {
        //是否成功
        if (mqttFuture.isSuccess()) {
            //只要成功都释放消息ID
            MqttMsgIdCache.releaseMsgId(clientId, msgId);
            //是否清理会话
            if (mqttConnectParameter.isCleanSession()) {
                //成功且清理会话则不管消息类型，从Channel上删除
                channel.attr(MqttConstant.SEND_MSG_MAP_ATTRIBUTE_KEY).get().remove(msgId);
            } else {
                //成功且不清理会话，对于发布消息，从消息存储器中删除
                if (publishMqttMsg) {
                    //删除完成的消息
                    mqttMsgStore.removeMsg(MqttMsgDirection.SEND, clientId, msgId);
                } else {
                    //订阅消息和取消订阅消息，从Channel中删除
                    channel.attr(MqttConstant.SEND_MSG_MAP_ATTRIBUTE_KEY).get().remove(msgId);
                }
            }
        } else {
            // 失败（对于清理会话，只有连接断开时，才会算失败，对于不清理会话，只有客户端关闭时才会算失败）
            if (mqttConnectParameter.isCleanSession()) {
                //释放消息ID
                MqttMsgIdCache.releaseMsgId(clientId, msgId);
                //清理会话，从Channel中删除
                channel.attr(MqttConstant.SEND_MSG_MAP_ATTRIBUTE_KEY).get().remove(msgId);
            } else {
                //不清理会话，那么只删除订阅、取消订阅消息，并且释放订阅、取消订阅的消息ID
                if (!publishMqttMsg) {
                    //释放消息ID
                    MqttMsgIdCache.releaseMsgId(clientId, msgId);
                    //不清理会话，订阅消息和取消订阅消息，从Channel中删除
                    channel.attr(MqttConstant.SEND_MSG_MAP_ATTRIBUTE_KEY).get().remove(msgId);
                }
            }
        }
    }


    /**
     * 发送消息检查
     *
     * @param channel
     * @param qos
     */
    private void sendMsgCheck(Channel channel, MqttQoS qos) {
        //关闭检查
        closeCheck();
        //在线检查
        onlineCheck(channel);
        //qos检查
        qosCheck(qos);
    }

    /**
     * 是否是高等级的qos（1,2）
     *
     * @param qos
     * @return
     */
    private boolean isHighQos(MqttQoS qos) {
        return (qos == MqttQoS.AT_LEAST_ONCE) || (qos == MqttQoS.EXACTLY_ONCE);
    }

    /**
     * 订阅检查
     *
     * @param channel
     * @param mqttSubInfoList
     */
    private void subscribeCheck(Channel channel, List<MqttSubInfo> mqttSubInfoList) {
        closeCheck();
        onlineCheck(channel);
        for (MqttSubInfo mqttSubInfo : mqttSubInfoList) {
            String topic = mqttSubInfo.getTopic();
            MqttQoS qos = mqttSubInfo.getQos();
            AssertUtils.notEmpty(topic, "topic is empty");
            AssertUtils.notNull(qos, "qos is null");
            qosCheck(qos);
        }
    }

    private void qosCheck(MqttQoS qos) {
        if (qos == MqttQoS.FAILURE) {
            throw new IllegalArgumentException(qos.value() + " is illegal qos");
        }
    }

    /**
     * 转换为MqttSubInfo列表
     *
     * @param topicList
     * @param qos
     * @return MqttSubInfo列表
     */
    private List<MqttSubInfo> toSubInfoList(List<String> topicList, MqttQoS qos) {
        List<MqttSubInfo> mqttSubInfoList = new ArrayList<>(topicList.size());
        for (String topic : topicList) {
            mqttSubInfoList.add(new MqttSubInfo(topic, qos));
        }
        return mqttSubInfoList;
    }

    /**
     * 连接检查
     *
     * @param channel
     */
    private void connectCheck(Channel channel) {
        //关闭检查
        closeCheck();
        //在线检查
        if (isOnline(channel)) {
            throw new MqttStateCheckException("client: " + clientId + " has already connected");
        }
        // 正在连接中检查
        if (isOpen(channel) || isConnected(channel)) {
            throw new MqttStateCheckException("client: " + clientId + " is connecting");
        }
    }

    /**
     * 第一次连接成功后，持久化的旧消息重试
     */
    private void oldMsgListRetry() {
        LOCK.readLock().lock();
        LogUtils.info(DefaultMqttClient.class, "client: " + clientId + " start old msg list retry");
        try {
            if (!isClose()) {
                List<MqttMsg> msgList = mqttMsgStore.getMsgList(MqttMsgDirection.SEND, clientId);
                if (EmptyUtils.isNotEmpty(msgList)) {
                    Channel channel = currentChannel;
                    for (MqttMsg mqttMsg : msgList) {
                        MqttMsgState msgState = mqttMsg.getMsgState();
                        MqttMsgDirection mqttMsgDirection = mqttMsg.getMqttMsgDirection();
                        //只重试消息方向是发送的，且PUBLISH和PUBREL的消息
                        if (mqttMsgDirection == MqttMsgDirection.SEND) {
                            if (msgState == MqttMsgState.PUBLISH || msgState == MqttMsgState.PUBREL) {
                                LogUtils.debug(DefaultMqttClient.class, "client: " + clientId + " add old retry msg: " + mqttMsg);
                                int msgId = mqttMsg.getMsgId();
                                MqttFuture msgFuture = new DefaultMqttFuture(clientId, msgId, mqttMsg);
                                msgFuture.addListener(mqttFuture -> releaseMsgIdAndRemoveMsg(channel, mqttFuture, msgId, true));
                                mqttRetrier.retry(msgFuture, MqttConstant.MSG_RETRY_MILLS, new MqttMsgRetryTask(this::getChannel, msgId, msgFuture), true);
                            }
                        }
                    }
                }
            }
        } finally {
            LOCK.readLock().unlock();
        }
    }

    /**
     * 开启自动重连任务
     */
    private void startReconnectTask() {
        LOCK.readLock().lock();
        LogUtils.info(DefaultMqttClient.class, "client: " + clientId + " start reconnecting scheduled task");
        try {
            if (!isClose() && !manualDisconnect) {
                EventLoopGroup eventLoopGroup = mqttConfiguration.getEventLoopGroup();
                long reconnectInterval = MqttUtils.getKeepAliveTimeSeconds(getChannel(), mqttConnectParameter.getKeepAliveTimeSeconds()) + 1;
                reconnectScheduledFuture = eventLoopGroup.scheduleWithFixedDelay(() -> {
                    Channel channel = getChannel();
                    if (isOpen(channel) || isClose()) {
                        return;
                    }
                    try {
                        MqttFuture<Channel> reconnectFuture = DefaultMqttClient.this.doConnect();
                        reconnectFuture.addListener(mqttFuture -> {
                            //重连成功
                            if (mqttFuture.isSuccess()) {
                                LogUtils.info(DefaultMqttClient.class, "client: " + clientId + " reconnection is successful");
                            } else {
                                //重连失败
                                LogUtils.warn(DefaultMqttClient.class, "client: " + clientId + " reconnection is failed,cause: " + mqttFuture.getCause().getMessage());
                            }
                        });
                    } catch (MqttStateCheckException mqttStateCheckException) {
                        //忽略Mqtt状态检查异常
                    }

                }, reconnectInterval, reconnectInterval, TimeUnit.SECONDS);
            }
        } finally {
            LOCK.readLock().unlock();
        }
    }

    @Override
    protected void doClose() {
        LOCK.writeLock().lock();
        try {
            Channel channel = currentChannel;
            if (channel != null) {
                if (channel.isOpen()) {
                    channel.close().addListener(future -> closeNotify());
                }
            } else {
                closeNotify();
            }
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    /**
     * 唤醒正在等待中的线程
     */
    private void closeNotify() {
        List<MqttMsg> msgList = mqttMsgStore.getMsgList(MqttMsgDirection.SEND, clientId);
        if (EmptyUtils.isNotEmpty(msgList)) {
            for (MqttMsg mqttMsg : msgList) {
                Integer msgId = mqttMsg.getMsgId();
                MqttFuture msgFuture = MqttFuture.getFuture(clientId, msgId);
                if (msgFuture != null) {
                    msgFuture.setFailure(new MqttException("client has been closed", clientId));
                }
            }
        }
    }

    /**
     * 是否在线，TCP是连接中（ESTABLISHED），且认证完成
     *
     * @param channel Channel
     * @return 是否在线
     */
    private boolean isOnline(Channel channel) {
        if (channel != null && channel.isActive() && channel.attr(MqttConstant.AUTH_STATE_ATTRIBUTE_KEY).get() == MqttAuthState.AUTH_SUCCESS) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isOnline() {
        return isOnline(currentChannel);
    }

    @Override
    public boolean isActive() {
        return (currentChannel != null && currentChannel.isActive());
    }


    /**
     * 是否连接中（ESTABLISHED）
     *
     * @param channel Channel
     * @return 是否连接中
     */
    private boolean isConnected(Channel channel) {
        if (channel != null && channel.isActive()) {
            return true;
        }
        return false;
    }

    /**
     * Channel是否打开中
     *
     * @param channel Channel
     * @return 是否打开中
     */
    private boolean isOpen(Channel channel) {
        if (channel != null && channel.isOpen()) {
            return true;
        }
        return false;
    }

    private void onlineCheck(Channel channel) {
        if (!isOnline(channel)) {
            throw new MqttStateCheckException("client: " + clientId + " is not connected.");
        }
    }


    @Override
    public void subscribeCallback(MqttSubscribeCallbackResult mqttSubscribeCallbackResult) {
        for (MqttCallback mqttCallback : mqttCallbackSet) {
            mqttCallback.subscribeCallback(mqttSubscribeCallbackResult);
        }
    }

    @Override
    public void unsubscribeCallback(MqttUnSubscribeCallbackResult mqttUnSubscribeCallbackResult) {
        for (MqttCallback mqttCallback : mqttCallbackSet) {
            mqttCallback.unsubscribeCallback(mqttUnSubscribeCallbackResult);
        }
    }

    @Override
    public void messageSendCallback(MqttSendCallbackResult mqttSendCallbackResult) {
        for (MqttCallback mqttCallback : mqttCallbackSet) {
            mqttCallback.messageSendCallback(mqttSendCallbackResult);
        }
    }

    @Override
    public void messageReceiveCallback(MqttReceiveCallbackResult receiveCallbackResult) {
        for (MqttCallback mqttCallback : mqttCallbackSet) {
            mqttCallback.messageReceiveCallback(receiveCallbackResult);
        }
    }

    @Override
    public void connectCompleteCallback(MqttConnectCallbackResult mqttConnectCallbackResult) {
        for (MqttCallback mqttCallback : mqttCallbackSet) {
            mqttCallback.connectCompleteCallback(mqttConnectCallbackResult);
        }
    }

    @Override
    public void channelConnectCallback(MqttConnectCallbackResult mqttConnectCallbackResult) {
        for (MqttCallback mqttCallback : mqttCallbackSet) {
            mqttCallback.channelConnectCallback(mqttConnectCallbackResult);
        }
    }


    @Override
    public void connectLostCallback(MqttConnectLostCallbackResult mqttConnectLostCallbackResult) {
        for (MqttCallback mqttCallback : mqttCallbackSet) {
            mqttCallback.connectLostCallback(mqttConnectLostCallbackResult);
        }
    }

    @Override
    public void heartbeatCallback(MqttHeartbeatCallbackResult mqttHeartbeatCallbackResult) {
        for (MqttCallback mqttCallback : mqttCallbackSet) {
            mqttCallback.heartbeatCallback(mqttHeartbeatCallbackResult);
        }
    }

    @Override
    public void channelExceptionCaught(MqttConnectParameter mqttConnectParameter, MqttChannelExceptionCallbackResult mqttChannelExceptionCallbackResult) {
        for (MqttCallback mqttCallback : mqttCallbackSet) {
            mqttCallback.channelExceptionCaught(mqttConnectParameter, mqttChannelExceptionCallbackResult);
        }
    }

    /**
     * Mqtt的发布消息重试任务
     */
    private class MqttMsgRetryTask implements Runnable {
        /**
         * 重试的消息ID，因为消息的状态可能会改变，所以需要每次使用消息ID查询
         */
        private final int msgId;
        /**
         * 对应消息的Future，用来判断是否完成
         */
        private final MqttFuture msgFuture;
        /**
         * 怎么获取一个Channel。对于清理会话来说，Channel是建立连接时的，对于不清理会话，Channel是每次最新的
         */
        private final Supplier<Channel> channelSupplier;

        private MqttMsgRetryTask(Supplier<Channel> channelSupplier, int msgId, MqttFuture msgFuture) {
            this.channelSupplier = channelSupplier;
            this.msgId = msgId;
            this.msgFuture = msgFuture;
        }

        @Override
        public void run() {
            LogUtils.debug(MqttMsgRetryTask.class, "client: " + clientId + ",old msg retry,msgId: " + msgId);
            Channel channel = channelSupplier.get();
            MqttMsg mqttMsg;
            //清理会话从Channel中获取消息，不清理则从消息存储器中获取
            if (mqttConnectParameter.isCleanSession()) {
                mqttMsg = (MqttMsg) channel.attr(MqttConstant.SEND_MSG_MAP_ATTRIBUTE_KEY).get().get(msgId);
            } else {
                mqttMsg = mqttMsgStore.getMsg(MqttMsgDirection.SEND, clientId, msgId);
            }
            //在线和未完成时才继续发送
            if (isOnline(channel) && !msgFuture.isDone() && mqttMsg != null) {
                MqttMsgState msgState = mqttMsg.getMsgState();
                //只有PUBLISH和PUBREL消息的需要重新发送，别的状态不需要发送了
                if (MqttMsgState.PUBLISH == msgState) {
                    mqttDelegateHandler.sendPublish(channel, mqttMsg, msgFuture);
                } else if (MqttMsgState.PUBREL == msgState) {
                    mqttDelegateHandler.sendPubrel(channel, mqttMsg);
                }
            }
        }
    }

}
