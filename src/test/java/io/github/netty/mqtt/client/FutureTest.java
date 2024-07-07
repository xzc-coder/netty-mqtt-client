package io.github.netty.mqtt.client;

import io.github.netty.mqtt.client.support.future.DefaultMqttFuture;
import io.github.netty.mqtt.client.support.future.MqttFuture;
import io.github.netty.mqtt.client.support.future.MqttFutureKey;
import io.github.netty.mqtt.client.util.PropertiesUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

/**
 * Future相关的测试用例
 * @author: xzc-coder
 */
@RunWith(JUnit4.class)
public class FutureTest {

    @BeforeClass
    public static void beforeClass() throws IOException {
        PropertiesUtils.loadTestProperties();
    }

    @Test
    public void testFutureSuccess() {
        Object key = new Object();
        MqttFutureKey futureKey = new MqttFutureKey(PropertiesUtils.getClientId(), key);
        MqttFuture mqttFuture = new DefaultMqttFuture(futureKey);
        //异步唤醒
        new Thread(() -> {
            try {
                //模拟业务，休眠100毫秒
                Thread.sleep(100);
            } catch (InterruptedException e) {
                //ignore
            }
            //唤醒
            mqttFuture.setSuccess(true);
        }).start();
        mqttFuture.awaitCompleteUninterruptibly();
        Assert.assertTrue((Boolean) mqttFuture.getResult() && mqttFuture.isSuccess());
    }


    @Test
    public void testFutureFail() {
        Object key = new Object();
        MqttFutureKey futureKey = new MqttFutureKey(PropertiesUtils.getClientId(), key);
        MqttFuture mqttFuture = new DefaultMqttFuture(futureKey);
        //异步唤醒
        new Thread(() -> {
            try {
                //模拟业务，休眠100毫秒
                Thread.sleep(100);
            } catch (InterruptedException e) {
                //ignore
            }
            //唤醒
            mqttFuture.setFailure(new RuntimeException("test future fail"));
        }).start();
        mqttFuture.awaitCompleteUninterruptibly();
        Object cause = mqttFuture.getCause();
        Assert.assertTrue((cause instanceof Exception) && !mqttFuture.isSuccess());
    }
}
