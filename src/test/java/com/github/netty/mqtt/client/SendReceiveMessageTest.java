package com.github.netty.mqtt.client;

import com.github.netty.mqtt.client.callback.MqttCallback;
import com.github.netty.mqtt.client.callback.MqttReceiveCallbackResult;
import com.github.netty.mqtt.client.callback.MqttSendCallbackResult;
import com.github.netty.mqtt.client.constant.MqttVersion;
import com.github.netty.mqtt.client.msg.MqttMsgInfo;
import com.github.netty.mqtt.client.util.PropertiesUtils;
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
 * @Date: 2024/6/28 21:54
 * @Description: 收发消息相关的测试用例
 * @author: xzc-coder
 */
@RunWith(JUnit4.class)
public class SendReceiveMessageTest {

    private static MqttClientFactory mqttClientFactory;

    private static final String TEST_TOPIC = "testSubscribe";

    private static final int WAIT_TIMEOUT_SECONDS = 2;

    @BeforeClass
    public static void beforeClass() throws IOException {
        mqttClientFactory = new DefaultMqttClientFactory();
        PropertiesUtils.loadTestProperties();
    }

    @AfterClass
    public static void afterClass() {
        mqttClientFactory.close();
    }

    @Test
    public void testMqttSendReceiveMessageQos0() throws InterruptedException {
        String host = PropertiesUtils.getHost();
        int port = PropertiesUtils.getPort();
        String clientId = PropertiesUtils.getClientId();
        byte[] bytes = new byte[]{1, 1, 1, 1};
        MqttQoS mqttQoS = MqttQoS.AT_MOST_ONCE;
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter(clientId);
        mqttConnectParameter.setHost(host);
        mqttConnectParameter.setPort(port);
        mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_3_1_1);
        MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicBoolean sendMsgSuccess = new AtomicBoolean(false);
        AtomicBoolean receiveMsgSuccess = new AtomicBoolean(false);
        mqttClient.addMqttCallback(new MqttCallback() {
            @Override
            public void messageSendCallback(MqttSendCallbackResult mqttSendCallbackResult) {
                String topic = mqttSendCallbackResult.getTopic();
                MqttQoS qos = mqttSendCallbackResult.getQos();
                byte[] payload = mqttSendCallbackResult.getPayload();
                if (Arrays.equals(payload, bytes) && TEST_TOPIC.equals(topic) && mqttQoS.equals(qos)) {
                    sendMsgSuccess.set(true);
                }
            }

            @Override
            public void messageReceiveCallback(MqttReceiveCallbackResult receiveCallbackResult) {
                byte[] payload = receiveCallbackResult.getPayload();
                String topic = receiveCallbackResult.getTopic();
                MqttQoS qos = receiveCallbackResult.getQos();
                if (Arrays.equals(payload, bytes) && TEST_TOPIC.equals(topic) && mqttQoS.equals(qos)) {
                    receiveMsgSuccess.set(true);
                    countDownLatch.countDown();
                }
            }
        });
        mqttClient.connect();
        mqttClient.subscribe(TEST_TOPIC, mqttQoS);
        //发送消息
        mqttClient.publish(bytes, TEST_TOPIC, mqttQoS);
        //阻塞等待唤醒
        countDownLatch.await(WAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        Assert.assertTrue(sendMsgSuccess.get() && receiveMsgSuccess.get());
    }


    @Test
    public void testMqttSendReceiveMessageQos1() throws InterruptedException {
        String host = PropertiesUtils.getHost();
        int port = PropertiesUtils.getPort();
        String clientId = PropertiesUtils.getClientId();
        byte[] bytes = new byte[]{1, 1, 1, 1};
        MqttQoS mqttQoS = MqttQoS.AT_LEAST_ONCE;
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter(clientId);
        mqttConnectParameter.setHost(host);
        mqttConnectParameter.setPort(port);
        mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_3_1_1);
        MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicBoolean sendMsgSuccess = new AtomicBoolean(false);
        AtomicBoolean receiveMsgSuccess = new AtomicBoolean(false);
        mqttClient.addMqttCallback(new MqttCallback() {
            @Override
            public void messageSendCallback(MqttSendCallbackResult mqttSendCallbackResult) {
                String topic = mqttSendCallbackResult.getTopic();
                MqttQoS qos = mqttSendCallbackResult.getQos();
                byte[] payload = mqttSendCallbackResult.getPayload();
                if (Arrays.equals(payload, bytes) && TEST_TOPIC.equals(topic) && mqttQoS.equals(qos)) {
                    sendMsgSuccess.set(true);
                }
            }

            @Override
            public void messageReceiveCallback(MqttReceiveCallbackResult receiveCallbackResult) {
                byte[] payload = receiveCallbackResult.getPayload();
                String topic = receiveCallbackResult.getTopic();
                MqttQoS qos = receiveCallbackResult.getQos();
                if (Arrays.equals(payload, bytes) && TEST_TOPIC.equals(topic) && mqttQoS.equals(qos)) {
                    receiveMsgSuccess.set(true);
                    countDownLatch.countDown();
                }
            }
        });
        mqttClient.connect();
        mqttClient.subscribe(TEST_TOPIC, mqttQoS);
        //发送消息
        mqttClient.publish(bytes, TEST_TOPIC, mqttQoS);
        //阻塞等待唤醒
        countDownLatch.await(WAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        Assert.assertTrue(sendMsgSuccess.get() && receiveMsgSuccess.get());
    }

    @Test
    public void testMqttSendReceiveMessageQos2() throws InterruptedException {
        String host = PropertiesUtils.getHost();
        int port = PropertiesUtils.getPort();
        String clientId = PropertiesUtils.getClientId();
        byte[] bytes = new byte[]{1, 1, 1, 1};
        MqttQoS mqttQoS = MqttQoS.AT_MOST_ONCE;
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter(clientId);
        mqttConnectParameter.setHost(host);
        mqttConnectParameter.setPort(port);
        mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_3_1_1);
        MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicBoolean sendMsgSuccess = new AtomicBoolean(false);
        AtomicBoolean receiveMsgSuccess = new AtomicBoolean(false);
        mqttClient.addMqttCallback(new MqttCallback() {
            @Override
            public void messageSendCallback(MqttSendCallbackResult mqttSendCallbackResult) {
                String topic = mqttSendCallbackResult.getTopic();
                MqttQoS qos = mqttSendCallbackResult.getQos();
                byte[] payload = mqttSendCallbackResult.getPayload();
                if (Arrays.equals(payload, bytes) && TEST_TOPIC.equals(topic) && mqttQoS.equals(qos)) {
                    sendMsgSuccess.set(true);
                }
            }

            @Override
            public void messageReceiveCallback(MqttReceiveCallbackResult receiveCallbackResult) {
                byte[] payload = receiveCallbackResult.getPayload();
                String topic = receiveCallbackResult.getTopic();
                MqttQoS qos = receiveCallbackResult.getQos();
                if (Arrays.equals(payload, bytes) && TEST_TOPIC.equals(topic) && mqttQoS.equals(qos)) {
                    receiveMsgSuccess.set(true);
                    countDownLatch.countDown();
                }
            }
        });
        mqttClient.connect();
        mqttClient.subscribe(TEST_TOPIC, mqttQoS);
        //发送消息
        mqttClient.publish(bytes, TEST_TOPIC, mqttQoS);
        //阻塞等待唤醒
        countDownLatch.await(WAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        Assert.assertTrue(sendMsgSuccess.get() && receiveMsgSuccess.get());
    }


    @Test
    public void testMqtt5SendReceiveMessageQos0() throws InterruptedException {
        String host = PropertiesUtils.getHost();
        int port = PropertiesUtils.getPort();
        String clientId = PropertiesUtils.getClientId();
        final String nameKey = "name";
        final String nameValue = "xzc-coder";
        byte[] bytes = new byte[]{1, 1, 1, 1};
        MqttQoS mqttQoS = MqttQoS.AT_MOST_ONCE;
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter(clientId);
        mqttConnectParameter.setHost(host);
        mqttConnectParameter.setPort(port);
        mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_5_0_0);
        MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicBoolean sendMsgSuccess = new AtomicBoolean(false);
        AtomicBoolean receiveMsgSuccess = new AtomicBoolean(false);
        mqttClient.addMqttCallback(new MqttCallback() {
            @Override
            public void messageSendCallback(MqttSendCallbackResult mqttSendCallbackResult) {
                String topic = mqttSendCallbackResult.getTopic();
                MqttQoS qos = mqttSendCallbackResult.getQos();
                byte[] payload = mqttSendCallbackResult.getPayload();
                String name = mqttSendCallbackResult.getUserMqttPropertyValue(nameKey);
                if (Arrays.equals(payload, bytes) && TEST_TOPIC.equals(topic) && mqttQoS.equals(qos) && nameValue.equals(name)) {
                    sendMsgSuccess.set(true);
                }
            }

            @Override
            public void messageReceiveCallback(MqttReceiveCallbackResult receiveCallbackResult) {
                byte[] payload = receiveCallbackResult.getPayload();
                String topic = receiveCallbackResult.getTopic();
                MqttQoS qos = receiveCallbackResult.getQos();
                String name = receiveCallbackResult.getUserMqttPropertyValue(nameKey);
                if (Arrays.equals(payload, bytes) && TEST_TOPIC.equals(topic) && mqttQoS.equals(qos) && nameValue.equals(name)) {
                    receiveMsgSuccess.set(true);
                    countDownLatch.countDown();
                }
            }
        });
        mqttClient.connect();
        mqttClient.subscribe(TEST_TOPIC, mqttQoS);
        //发送消息
        MqttMsgInfo mqttMsgInfo = new MqttMsgInfo(TEST_TOPIC, bytes, mqttQoS);
        mqttMsgInfo.addMqttUserProperty(nameKey, nameValue);
        mqttClient.publish(mqttMsgInfo);
        //阻塞等待唤醒
        countDownLatch.await(WAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        Assert.assertTrue(sendMsgSuccess.get() && receiveMsgSuccess.get());
    }


    @Test
    public void testMqtt5SendReceiveMessageQos1() throws InterruptedException {
        String host = PropertiesUtils.getHost();
        int port = PropertiesUtils.getPort();
        String clientId = PropertiesUtils.getClientId();
        final String nameKey = "name";
        final String nameValue = "xzc-coder";
        byte[] bytes = new byte[]{1, 1, 1, 1};
        MqttQoS mqttQoS = MqttQoS.AT_LEAST_ONCE;
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter(clientId);
        mqttConnectParameter.setHost(host);
        mqttConnectParameter.setPort(port);
        mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_5_0_0);
        MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicBoolean sendMsgSuccess = new AtomicBoolean(false);
        AtomicBoolean receiveMsgSuccess = new AtomicBoolean(false);
        mqttClient.addMqttCallback(new MqttCallback() {
            @Override
            public void messageSendCallback(MqttSendCallbackResult mqttSendCallbackResult) {
                String topic = mqttSendCallbackResult.getTopic();
                MqttQoS qos = mqttSendCallbackResult.getQos();
                byte[] payload = mqttSendCallbackResult.getPayload();
                String name = mqttSendCallbackResult.getUserMqttPropertyValue(nameKey);
                if (Arrays.equals(payload, bytes) && TEST_TOPIC.equals(topic) && mqttQoS.equals(qos) && nameValue.equals(name)) {
                    sendMsgSuccess.set(true);
                }
            }

            @Override
            public void messageReceiveCallback(MqttReceiveCallbackResult receiveCallbackResult) {
                byte[] payload = receiveCallbackResult.getPayload();
                String topic = receiveCallbackResult.getTopic();
                MqttQoS qos = receiveCallbackResult.getQos();
                String name = receiveCallbackResult.getUserMqttPropertyValue(nameKey);
                if (Arrays.equals(payload, bytes) && TEST_TOPIC.equals(topic) && mqttQoS.equals(qos) && nameValue.equals(name)) {
                    receiveMsgSuccess.set(true);
                    countDownLatch.countDown();
                }
            }
        });
        mqttClient.connect();
        mqttClient.subscribe(TEST_TOPIC, mqttQoS);
        //发送消息
        MqttMsgInfo mqttMsgInfo = new MqttMsgInfo(TEST_TOPIC, bytes, mqttQoS);
        mqttMsgInfo.addMqttUserProperty(nameKey, nameValue);
        mqttClient.publish(mqttMsgInfo);
        //阻塞等待唤醒
        countDownLatch.await(WAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        Assert.assertTrue(sendMsgSuccess.get() && receiveMsgSuccess.get());
    }

    @Test
    public void testMqtt5SendReceiveMessageQos2() throws InterruptedException {
        String host = PropertiesUtils.getHost();
        int port = PropertiesUtils.getPort();
        String clientId = PropertiesUtils.getClientId();
        final String nameKey = "name";
        final String nameValue = "xzc-coder";
        byte[] bytes = new byte[]{1, 1, 1, 1};
        MqttQoS mqttQoS = MqttQoS.AT_LEAST_ONCE;
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter(clientId);
        mqttConnectParameter.setHost(host);
        mqttConnectParameter.setPort(port);
        mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_5_0_0);
        MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicBoolean sendMsgSuccess = new AtomicBoolean(false);
        AtomicBoolean receiveMsgSuccess = new AtomicBoolean(false);
        mqttClient.addMqttCallback(new MqttCallback() {
            @Override
            public void messageSendCallback(MqttSendCallbackResult mqttSendCallbackResult) {
                String topic = mqttSendCallbackResult.getTopic();
                MqttQoS qos = mqttSendCallbackResult.getQos();
                byte[] payload = mqttSendCallbackResult.getPayload();
                String name = mqttSendCallbackResult.getUserMqttPropertyValue(nameKey);
                if (Arrays.equals(payload, bytes) && TEST_TOPIC.equals(topic) && mqttQoS.equals(qos) && nameValue.equals(name)) {
                    sendMsgSuccess.set(true);
                }
            }

            @Override
            public void messageReceiveCallback(MqttReceiveCallbackResult receiveCallbackResult) {
                byte[] payload = receiveCallbackResult.getPayload();
                String topic = receiveCallbackResult.getTopic();
                MqttQoS qos = receiveCallbackResult.getQos();
                String name = receiveCallbackResult.getUserMqttPropertyValue(nameKey);
                if (Arrays.equals(payload, bytes) && TEST_TOPIC.equals(topic) && mqttQoS.equals(qos) && nameValue.equals(name)) {
                    receiveMsgSuccess.set(true);
                    countDownLatch.countDown();
                }
            }
        });
        mqttClient.connect();
        mqttClient.subscribe(TEST_TOPIC, mqttQoS);
        //发送消息
        MqttMsgInfo mqttMsgInfo = new MqttMsgInfo(TEST_TOPIC, bytes, mqttQoS);
        mqttMsgInfo.addMqttUserProperty(nameKey, nameValue);
        mqttClient.publish(mqttMsgInfo);
        //阻塞等待唤醒
        countDownLatch.await(WAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        Assert.assertTrue(sendMsgSuccess.get() && receiveMsgSuccess.get());
    }
}
