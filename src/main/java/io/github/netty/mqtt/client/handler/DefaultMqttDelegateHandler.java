package io.github.netty.mqtt.client.handler;

import io.github.netty.mqtt.client.MqttConnectParameter;
import io.github.netty.mqtt.client.callback.*;
import io.github.netty.mqtt.client.connector.MqttAuthInstruct;
import io.github.netty.mqtt.client.connector.MqttAuthenticator;
import io.github.netty.mqtt.client.constant.*;

import io.github.netty.mqtt.client.constant.MqttConstant;
import io.github.netty.mqtt.client.exception.MqttException;
import io.github.netty.mqtt.client.msg.*;
import io.github.netty.mqtt.client.msg.*;
import io.github.netty.mqtt.client.store.MqttMsgStore;
import io.github.netty.mqtt.client.support.future.MqttFuture;
import io.github.netty.mqtt.client.support.util.AssertUtils;
import io.github.netty.mqtt.client.support.util.EmptyUtils;
import io.github.netty.mqtt.client.support.util.LogUtils;
import io.github.netty.mqtt.client.support.util.MqttUtils;
import io.github.netty.mqtt.client.callback.*;
import io.github.netty.mqtt.client.constant.MqttAuthState;
import io.github.netty.mqtt.client.constant.MqttMsgDirection;
import io.github.netty.mqtt.client.constant.MqttMsgState;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.codec.mqtt.MqttVersion;
import io.netty.util.Attribute;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 默认的MQTT委托处理器
 * @author: xzc-coder
 */
public class DefaultMqttDelegateHandler implements MqttDelegateHandler {

    /**
     * MQTT连接参数
     */
    private final MqttConnectParameter mqttConnectParameter;
    /**
     * MQTT回调器
     */
    private final MqttCallback mqttCallback;
    /**
     * M客户端ID
     */
    private final String clientId;
    /**
     * MQTT消息存储器
     */
    private final MqttMsgStore mqttMsgStore;

    public DefaultMqttDelegateHandler(MqttConnectParameter mqttConnectParameter, MqttCallback mqttCallback, MqttMsgStore mqttMsgStore) {
        AssertUtils.notNull(mqttConnectParameter, "mqttConnectParameter is null");
        AssertUtils.notNull(mqttCallback, "mqttCallback is null");
        AssertUtils.notNull(mqttMsgStore, "mqttMsgStore is null");
        this.mqttConnectParameter = mqttConnectParameter;
        this.mqttCallback = mqttCallback;
        this.mqttMsgStore = mqttMsgStore;
        this.clientId = mqttConnectParameter.getClientId();
    }

    @Override
    public void channelConnect(Channel channel) {
        MqttConnectCallbackResult mqttConnectCallbackResult = new MqttConnectCallbackResult(clientId, channel.attr(io.github.netty.mqtt.client.constant.MqttConstant.AUTH_STATE_ATTRIBUTE_KEY).get());
        mqttConnectCallbackResult.setMqttVersion(mqttConnectParameter.getMqttVersion());
        mqttCallback.channelConnectCallback(mqttConnectCallbackResult);
    }

    @Override
    public void sendConnect(Channel channel) {
        //连接相关
        MqttVersion nettyMqttVersion;
        if (mqttConnectParameter.getMqttVersion() == io.github.netty.mqtt.client.constant.MqttVersion.MQTT_5_0_0) {
            nettyMqttVersion = MqttVersion.MQTT_5;
        } else {
            nettyMqttVersion = MqttVersion.MQTT_3_1_1;
        }
        int keepAliveTimeSeconds = mqttConnectParameter.getKeepAliveTimeSeconds();
        boolean cleanSession = mqttConnectParameter.isCleanSession();
        String clientId = mqttConnectParameter.getClientId();
        MqttProperties mqttProperties = MqttUtils.getConnectMqttProperties(mqttConnectParameter);
        //账号密码
        String username = mqttConnectParameter.getUsername();
        char[] password = mqttConnectParameter.getPassword();
        byte[] passwordBytes = null;
        if (password != null) {
            passwordBytes = new String(password).getBytes(StandardCharsets.UTF_8);
        }
        //遗嘱相关
        MqttWillMsg willMsg = mqttConnectParameter.getWillMsg();
        boolean hasWill = mqttConnectParameter.hasWill();
        MqttQoS willQos = MqttQoS.AT_MOST_ONCE;
        boolean isWillRetain = false;
        String willTopic = null;
        byte[] willMessageBytes = null;
        MqttProperties willProperties = MqttProperties.NO_PROPERTIES;
        if (hasWill) {
            willQos = willMsg.getWillQos();
            isWillRetain = willMsg.isWillRetain();
            willTopic = willMsg.getWillTopic();
            willMessageBytes = willMsg.getWillMessageBytes();
            willProperties = MqttUtils.getWillMqttProperties(mqttConnectParameter.getMqttVersion(), willMsg);
        }
        //发送报文
        MqttConnectMessage connectMessage = MqttMessageBuilders.connect().clientId(clientId).properties(mqttProperties).username(username).password(passwordBytes).cleanSession(cleanSession).protocolVersion(nettyMqttVersion).keepAlive(keepAliveTimeSeconds).willFlag(hasWill).willMessage(willMessageBytes).willQoS(willQos).willRetain(isWillRetain).willTopic(willTopic).willProperties(willProperties).build();
        channel.writeAndFlush(connectMessage);

    }

    @Override
    public void connack(Channel channel, MqttConnAckMessage mqttConnAckMessage) {
        String clientId = mqttConnectParameter.getClientId();
        MqttConnAckVariableHeader mqttVariableHeader = mqttConnAckMessage.variableHeader();
        MqttConnectReturnCode mqttConnectReturnCode = mqttVariableHeader.connectReturnCode();
        boolean sessionPresent = mqttVariableHeader.isSessionPresent();
        MqttProperties mqttProperties = mqttVariableHeader.properties();
        //获取Future
        MqttFuture<Channel> connectMqttFuture = MqttFuture.getFuture(clientId, channel.id().asShortText());
        MqttConnectCallbackResult mqttConnectCallbackResult;
        //根据返回Code判断是否成功
        if (MqttConnectReturnCode.CONNECTION_ACCEPTED.equals(mqttConnectReturnCode)) {
            LogUtils.info(DefaultMqttDelegateHandler.class, "client: " + clientId + " MQTT authentication successful");
            //设置为已认证
            Attribute<MqttAuthState> mqttAuthStateAttribute = channel.attr(io.github.netty.mqtt.client.constant.MqttConstant.AUTH_STATE_ATTRIBUTE_KEY);
            mqttAuthStateAttribute.set(MqttAuthState.AUTH_SUCCESS);
            //当Broker返回会话不存在时，需要清除消息存储器中的消息
            if (!sessionPresent) {
                mqttMsgStore.clearMsg(mqttConnectParameter.getClientId());
            }
            //设置成功
            if (connectMqttFuture != null) {
                connectMqttFuture.setSuccess(channel);
            }
            //回调
            mqttConnectCallbackResult = new MqttConnectCallbackResult(clientId, channel.attr(io.github.netty.mqtt.client.constant.MqttConstant.AUTH_STATE_ATTRIBUTE_KEY).get(), sessionPresent);
            mqttConnectCallbackResult.setMqttVersion(mqttConnectParameter.getMqttVersion());
            mqttConnectCallbackResult.setMqttProperties(mqttProperties);
            mqttConnectCallbackResult.setSessionPresent(sessionPresent);
            mqttCallback.connectCompleteCallback(mqttConnectCallbackResult);
        } else {
            String connectReturnCode = Integer.toString(mqttConnectReturnCode.byteValue(), MqttConstant.NETTY_MQTT_CONNECT_RETURN_CODE_RADIX);
            LogUtils.info(DefaultMqttDelegateHandler.class, addReasonString("client: " + clientId + " MQTT authentication failed,returnCode:" + connectReturnCode, mqttProperties));
            //设置为认证失败
            Attribute<MqttAuthState> mqttAuthStateAttribute = channel.attr(io.github.netty.mqtt.client.constant.MqttConstant.AUTH_STATE_ATTRIBUTE_KEY);
            mqttAuthStateAttribute.set(MqttAuthState.AUTH_FAIL);
            String exceptionMessage = "auth failed,returnCode:" + connectReturnCode + ",please refer to: io.netty.handler.codec.mqtt.MqttConnectReturnCode";
            MqttException mqttException = new MqttException(exceptionMessage, clientId);
            //设置失败
            if (connectMqttFuture != null) {
                connectMqttFuture.setFailure(mqttException);
            }
            //回调
            mqttConnectCallbackResult = new MqttConnectCallbackResult(clientId, channel.attr(io.github.netty.mqtt.client.constant.MqttConstant.AUTH_STATE_ATTRIBUTE_KEY).get(), mqttException, mqttConnectReturnCode.byteValue());
            mqttConnectCallbackResult.setMqttVersion(mqttConnectParameter.getMqttVersion());
            mqttConnectCallbackResult.setMqttProperties(mqttProperties);
            mqttCallback.connectCompleteCallback(mqttConnectCallbackResult);
            //关闭连接
            channel.close();
        }
    }

    @Override
    public void auth(Channel channel, MqttMessage mqttAuthMessage) {
        MqttReasonCodeAndPropertiesVariableHeader mqttVariableHeader = (MqttReasonCodeAndPropertiesVariableHeader) mqttAuthMessage.variableHeader();
        if (mqttVariableHeader.reasonCode() == io.github.netty.mqtt.client.constant.MqttConstant.AUTH_CONTINUE_REASON_CODE) {
            MqttAuthenticator mqttAuthenticator = mqttConnectParameter.getMqttAuthenticator();
            MqttProperties properties = mqttVariableHeader.properties();
            //认证器不为空才进行认证
            if (mqttAuthenticator != null) {
                //Broke的认证方法和认证数据
                MqttProperties.StringProperty authenticationMethodProperty = (MqttProperties.StringProperty) properties.getProperty(MqttProperties.MqttPropertyType.AUTHENTICATION_METHOD.value());
                MqttProperties.BinaryProperty authenticationDataProperty = (MqttProperties.BinaryProperty) properties.getProperty(MqttProperties.MqttPropertyType.AUTHENTICATION_DATA.value());
                String authenticationMethod = null;
                byte[] authenticationData = null;
                if (authenticationMethodProperty != null) {
                    authenticationMethod = authenticationMethodProperty.value();
                }
                if (authenticationDataProperty != null) {
                    authenticationData = authenticationDataProperty.value();
                }
                //进行认证交互
                MqttAuthInstruct mqttAuthInstruct = mqttAuthenticator.authing(authenticationMethod, authenticationData);
                if (mqttAuthInstruct != null) {
                    //根据认证指示进行下一步的操作
                    MqttAuthInstruct.Instruct nextInstruct = mqttAuthInstruct.getNextInstruct();
                    //待发送的认证数据
                    byte[] sendAuthenticationData = mqttAuthInstruct.getAuthenticationData();
                    String reasonString = mqttAuthInstruct.getReasonString();
                    MqttProperties.UserProperties mqttUserProperties = mqttAuthInstruct.getMqttUserProperties();
                    MqttProperties authMqttProperties;
                    byte authenticateReasonCode;
                    //如果是认证失败和认证继续，则发送继续认证的原因码
                    if (nextInstruct == MqttAuthInstruct.Instruct.AUTH_FAIL || nextInstruct == MqttAuthInstruct.Instruct.AUTH_CONTINUE) {
                        authenticateReasonCode = io.github.netty.mqtt.client.constant.MqttConstant.AUTH_CONTINUE_REASON_CODE;
                    } else if (nextInstruct == MqttAuthInstruct.Instruct.RE_AUTH) {
                        //重新认证
                        authenticateReasonCode = io.github.netty.mqtt.client.constant.MqttConstant.AUTH_RE_AUTH_REASON_CODE;
                    } else {
                        throw new MqttException("client:" + mqttConnectParameter.getClientId() + " auth has error occurred,nextInstruct value is illegal");
                    }
                    //发送认证报文
                    authMqttProperties = MqttUtils.getAuthMqttProperties(authenticationMethod, sendAuthenticationData, reasonString, mqttUserProperties);
                    sendAuth(channel, authenticateReasonCode, authMqttProperties);
                }
            }
        }
    }

    @Override
    public void sendAuth(Channel channel, byte reasonCode, MqttProperties mqttProperties) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.AUTH, false, MqttQoS.AT_MOST_ONCE, false, 0);
        MqttReasonCodeAndPropertiesVariableHeader mqttReasonCodeAndPropertiesVariableHeader = new MqttReasonCodeAndPropertiesVariableHeader(reasonCode, mqttProperties);
        //认证报文
        MqttMessage mqttMessage = new MqttMessage(mqttFixedHeader, mqttReasonCodeAndPropertiesVariableHeader);
        channel.writeAndFlush(mqttMessage);
    }

    @Override
    public void sendDisconnect(Channel channel, MqttFuture mqttFuture, MqttDisconnectMsg mqttDisconnectMsg) {
        //调用该方法断开连接的都属于正常断开
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.DISCONNECT, false, MqttQoS.AT_MOST_ONCE, false, 0);
        byte reasonCode = mqttDisconnectMsg.getReasonCode();
        channel.attr(io.github.netty.mqtt.client.constant.MqttConstant.DISCONNECT_REASON_CODE_ATTRIBUTE_KEY).set(reasonCode);
        MqttProperties mqttProperties = MqttUtils.getDisconnectMqttProperties(mqttDisconnectMsg);
        MqttReasonCodeAndPropertiesVariableHeader mqttVariableHeader = new MqttReasonCodeAndPropertiesVariableHeader(reasonCode, mqttProperties);
        MqttMessage mqttMessage = new MqttMessage(mqttFixedHeader, mqttVariableHeader);
        channel.writeAndFlush(mqttMessage).addListener(future -> {
            ChannelFuture closeFuture = channel.close();
            closeFuture.addListener(closeCompleteFuture -> {
                if (closeCompleteFuture.isSuccess()) {
                    mqttFuture.setSuccess(null);
                } else {
                    mqttFuture.setFailure(closeCompleteFuture.cause());
                }
            });
        });
    }

    @Override
    public void disconnect(Channel channel, MqttMessage mqttMessage) {
        MqttProperties mqttProperties = null;
        Byte reasonCode = null;
        if (mqttMessage != null) {
            MqttReasonCodeAndPropertiesVariableHeader mqttVariableHeader = (MqttReasonCodeAndPropertiesVariableHeader) mqttMessage.variableHeader();
            reasonCode = mqttVariableHeader.reasonCode();
            channel.attr(io.github.netty.mqtt.client.constant.MqttConstant.DISCONNECT_REASON_CODE_ATTRIBUTE_KEY).set(reasonCode);
            mqttProperties = mqttVariableHeader.properties();
            String reasonString = MqttUtils.getReasonString(mqttProperties);
            if (EmptyUtils.isNotBlank(reasonString)) {
                LogUtils.warn(DefaultMqttDelegateHandler.class, "client:" + clientId + " disconnected,reason string : " + reasonString);
            }
        }
        MqttAuthState mqttAuthState = channel.attr(io.github.netty.mqtt.client.constant.MqttConstant.AUTH_STATE_ATTRIBUTE_KEY).get();
        MqttConnectLostCallbackResult mqttConnectLostCallbackResult = new MqttConnectLostCallbackResult(clientId, mqttAuthState);
        mqttConnectLostCallbackResult.setReasonCode(reasonCode);
        mqttConnectLostCallbackResult.setMqttProperties(mqttProperties);
        mqttConnectLostCallbackResult.setMqttVersion(mqttConnectParameter.getMqttVersion());
        mqttCallback.connectLostCallback(mqttConnectLostCallbackResult);
    }


    @Override
    public void sendSubscribe(Channel channel, MqttSubMsg mqttSubMsg) {
        //固定头，除了类型和remainingLength，其它的值都没有用，可变头长度为0
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.SUBSCRIBE, false, MqttQoS.AT_LEAST_ONCE, false, 2);
        //订阅属性处理
        MqttProperties mqttProperties = new MqttProperties();
        if (mqttSubMsg.getSubscriptionIdentifier() != null) {
            mqttProperties.add(new MqttProperties.IntegerProperty(MqttProperties.MqttPropertyType.SUBSCRIPTION_IDENTIFIER.value(), mqttSubMsg.getSubscriptionIdentifier()));
        }
        //用户属性
        mqttProperties.add(mqttSubMsg.getMqttUserProperties());
        //可变头
        MqttMessageIdAndPropertiesVariableHeader variableHeader = new MqttMessageIdAndPropertiesVariableHeader(mqttSubMsg.getMsgId(), mqttProperties);
        //消息体
        List<MqttTopicSubscription> mqttTopicSubscriptionList = toMqttTopicSubscriptionList(mqttSubMsg.getMqttSubInfoList());
        MqttSubscribePayload mqttSubscribePayload = new MqttSubscribePayload(mqttTopicSubscriptionList);
        //发送订阅报文
        MqttSubscribeMessage mqttSubscribeMessage = new MqttSubscribeMessage(mqttFixedHeader, variableHeader, mqttSubscribePayload);
        channel.writeAndFlush(mqttSubscribeMessage);
    }


    @Override
    public void suback(Channel channel, MqttSubAckMessage mqttSubAckMessage) {
        //原因码集合
        List<Integer> serverQosList = mqttSubAckMessage.payload().reasonCodes();
        MqttMessageIdAndPropertiesVariableHeader mqttVariableHeader = mqttSubAckMessage.idAndPropertiesVariableHeader();
        int msgId = mqttVariableHeader.messageId();
        MqttProperties mqttProperties = mqttVariableHeader.properties();
        List<MqttSubscribeCallbackInfo> subscribeCallbackInfoList = new ArrayList<>();
        //获取Future待会进行唤醒
        MqttFuture<Void> subscribeFuture = MqttFuture.getFuture(clientId, msgId);
        //订阅信息集合
        List<MqttSubInfo> mqttSubInfoList;
        if (subscribeFuture != null) {
            //只要Future存在则设置成功，具体的失败在响应回调中
            subscribeFuture.setSuccess(null);
            //拿到原始的订阅主题集合
            mqttSubInfoList = ((MqttSubMsg) subscribeFuture.getParameter()).getMqttSubInfoList();
            if (EmptyUtils.isNotEmpty(mqttSubInfoList) && mqttSubInfoList.size() == serverQosList.size()) {
                //遍历Broker响应的QOS，与原来的主题进行关联
                for (int i = 0; i < serverQosList.size(); i++) {
                    int serverQos = serverQosList.get(i);
                    MqttQoS qoS = MqttQoS.valueOf(serverQos);
                    MqttSubscribeCallbackInfo mqttSubscribeCallbackInfo = new MqttSubscribeCallbackInfo();
                    mqttSubscribeCallbackInfo.setServerQos(qoS);
                    if (MqttQoS.AT_MOST_ONCE.equals(qoS) || MqttQoS.AT_LEAST_ONCE.equals(qoS) || MqttQoS.EXACTLY_ONCE.equals(qoS)) {
                        mqttSubscribeCallbackInfo.setSubscribed(true);
                    } else {
                        mqttSubscribeCallbackInfo.setSubscribed(false);
                    }
                    if (!mqttSubInfoList.isEmpty()) {
                        MqttSubInfo mqttSubInfo = mqttSubInfoList.get(i);
                        mqttSubscribeCallbackInfo.setSubscribeTopic(mqttSubInfo.getTopic());
                        mqttSubscribeCallbackInfo.setSubscribeQos(mqttSubInfo.getQos());
                    }
                    subscribeCallbackInfoList.add(mqttSubscribeCallbackInfo);
                }
                //订阅回调
                MqttSubscribeCallbackResult mqttSubscribeCallbackResult = new MqttSubscribeCallbackResult(clientId, msgId, subscribeCallbackInfoList);
                mqttSubscribeCallbackResult.setMqttVersion(mqttConnectParameter.getMqttVersion());
                mqttSubscribeCallbackResult.setMqttProperties(mqttProperties);
                mqttCallback.subscribeCallback(mqttSubscribeCallbackResult);
            }
        }
    }

    @Override
    public void sendUnsubscribe(Channel channel, MqttUnsubMsg mqttUnsubMsg) {
        //固定头
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.UNSUBSCRIBE, false, MqttQoS.AT_LEAST_ONCE, false, 0x02);
        //取消订阅属性
        MqttProperties mqttProperties = new MqttProperties();
        mqttProperties.add(mqttUnsubMsg.getMqttUserProperties());
        //可变头
        MqttMessageIdAndPropertiesVariableHeader mqttVariableHeader = new MqttMessageIdAndPropertiesVariableHeader(mqttUnsubMsg.getMsgId(), mqttProperties);
        //载荷
        MqttUnsubscribePayload MqttUnsubscribeMessage = new MqttUnsubscribePayload(mqttUnsubMsg.getTopicList());
        //取消订阅报文
        MqttUnsubscribeMessage mqttUnsubscribeMessage = new MqttUnsubscribeMessage(mqttFixedHeader, mqttVariableHeader, MqttUnsubscribeMessage);
        channel.writeAndFlush(mqttUnsubscribeMessage);
    }

    @Override
    public void unsuback(Channel channel, MqttUnsubAckMessage mqttUnsubAckMessage) {
        MqttMessageIdAndPropertiesVariableHeader mqttVariableHeader = mqttUnsubAckMessage.idAndPropertiesVariableHeader();
        int msgId = mqttVariableHeader.messageId();
        MqttProperties mqttProperties = mqttVariableHeader.properties();
        //从Future中获取原始的取消订阅主题集合
        MqttFuture<Void> unsubscribeFuture = MqttFuture.getFuture(clientId, msgId);
        List<String> topicList;
        if (unsubscribeFuture != null) {
            //取消订阅都为成功，具体的失败在回调结果中
            unsubscribeFuture.setSuccess(null);
            topicList = ((MqttUnsubMsg) unsubscribeFuture.getParameter()).getTopicList();
            if (EmptyUtils.isNotEmpty(topicList)) {
                MqttUnsubAckPayload mqttUnsubAckPayload = mqttUnsubAckMessage.payload();
                MqttUnSubscribeCallbackResult mqttUnSubscribeCallbackResult;
                //创建回调结果
                if (mqttUnsubAckPayload != null) {
                    List<Short> unsubscribeReasonCodeList = mqttUnsubAckPayload.unsubscribeReasonCodes();
                    mqttUnSubscribeCallbackResult = new MqttUnSubscribeCallbackResult(clientId, msgId, topicList, unsubscribeReasonCodeList);
                    mqttUnSubscribeCallbackResult.setMqttProperties(mqttProperties);
                } else {
                    mqttUnSubscribeCallbackResult = new MqttUnSubscribeCallbackResult(clientId, msgId, topicList);
                }
                //回调
                mqttCallback.unsubscribeCallback(mqttUnSubscribeCallbackResult);
            }
        }
    }

    @Override
    public void sendPingreq(Channel channel) {
        //固定头
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PINGREQ, false, MqttQoS.AT_MOST_ONCE, false, 0);
        //心跳消息
        MqttMessage mqttMessage = new MqttMessage(fixedHeader);
        channel.writeAndFlush(mqttMessage);
    }

    @Override
    public void pingresp(Channel channel, MqttMessage mqttPingRespMessage) {
        mqttCallback.heartbeatCallback(new MqttHeartbeatCallbackResult(clientId));
    }

    @Override
    public void sendPublish(Channel channel, MqttMsg mqttMsg, MqttFuture msgFuture) {
        MqttQoS qos = mqttMsg.getQos();
        int msgId = mqttMsg.getMsgId();
        //别名处理
        String topic = topicAliasHandle(channel, mqttMsg);
        //固定头
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH, mqttMsg.isDup(), qos, mqttMsg.isRetain(), 0);
        //可变头
        MqttPublishVariableHeader mqttVariableHeader = new MqttPublishVariableHeader(topic, msgId, mqttMsg.getMqttProperties());
        //消息
        MqttPublishMessage mqttPublishMessage = new MqttPublishMessage(mqttFixedHeader, mqttVariableHeader, PooledByteBufAllocator.DEFAULT.directBuffer().writeBytes(mqttMsg.getPayload()));
        //qos为0时，直接回调即可，因为不保证送达
        if (MqttQoS.AT_MOST_ONCE == qos) {
            channel.writeAndFlush(mqttPublishMessage).addListener(future -> {
                msgFuture.setSuccess(null);
                mqttCallback.messageSendCallback(new MqttSendCallbackResult(clientId, mqttMsg));
            });
        } else if (MqttQoS.AT_LEAST_ONCE == qos || MqttQoS.EXACTLY_ONCE == qos) {
            channel.writeAndFlush(mqttPublishMessage);
        }
    }


    @Override
    public void publish(Channel channel, MqttPublishMessage mqttPublishMessage) {
        //publish报文的基本信息
        MqttPublishVariableHeader mqttVariableHeader = mqttPublishMessage.variableHeader();
        MqttFixedHeader mqttFixedHeader = mqttPublishMessage.fixedHeader();
        int msgId = mqttVariableHeader.packetId();
        MqttProperties mqttProperties = mqttVariableHeader.properties();
        String topic = mqttVariableHeader.topicName();
        MqttQoS qos = mqttFixedHeader.qosLevel();
        boolean retain = mqttFixedHeader.isRetain();
        boolean dup = mqttFixedHeader.isDup();
        ByteBuf byteBuf = mqttPublishMessage.payload();
        byte[] payload = ByteBufUtil.getBytes(byteBuf);
        MqttMsg mqttMsg = new MqttMsg(msgId, payload, topic, qos, retain, dup);
        mqttMsg.setMqttMsgDirection(MqttMsgDirection.RECEIVE);
        mqttMsg.setMqttProperties(mqttProperties);
        if (MqttQoS.AT_MOST_ONCE == qos) {
            mqttMsg.setMsgState(MqttMsgState.PUBLISH);
            //qos为0的直接回调
            MqttReceiveCallbackResult mqttReceiveCallbackResult = new MqttReceiveCallbackResult(clientId, mqttMsg);
            mqttReceiveCallbackResult.setMqttVersion(mqttConnectParameter.getMqttVersion());
            mqttCallback.messageReceiveCallback(mqttReceiveCallbackResult);
        } else if (MqttQoS.AT_LEAST_ONCE == qos) {
            mqttMsg.setMsgState(MqttMsgState.PUBACK);
            //qos为1的 发送响应报文后也回调
            sendPuback(channel, mqttMsg);
        } else if (MqttQoS.EXACTLY_ONCE == qos) {
            mqttMsg.setMsgState(MqttMsgState.PUBREC);
            //qos2的消息需要先存储
            putReceiveQos2Msg(channel, mqttMsg);
            sendPubrec(channel, mqttMsg);
        }
    }

    @Override
    public void sendPuback(Channel channel, MqttMsg mqttMsg) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBACK, mqttMsg.isDup(), MqttQoS.AT_MOST_ONCE, mqttMsg.isRetain(), 0x02);
        MqttMessageIdVariableHeader from = MqttMessageIdVariableHeader.from(mqttMsg.getMsgId());
        //发送puback报文
        MqttPubAckMessage mqttPubAckMessage = new MqttPubAckMessage(mqttFixedHeader, from);
        channel.writeAndFlush(mqttPubAckMessage).addListener(future -> {
            //发送报文出去后直接回调
            MqttReceiveCallbackResult mqttReceiveCallbackResult = new MqttReceiveCallbackResult(clientId, mqttMsg);
            mqttReceiveCallbackResult.setMqttVersion(mqttConnectParameter.getMqttVersion());
            mqttCallback.messageReceiveCallback(mqttReceiveCallbackResult);
        });
    }

    @Override
    public void puback(Channel channel, MqttPubAckMessage mqttPubAckMessage) {
        MqttPubReplyMessageVariableHeader mqttVariableHeader = (MqttPubReplyMessageVariableHeader) mqttPubAckMessage.variableHeader();
        MqttFixedHeader mqttFixedHeader = mqttPubAckMessage.fixedHeader();
        int msgId = mqttVariableHeader.messageId();
        MqttProperties mqttProperties = mqttVariableHeader.properties();
        logReceiveMsgReasonStringIfNecessary(msgId, mqttProperties, mqttFixedHeader.messageType());
        MqttFuture sendFuture = MqttFuture.getFuture(clientId, msgId);
        MqttMsg mqttMsg = removeHighQosMsg(channel, msgId, MqttMsgDirection.SEND);
        if (sendFuture != null) {
            sendFuture.setSuccess(null);
        }
        //不存在mqttMsg则表示重复接受
        if (mqttMsg != null) {
            byte reasonCode = mqttVariableHeader.reasonCode();
            mqttMsg.setMsgState(MqttMsgState.PUBACK);
            mqttMsg.setReasonCode(reasonCode);
            mqttCallback.messageSendCallback(new MqttSendCallbackResult(clientId, mqttMsg));
        }
    }

    @Override
    public void sendPubrec(Channel channel, MqttMsg mqttMsg) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBREC, false, MqttQoS.AT_MOST_ONCE, false, 0x02);
        MqttMessageIdVariableHeader mqttVariableHeader = MqttMessageIdVariableHeader.from(mqttMsg.getMsgId());
        //发送pubrec报文
        MqttMessage mqttMessage = new MqttMessage(mqttFixedHeader, mqttVariableHeader);
        channel.writeAndFlush(mqttMessage);
    }


    @Override
    public void pubrec(Channel channel, MqttMessage mqttMessage) {
        //pubrec的基本信息
        MqttPubReplyMessageVariableHeader mqttVariableHeader = (MqttPubReplyMessageVariableHeader) mqttMessage.variableHeader();
        MqttFixedHeader mqttFixedHeader = mqttMessage.fixedHeader();
        int msgId = mqttVariableHeader.messageId();
        byte reasonCode = mqttVariableHeader.reasonCode();
        MqttProperties mqttProperties = mqttVariableHeader.properties();
        logReceiveMsgReasonStringIfNecessary(msgId, mqttProperties, mqttFixedHeader.messageType());
        MqttMsg mqttMsg = getQos2SendMsg(channel, msgId);
        if (mqttMsg != null) {
            updateQos2MsgState(channel, msgId, MqttMsgState.PUBREL, MqttMsgDirection.SEND);
        } else {
            LogUtils.warn(DefaultMqttDelegateHandler.class, "client: " + clientId + "received an unstored illegal pubrec packet with message ID " + msgId);
            mqttMsg = new MqttMsg(msgId, null, null, null, MqttMsgState.INVALID);
        }
        mqttMsg.setReasonCode(reasonCode);
        //不管有没有错误都响应pubrel报文，让其继续
        sendPubrel(channel, mqttMsg);
    }

    @Override
    public void sendPubrel(Channel channel, MqttMsg mqttMsg) {
        //发送pubrel报文
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBREL, false, MqttQoS.AT_LEAST_ONCE, false, 0x02);
        MqttMessageIdVariableHeader mqttVariableHeader = MqttMessageIdVariableHeader.from(mqttMsg.getMsgId());
        MqttMessage mqttMessage = new MqttMessage(mqttFixedHeader, mqttVariableHeader);
        channel.writeAndFlush(mqttMessage);
    }

    @Override
    public void pubrel(Channel channel, MqttMessage mqttMessage) {
        MqttPubReplyMessageVariableHeader mqttVariableHeader = (MqttPubReplyMessageVariableHeader) mqttMessage.variableHeader();
        MqttFixedHeader mqttFixedHeader = mqttMessage.fixedHeader();
        int msgId = mqttVariableHeader.messageId();
        byte reasonCode = mqttVariableHeader.reasonCode();
        MqttProperties mqttProperties = mqttVariableHeader.properties();
        logReceiveMsgReasonStringIfNecessary(msgId, mqttProperties, mqttFixedHeader.messageType());
        MqttMsg mqttMsg = removeHighQosMsg(channel, msgId, MqttMsgDirection.RECEIVE);
        if (mqttMsg != null) {
            mqttMsg.setMsgState(MqttMsgState.PUBCOMP);
        } else {
            LogUtils.warn(DefaultMqttDelegateHandler.class, "client: " + clientId + "received an unstored illegal pubrel packet with message ID " + msgId);
            mqttMsg = new MqttMsg(msgId, null, null, null, MqttMsgState.INVALID);
        }
        mqttMsg.setReasonCode(reasonCode);
        sendPubcomp(channel, mqttMsg);
    }


    @Override
    public void sendPubcomp(Channel channel, MqttMsg mqttMsg) {
        //发送pubcomp报文
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBCOMP, false, MqttQoS.AT_MOST_ONCE, false, 0x02);
        MqttMessageIdVariableHeader mqttVariableHeader = MqttMessageIdVariableHeader.from(mqttMsg.getMsgId());
        MqttMessage mqttMessage = new MqttMessage(mqttFixedHeader, mqttVariableHeader);
        channel.writeAndFlush(mqttMessage).addListener(future -> {
            //pubcomp发送出去后回调
            if (MqttMsgState.INVALID != mqttMsg.getMsgState()) {
                MqttReceiveCallbackResult mqttReceiveCallbackResult = new MqttReceiveCallbackResult(clientId, mqttMsg);
                mqttReceiveCallbackResult.setMqttVersion(mqttConnectParameter.getMqttVersion());
                mqttCallback.messageReceiveCallback(mqttReceiveCallbackResult);
            }
        });
    }


    @Override
    public void pubcomp(Channel channel, MqttMessage mqttMessage) {
        MqttPubReplyMessageVariableHeader mqttVariableHeader = (MqttPubReplyMessageVariableHeader) mqttMessage.variableHeader();
        MqttFixedHeader mqttFixedHeader = mqttMessage.fixedHeader();
        int msgId = mqttVariableHeader.messageId();
        byte reasonCode = mqttVariableHeader.reasonCode();
        MqttProperties mqttProperties = mqttVariableHeader.properties();
        logReceiveMsgReasonStringIfNecessary(msgId, mqttProperties, mqttFixedHeader.messageType());
        //pubcomp消息，则为客户端发送，删除
        MqttMsg mqttMsg = removeHighQosMsg(channel, msgId, MqttMsgDirection.SEND);
        //必须在移除之后，否则会被异步的兜底监听删除
        MqttFuture sendFuture = MqttFuture.getFuture(clientId, msgId);
        if (sendFuture != null) {
            sendFuture.setSuccess(null);
        }
        if (mqttMsg != null) {
            mqttMsg.setReasonCode(reasonCode);
            mqttMsg.setMsgState(MqttMsgState.PUBCOMP);
            //回调
            mqttCallback.messageSendCallback(new MqttSendCallbackResult(clientId, mqttMsg));
        }
    }

    @Override
    public void exceptionCaught(Channel channel, Throwable cause) {
        //异常回调
        MqttAuthState mqttAuthState = channel.attr(io.github.netty.mqtt.client.constant.MqttConstant.AUTH_STATE_ATTRIBUTE_KEY).get();
        MqttChannelExceptionCallbackResult mqttChannelExceptionCallbackResult = new MqttChannelExceptionCallbackResult(clientId, mqttAuthState, cause);
        mqttCallback.channelExceptionCaught(mqttConnectParameter, mqttChannelExceptionCallbackResult);
    }


    /**
     * 转换为Netty的Mqtt订阅消息集合
     *
     * @param mqttSubInfoList MQTT订阅消息集合
     * @return Netty的Mqtt订阅消息集合
     */
    private List<MqttTopicSubscription> toMqttTopicSubscriptionList(List<MqttSubInfo> mqttSubInfoList) {
        List<MqttTopicSubscription> mqttTopicSubscriptionList = new ArrayList<>(mqttSubInfoList.size());
        for (MqttSubInfo mqttSubInfo : mqttSubInfoList) {
            MqttSubscriptionOption mqttSubscriptionOption = new MqttSubscriptionOption(mqttSubInfo.getQos(), mqttSubInfo.isNoLocal(), mqttSubInfo.isRetainAsPublished(), mqttSubInfo.getRetainHandling());
            MqttTopicSubscription mqttTopicSubscription = new MqttTopicSubscription(mqttSubInfo.getTopic(), mqttSubscriptionOption);
            mqttTopicSubscriptionList.add(mqttTopicSubscription);
        }
        return mqttTopicSubscriptionList;
    }

    /**
     * 获取QOS2的发送消息（因为qos2的消息需要存储）
     *
     * @param channel 通道
     * @param msgId   消息ID
     * @return MqttMsg
     */
    private MqttMsg getQos2SendMsg(Channel channel, int msgId) {
        MqttMsg mqttMsg = null;
        if (mqttConnectParameter.isCleanSession()) {
            //清理会话直接从Channel中获取
            Object msg = channel.attr(io.github.netty.mqtt.client.constant.MqttConstant.SEND_MSG_MAP_ATTRIBUTE_KEY).get().get(msgId);
            if (msg instanceof MqttMsg) {
                mqttMsg = (MqttMsg) msg;
            }
        } else {
            //不清理会话从消息存储器中获取
            mqttMsg = mqttMsgStore.getMsg(MqttMsgDirection.SEND, clientId, msgId);
        }
        return mqttMsg;
    }

    private void putReceiveQos2Msg(Channel channel, MqttMsg mqttMsg) {
        if (mqttConnectParameter.isCleanSession()) {
            channel.attr(io.github.netty.mqtt.client.constant.MqttConstant.RECEIVE_MSG_MAP_ATTRIBUTE_KEY).get().put(mqttMsg.getMsgId(), mqttMsg);
        } else {
            mqttMsgStore.putMsg(MqttMsgDirection.RECEIVE, clientId, mqttMsg);
        }
    }

    private MqttMsg removeHighQosMsg(Channel channel, int msgId, MqttMsgDirection msgDirection) {
        MqttMsg mqttMsg = null;
        if (mqttConnectParameter.isCleanSession()) {
            if (msgDirection == MqttMsgDirection.SEND) {
                Object msg = channel.attr(io.github.netty.mqtt.client.constant.MqttConstant.SEND_MSG_MAP_ATTRIBUTE_KEY).get().remove(msgId);
                if (msg instanceof MqttMsg) {
                    mqttMsg = (MqttMsg) msg;
                }
            } else {
                mqttMsg = channel.attr(io.github.netty.mqtt.client.constant.MqttConstant.RECEIVE_MSG_MAP_ATTRIBUTE_KEY).get().remove(msgId);
            }
        } else {
            mqttMsg = mqttMsgStore.removeMsg(msgDirection, clientId, msgId);
        }
        return mqttMsg;
    }

    /**
     * 更新QOS2的消息状态
     *
     * @param channel      channel
     * @param msgId        消息ID
     * @param msgState     消息状态
     * @param msgDirection 消息方向
     */
    private void updateQos2MsgState(Channel channel, int msgId, MqttMsgState msgState, MqttMsgDirection msgDirection) {
        if (mqttConnectParameter.isCleanSession()) {
            //清除会话则从Channel中获取
            if (msgDirection == MqttMsgDirection.SEND) {
                Object msg = channel.attr(io.github.netty.mqtt.client.constant.MqttConstant.SEND_MSG_MAP_ATTRIBUTE_KEY).get().get(msgId);
                if (msg instanceof MqttMsg) {
                    MqttMsg mqttMsg = (MqttMsg) msg;
                    mqttMsg.setMsgState(msgState);
                }
            } else {
                MqttMsg mqttMsg = channel.attr(io.github.netty.mqtt.client.constant.MqttConstant.RECEIVE_MSG_MAP_ATTRIBUTE_KEY).get().get(msgId);
                if (mqttMsg != null) {
                    mqttMsg.setMsgState(msgState);
                }
            }
        } else {
            //不清理会话从消息存储器中获取并更新
            MqttMsg mqttMsg = mqttMsgStore.getMsg(msgDirection, clientId, msgId);
            if (mqttMsg != null) {
                mqttMsg.setMsgState(msgState);
                mqttMsgStore.putMsg(msgDirection, clientId, mqttMsg);
            }
        }
    }

    /**
     * 字符串添加响应字符串
     *
     * @param content        内容
     * @param mqttProperties MQTT用户属性
     * @return 拼接后的字符串
     */
    private String addReasonString(String content, MqttProperties mqttProperties) {
        String reasonString = MqttUtils.getReasonString(mqttProperties);
        if (EmptyUtils.isNotBlank(reasonString)) {
            content = content + ",reasonString :" + reasonString;
        }
        return content;
    }


    /**
     * 主题别名处理，返回新的主题
     *
     * @param channel 通道
     * @param mqttMsg MQTT消息
     * @return 主题名
     */
    private String topicAliasHandle(Channel channel, MqttMsg mqttMsg) {
        String topic = mqttMsg.getTopic();
        MqttProperties mqttProperties = mqttMsg.getMqttProperties();
        if (mqttProperties != null && mqttConnectParameter.getMqttVersion() == io.github.netty.mqtt.client.constant.MqttVersion.MQTT_5_0_0) {
            MqttProperties.IntegerProperty topicAliasProperty = (MqttProperties.IntegerProperty) mqttProperties.getProperty(MqttProperties.MqttPropertyType.TOPIC_ALIAS.value());
            Attribute<Map<String, Integer>> topicAliasMapAttribute = channel.attr(io.github.netty.mqtt.client.constant.MqttConstant.TOPIC_ALIAS_MAP_ATTRIBUTE_KEY);
            if (topicAliasProperty != null && topicAliasMapAttribute != null) {
                int nowTopicAlias = topicAliasProperty.value();
                Map<String, Integer> topicAliasMap = topicAliasMapAttribute.get();
                //对主题名加锁，防止并发时，主题别名值被修改
                synchronized (topic.intern()) {
                    Integer topicAlias = topicAliasMap.get(topic);
                    if (topicAlias != null) {
                        if (topicAlias.equals(nowTopicAlias)) {
                            //别名一致，返回空字符作为主题
                            topic = io.github.netty.mqtt.client.constant.MqttConstant.EMPTY_STR;
                        } else {
                            //别名不一致时，使用现在的替换
                            topicAliasMap.put(topic, nowTopicAlias);
                        }
                    } else {
                        //暂存别名，下一次才替换主题
                        topicAliasMap.put(topic, nowTopicAlias);
                    }
                }
            }
        }
        return topic;
    }

    /**
     * 打印接受消息的原因字符串（存在的时候）
     *
     * @param messageId       消息ID
     * @param mqttProperties  MQTT用户属性
     * @param mqttMessageType 消息类型
     */
    private void logReceiveMsgReasonStringIfNecessary(int messageId, MqttProperties mqttProperties, MqttMessageType mqttMessageType) {
        if (mqttProperties != null) {
            MqttProperties.StringProperty reasonStringProperty = (MqttProperties.StringProperty) mqttProperties.getProperty(MqttProperties.MqttPropertyType.REASON_STRING.value());
            if (reasonStringProperty != null) {
                String value = reasonStringProperty.value();
                if (EmptyUtils.isNotBlank(value)) {
                    LogUtils.warn(DefaultMqttDelegateHandler.class, "client:" + mqttConnectParameter.getClientId() + ",received an exception with a message id of " + messageId + " and a message type of " + mqttMessageType + ",reason string :" + value);
                }
            }
        }
    }


}
