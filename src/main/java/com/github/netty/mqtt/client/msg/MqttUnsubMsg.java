package com.github.netty.mqtt.client.msg;

import com.github.netty.mqtt.client.support.util.AssertUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * @Date: 2023/8/25 11:21
 * @Description: MQTT取消订阅消息
 * @author: xzc-coder
 */
public class MqttUnsubMsg {


    private final int msgId;
    /**
     * 取消订阅列表
     */
    private final List<String> topicList = new ArrayList<>();

    public MqttUnsubMsg(int msgId, List<String> topicList) {
        AssertUtils.notEmpty(topicList, "topicList is empty");
        this.msgId = msgId;
        this.topicList.addAll(topicList);
    }

    public MqttUnsubMsg(int msgId, String topic) {
        AssertUtils.notNull(topic, "topic is null");
        this.msgId = msgId;
        topicList.add(topic);
    }

    public List<String> getTopicList() {
        return topicList;
    }

    public int getMsgId() {
        return msgId;
    }


    @Override
    public String toString() {
        return "MqttUnsubMsg{" +
                "msgId=" + msgId +
                ", topicList=" + topicList +
                '}';
    }
}
