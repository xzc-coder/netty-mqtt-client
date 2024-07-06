package com.github.netty.mqtt.client.callback;


import com.github.netty.mqtt.client.constant.MqttConstant;
import com.github.netty.mqtt.client.support.util.AssertUtils;
import com.github.netty.mqtt.client.support.util.EmptyUtils;

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
    /**
     * MQTT5
     * 原因码集合
     */
    private final List<Short> unsubscribeReasonCodeList = new ArrayList<>();

    /**
     * 取消订阅信息集合
     */
    private final List<MqttUnSubscribeCallbackInfo> unsubscribeInfoList = new ArrayList<>();

    public MqttUnSubscribeCallbackResult(String clientId, int msgId, List<String> topicList) {
        this(clientId,msgId,topicList,null);
    }

    public MqttUnSubscribeCallbackResult(String clientId, int msgId, List<String> topicList,List<Short> unsubscribeReasonCodeList) {
        super(clientId);
        AssertUtils.notNull(topicList, "topicList is null");
        this.msgId = msgId;
        this.topicList.addAll(topicList);
        if(EmptyUtils.isEmpty(unsubscribeReasonCodeList)) {
            for(int i = 0; i < topicList.size(); i++) {
                unsubscribeReasonCodeList.add(MqttConstant.UNSUBSCRIPTION_SUCCESS_REASON_CODE);
            }
        }else {
            this.unsubscribeReasonCodeList.addAll(unsubscribeReasonCodeList);
        }
        for(int i = 0; i < topicList.size(); i++) {
            short reasonCode = unsubscribeReasonCodeList.get(i);
            String topic = topicList.get(i);
            MqttUnSubscribeCallbackInfo unSubscribeCallbackInfo;
            if(MqttConstant.UNSUBSCRIPTION_SUCCESS_REASON_CODE == reasonCode) {
                unSubscribeCallbackInfo = new MqttUnSubscribeCallbackInfo(true,topic);
            }else {
                unSubscribeCallbackInfo = new MqttUnSubscribeCallbackInfo(false,reasonCode,topic);
            }
            this.unsubscribeInfoList.add(unSubscribeCallbackInfo);
        }
    }

    public int getMsgId() {
        return msgId;
    }

    public List<String> getTopicList() {
        return topicList;
    }

    public List<Short> getUnsubscribeReasonCodeList() {
        return unsubscribeReasonCodeList;
    }

    public List<MqttUnSubscribeCallbackInfo> getUnsubscribeInfoList() {
        return unsubscribeInfoList;
    }

    @Override
    public String toString() {
        return "MqttUnSubscribeCallbackResult{" +
                "msgId=" + msgId +
                ", topicList=" + topicList +
                ", unsubscribeReasonCodeList=" + unsubscribeReasonCodeList +
                ", unsubscribeInfoList=" + unsubscribeInfoList +
                "} " + super.toString();
    }
}
