package com.github.netty.mqtt.client.callback;

import com.github.netty.mqtt.client.constant.MqttAuthState;
import com.github.netty.mqtt.client.support.util.AssertUtils;
import io.netty.handler.codec.mqtt.MqttProperties;

/**
 * @Date: 2021/12/28 16:39
 * @Description: MQTT连接丢失回调结果
 * @author: xzc-coder
 */
public class MqttConnectLostCallbackResult extends MqttCallbackResult {

    /**
     * MQTT认证状态
     */
    private final MqttAuthState mqttAuthState;

    /**
     * MQTT5
     * 原因码，正常断开则为0x00，为null或其他值为异常断开
     */
    private Byte reasonCode;


    public MqttConnectLostCallbackResult(String clientId, MqttAuthState mqttAuthState) {
        super(clientId);
        AssertUtils.notNull(mqttAuthState, "mqttAuthState is null");
        this.mqttAuthState = mqttAuthState;
    }


    public Byte getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(Byte reasonCode) {
        this.reasonCode = reasonCode;
    }

    public MqttAuthState getMqttAuthState() {
        return mqttAuthState;
    }

    /**
     * MQTT5
     * 获取原因字符串
     *
     * @return 原因字符串
     */
    public String getReasonString() {
        String reasonString = null;
        if (mqttProperties != null) {
            reasonString = getStringMqttPropertyValue(MqttProperties.MqttPropertyType.REASON_STRING);
        }
        return reasonString;
    }

    /**
     * MQTT5
     * 获取服务端参考
     *
     * @return 服务端参考
     */
    public String getServerReference() {
        String serverReference = null;
        if (mqttProperties != null) {
            serverReference = getStringMqttPropertyValue(MqttProperties.MqttPropertyType.SERVER_REFERENCE);
        }
        return serverReference;
    }


    @Override
    public String toString() {
        return "MqttConnectLostCallbackResult{" +
                "mqttAuthState=" + mqttAuthState +
                ", reasonCode=" + reasonCode +
                "} " + super.toString();
    }
}
