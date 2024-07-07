package io.github.netty.mqtt.client;

import io.github.netty.mqtt.client.constant.MqttMsgDirection;
import io.github.netty.mqtt.client.constant.MqttMsgState;
import io.github.netty.mqtt.client.msg.MqttMsg;
import io.github.netty.mqtt.client.support.util.MqttUtils;
import io.netty.handler.codec.mqtt.MqttProperties;
import io.netty.handler.codec.mqtt.MqttQoS;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * 收发消息相关的测试用例
 * @author: xzc-coder
 */
@RunWith(JUnit4.class)
public class SerializableTest {

    @Test
    public void serializableMqttByteTest() {
        MqttMsg mqttMsg = new MqttMsg(1, "test");
        mqttMsg.setMsgState(MqttMsgState.PUBLISH);
        mqttMsg.setMqttMsgDirection(MqttMsgDirection.SEND);
        mqttMsg.setDup(true);
        mqttMsg.setRetain(true);
        mqttMsg.setQos(MqttQoS.AT_LEAST_ONCE);
        mqttMsg.setPayload(new byte[]{1, 2, 3});
        byte[] bytes = MqttUtils.serializableMsg(mqttMsg);
        MqttMsg tempMqttMsg = MqttUtils.deserializableMsg(bytes);
        Assert.assertEquals(mqttMsg, tempMqttMsg);
    }

    @Test
    public void serializableMqttBase64Test() {
        MqttMsg mqttMsg = new MqttMsg(1, "test");
        mqttMsg.setMsgState(MqttMsgState.PUBLISH);
        mqttMsg.setMqttMsgDirection(MqttMsgDirection.SEND);
        mqttMsg.setDup(true);
        mqttMsg.setRetain(true);
        mqttMsg.setQos(MqttQoS.AT_LEAST_ONCE);
        mqttMsg.setPayload(new byte[]{1, 2, 3});
        String base64 = MqttUtils.serializableMsgBase64(mqttMsg);
        MqttMsg tempMqttMsg = MqttUtils.deserializableMsgBase64(base64);
        Assert.assertEquals(mqttMsg, tempMqttMsg);
    }


    @Test
    public void serializableByteTest5() {
        MqttMsg mqttMsg = new MqttMsg(1, "test");
        mqttMsg.setMsgState(MqttMsgState.PUBLISH);
        mqttMsg.setMqttMsgDirection(MqttMsgDirection.SEND);
        mqttMsg.setDup(true);
        mqttMsg.setRetain(true);
        mqttMsg.setQos(MqttQoS.AT_LEAST_ONCE);
        mqttMsg.setPayload(new byte[]{1, 2, 3});
        //mqtt5的参数
        mqttMsg.setReasonCode((byte) 1);
        MqttProperties mqttProperties = new MqttProperties();
        //只支持发布消息的属性
        mqttProperties.add(new MqttProperties.IntegerProperty(MqttProperties.MqttPropertyType.TOPIC_ALIAS.value(), 20));
        mqttMsg.setMqttProperties(mqttProperties);
        byte[] bytes = MqttUtils.serializableMsg(mqttMsg);
        MqttMsg tempMqttMsg = MqttUtils.deserializableMsg(bytes);
        //因为MqttProperties没有重写equals，所以不能使用MqttMsg的equals
        Assert.assertNotNull(tempMqttMsg);
        MqttProperties.MqttProperty mqttProperty = mqttMsg.getMqttProperties().getProperty(MqttProperties.MqttPropertyType.TOPIC_ALIAS.value());
        MqttProperties.MqttProperty tempMqttProperty = tempMqttMsg.getMqttProperties().getProperty(MqttProperties.MqttPropertyType.TOPIC_ALIAS.value());
        Assert.assertEquals(mqttProperty, tempMqttProperty);
    }

    @Test
    public void serializableBase64Test5() {
        MqttMsg mqttMsg = new MqttMsg(1, "test");
        mqttMsg.setMsgState(MqttMsgState.PUBLISH);
        mqttMsg.setMqttMsgDirection(MqttMsgDirection.SEND);
        mqttMsg.setDup(true);
        mqttMsg.setRetain(true);
        mqttMsg.setQos(MqttQoS.AT_LEAST_ONCE);
        mqttMsg.setPayload(new byte[]{1, 2, 3});
        //mqtt5的参数
        mqttMsg.setReasonCode((byte) 1);
        MqttProperties mqttProperties = new MqttProperties();
        //只支持发布消息的属性
        mqttProperties.add(new MqttProperties.IntegerProperty(MqttProperties.MqttPropertyType.TOPIC_ALIAS.value(), 20));
        mqttMsg.setMqttProperties(mqttProperties);
        String base64 = MqttUtils.serializableMsgBase64(mqttMsg);
        MqttMsg tempMqttMsg = MqttUtils.deserializableMsgBase64(base64);
        //因为MqttProperties没有重写equals，所以不能使用MqttMsg的equals
        Assert.assertNotNull(tempMqttMsg);
        MqttProperties.MqttProperty mqttProperty = mqttMsg.getMqttProperties().getProperty(MqttProperties.MqttPropertyType.TOPIC_ALIAS.value());
        MqttProperties.MqttProperty tempMqttProperty = tempMqttMsg.getMqttProperties().getProperty(MqttProperties.MqttPropertyType.TOPIC_ALIAS.value());
        Assert.assertEquals(mqttProperty, tempMqttProperty);
    }


}
