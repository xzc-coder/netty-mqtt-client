package com.github.netty.mqtt.client.callback;

import com.github.netty.mqtt.client.support.util.AssertUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date: 2022/1/6 15:44
 * @Description: mqtt订阅回调结果
 * @author: xzc-coder
 */
public class MqttSubscribeCallbackResult extends MqttCallbackResult {

    private final List<MqttSubscribeCallbackInfo> subscribeCallbackInfoList = new ArrayList<>();
    private final int msgId;

    public MqttSubscribeCallbackResult(String clientId,int msgId,List<MqttSubscribeCallbackInfo> subscribeCallbackInfoList) {
        super(clientId);
        AssertUtils.notNull(subscribeCallbackInfoList,"subscribeCallbackInfoList is null");
        this.subscribeCallbackInfoList.addAll(subscribeCallbackInfoList);
        this.msgId = msgId;
    }

    public List<MqttSubscribeCallbackInfo> getSubscribeCallbackInfoList() {
        return subscribeCallbackInfoList;
    }

    public int getMsgId() {
        return msgId;
    }

    @Override
    public String toString() {
        return "MqttSubscribeCallbackResult{" +
                "subscribeCallbackInfoList=" + subscribeCallbackInfoList +
                ", msgId=" + msgId +
                "} " + super.toString();
    }
}
