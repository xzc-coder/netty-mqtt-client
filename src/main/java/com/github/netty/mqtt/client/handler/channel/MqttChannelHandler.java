package com.github.netty.mqtt.client.handler.channel;


import com.github.netty.mqtt.client.constant.MqttConstant;
import com.github.netty.mqtt.client.exception.MqttException;
import com.github.netty.mqtt.client.handler.DefaultMqttDelegateHandler;
import com.github.netty.mqtt.client.handler.MqttDelegateHandler;
import com.github.netty.mqtt.client.support.util.AssertUtils;
import com.github.netty.mqtt.client.support.util.LogUtils;
import io.netty.channel.*;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @Date: 2021/12/24 13:11
 * @Description: MQTT在Netty中的ChannelHandler
 * @author: xzc-coder
 */
@ChannelHandler.Sharable
public class MqttChannelHandler extends SimpleChannelInboundHandler<MqttMessage> implements ChannelOutboundHandler {

    /**
     * MQTT消息委托器
     */
    private final MqttDelegateHandler mqttDelegateHandler;
    /**
     * 发送心跳的定时任务间隔，最好比心跳小一些
     */
    private final long keepAliveScheduleIntervalMillis;
    /**
     * 心跳定时任务的基数
     */
    private final float keepAliveScheduleBase = 0.75f;

    public MqttChannelHandler(MqttDelegateHandler mqttDelegateHandler, long keepAliveTimeSecond) {
        AssertUtils.notNull(mqttDelegateHandler, "mqttDelegateHandler is null");
        this.mqttDelegateHandler = mqttDelegateHandler;
        this.keepAliveScheduleIntervalMillis = (long) (keepAliveTimeSecond * 1000 * keepAliveScheduleBase);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String clientId = channel.attr(MqttConstant.MQTT_CLIENT_ID_ATTRIBUTE_KEY).get();
        LogUtils.info(MqttChannelHandler.class, "client:" + clientId + " tcp connection successful,local:" + channel.localAddress() + ",remote:" + channel.remoteAddress());
        mqttDelegateHandler.channelConnect(channel);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MqttMessage mqttMessage) throws Exception {
        Channel channel = ctx.channel();
        String clientId = channel.attr(MqttConstant.MQTT_CLIENT_ID_ATTRIBUTE_KEY).get();
        DecoderResult decoderResult = mqttMessage.decoderResult();
        if (decoderResult.isSuccess()) {
            MqttFixedHeader mqttFixedHeader = mqttMessage.fixedHeader();
            MqttMessageType mqttMessageType = mqttFixedHeader.messageType();
            LogUtils.debug(MqttChannelHandler.class, "client:" + clientId + ", read mqtt " + mqttMessageType + " package：" + mqttMessage);
            switch (mqttMessageType) {
                case CONNACK:
                    //连接确认
                    MqttConnAckMessage mqttConnAckMessage = (MqttConnAckMessage) mqttMessage;
                    //成功则启动心跳定时任务
                    if (MqttConnectReturnCode.CONNECTION_ACCEPTED.equals(mqttConnAckMessage.variableHeader().connectReturnCode())) {
                        //定时任务间隔，比心跳间隔小，因为执行回调等需要时间，线程可能再跑其他任务
                        //心跳
                        Runnable task = new Runnable() {
                            @Override
                            public void run() {
                                if (channel.isActive()) {
                                    mqttDelegateHandler.sendPingreq(ctx.channel());
                                    channel.eventLoop().schedule(this, keepAliveScheduleIntervalMillis, TimeUnit.MILLISECONDS);
                                }
                            }
                        };
                        channel.eventLoop().schedule(task, keepAliveScheduleIntervalMillis, TimeUnit.MILLISECONDS);
                    }
                    mqttDelegateHandler.connack(channel, mqttConnAckMessage);
                    break;
                case SUBACK:
                    mqttDelegateHandler.suback(channel, (MqttSubAckMessage) mqttMessage);
                    break;
                case UNSUBACK:
                    mqttDelegateHandler.unsuback(channel, (MqttUnsubAckMessage) mqttMessage);
                    break;
                case PINGRESP:
                    mqttDelegateHandler.pingresp(channel, mqttMessage);
                    break;
                case PUBLISH:
                    mqttDelegateHandler.publish(channel, (MqttPublishMessage) mqttMessage);
                    break;
                case PUBACK:
                    mqttDelegateHandler.puback(channel, (MqttPubAckMessage) mqttMessage);
                    break;
                case PUBREC:
                    mqttDelegateHandler.pubrec(channel, mqttMessage);
                    break;
                case PUBREL:
                    mqttDelegateHandler.pubrel(channel, mqttMessage);
                    break;
                case PUBCOMP:
                    mqttDelegateHandler.pubcomp(channel, mqttMessage);
                    break;
                default:
                    LogUtils.warn(MqttChannelHandler.class, "client: " + clientId + " received a location type message");
            }
        } else {
            throw new MqttException(decoderResult.cause(),clientId);
        }
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String clientId = channel.attr(MqttConstant.MQTT_CLIENT_ID_ATTRIBUTE_KEY).get();
        LogUtils.info(MqttChannelHandler.class, "client:" + clientId + " tcp disconnected,local:" + channel.localAddress() + ",remote:" + channel.remoteAddress());
        mqttDelegateHandler.disconnect(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        try {
            Channel channel = ctx.channel();
            String clientId = channel.attr(MqttConstant.MQTT_CLIENT_ID_ATTRIBUTE_KEY).get();
            LogUtils.error(MqttChannelHandler.class, "client:" + clientId + " encountered an exception in the channel,excepiton:" + cause.getMessage());
            mqttDelegateHandler.exceptionCaught(ctx.channel(), cause);
        } finally {
            ctx.channel().close();
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (IdleState.READER_IDLE == idleStateEvent.state()) {
                ctx.close();
            }
        }
    }

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        ctx.bind(localAddress, promise);
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        ctx.connect(remoteAddress, localAddress, promise);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.disconnect(promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.close(promise);
    }

    @Override
    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.deregister(promise);
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        ctx.read();
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        Channel channel = ctx.channel();
        String clientId = channel.attr(MqttConstant.MQTT_CLIENT_ID_ATTRIBUTE_KEY).get();
        if (msg instanceof MqttMessage) {
            MqttMessage mqttMessage = (MqttMessage) msg;
            MqttMessageType mqttMessageType = mqttMessage.fixedHeader().messageType();
            LogUtils.debug(MqttChannelHandler.class, "client:" + clientId + ", write mqtt " + mqttMessageType + " package：" + mqttMessage);
        }
        ctx.write(msg, promise);
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
