package com.github.netty.mqtt.client;

import com.github.netty.mqtt.client.callback.*;
import com.github.netty.mqtt.client.constant.MqttVersion;
import com.github.netty.mqtt.client.msg.MqttSubInfo;
import com.github.netty.mqtt.client.support.util.EmptyUtils;
import com.github.netty.mqtt.client.util.PropertiesUtils;
import io.netty.handler.codec.mqtt.MqttProperties;
import io.netty.handler.codec.mqtt.MqttQoS;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Date: 2024/6/29 15:02
 * @Description: 订阅相关的测试用例
 * @author: xzc-coder
 */
@RunWith(JUnit4.class)
public class SubscribeTest {

    private static final MqttClientFactory mqttClientFactory = new DefaultMqttClientFactory();
    /**
     * 临时客户端，用来发消息
     */
    private static MqttClient tempMqttClient;

    private static final String TEST_TOPIC = "testSubscribe";

    private static final int WAIT_TIMEOUT_SECONDS = 2;

    @BeforeClass
    public static void beforeClass() throws IOException {
        PropertiesUtils.loadTestProperties();
        String host = PropertiesUtils.getHost();
        int port = PropertiesUtils.getPort();
        String clientId = System.currentTimeMillis() + "-test";
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter(clientId);
        mqttConnectParameter.setHost(host);
        mqttConnectParameter.setPort(port);
        mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_5_0_0);
        tempMqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        tempMqttClient.connect();
    }

    @AfterClass
    public static void afterClass() {
        mqttClientFactory.close();
    }

    @Test
    public void testSubscribe() throws InterruptedException {
        String host = PropertiesUtils.getHost();
        int port = PropertiesUtils.getPort();
        String clientId = PropertiesUtils.getClientId();
        byte[] bytes = new byte[]{1, 1, 1, 1};
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter(clientId);
        mqttConnectParameter.setHost(host);
        mqttConnectParameter.setPort(port);
        mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_3_1_1);
        MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicBoolean subscribeSuccess = new AtomicBoolean(false);
        AtomicBoolean receiveSubscribeSuccess = new AtomicBoolean(false);
        mqttClient.addMqttCallback(new MqttCallback() {
            @Override
            public void subscribeCallback(MqttSubscribeCallbackResult mqttSubscribeCallbackResult) {
                if (EmptyUtils.isNotEmpty(mqttSubscribeCallbackResult.getSubscribeCallbackInfoList())) {
                    MqttSubscribeCallbackInfo mqttSubscribeCallbackInfo = mqttSubscribeCallbackResult.getSubscribeCallbackInfoList().get(0);
                    String subscribeTopic = mqttSubscribeCallbackInfo.getSubscribeTopic();
                    boolean subscribed = mqttSubscribeCallbackInfo.isSubscribed();
                    subscribeSuccess.set((subscribed && TEST_TOPIC.equals(subscribeTopic)));
                }
            }

            @Override
            public void messageReceiveCallback(MqttReceiveCallbackResult receiveCallbackResult) {
                byte[] payload = receiveCallbackResult.getPayload();
                String topic = receiveCallbackResult.getTopic();
                if (Arrays.equals(payload, bytes) && TEST_TOPIC.equals(topic)) {
                    receiveSubscribeSuccess.set(true);
                    countDownLatch.countDown();
                }
            }
        });
        mqttClient.connect();
        mqttClient.subscribe(TEST_TOPIC, MqttQoS.EXACTLY_ONCE);
        //发送消息
        tempMqttClient.publish(bytes, TEST_TOPIC, MqttQoS.EXACTLY_ONCE);
        //阻塞等待唤醒
        countDownLatch.await(WAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        Assert.assertTrue(subscribeSuccess.get() && receiveSubscribeSuccess.get());
    }

    @Test
    public void testSubscribe5() throws InterruptedException {
        String host = PropertiesUtils.getHost();
        int port = PropertiesUtils.getPort();
        String clientId = PropertiesUtils.getClientId();
        byte[] bytes = new byte[]{1, 1, 1, 1};
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter(clientId);
        mqttConnectParameter.setHost(host);
        mqttConnectParameter.setPort(port);
        mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_5_0_0);
        MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        MqttProperties.UserProperties mqttUserProperties = new MqttProperties.UserProperties();
        mqttUserProperties.add("name", "xzc-coder");
        AtomicBoolean subscribeSuccess = new AtomicBoolean(false);
        AtomicBoolean receiveSubscribeSuccess = new AtomicBoolean(false);
        mqttClient.addMqttCallback(new MqttCallback() {
            @Override
            public void subscribeCallback(MqttSubscribeCallbackResult mqttSubscribeCallbackResult) {
                if (EmptyUtils.isNotEmpty(mqttSubscribeCallbackResult.getSubscribeCallbackInfoList())) {
                    MqttSubscribeCallbackInfo mqttSubscribeCallbackInfo = mqttSubscribeCallbackResult.getSubscribeCallbackInfoList().get(0);
                    String subscribeTopic = mqttSubscribeCallbackInfo.getSubscribeTopic();
                    boolean subscribed = mqttSubscribeCallbackInfo.isSubscribed();
                    subscribeSuccess.set((subscribed && TEST_TOPIC.equals(subscribeTopic)));
                }
            }

            @Override
            public void messageReceiveCallback(MqttReceiveCallbackResult receiveCallbackResult) {
                byte[] payload = receiveCallbackResult.getPayload();
                String topic = receiveCallbackResult.getTopic();
                if (Arrays.equals(payload, bytes) && TEST_TOPIC.equals(topic)) {
                    receiveSubscribeSuccess.set(true);
                    countDownLatch.countDown();
                }
            }
        });
        mqttClient.connect();
        mqttClient.subscribe(new MqttSubInfo(TEST_TOPIC, MqttQoS.EXACTLY_ONCE), 1, mqttUserProperties);
        //发送消息
        tempMqttClient.publish(bytes, TEST_TOPIC, MqttQoS.EXACTLY_ONCE);
        //阻塞等待唤醒
        countDownLatch.await(WAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        Assert.assertTrue(subscribeSuccess.get() && receiveSubscribeSuccess.get());
    }

    @Test
    public void testUnsubscribe() throws InterruptedException {
        String host = PropertiesUtils.getHost();
        int port = PropertiesUtils.getPort();
        String clientId = PropertiesUtils.getClientId();
        byte[] bytes = new byte[]{1, 1, 1, 1};
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter(clientId);
        mqttConnectParameter.setHost(host);
        mqttConnectParameter.setPort(port);
        mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_3_1_1);
        MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicBoolean subscribeSuccess = new AtomicBoolean(false);
        AtomicBoolean receiveSubscribeSuccess = new AtomicBoolean(false);
        AtomicBoolean unsubscribeSuccess = new AtomicBoolean(false);
        mqttClient.addMqttCallback(new MqttCallback() {
            @Override
            public void subscribeCallback(MqttSubscribeCallbackResult mqttSubscribeCallbackResult) {
                if (EmptyUtils.isNotEmpty(mqttSubscribeCallbackResult.getSubscribeCallbackInfoList())) {
                    MqttSubscribeCallbackInfo mqttSubscribeCallbackInfo = mqttSubscribeCallbackResult.getSubscribeCallbackInfoList().get(0);
                    String subscribeTopic = mqttSubscribeCallbackInfo.getSubscribeTopic();
                    boolean subscribed = mqttSubscribeCallbackInfo.isSubscribed();
                    subscribeSuccess.set((subscribed && TEST_TOPIC.equals(subscribeTopic)));
                }
            }

            @Override
            public void unsubscribeCallback(MqttUnSubscribeCallbackResult mqttUnSubscribeCallbackResult) {
                if (EmptyUtils.isNotEmpty(mqttUnSubscribeCallbackResult.getUnsubscribeInfoList())) {
                    MqttUnSubscribeCallbackInfo unSubscribeCallbackInfo = mqttUnSubscribeCallbackResult.getUnsubscribeInfoList().get(0);
                    boolean unSubscribed = unSubscribeCallbackInfo.isUnSubscribed();
                    String topic = unSubscribeCallbackInfo.getTopic();
                    unsubscribeSuccess.set((unSubscribed && TEST_TOPIC.equals(topic)));
                }
            }

            @Override
            public void messageReceiveCallback(MqttReceiveCallbackResult receiveCallbackResult) {
                byte[] payload = receiveCallbackResult.getPayload();
                String topic = receiveCallbackResult.getTopic();
                if (Arrays.equals(payload, bytes) && TEST_TOPIC.equals(topic)) {
                    receiveSubscribeSuccess.set(true);
                    countDownLatch.countDown();
                }
            }
        });
        mqttClient.connect();
        mqttClient.subscribe(TEST_TOPIC, MqttQoS.EXACTLY_ONCE);
        //取消订阅
        mqttClient.unsubscribe(TEST_TOPIC);
        //发送消息
        tempMqttClient.publish(bytes, TEST_TOPIC, MqttQoS.EXACTLY_ONCE);
        //阻塞等待唤醒，取消订阅此处会超时唤醒以证明取消订阅后接受不到消息
        countDownLatch.await(WAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        //取消订阅后接受消息应该为false
        Assert.assertTrue(subscribeSuccess.get() && !receiveSubscribeSuccess.get() && unsubscribeSuccess.get());
    }


    @Test
    public void testUnsubscribe5() throws InterruptedException {
        String host = PropertiesUtils.getHost();
        int port = PropertiesUtils.getPort();
        String clientId = PropertiesUtils.getClientId();
        byte[] bytes = new byte[]{1, 1, 1, 1};
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter(clientId);
        mqttConnectParameter.setHost(host);
        mqttConnectParameter.setPort(port);
        mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_5_0_0);
        MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        AtomicBoolean subscribeSuccess = new AtomicBoolean(false);
        AtomicBoolean receiveSubscribeSuccess = new AtomicBoolean(false);
        AtomicBoolean unsubscribeSuccess = new AtomicBoolean(false);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        mqttClient.addMqttCallback(new MqttCallback() {
            @Override
            public void subscribeCallback(MqttSubscribeCallbackResult mqttSubscribeCallbackResult) {
                if (EmptyUtils.isNotEmpty(mqttSubscribeCallbackResult.getSubscribeCallbackInfoList())) {
                    MqttSubscribeCallbackInfo mqttSubscribeCallbackInfo = mqttSubscribeCallbackResult.getSubscribeCallbackInfoList().get(0);
                    String subscribeTopic = mqttSubscribeCallbackInfo.getSubscribeTopic();
                    boolean subscribed = mqttSubscribeCallbackInfo.isSubscribed();
                    subscribeSuccess.set((subscribed && TEST_TOPIC.equals(subscribeTopic)));
                }
            }

            @Override
            public void unsubscribeCallback(MqttUnSubscribeCallbackResult mqttUnSubscribeCallbackResult) {
                if (EmptyUtils.isNotEmpty(mqttUnSubscribeCallbackResult.getUnsubscribeInfoList())) {
                    MqttUnSubscribeCallbackInfo unSubscribeCallbackInfo = mqttUnSubscribeCallbackResult.getUnsubscribeInfoList().get(0);
                    boolean unSubscribed = unSubscribeCallbackInfo.isUnSubscribed();
                    String topic = unSubscribeCallbackInfo.getTopic();
                    unsubscribeSuccess.set((unSubscribed && TEST_TOPIC.equals(topic)));
                }
            }

            @Override
            public void messageReceiveCallback(MqttReceiveCallbackResult receiveCallbackResult) {
                String topic = receiveCallbackResult.getTopic();
                byte[] payload = receiveCallbackResult.getPayload();
                if (Arrays.equals(payload, bytes) && TEST_TOPIC.equals(topic)) {
                    receiveSubscribeSuccess.set(true);
                    countDownLatch.countDown();
                }
            }
        });
        mqttClient.connect();
        mqttClient.subscribe(TEST_TOPIC, MqttQoS.EXACTLY_ONCE);
        //取消订阅 添加MQTT5信息
        MqttProperties.UserProperties mqttUserProperties = new MqttProperties.UserProperties();
        mqttUserProperties.add("name", "xzc-coder");
        mqttClient.unsubscribe(TEST_TOPIC, mqttUserProperties);
        //发送消息
        tempMqttClient.publishFuture(bytes, TEST_TOPIC, MqttQoS.EXACTLY_ONCE);
        //阻塞等待唤醒，取消订阅此处会超时唤醒以证明取消订阅后接受不到消息
        countDownLatch.await(WAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        //取消订阅后接受消息应该为false
        Assert.assertTrue(subscribeSuccess.get() && !receiveSubscribeSuccess.get() && unsubscribeSuccess.get());
    }
}
