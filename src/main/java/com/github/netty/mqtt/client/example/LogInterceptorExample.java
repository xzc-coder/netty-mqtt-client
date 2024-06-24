package com.github.netty.mqtt.client.example;

import com.github.netty.mqtt.client.DefaultMqttClientFactory;
import com.github.netty.mqtt.client.MqttClient;
import com.github.netty.mqtt.client.MqttClientFactory;
import com.github.netty.mqtt.client.MqttConnectParameter;
import com.github.netty.mqtt.client.connector.MqttConnector;
import com.github.netty.mqtt.client.handler.MqttDelegateHandler;
import com.github.netty.mqtt.client.plugin.Interceptor;
import com.github.netty.mqtt.client.plugin.Intercepts;
import com.github.netty.mqtt.client.plugin.Invocation;
import com.github.netty.mqtt.client.store.MemoryMqttMsgStore;
import com.github.netty.mqtt.client.store.MqttMsgStore;
import com.github.netty.mqtt.client.support.proxy.CglibProxyFactory;
import com.github.netty.mqtt.client.support.util.LogUtils;
import io.netty.handler.codec.mqtt.MqttQoS;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @Date: 2023/9/5 10:32
 * @Description: 日志拦截器案例
 * @author: xzc-coder
 */
@Intercepts(type = {MqttClient.class})
public class LogInterceptorExample implements Interceptor {


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        Method method = invocation.getMethod();
        if (method.getName().equals("publish") && args.length > 1) {
            byte[] payload = (byte[]) args[0];
            String content = new String(payload, StandardCharsets.UTF_8);
            String topic = (String) args[1];
            LogUtils.info(this.getClass(),"MqttClient 方法：" + method.getName() + " 被拦截,主题：" + topic + " 内容：" + content);
        }
        return invocation.proceed();
    }

    public static void main(String[] args) throws IOException {
        MqttClientFactory mqttClientFactory = new DefaultMqttClientFactory();
        mqttClientFactory.setProxyFactory(new CglibProxyFactory());
        MqttMsgStore mqttMsgStore = new MemoryMqttMsgStore();
        mqttClientFactory.setMqttMsgStore(mqttMsgStore);
        //添加拦截器
        mqttClientFactory.addInterceptor(new LogInterceptorExample());
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter("test-interceptor");
        mqttConnectParameter.setAutoReconnect(true);
        mqttConnectParameter.setHost("broker.emqx.io");
        mqttConnectParameter.setPort(1883);
        mqttConnectParameter.setSsl(false);
        mqttConnectParameter.setCleanSession(true);
        mqttConnectParameter.setConnectTimeoutSeconds(30);
        MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        mqttClient.connect();
        mqttClient.subscribe("testInterceptor", MqttQoS.AT_MOST_ONCE);
        //发送消息
        mqttClient.publish("hello world!".getBytes(StandardCharsets.UTF_8), "testInterceptor");
        //阻塞
        System.in.read();
        //关闭
        mqttClient.disconnect();
        mqttClient.close();
        mqttClientFactory.close();
    }
}
