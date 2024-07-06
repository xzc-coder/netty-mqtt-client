package com.github.netty.mqtt.client;

import com.github.netty.mqtt.client.callback.MqttCallback;
import com.github.netty.mqtt.client.callback.MqttConnectCallbackResult;
import com.github.netty.mqtt.client.constant.MqttVersion;
import com.github.netty.mqtt.client.msg.MqttDisconnectMsg;
import com.github.netty.mqtt.client.support.future.MqttFutureWrapper;
import com.github.netty.mqtt.client.util.PropertiesUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Date: 2024/6/28 20:12
 * @Description: 连接相关的测试用例
 * @author: xzc-coder
 */
@RunWith(JUnit4.class)
public class ConnectTest {

    private static MqttClientFactory mqttClientFactory = new DefaultMqttClientFactory();

    @BeforeClass
    public static void beforeClass() throws IOException {
        PropertiesUtils.loadTestProperties();
    }

    @AfterClass
    public static void afterClass() {
        mqttClientFactory.close();
    }


    @Test
    public void testMqttConnect() {
        String host = PropertiesUtils.getHost();
        int port = PropertiesUtils.getPort();
        String clientId = PropertiesUtils.getClientId();
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter(clientId);
        mqttConnectParameter.setHost(host);
        mqttConnectParameter.setPort(port);
        mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_3_1_1);
        MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        try {
            mqttClient.connect();
        } catch (Exception e) {
            Assert.fail("clientId connect failed,cause : " + e.getMessage());
        }
        Assert.assertTrue(mqttClient.isOnline());
    }


    @Test
    public void testMqtt5Connect() {
        String host = PropertiesUtils.getHost();
        int port = PropertiesUtils.getPort();
        String clientId = PropertiesUtils.getClientId();
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter(clientId);
        mqttConnectParameter.setHost(host);
        mqttConnectParameter.setPort(port);
        mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_5_0_0);
        //mqtt5的参数
        mqttConnectParameter.setReceiveMaximum(10240);
        mqttConnectParameter.setTopicAliasMaximum(2000);
        mqttConnectParameter.addMqttUserProperty("name", "xzc-coder");
        mqttConnectParameter.addMqttUserProperty("sex", "male");
        MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        try {
            mqttClient.connect();
        } catch (Exception e) {
            Assert.fail("clientId connect failed,cause : " + e.getMessage());
        }
        Assert.assertTrue(mqttClient.isOnline());
    }

    @Test
    public void testMqttConnectListener() {
        String host = PropertiesUtils.getHost();
        int port = PropertiesUtils.getPort();
        String clientId = PropertiesUtils.getClientId();
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter(clientId);
        mqttConnectParameter.setHost(host);
        mqttConnectParameter.setPort(port);
        mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_3_1_1);
        MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        MqttFutureWrapper mqttFutureWrapper = mqttClient.connectFuture();
        mqttFutureWrapper.addListener(mqttFuture -> {
            Assert.assertTrue(mqttFuture.isSuccess());
            Assert.assertTrue(mqttClient.isOnline());
        });
        mqttFutureWrapper.syncUninterruptibly();
    }

    @Test
    public void testMqtt5ConnectListener() {
        String host = PropertiesUtils.getHost();
        int port = PropertiesUtils.getPort();
        String clientId = PropertiesUtils.getClientId();
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter(clientId);
        mqttConnectParameter.setHost(host);
        mqttConnectParameter.setPort(port);
        mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_5_0_0);
        //mqtt5的参数
        mqttConnectParameter.setReceiveMaximum(10240);
        mqttConnectParameter.setTopicAliasMaximum(2000);
        mqttConnectParameter.addMqttUserProperty("name", "xzc-coder");
        mqttConnectParameter.addMqttUserProperty("sex", "male");
        MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        MqttFutureWrapper mqttFutureWrapper = mqttClient.connectFuture();
        mqttFutureWrapper.addListener(mqttFuture -> {
            Assert.assertTrue(mqttFuture.isSuccess());
            Assert.assertTrue(mqttClient.isOnline());
        });
        mqttFutureWrapper.syncUninterruptibly();
    }

    @Test
    public void testMqttConnectCallback() {
        String host = PropertiesUtils.getHost();
        int port = PropertiesUtils.getPort();
        String clientId = PropertiesUtils.getClientId();
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter(clientId);
        mqttConnectParameter.setHost(host);
        mqttConnectParameter.setPort(port);
        mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_3_1_1);
        MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        //添加回调
        mqttClient.addMqttCallback(new MqttCallback() {
            @Override
            public void connectCompleteCallback(MqttConnectCallbackResult mqttConnectCallbackResult) {
                Assert.assertTrue(mqttClient.isOnline());
            }
        });
        mqttClient.connect();
    }


    @Test
    public void testMqtt5ConnectCallback() {
        String host = PropertiesUtils.getHost();
        int port = PropertiesUtils.getPort();
        String clientId = PropertiesUtils.getClientId();
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter(clientId);
        mqttConnectParameter.setHost(host);
        mqttConnectParameter.setPort(port);
        mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_5_0_0);
        //mqtt5的参数
        mqttConnectParameter.setReceiveMaximum(10240);
        mqttConnectParameter.setTopicAliasMaximum(2000);
        mqttConnectParameter.addMqttUserProperty("name", "xzc-coder");
        mqttConnectParameter.addMqttUserProperty("sex", "male");
        MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        //添加回调
        mqttClient.addMqttCallback(new MqttCallback() {
            @Override
            public void connectCompleteCallback(MqttConnectCallbackResult mqttConnectCallbackResult) {
                Assert.assertTrue(mqttClient.isOnline());
            }
        });
        mqttClient.connect();
    }

    @Test
    public void testMqttDisconnect() {
        String host = PropertiesUtils.getHost();
        int port = PropertiesUtils.getPort();
        String clientId = PropertiesUtils.getClientId();
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter(clientId);
        mqttConnectParameter.setHost(host);
        mqttConnectParameter.setPort(port);
        mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_3_1_1);
        MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        mqttClient.connect();
        Assert.assertTrue(mqttClient.isOnline());
        mqttClient.disconnect();
        Assert.assertFalse(mqttClient.isActive());
    }


    @Test
    public void testMqtt5Disconnect() {
        String host = PropertiesUtils.getHost();
        int port = PropertiesUtils.getPort();
        String clientId = PropertiesUtils.getClientId();
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter(clientId);
        mqttConnectParameter.setHost(host);
        mqttConnectParameter.setPort(port);
        mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_5_0_0);
        MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        mqttClient.connect();
        Assert.assertTrue(mqttClient.isOnline());
        //使用MQTT5参数断开连接
        MqttDisconnectMsg mqttDisconnectMsg = new MqttDisconnectMsg();
        mqttDisconnectMsg.setReasonCode((byte) 0);
        mqttDisconnectMsg.setSessionExpiryIntervalSeconds(1);
        mqttClient.disconnect(mqttDisconnectMsg);
        Assert.assertFalse(mqttClient.isActive());
    }

    @Test
    public void testReconnect() throws InterruptedException {
        String host = PropertiesUtils.getHost();
        int port = PropertiesUtils.getPort();
        String clientId = PropertiesUtils.getClientId();
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter(clientId);
        mqttConnectParameter.setHost(host);
        mqttConnectParameter.setPort(port);
        //设置自动重连
        mqttConnectParameter.setAutoReconnect(true);
        int keepAliveSeconds = 1;
        //心跳是活跃时间的3倍，则会超时被断开
        BigDecimal keepAliveTimeCoefficient = new BigDecimal("3");
        mqttConnectParameter.setKeepAliveTimeSeconds(keepAliveSeconds);
        mqttConnectParameter.setKeepAliveTimeCoefficient(keepAliveTimeCoefficient);
        mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_3_1_1);
        MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        AtomicInteger connectCount = new AtomicInteger(0);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        int reconnectTimeoutSeconds = keepAliveSeconds * 5;
        mqttClient.addMqttCallback(new MqttCallback() {
            @Override
            public void connectCompleteCallback(MqttConnectCallbackResult mqttConnectCallbackResult) {
                //连接一次则加1
                int count = connectCount.incrementAndGet();
                if(count > 1) {
                    //有过重连则唤醒
                    countDownLatch.countDown();
                }
            }
        });
        mqttClient.connect();
        countDownLatch.await(reconnectTimeoutSeconds, TimeUnit.SECONDS);
        Assert.assertTrue(connectCount.get() > 1);
    }

    @Test
    public void testMqttClose() {
        String host = PropertiesUtils.getHost();
        int port = PropertiesUtils.getPort();
        String clientId = PropertiesUtils.getClientId();
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter(clientId);
        mqttConnectParameter.setHost(host);
        mqttConnectParameter.setPort(port);
        mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_3_1_1);
        MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        Assert.assertFalse(mqttClient.isClose());
        mqttClient.close();
        Assert.assertTrue(mqttClient.isClose());
    }

    @Test
    public void testMqtt5Close() {
        String host = PropertiesUtils.getHost();
        int port = PropertiesUtils.getPort();
        String clientId = PropertiesUtils.getClientId();
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter(clientId);
        mqttConnectParameter.setHost(host);
        mqttConnectParameter.setPort(port);
        mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_5_0_0);
        MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        Assert.assertFalse(mqttClient.isClose());
        mqttClient.close();
        Assert.assertTrue(mqttClient.isClose());
    }

}
