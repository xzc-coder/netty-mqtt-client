package com.github.netty.mqtt.client.handler;

import com.github.netty.mqtt.client.MqttConnectParameter;
import com.github.netty.mqtt.client.callback.*;
import com.github.netty.mqtt.client.constant.MqttAuthState;
import com.github.netty.mqtt.client.constant.MqttConstant;
import com.github.netty.mqtt.client.constant.MqttMsgDirection;
import com.github.netty.mqtt.client.constant.MqttMsgState;
import com.github.netty.mqtt.client.exception.MqttException;
import com.github.netty.mqtt.client.msg.*;
import com.github.netty.mqtt.client.store.MqttMsgStore;
import com.github.netty.mqtt.client.support.future.MqttFuture;
import com.github.netty.mqtt.client.support.util.AssertUtils;
import com.github.netty.mqtt.client.support.util.EmptyUtils;
import com.github.netty.mqtt.client.support.util.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.Attribute;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @Date: 2021/12/24 13:11
 * @Description: 默认的MQTT委托处理器
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
        mqttCallback.channelConnectCallback(new MqttConnectCallbackResult(clientId, channel.attr(MqttConstant.AUTH_STATE_ATTRIBUTE_KEY).get()));
    }

    @Override
    public void sendConnect(Channel channel) {
        //连接相关
        int keepAliveTimeSeconds = mqttConnectParameter.getKeepAliveTimeSeconds();
        boolean cleanSession = mqttConnectParameter.isCleanSession();
        String clientId = mqttConnectParameter.getClientId();
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
        if (hasWill) {
            willQos = willMsg.getWillQos();
            isWillRetain = willMsg.isWillRetain();
            willTopic = willMsg.getWillTopic();
            willMessageBytes = willMsg.getWillMessageBytes();
        }
        MqttConnectMessage connectMessage = MqttMessageBuilders.connect().clientId(clientId).username(username).password(passwordBytes).cleanSession(cleanSession).protocolVersion(MqttVersion.MQTT_3_1_1).keepAlive(keepAliveTimeSeconds).willFlag(hasWill).willMessage(willMessageBytes).willQoS(willQos).willRetain(isWillRetain).willTopic(willTopic).build();
        channel.writeAndFlush(connectMessage);
    }

    @Override
    public void connack(Channel channel, MqttConnAckMessage mqttConnAckMessage) {
        String clientId = mqttConnectParameter.getClientId();
        MqttConnectReturnCode mqttConnectReturnCode = mqttConnAckMessage.variableHeader().connectReturnCode();
        boolean sessionPresent = mqttConnAckMessage.variableHeader().isSessionPresent();
        //获取Future
        MqttFuture<Channel> connectMqttFuture = MqttFuture.getFuture(clientId, channel.id().asShortText());
        MqttConnectCallbackResult mqttConnectCallbackResult;
        //根据返回Code判断是否成功
        if (MqttConnectReturnCode.CONNECTION_ACCEPTED.equals(mqttConnectReturnCode)) {
            LogUtils.info(DefaultMqttDelegateHandler.class, "client: " + clientId + " MQTT authentication successful");
            //设置为已认证
            Attribute<MqttAuthState> mqttAuthStateAttribute = channel.attr(MqttConstant.AUTH_STATE_ATTRIBUTE_KEY);
            mqttAuthStateAttribute.set(MqttAuthState.AUTH_SUCCESS);
            //当Broker返回会话不存在时，需要清除消息存储器中的消息
            if(!sessionPresent) {
                mqttMsgStore.clearMsg(mqttConnectParameter.getClientId());
            }
            //设置成功
            if (connectMqttFuture != null) {
                connectMqttFuture.setSuccess(channel);
            }
            //回调
            mqttConnectCallbackResult = new MqttConnectCallbackResult(clientId, channel.attr(MqttConstant.AUTH_STATE_ATTRIBUTE_KEY).get(),sessionPresent);
            mqttCallback.connectCompleteCallback(mqttConnectCallbackResult);
        } else {
            String connectReturnCode = Integer.toString(mqttConnectReturnCode.byteValue(), com.github.netty.mqtt.client.constant.MqttConstant.NETTY_MQTT_CONNECT_RETURN_CODE_RADIX);
            LogUtils.info(DefaultMqttDelegateHandler.class, "client: " + clientId + " MQTT authentication failed,returnCode:" + connectReturnCode);
            //设置为认证失败
            Attribute<MqttAuthState> mqttAuthStateAttribute = channel.attr(MqttConstant.AUTH_STATE_ATTRIBUTE_KEY);
            mqttAuthStateAttribute.set(MqttAuthState.AUTH_FAIL);
            String exceptionMessage = "auth failed,returnCode:" + connectReturnCode + ",please refer to: io.netty.handler.codec.mqtt.MqttConnectReturnCode";
            MqttException mqttException = new MqttException(exceptionMessage, clientId);
            //设置失败
            if (connectMqttFuture != null) {
                connectMqttFuture.setFailure(mqttException);
            }
            //唤醒
            mqttConnectCallbackResult = new MqttConnectCallbackResult(clientId, channel.attr(MqttConstant.AUTH_STATE_ATTRIBUTE_KEY).get(), mqttException,mqttConnectReturnCode.byteValue());
            mqttCallback.connectCompleteCallback(mqttConnectCallbackResult);
            //关闭连接
            channel.close();
        }
    }

    @Override
    public void sendDisconnect(Channel channel, MqttFuture mqttFuture) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.DISCONNECT, false, MqttQoS.AT_MOST_ONCE, false, 0);
        MqttMessage mqttMessage = new MqttMessage(mqttFixedHeader);
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
    public void disconnect(Channel channel) {
        MqttAuthState mqttAuthState = channel.attr(MqttConstant.AUTH_STATE_ATTRIBUTE_KEY).get();
        MqttConnectLostCallbackResult mqttConnectLostCallbackResult = new MqttConnectLostCallbackResult(clientId, mqttAuthState);
        mqttCallback.connectLostCallback(mqttConnectLostCallbackResult);
    }


    @Override
    public void sendSubscribe(Channel channel, MqttSubMsg mqttSubMsg) {
        //固定头，除了类型和remainingLength，其它的值都没有用，可变头长度为0
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.SUBSCRIBE, false, MqttQoS.AT_LEAST_ONCE, false, 2);
        //可变头
        MqttMessageIdVariableHeader mqttMessageIdVariableHeader = MqttMessageIdVariableHeader.from(mqttSubMsg.getMsgId());
        //消息体
        List<MqttTopicSubscription> mqttTopicSubscriptionList = toMqttTopicSubscriptionList(mqttSubMsg.getMqttSubInfoList());
        MqttSubscribePayload mqttSubscribePayload = new MqttSubscribePayload(mqttTopicSubscriptionList);
        MqttSubscribeMessage mqttSubscribeMessage = new MqttSubscribeMessage(mqttFixedHeader, mqttMessageIdVariableHeader, mqttSubscribePayload);
        channel.writeAndFlush(mqttSubscribeMessage);
    }


    @Override
    public void suback(Channel channel, MqttSubAckMessage mqttSubAckMessage) {
        List<Integer> serverQosList = mqttSubAckMessage.payload().grantedQoSLevels();
        int msgId = mqttSubAckMessage.variableHeader().messageId();
        List<MqttSubscribeCallbackInfo> subscribeCallbackInfoList = new ArrayList<>();
        MqttFuture<Void> subscribeFuture = MqttFuture.getFuture(clientId, msgId);
        List<MqttSubInfo> mqttSubInfoList;
        if (subscribeFuture != null) {
            subscribeFuture.setSuccess(null);
            mqttSubInfoList = ((MqttSubMsg) subscribeFuture.getParameter()).getMqttSubInfoList();
            if (EmptyUtils.isNotEmpty(mqttSubInfoList) && mqttSubInfoList.size() == serverQosList.size()) {
                for (int i = 0; i < serverQosList.size(); i++) {
                    int serverQos = serverQosList.get(i);
                    MqttQoS qoS = MqttQoS.valueOf(serverQos);
                    MqttSubscribeCallbackInfo mqttSubscribeCallbackInfo = new MqttSubscribeCallbackInfo();
                    mqttSubscribeCallbackInfo.setServerQos(qoS);
                    if (MqttQoS.FAILURE.equals(qoS)) {
                        mqttSubscribeCallbackInfo.setSubscribed(false);
                    } else {
                        mqttSubscribeCallbackInfo.setSubscribed(true);
                    }
                    if (!mqttSubInfoList.isEmpty()) {
                        MqttSubInfo mqttSubInfo = mqttSubInfoList.get(i);
                        mqttSubscribeCallbackInfo.setSubscribeTopic(mqttSubInfo.getTopic());
                        mqttSubscribeCallbackInfo.setSubscribeQos(mqttSubInfo.getQos());
                    }
                    subscribeCallbackInfoList.add(mqttSubscribeCallbackInfo);
                }
                MqttSubscribeCallbackResult mqttSubscribeCallbackResult = new MqttSubscribeCallbackResult(clientId, msgId, subscribeCallbackInfoList);
                mqttCallback.subscribeCallback(mqttSubscribeCallbackResult);
            }
        }
    }

    @Override
    public void sendUnsubscribe(Channel channel, MqttUnsubMsg mqttUnsubMsg) {
        //固定头 qos位要为 1
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.UNSUBSCRIBE, false, MqttQoS.AT_LEAST_ONCE, false, 0x02);
        //可变头
        MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(mqttUnsubMsg.getMsgId());
        //载荷
        MqttUnsubscribePayload MqttUnsubscribeMessage = new MqttUnsubscribePayload(mqttUnsubMsg.getTopicList());
        //取消订阅消息
        MqttUnsubscribeMessage mqttUnsubscribeMessage = new MqttUnsubscribeMessage(mqttFixedHeader, variableHeader, MqttUnsubscribeMessage);
        channel.writeAndFlush(mqttUnsubscribeMessage);
    }

    @Override
    public void unsuback(Channel channel, MqttUnsubAckMessage mqttUnsubAckMessage) {
        int msgId = mqttUnsubAckMessage.variableHeader().messageId();
        MqttFuture<Void> unsubscribeFuture = MqttFuture.getFuture(clientId, msgId);
        List<String> topicList;
        if (unsubscribeFuture != null) {
            unsubscribeFuture.setSuccess(null);
            topicList = ((MqttUnsubMsg) unsubscribeFuture.getParameter()).getTopicList();
            if (EmptyUtils.isNotEmpty(topicList)) {
                MqttUnSubscribeCallbackResult mqttUnSubscribeCallbackResult = new MqttUnSubscribeCallbackResult(clientId, msgId, topicList);
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
        //固定头
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH, mqttMsg.isDup(), qos, mqttMsg.isRetain(), 0);
        //可变头
        MqttPublishVariableHeader mqttPublishVariableHeader = new MqttPublishVariableHeader(mqttMsg.getTopic(), msgId);
        //消息
        MqttPublishMessage mqttPublishMessage = new MqttPublishMessage(mqttFixedHeader, mqttPublishVariableHeader, PooledByteBufAllocator.DEFAULT.directBuffer().writeBytes(mqttMsg.getPayload()));
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
        int msgId = mqttPublishMessage.variableHeader().packetId();
        String topic = mqttPublishMessage.variableHeader().topicName();
        MqttQoS qos = mqttPublishMessage.fixedHeader().qosLevel();
        boolean retain = mqttPublishMessage.fixedHeader().isRetain();
        boolean dup = mqttPublishMessage.fixedHeader().isDup();
        ByteBuf byteBuf = mqttPublishMessage.payload();
        byte[] payload = ByteBufUtil.getBytes(byteBuf);
        MqttMsg mqttMsg = new MqttMsg(msgId, payload, topic, qos, retain, dup);
        mqttMsg.setMqttMsgDirection(MqttMsgDirection.RECEIVE);
        if (MqttQoS.AT_MOST_ONCE == qos) {
            mqttMsg.setMsgState(MqttMsgState.PUBLISH);
            mqttCallback.messageReceiveCallback(new MqttReceiveCallbackResult(clientId, mqttMsg));
        } else if (MqttQoS.AT_LEAST_ONCE == qos) {
            mqttMsg.setMsgState(MqttMsgState.PUBACK);
            sendPuback(channel, mqttMsg);
        } else if (MqttQoS.EXACTLY_ONCE == qos) {
            mqttMsg.setMsgState(MqttMsgState.PUBREC);
            putReceiveQos2Msg(channel, mqttMsg);
            sendPubrec(channel, mqttMsg);
        }
    }

    @Override
    public void sendPuback(Channel channel, MqttMsg mqttMsg) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBACK, mqttMsg.isDup(), MqttQoS.AT_MOST_ONCE, mqttMsg.isRetain(), 0x02);
        MqttMessageIdVariableHeader from = MqttMessageIdVariableHeader.from(mqttMsg.getMsgId());
        MqttPubAckMessage mqttPubAckMessage = new MqttPubAckMessage(mqttFixedHeader, from);
        channel.writeAndFlush(mqttPubAckMessage).addListener(future -> mqttCallback.messageReceiveCallback(new MqttReceiveCallbackResult(clientId, mqttMsg)));
    }

    @Override
    public void puback(Channel channel, MqttPubAckMessage mqttPubAckMessage) {
        int msgId = mqttPubAckMessage.variableHeader().messageId();
        MqttFuture sendFuture = MqttFuture.getFuture(clientId, msgId);
        MqttMsg mqttMsg = removeQos2Msg(channel, msgId, MqttMsgDirection.SEND);
        if (sendFuture != null) {
            sendFuture.setSuccess(null);
        }
        if (mqttMsg != null) {
            mqttMsg.setMsgState(MqttMsgState.PUBACK);
            mqttCallback.messageSendCallback(new MqttSendCallbackResult(clientId, mqttMsg));
        }
    }

    @Override
    public void sendPubrec(Channel channel, MqttMsg mqttMsg) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBREC, false, MqttQoS.AT_MOST_ONCE, false, 0x02);
        MqttMessageIdVariableHeader from = MqttMessageIdVariableHeader.from(mqttMsg.getMsgId());
        MqttMessage mqttMessage = new MqttMessage(mqttFixedHeader, from);
        channel.writeAndFlush(mqttMessage);
    }


    @Override
    public void pubrec(Channel channel, MqttMessage mqttMessage) {
        MqttMessageIdVariableHeader messageIdVariableHeader = (MqttMessageIdVariableHeader) mqttMessage.variableHeader();
        int msgId = messageIdVariableHeader.messageId();
        MqttMsg mqttMsg = getQos2SendMsg(channel, msgId);
        if (mqttMsg != null) {
            updateQos2MsgState(channel, msgId, MqttMsgState.PUBREL, MqttMsgDirection.SEND);
        } else {
            LogUtils.warn(DefaultMqttDelegateHandler.class, "client: " + clientId + "received an unstored illegal pubrec packet with message ID " + msgId);
            mqttMsg = new MqttMsg(msgId, null, null, null, MqttMsgState.INVALID);
        }
        sendPubrel(channel, mqttMsg);
    }

    @Override
    public void sendPubrel(Channel channel, MqttMsg mqttMsg) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBREL, false, MqttQoS.AT_LEAST_ONCE, false, 0x02);
        MqttMessageIdVariableHeader mqttMessageIdVariableHeader = MqttMessageIdVariableHeader.from(mqttMsg.getMsgId());
        MqttMessage mqttMessage = new MqttMessage(mqttFixedHeader, mqttMessageIdVariableHeader);
        channel.writeAndFlush(mqttMessage);
    }

    @Override
    public void pubrel(Channel channel, MqttMessage mqttMessage) {
        MqttMessageIdVariableHeader messageIdVariableHeader = (MqttMessageIdVariableHeader) mqttMessage.variableHeader();
        int msgId = messageIdVariableHeader.messageId();
        MqttMsg mqttMsg = removeQos2Msg(channel, msgId, MqttMsgDirection.RECEIVE);
        if (mqttMsg != null) {
            mqttMsg.setMsgState(MqttMsgState.PUBCOMP);
        } else {
            LogUtils.warn(DefaultMqttDelegateHandler.class, "client: " + clientId + "received an unstored illegal pubrel packet with message ID " + msgId);
            mqttMsg = new MqttMsg(msgId, null, null, null, MqttMsgState.INVALID);
        }
        sendPubcomp(channel, mqttMsg);
    }


    @Override
    public void sendPubcomp(Channel channel, MqttMsg mqttMsg) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBCOMP, false, MqttQoS.AT_MOST_ONCE, false, 0x02);
        MqttMessageIdVariableHeader from = MqttMessageIdVariableHeader.from(mqttMsg.getMsgId());
        MqttMessage mqttMessage = new MqttMessage(mqttFixedHeader, from);
        channel.writeAndFlush(mqttMessage).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if (MqttMsgState.INVALID != mqttMsg.getMsgState()) {
                    mqttCallback.messageReceiveCallback(new MqttReceiveCallbackResult(clientId, mqttMsg));
                }
            }
        });
    }


    @Override
    public void pubcomp(Channel channel, MqttMessage mqttMessage) {
        MqttMessageIdVariableHeader messageIdVariableHeader = (MqttMessageIdVariableHeader) mqttMessage.variableHeader();
        int msgId = messageIdVariableHeader.messageId();
        MqttFuture sendFuture = MqttFuture.getFuture(clientId, msgId);
        if (sendFuture != null) {
            sendFuture.setSuccess(null);
        }
        //pubcomp消息，则为客户端发送，删除
        MqttMsg mqttMsg = removeQos2Msg(channel, msgId, MqttMsgDirection.SEND);
        if (mqttMsg != null) {
            mqttMsg.setMsgState(MqttMsgState.PUBCOMP);
            //回调
            mqttCallback.messageSendCallback(new MqttSendCallbackResult(clientId, mqttMsg));
        }
    }

    @Override
    public void exceptionCaught(Channel channel, Throwable cause) {
        MqttAuthState mqttAuthState = channel.attr(MqttConstant.AUTH_STATE_ATTRIBUTE_KEY).get();
        MqttChannelExceptionCallbackResult mqttChannelExceptionCallbackResult = new MqttChannelExceptionCallbackResult(clientId, mqttAuthState, cause);
        mqttCallback.channelExceptionCaught(mqttConnectParameter, mqttChannelExceptionCallbackResult);
    }


    private List<MqttTopicSubscription> toMqttTopicSubscriptionList(List<MqttSubInfo> mqttSubInfoList) {
        List<MqttTopicSubscription> mqttTopicSubscriptionList = new ArrayList<>(mqttSubInfoList.size());
        for (MqttSubInfo mqttSubInfo : mqttSubInfoList) {
            MqttTopicSubscription mqttTopicSubscription = new MqttTopicSubscription(mqttSubInfo.getTopic(), mqttSubInfo.getQos());
            mqttTopicSubscriptionList.add(mqttTopicSubscription);
        }
        return mqttTopicSubscriptionList;
    }


    private MqttMsg getQos2SendMsg(Channel channel, int msgId) {
        MqttMsg mqttMsg = null;
        if (mqttConnectParameter.isCleanSession()) {
            Object msg = channel.attr(MqttConstant.SEND_MSG_MAP_ATTRIBUTE_KEY).get().get(msgId);
            if (msg instanceof MqttMsg) {
                mqttMsg = (MqttMsg) msg;
            }
        } else {
            mqttMsg = mqttMsgStore.getMsg(MqttMsgDirection.SEND, clientId, msgId);
        }
        return mqttMsg;
    }

    private void putReceiveQos2Msg(Channel channel, MqttMsg mqttMsg) {
        if (mqttConnectParameter.isCleanSession()) {
            channel.attr(MqttConstant.RECEIVE_MSG_MAP_ATTRIBUTE_KEY).get().put(mqttMsg.getMsgId(), mqttMsg);
        } else {
            mqttMsgStore.putMsg(MqttMsgDirection.RECEIVE, clientId, mqttMsg);
        }
    }

    private MqttMsg removeQos2Msg(Channel channel, int msgId, MqttMsgDirection msgDirection) {
        MqttMsg mqttMsg = null;
        if (mqttConnectParameter.isCleanSession()) {
            if (msgDirection == MqttMsgDirection.SEND) {
                Object msg = channel.attr(MqttConstant.SEND_MSG_MAP_ATTRIBUTE_KEY).get().remove(msgId);
                if (msg instanceof MqttMsg) {
                    mqttMsg = (MqttMsg) msg;
                }
            } else {
                mqttMsg = channel.attr(MqttConstant.RECEIVE_MSG_MAP_ATTRIBUTE_KEY).get().remove(msgId);
            }
        } else {
            mqttMsg = mqttMsgStore.removeMsg(msgDirection, clientId, msgId);
        }
        return mqttMsg;
    }

    private void updateQos2MsgState(Channel channel, int msgId, MqttMsgState msgState, MqttMsgDirection msgDirection) {
        if (mqttConnectParameter.isCleanSession()) {
            if (msgDirection == MqttMsgDirection.SEND) {
                Object msg = channel.attr(MqttConstant.SEND_MSG_MAP_ATTRIBUTE_KEY).get().get(msgId);
                if (msg instanceof MqttMsg) {
                    MqttMsg mqttMsg = (MqttMsg) msg;
                    mqttMsg.setMsgState(msgState);
                }
            } else {
                MqttMsg mqttMsg = channel.attr(MqttConstant.RECEIVE_MSG_MAP_ATTRIBUTE_KEY).get().get(msgId);
                if (mqttMsg != null) {
                    mqttMsg.setMsgState(msgState);
                }
            }
        } else {
            MqttMsg mqttMsg = mqttMsgStore.getMsg(msgDirection, clientId, msgId);
            if (mqttMsg != null) {
                mqttMsg.setMsgState(msgState);
                mqttMsgStore.putMsg(msgDirection, clientId, mqttMsg);
            }
        }
    }
}
