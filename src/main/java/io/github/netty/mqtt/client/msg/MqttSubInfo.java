package io.github.netty.mqtt.client.msg;

import io.github.netty.mqtt.client.support.util.AssertUtils;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubscriptionOption;

/**
 * MQTT单个订阅信息
 * @author: xzc-coder
 */
public class MqttSubInfo {

    /**
     * 主题
     */
    private final String topic;
    /**
     * qos
     */
    private final MqttQoS qos;

    /**
     * MQTT5
     * 非本地，是否不接受自己发布的消息,false表示接受，true表示不接受，主要适用于桥接场景
     */
    private boolean noLocal;

    /**
     * MQTT5
     * broker转发该消息是是否保持消息中Retain的标识
     */
    private boolean retainAsPublished;
    /**
     * MQTT5
     * 当订阅建立时，保留消息如何处理。
     */
    private MqttSubscriptionOption.RetainedHandlingPolicy retainHandling;


    public MqttSubInfo(String topic, MqttQoS qos) {
        this(topic,qos,false);
    }

    public MqttSubInfo(String topic, MqttQoS qos, boolean noLocal){
        this(topic,qos,noLocal,false);
    }

    public MqttSubInfo(String topic, MqttQoS qos, boolean noLocal, boolean retainAsPublished) {
        this(topic,qos,noLocal,retainAsPublished,MqttSubscriptionOption.RetainedHandlingPolicy.SEND_AT_SUBSCRIBE);
    }

    public MqttSubInfo(String topic, MqttQoS qos, boolean noLocal, boolean retainAsPublished, MqttSubscriptionOption.RetainedHandlingPolicy retainHandling) {
        AssertUtils.notNull(topic, "topic is null");
        AssertUtils.notNull(qos, "qos is null");
        AssertUtils.notNull(retainHandling,"retainHandling is null");
        this.topic = topic;
        this.qos = qos;
        this.noLocal = noLocal;
        this.retainAsPublished = retainAsPublished;
        this.retainHandling = retainHandling;
    }

    public String getTopic() {
        return topic;
    }


    public MqttQoS getQos() {
        return qos;
    }

    public boolean isNoLocal() {
        return noLocal;
    }

    public void setNoLocal(boolean noLocal) {
        this.noLocal = noLocal;
    }

    public boolean isRetainAsPublished() {
        return retainAsPublished;
    }

    public void setRetainAsPublished(boolean retainAsPublished) {
        this.retainAsPublished = retainAsPublished;
    }

    public MqttSubscriptionOption.RetainedHandlingPolicy getRetainHandling() {
        return retainHandling;
    }

    public void setRetainHandling(MqttSubscriptionOption.RetainedHandlingPolicy retainHandling) {
        if(retainHandling != null) {
            this.retainHandling = retainHandling;
        }
    }

    @Override
    public String toString() {
        return "MqttSubInfo{" +
                "topic='" + topic + '\'' +
                ", qos=" + qos +
                ", noLocal=" + noLocal +
                ", retainAsPublished=" + retainAsPublished +
                ", retainHandling=" + retainHandling +
                '}';
    }
}
