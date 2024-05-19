package com.github.netty.mqtt.client.msg;


import com.github.netty.mqtt.client.support.util.AssertUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Date: 2023/8/24 14:38
 * @Description: MQTT订阅消息
 * @author: xzc-coder
 */
public class MqttSubMsg {

    /**
     * 消息ID
     */
    private final int msgId;
    /**
     * 订阅的主题列表
     */
    private final List<MqttSubInfo> mqttSubInfoList = new ArrayList<>();


    public MqttSubMsg(int msgId, List<MqttSubInfo> mqttSubInfoList) {
        AssertUtils.notEmpty(mqttSubInfoList, "mqttSubInfoList is empty");
        this.msgId = msgId;
        this.mqttSubInfoList.addAll(mqttSubInfoList);
    }

    public MqttSubMsg(int msgId, MqttSubInfo mqttSubInfo) {
        AssertUtils.notNull(mqttSubInfo, "mqttSubInfo is null");
        this.msgId = msgId;
        mqttSubInfoList.add(mqttSubInfo);
    }

    public List<MqttSubInfo> getMqttSubInfoList() {
        return mqttSubInfoList;
    }

    public int getMsgId() {
        return msgId;
    }

    @Override
    public String toString() {
        return "MqttSubMsg{" +
                "msgId=" + msgId +
                ", mqttSubInfoList=" + mqttSubInfoList +
                '}';
    }
}
