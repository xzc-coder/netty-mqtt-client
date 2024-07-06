package com.github.netty.mqtt.client.handler.channel;


import com.github.netty.mqtt.client.MqttConnectParameter;
import com.github.netty.mqtt.client.constant.MqttConstant;
import com.github.netty.mqtt.client.exception.MqttException;
import com.github.netty.mqtt.client.handler.DefaultMqttDelegateHandler;
import com.github.netty.mqtt.client.handler.MqttDelegateHandler;
import com.github.netty.mqtt.client.support.util.AssertUtils;
import com.github.netty.mqtt.client.support.util.LogUtils;
import com.github.netty.mqtt.client.support.util.MqttUtils;
import io.netty.channel.*;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.math.BigDecimal;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @Date: 2021/12/24 13:11
 * @Description: MQTT在Netty中的ChannelHandler
 * @author: xzc-coder
 */
@ChannelHandler.Sharable
public final class MqttChannelHandler extends SimpleChannelInboundHandler<MqttMessage> implements ChannelOutboundHandler {

    /**
     * MQTT消息委托器
     */
    private final MqttDelegateHandler mqttDelegateHandler;

    /**
     * MQTT连接参数
     */
    private final MqttConnectParameter mqttConnectParameter;

    public MqttChannelHandler(MqttDelegateHandler mqttDelegateHandler, MqttConnectParameter mqttConnectParameter) {
        AssertUtils.notNull(mqttDelegateHandler, "mqttDelegateHandler is null");
        AssertUtils.notNull(mqttConnectParameter, "mqttConnectParameter is null");
        this.mqttDelegateHandler = mqttDelegateHandler;
        this.mqttConnectParameter = mqttConnectParameter;
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
                        //连接成功处理
                        connectSuccessHandle(channel,mqttConnAckMessage);
                    }
                    mqttDelegateHandler.connack(channel, mqttConnAckMessage);
                    break;
                case DISCONNECT:
                    mqttDelegateHandler.disconnect(channel, mqttMessage);
                    break;
                case AUTH:
                    mqttDelegateHandler.auth(channel,mqttMessage);
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
        mqttDelegateHandler.disconnect(ctx.channel(),null);
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
                LogUtils.warn(MqttChannelHandler.class, "client:" + mqttConnectParameter.getClientId() + " readOutTime,will disconnect.");
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


    private void connectSuccessHandle(Channel channel, MqttConnAckMessage mqttConnAckMessage) {
        //获取并设置心跳间隔
        Integer keepAliveTimeSeconds = MqttUtils.getIntegerMqttPropertyValue(mqttConnAckMessage.variableHeader().properties(), MqttProperties.MqttPropertyType.SERVER_KEEP_ALIVE, mqttConnectParameter.getKeepAliveTimeSeconds());
        channel.attr(MqttConstant.KEEP_ALIVE_TIME_ATTRIBUTE_KEY).set(keepAliveTimeSeconds);
        //1000要加L 不然会溢出
        long keepAliveTimeMills = keepAliveTimeSeconds * 1000L;
        //定时任务间隔
        long scheduleMills = mqttConnectParameter.getKeepAliveTimeCoefficient().multiply(new BigDecimal(keepAliveTimeMills)).longValue();
        long readIdleMills = keepAliveTimeMills + (keepAliveTimeMills >> 1);
        //添加一个空闲检测处理器,读检测，即1.5倍的心跳时间内，没有读取到任何数据则断开连接
        channel.pipeline().addBefore(MqttConstant.NETTY_DECODER_HANDLER_NAME,MqttConstant.NETTY_IDLE_HANDLER_NAME, new IdleStateHandler(readIdleMills, 0, 0, TimeUnit.MILLISECONDS));
        //心跳定时任务间隔执行
        Runnable task = new Runnable() {
            @Override
            public void run() {
                if (channel.isActive()) {
                    mqttDelegateHandler.sendPingreq(channel);
                    channel.eventLoop().schedule(this, scheduleMills, TimeUnit.MILLISECONDS);
                }
            }
        };
        channel.eventLoop().schedule(task, scheduleMills, TimeUnit.MILLISECONDS);
    }
}
