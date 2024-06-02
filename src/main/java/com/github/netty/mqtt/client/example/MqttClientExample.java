package com.github.netty.mqtt.client.example;

import com.github.netty.mqtt.client.DefaultMqttClientFactory;
import com.github.netty.mqtt.client.MqttClient;
import com.github.netty.mqtt.client.MqttClientFactory;
import com.github.netty.mqtt.client.MqttConnectParameter;
import com.github.netty.mqtt.client.callback.*;
import com.github.netty.mqtt.client.msg.MqttMsg;
import com.github.netty.mqtt.client.store.MemoryMqttMsgStore;
import com.github.netty.mqtt.client.store.MqttMsgStore;
import com.github.netty.mqtt.client.store.RedisMqttMsgStore;
import com.github.netty.mqtt.client.support.util.LogUtils;
import io.netty.handler.codec.mqtt.MqttQoS;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * @Date: 2023/9/5 10:57
 * @Description: MQTT客户端示例
 * @author: xzc-coder
 */

public class MqttClientExample {

    public static void main(String[] args) throws IOException, InterruptedException {
        MqttClientFactory mqttClientFactory = new DefaultMqttClientFactory();
        //使用redis消息存储器
        MqttMsgStore mqttMsgStore = new RedisMqttMsgStore(new JedisPool("127.0.0.1", 6379));
        mqttClientFactory.setMqttMsgStore(mqttMsgStore);
        //使用内存消息存储器
//        MqttMsgStore mqttMsgStore = new MemoryMqttMsgStore();
//        mqttClientFactory.setMqttMsgStore(mqttMsgStore);
        //创建连接参数，设置客户端ID
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter("xzc_test");
        //是否自动重连
        mqttConnectParameter.setAutoReconnect(true);
        //Host
        mqttConnectParameter.setHost("broker.emqx.io");
        //端口
        mqttConnectParameter.setPort(1883);
        //是否使用SSL/TLS
        mqttConnectParameter.setSsl(false);
        //是否清除会话
        mqttConnectParameter.setCleanSession(false);
        //心跳间隔
        mqttConnectParameter.setKeepAliveTimeSeconds(60);
        //连接超时时间
        mqttConnectParameter.setConnectTimeoutSeconds(30);
        //创建一个客户端
        MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        //添加回调器
        mqttClient.addMqttCallback(new DefaultMqttCallback());
        //阻塞连接至完成或超时
        mqttClient.connect();
        //订阅主题消息
        mqttClient.subscribe("testMqttClient", MqttQoS.EXACTLY_ONCE);
        Thread.sleep(1000);
        //发送消息
        mqttClient.publish("hello world!".getBytes(StandardCharsets.UTF_8), "testMqttClient", MqttQoS.EXACTLY_ONCE);
        //阻塞
        System.in.read();
        //断开连接
        mqttClient.disconnect();
        //关闭客户端
        mqttClient.close();
        //关闭客户端工厂
        mqttClientFactory.close();
    }

    public static class DefaultMqttCallback implements MqttCallback {
        @Override
        public void subscribeCallback(MqttSubscribeCallbackResult mqttSubscribeCallbackResult) {
            List<MqttSubscribeCallbackInfo> subscribeCallbackInfoList = mqttSubscribeCallbackResult.getSubscribeCallbackInfoList();
            for (MqttSubscribeCallbackInfo mqttSubscribeCallbackInfo : subscribeCallbackInfoList) {
                LogUtils.info(this.getClass(), "订阅主题：" + mqttSubscribeCallbackInfo.getSubscribeTopic() + "完成");
            }
        }

        @Override
        public void unsubscribeCallback(MqttUnSubscribeCallbackResult mqttUnSubscribeCallbackResult) {
            List<String> unSubscribeCallbackResultTopicList = mqttUnSubscribeCallbackResult.getTopicList();
            for (String topic : unSubscribeCallbackResultTopicList) {
                LogUtils.info(this.getClass(), "取消订阅主题：" + topic + "完成");
            }
        }

        @Override
        public void messageSendCallback(MqttSendCallbackResult mqttSendCallbackResult) {
            MqttMsg mqttMsg = mqttSendCallbackResult.getMqttMsg();
            String content = new String(mqttMsg.getPayload());
            String topic = mqttMsg.getTopic();
            LogUtils.info(this.getClass(), "主题：" + topic + "，消息：" + content + " 发送成功");
        }

        @Override
        public void messageReceiveCallback(MqttReceiveCallbackResult receiveCallbackResult) {
            MqttMsg mqttMsg = receiveCallbackResult.getMqttMsg();
            String content = new String(mqttMsg.getPayload());
            String topic = mqttMsg.getTopic();
            LogUtils.info(this.getClass(), "接收到主题：" + topic + "，消息：" + content);
        }

        @Override
        public void channelConnectCallback(MqttConnectCallbackResult mqttConnectCallbackResult) {
            String clientId = mqttConnectCallbackResult.getClientId();
            Throwable cause = mqttConnectCallbackResult.getCause();
            if (cause == null) {
                LogUtils.info(this.getClass(), "客户端：" + clientId + "，TCP连接成功");
            } else {
                LogUtils.error(this.getClass(), "客户端：" + clientId + "，TCP连接失败," + "原因:" + cause.getMessage());
            }
        }

        @Override
        public void connectCompleteCallback(MqttConnectCallbackResult mqttConnectCallbackResult) {
            String clientId = mqttConnectCallbackResult.getClientId();
            Throwable cause = mqttConnectCallbackResult.getCause();
            if (cause == null) {
                LogUtils.info(this.getClass(), "客户端：" + clientId + "，连接成功");
            } else {
                LogUtils.error(this.getClass(), "客户端：" + clientId + "，连接失败," + "原因:" + cause.getMessage());
            }
        }

        @Override
        public void connectLostCallback(MqttConnectLostCallbackResult mqttConnectLostCallbackResult) {
            String clientId = mqttConnectLostCallbackResult.getClientId();
            LogUtils.info(this.getClass(), "客户端：" + clientId + "，连接丢失");
        }

        @Override
        public void heartbeatCallback(MqttHeartbeatCallbackResult mqttHeartbeatCallbackResult) {
            String clientId = mqttHeartbeatCallbackResult.getClientId();
            LogUtils.info(this.getClass(), "客户端：" + clientId + "，收到心跳");
        }

        @Override
        public void channelExceptionCaught(MqttConnectParameter mqttConnectParameter, MqttChannelExceptionCallbackResult mqttChannelExceptionCallbackResult) {
            String clientId = mqttChannelExceptionCallbackResult.getClientId();
            Throwable cause = mqttChannelExceptionCallbackResult.getCause();
            LogUtils.info(this.getClass(), "客户端：" + clientId + " 通道异常，原因:" + cause);
        }
    }
}
