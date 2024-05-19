package com.github.netty.mqtt.client.callback;


import com.github.netty.mqtt.client.support.util.AssertUtils;

/**
 * @Date: 2022/4/11 16:04
 * @Description: MQTT回调的结果
 * @author: xzc-coder
 */
public class MqttCallbackResult {

    /**
     * 客户端ID
     */
    protected final String clientId;
    /**
     *
     */
    protected final long createTimestamp;


    public MqttCallbackResult(String clientId) {
        AssertUtils.notNull(clientId,"clientId is null");
        this.clientId = clientId;
        this.createTimestamp = System.currentTimeMillis();
    }

    public long getCreateTimestamp() {
        return createTimestamp;
    }

    public String getClientId() {
        return clientId;
    }

    @Override
    public String toString() {
        return "MqttCallbackResult{" +
                "clientId='" + clientId + '\'' +
                ", createTimestamp=" + createTimestamp +
                '}';
    }
}
