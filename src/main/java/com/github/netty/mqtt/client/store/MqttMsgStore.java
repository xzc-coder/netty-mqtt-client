package com.github.netty.mqtt.client.store;

import com.github.netty.mqtt.client.constant.MqttMsgDirection;
import com.github.netty.mqtt.client.msg.MqttMsg;

import java.util.List;

/**
 * @Date: 2023/8/28 17:17
 * @Description: MQTT消息储存器接口
 * @author: xzc-coder
 */
public interface MqttMsgStore {

    /**
     * 获取一个MQTT消息
     *
     * @param mqttMsgDirection 消息的方向
     * @param clientId         客户端ID
     * @param msgId            消息ID
     * @return MQTT消息
     */
    MqttMsg getMsg(MqttMsgDirection mqttMsgDirection, String clientId, int msgId);

    /**
     * 存储一个MQTT消息
     *
     * @param mqttMsgDirection 方向
     * @param clientId         客户端ID
     * @param mqttMsg          MQTT消息
     */
    void putMsg(MqttMsgDirection mqttMsgDirection, String clientId, MqttMsg mqttMsg);

    /**
     * 移除一个MQTT消息
     *
     * @param mqttMsgDirection 方向
     * @param clientId         客户端ID
     * @param msgId            消息ID
     * @return MQTT消息
     */
    MqttMsg removeMsg(MqttMsgDirection mqttMsgDirection, String clientId, int msgId);

    /**
     * 拿到客户端的MQTT消息列表
     *
     * @param mqttMsgDirection 方向
     * @param clientId         客户端ID
     * @return MQTT消息列表
     */
    List<MqttMsg> getMsgList(MqttMsgDirection mqttMsgDirection, String clientId);

    /**
     * 清理客户端的MQTT消息
     *
     * @param mqttMsgDirection 方向
     * @param clientId         客户端ID
     */
    void clearMsg(MqttMsgDirection mqttMsgDirection, String clientId);


    /**
     * 清理客户端的MQTT消息
     * @param clientId 客户端ID
     */
    default void clearMsg(String clientId) {
        clearMsg(MqttMsgDirection.SEND,clientId);
        clearMsg(MqttMsgDirection.RECEIVE,clientId);
    }

    /**
     * 关闭消息存储器
     */
    default void close() {

    }


}
