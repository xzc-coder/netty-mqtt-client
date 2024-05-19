package com.github.netty.mqtt.client.callback;


import com.github.netty.mqtt.client.support.util.AssertUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date: 2021/12/27 15:17
 * @Description: MQTT取消订阅回调结果
 * @author: xzc-coder
 */
public class MqttUnSubscribeCallbackResult extends MqttCallbackResult {

    /**
     * 消息ID
     */
    private final int msgId;
    /**
     * 取消订阅的主题集合
     */
    private final List<String> topicList = new ArrayList<>();

    public MqttUnSubscribeCallbackResult(String clientId, int msgId, List<String> topicList) {
        super(clientId);
        AssertUtils.notNull(topicList, "topicList is null");
        this.msgId = msgId;
        this.topicList.addAll(topicList);
    }

    public int getMsgId() {
        return msgId;
    }

    public List<String> getTopicList() {
        return topicList;
    }

    @Override
    public String toString() {
        return "MqttUnSubscribeCallbackResult{" +
                "msgId=" + msgId +
                ", topicList=" + topicList +
                "} " + super.toString();
    }
}
