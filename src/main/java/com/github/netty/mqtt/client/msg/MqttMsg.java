package com.github.netty.mqtt.client.msg;


import com.github.netty.mqtt.client.constant.MqttMsgDirection;
import com.github.netty.mqtt.client.constant.MqttMsgState;
import io.netty.handler.codec.mqtt.MqttQoS;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @Date: 2023/8/24 14:17
 * @Description: MQTT消息
 * @author: xzc-coder
 */
public class MqttMsg implements Serializable {


    private static final long serialVersionUID = 8823986273348138027L;

    /**
     * 消息ID
     */
    private final Integer msgId;
    /**
     * 主题
     */
    private final String topic;
    /**
     * qos
     */
    private final MqttQoS qos;
    /**
     * 是否保存
     */
    private final boolean retain;
    /**
     * 消息状态
     */
    private MqttMsgState msgState;
    /**
     * 载荷
     */
    private final byte[] payload;

    private boolean dup;
    /**
     * 消息方向
     */
    private MqttMsgDirection mqttMsgDirection;
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

    public void setCreateTimestamp(long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }


    @Override
    public String toString() {
        return "MqttMsg{" +
                "msgId=" + msgId +
                ", topic='" + topic + '\'' +
                ", qos=" + qos +
                ", retain=" + retain +
                ", msgState=" + msgState +
                ", payload=" + Arrays.toString(payload) +
                ", dup=" + dup +
                ", mqttMsgDirection=" + mqttMsgDirection +
                ", createTimestamp=" + createTimestamp +
                '}';
    }
}
