package com.github.netty.mqtt.client.msg;


import com.github.netty.mqtt.client.constant.MqttConstant;
import com.github.netty.mqtt.client.constant.MqttMsgDirection;
import com.github.netty.mqtt.client.constant.MqttMsgState;
import io.netty.handler.codec.mqtt.MqttProperties;
import io.netty.handler.codec.mqtt.MqttQoS;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * @Date: 2023/8/24 14:17
 * @Description: MQTT消息
 * @author: xzc-coder
 */
public class MqttMsg implements Serializable {


    private static final long serialVersionUID = 8823986273348138028L;

    /**
     * 消息ID
     */
    private Integer msgId;
    /**
     * 主题
     */
    private String topic;
    /**
     * qos
     */
    private MqttQoS qos;
    /**
     * 是否保留消息
     */
    private boolean retain;
    /**
     * 消息状态
     */
    private MqttMsgState msgState;
    /**
     * 载荷
     */
    private byte[] payload;

    /**
     * 是否重复发送
     */
    private boolean dup;
    /**
     * 消息方向
     */
    private MqttMsgDirection mqttMsgDirection;

    /**
     * MQTT5
     * 发布消息属性，不序列化
     */
    private transient MqttProperties mqttProperties;

    /**
     * 原因码
     */
    private byte reasonCode;

    /**
     * 创建时间戳
     */
    private long createTimestamp;


    public MqttMsg(int msgId, String topic) {
        this(msgId, new byte[0], topic);
    }

    public MqttMsg(int msgId, byte[] payload, String topic) {
        this(msgId, payload, topic, MqttQoS.AT_MOST_ONCE);
    }

    public MqttMsg(int msgId, byte[] payload, String topic, MqttQoS qos) {
        this(msgId, payload, topic, qos, false);
    }

    public MqttMsg(int msgId, byte[] payload, String topic, MqttQoS qos, boolean retain) {
        this(msgId, payload, topic, qos, retain, false);
    }

    public MqttMsg(int msgId, byte[] payload, String topic, MqttQoS qos, boolean retain, MqttMsgDirection mqttMsgDirection) {
        this(msgId, payload, topic, qos, retain, false, MqttMsgState.PUBLISH, mqttMsgDirection);
    }

    public MqttMsg(int msgId, byte[] payload, String topic, MqttQoS qos, boolean retain, MqttMsgState msgState) {
        this(msgId, payload, topic, qos, retain, false, msgState, MqttMsgDirection.SEND);
    }

    public MqttMsg(int msgId, byte[] payload, String topic, MqttQoS qos, boolean retain, MqttMsgState msgState, MqttMsgDirection mqttMsgDirection) {
        this(msgId, payload, topic, qos, retain, false, msgState, mqttMsgDirection);
    }


    public MqttMsg(int msgId, byte[] payload, String topic, MqttQoS qos, MqttMsgState msgState) {
        this(msgId, payload, topic, qos, false, false, msgState, MqttMsgDirection.SEND);
    }

    public MqttMsg(int msgId, byte[] payload, String topic, MqttQoS qos, MqttMsgDirection mqttMsgDirection) {
        this(msgId, payload, topic, qos, false, false, MqttMsgState.PUBLISH, mqttMsgDirection);
    }

    public MqttMsg(int msgId, byte[] payload, String topic, MqttQoS qos, boolean retain, boolean dup) {
        this(msgId, payload, topic, qos, retain, dup, MqttMsgState.PUBLISH, MqttMsgDirection.SEND);
    }

    public MqttMsg(int msgId, byte[] payload, String topic, MqttQoS qos, boolean retain, boolean dup, MqttMsgState msgState, MqttMsgDirection mqttMsgDirection) {
        this.msgId = msgId;
        this.payload = payload;
        this.topic = topic;
        this.qos = qos;
        this.retain = retain;
        this.dup = dup;
        this.msgState = msgState;
        this.mqttMsgDirection = mqttMsgDirection;
        this.reasonCode = MqttConstant.MESSAGE_SUCCESS_REASON_CODE;
        this.createTimestamp = System.currentTimeMillis();
    }

    public String getTopic() {
        return topic;
    }

    public MqttQoS getQos() {
        return qos;
    }

    public boolean isRetain() {
        return retain;
    }

    public MqttMsgState getMsgState() {
        return msgState;
    }

    public void setMsgState(MqttMsgState msgState) {
        this.msgState = msgState;
    }

    public byte[] getPayload() {
        return payload;
    }

    public boolean isDup() {
        return dup;
    }

    public void setDup(boolean dup) {
        this.dup = dup;
    }

    public int getMsgId() {
        return msgId;
    }

    public MqttMsgDirection getMqttMsgDirection() {
        return mqttMsgDirection;
    }

    public void setMqttMsgDirection(MqttMsgDirection mqttMsgDirection) {
        this.mqttMsgDirection = mqttMsgDirection;
    }

    public long getCreateTimestamp() {
        return createTimestamp;
    }

    public void setMsgId(Integer msgId) {
        this.msgId = msgId;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setQos(MqttQoS qos) {
        this.qos = qos;
    }

    public void setRetain(boolean retain) {
        this.retain = retain;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public void setCreateTimestamp(long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public MqttProperties getMqttProperties() {
        return mqttProperties;
    }

    public void setMqttProperties(MqttProperties mqttProperties) {
        this.mqttProperties = mqttProperties;
    }

    public byte getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(byte reasonCode) {
        this.reasonCode = reasonCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MqttMsg)) return false;
        MqttMsg mqttMsg = (MqttMsg) o;
        return retain == mqttMsg.retain && dup == mqttMsg.dup && reasonCode == mqttMsg.reasonCode && createTimestamp == mqttMsg.createTimestamp && Objects.equals(msgId, mqttMsg.msgId) && Objects.equals(topic, mqttMsg.topic) && qos == mqttMsg.qos && msgState == mqttMsg.msgState && Arrays.equals(payload, mqttMsg.payload) && mqttMsgDirection == mqttMsg.mqttMsgDirection && Objects.equals(mqttProperties, mqttMsg.mqttProperties);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(msgId, topic, qos, retain, msgState, dup, mqttMsgDirection, mqttProperties, reasonCode, createTimestamp);
        result = 31 * result + Arrays.hashCode(payload);
        return result;
    }
}
