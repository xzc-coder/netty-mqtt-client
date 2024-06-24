package com.github.netty.mqtt.client.plugin;

import com.github.netty.mqtt.client.MqttClient;
import com.github.netty.mqtt.client.MqttConfiguration;
import com.github.netty.mqtt.client.MqttConnectParameter;
import com.github.netty.mqtt.client.callback.MqttCallback;
import com.github.netty.mqtt.client.connector.MqttConnector;
import com.github.netty.mqtt.client.handler.MqttDelegateHandler;
import com.github.netty.mqtt.client.msg.MqttMsg;
import com.github.netty.mqtt.client.msg.MqttSubInfo;
import com.github.netty.mqtt.client.msg.MqttSubMsg;
import com.github.netty.mqtt.client.msg.MqttUnsubMsg;
import com.github.netty.mqtt.client.support.future.MqttFuture;
import com.github.netty.mqtt.client.support.future.MqttFutureWrapper;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;


/**
 * @Date: 2024/6/24 20:09
 * @Description: 拦截器接口
 * @author: xzc-coder
 */
public abstract class CglibTargetHelper {


    /**
     * 根据具体的对象创建包装对象
     *
     * @param target 目标对象
     * @return 包装对象
     */
    public static Object createCglibTarget(Object target) {
        Object cglibTarget;
        if (target instanceof MqttClient) {
            cglibTarget = new CglibTargetMqttClient();
        } else if (target instanceof MqttConnector) {
            cglibTarget = new CglibTargetMqttConnector();
        } else if (target instanceof MqttDelegateHandler) {
            cglibTarget = new CglibTargetMqttDelegateHandler();
        } else {
            cglibTarget = target;
        }
        return cglibTarget;
    }


    public static class CglibTargetMqttClient implements MqttClient {

        @Override
        public InetSocketAddress getLocalAddress() {
            return null;
        }

        @Override
        public InetSocketAddress getRemoteAddress() {
            return null;
        }

        @Override
        public String getClientId() {
            return null;
        }

        @Override
        public MqttConnectParameter getMqttConnectParameter() {
            return null;
        }

        @Override
        public MqttFutureWrapper connectFuture() {
            return null;
        }

        @Override
        public void connect() {

        }

        @Override
        public void disconnect() {

        }

        @Override
        public MqttFutureWrapper disconnectFuture() {
            return null;
        }

        @Override
        public MqttFutureWrapper publishFuture(byte[] payload, String topic, MqttQoS qos, boolean retain) {
            return null;
        }

        @Override
        public MqttFutureWrapper publishFuture(byte[] payload, String topic, MqttQoS qos) {
            return null;
        }

        @Override
        public MqttFutureWrapper publishFuture(byte[] payload, String topic) {
            return null;
        }

        @Override
        public void publish(byte[] payload, String topic, MqttQoS qos, boolean retain) {

        }

        @Override
        public void publish(byte[] payload, String topic, MqttQoS qos) {

        }

        @Override
        public void publish(byte[] payload, String topic) {

        }

        @Override
        public void subscribe(String topic, MqttQoS qos) {

        }

        @Override
        public void subscribes(List<MqttSubInfo> mqttSubInfoList) {

        }

        @Override
        public void subscribes(List<String> topicList, MqttQoS qos) {

        }

        @Override
        public MqttFutureWrapper subscribesFuture(List<String> topicList, MqttQoS qos) {
            return null;
        }

        @Override
        public MqttFutureWrapper subscribeFuture(String topic, MqttQoS qos) {
            return null;
        }

        @Override
        public MqttFutureWrapper subscribesFuture(List<MqttSubInfo> mqttSubInfoList) {
            return null;
        }

        @Override
        public void unsubscribes(List<String> topicList) {

        }

        @Override
        public void unsubscribe(String topic) {

        }

        @Override
        public MqttFutureWrapper unsubscribeFuture(String topic) {
            return null;
        }

        @Override
        public MqttFutureWrapper unsubscribesFuture(List<String> topicList) {
            return null;
        }

        @Override
        public void addMqttCallback(MqttCallback mqttCallback) {

        }

        @Override
        public void addMqttCallbacks(Collection<MqttCallback> mqttCallbacks) {

        }

        @Override
        public boolean isClose() {
            return false;
        }

        @Override
        public void close() {

        }
    }


    public static class CglibTargetMqttConnector implements MqttConnector {

        @Override
        public MqttFuture<Channel> connect() {
            return null;
        }

        @Override
        public MqttDelegateHandler getMqttDelegateHandler() {
            return null;
        }

        @Override
        public MqttConfiguration getMqttConfiguration() {
            return null;
        }
    }

    public static class CglibTargetMqttDelegateHandler implements MqttDelegateHandler {

        @Override
        public void channelConnect(Channel channel) {

        }

        @Override
        public void sendConnect(Channel channel) {

        }

        @Override
        public void connack(Channel channel, MqttConnAckMessage mqttConnAckMessage) {

        }

        @Override
        public void sendDisconnect(Channel channel, MqttFuture mqttFuture) {

        }

        @Override
        public void disconnect(Channel channel) {

        }

        @Override
        public void sendSubscribe(Channel channel, MqttSubMsg mqttSubMsg) {

        }

        @Override
        public void suback(Channel channel, MqttSubAckMessage mqttSubAckMessage) {

        }

        @Override
        public void sendUnsubscribe(Channel channel, MqttUnsubMsg mqttUnsubMsg) {

        }

        @Override
        public void unsuback(Channel channel, MqttUnsubAckMessage mqttUnsubAckMessage) {

        }

        @Override
        public void sendPingreq(Channel channel) {

        }

        @Override
        public void pingresp(Channel channel, MqttMessage mqttPingRespMessage) {

        }

        @Override
        public void sendPublish(Channel channel, MqttMsg mqttMsg, MqttFuture msgFuture) {

        }

        @Override
        public void publish(Channel channel, MqttPublishMessage mqttPublishMessage) {

        }

        @Override
        public void sendPuback(Channel channel, MqttMsg mqttMsg) {

        }

        @Override
        public void puback(Channel channel, MqttPubAckMessage mqttPubAckMessage) {

        }

        @Override
        public void sendPubrec(Channel channel, MqttMsg mqttMsg) {

        }

        @Override
        public void pubrec(Channel channel, MqttMessage mqttMessage) {

        }

        @Override
        public void sendPubrel(Channel channel, MqttMsg mqttMsg) {

        }

        @Override
        public void pubrel(Channel channel, MqttMessage mqttMessage) {

        }

        @Override
        public void sendPubcomp(Channel channel, MqttMsg mqttMsg) {

        }

        @Override
        public void pubcomp(Channel channel, MqttMessage mqttMessage) {

        }

        @Override
        public void exceptionCaught(Channel channel, Throwable cause) {

        }
    }

}
