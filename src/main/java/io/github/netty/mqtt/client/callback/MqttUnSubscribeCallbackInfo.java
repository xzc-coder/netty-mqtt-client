package io.github.netty.mqtt.client.callback;


import io.github.netty.mqtt.client.constant.MqttConstant;

/**
 * MQTT取消订阅回调结果项
 * @author: xzc-coder
 */
public class MqttUnSubscribeCallbackInfo {

    /**
     * 该条主题是否取消订阅成功
     */
    private final boolean isUnSubscribed;
    /**
     * 取消订阅失败时，原因码
     */
    private final Short reasonCode;

    /**
     * 主题
     */
    private final String topic;

    public MqttUnSubscribeCallbackInfo(boolean isUnSubscribed, String topic) {
        this(isUnSubscribed, MqttConstant.UNSUBSCRIPTION_SUCCESS_REASON_CODE,topic);
    }

    public MqttUnSubscribeCallbackInfo(boolean isUnSubscribed, Short reasonCode, String topic) {
        this.isUnSubscribed = isUnSubscribed;
        this.reasonCode = reasonCode;
        this.topic = topic;
    }

    public boolean isUnSubscribed() {
        return isUnSubscribed;
    }

    public Short getReasonCode() {
        return reasonCode;
    }

    public String getTopic() {
        return topic;
    }
}
