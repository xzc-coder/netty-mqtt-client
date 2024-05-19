package com.github.netty.mqtt.client.store;

import com.github.netty.mqtt.client.constant.MqttMsgDirection;
import com.github.netty.mqtt.client.msg.MqttMsg;
import com.github.netty.mqtt.client.support.util.AssertUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Date: 2023/8/28 17:18
 * @Description: MQTT内存消息储存器
 * @author: xzc-coder
 */
public class MemoryMqttMsgStore implements MqttMsgStore {
    private final Map<String, MqttMsgMap> clientSendMsgMap = new ConcurrentHashMap<>();

    private final Map<String, MqttMsgMap> clientReceiveMsgMap = new ConcurrentHashMap<>();


    @Override
    public MqttMsg getMsg(MqttMsgDirection mqttMsgDirection, String clientId, int msgId) {
        AssertUtils.notNull(clientId, "clientId is null");
        AssertUtils.notNull(mqttMsgDirection, "mqttMsgStoreType is null");
        MqttMsg mqttMsg;
        switch (mqttMsgDirection) {
            case SEND:
                mqttMsg = getSendMsg(clientId, msgId);
                break;
            case RECEIVE:
                mqttMsg = getReceiveMsg(clientId, msgId);
                break;
            default:
                throw new IllegalArgumentException(mqttMsgDirection.name() + " is illegal");
        }
        return mqttMsg;
    }

    private MqttMsg getReceiveMsg(String clientId, int msgId) {
        MqttMsg mqttMsg = null;
        MqttMsgMap mqttMsgMap = clientReceiveMsgMap.get(clientId);
        if (mqttMsgMap != null) {
            mqttMsg = mqttMsgMap.get(msgId);
        }
        return mqttMsg;
    }

    private MqttMsg getSendMsg(String clientId, int msgId) {
        MqttMsg mqttMsg = null;
        MqttMsgMap mqttMsgMap = clientSendMsgMap.get(clientId);
        if (mqttMsgMap != null) {
            mqttMsg = mqttMsgMap.get(msgId);
        }
        return mqttMsg;
    }

    @Override
    public void putMsg(MqttMsgDirection mqttMsgDirection, String clientId, MqttMsg mqttMsg) {
        AssertUtils.notNull(clientId, "clientId is null");
        AssertUtils.notNull(mqttMsg, "mqttMsg is null");
        AssertUtils.notNull(mqttMsgDirection, "mqttMsgStoreType is null");
        switch (mqttMsgDirection) {
            case SEND:
                putSendMsg(clientId, mqttMsg);
                break;
            case RECEIVE:
                putReceiveMsg(clientId, mqttMsg);
                break;
            default:
                throw new IllegalArgumentException(mqttMsgDirection.name() + " is illegal");
        }
    }


    private void putSendMsg(String clientId, MqttMsg mqttMsg) {
        MqttMsgMap mqttMsgMap = clientSendMsgMap.get(clientId);
        if (mqttMsgMap == null) {
            synchronized (clientId.intern()) {
                mqttMsgMap = clientSendMsgMap.getOrDefault(clientId, new MqttMsgMap());
                clientSendMsgMap.putIfAbsent(clientId, mqttMsgMap);
            }
        }
        mqttMsgMap.put(mqttMsg);
    }

    private void putReceiveMsg(String clientId, MqttMsg mqttMsg) {
        MqttMsgMap mqttMsgMap = clientReceiveMsgMap.get(clientId);
        if (mqttMsgMap == null) {
            synchronized (clientId.intern()) {
                mqttMsgMap = clientReceiveMsgMap.getOrDefault(clientId, new MqttMsgMap());
                clientReceiveMsgMap.putIfAbsent(clientId, mqttMsgMap);
            }
        }
        mqttMsgMap.put(mqttMsg);
    }

    @Override
    public MqttMsg removeMsg(MqttMsgDirection mqttMsgDirection, String clientId, int msgId) {
        AssertUtils.notNull(clientId, "clientId is null");
        AssertUtils.notNull(mqttMsgDirection, "mqttMsgStoreType is null");
        MqttMsg mqttMsg;
        switch (mqttMsgDirection) {
            case SEND:
                mqttMsg = removeSendMsg(clientId, msgId);
                break;
            case RECEIVE:
                mqttMsg = removeReceiveMsg(clientId, msgId);
                break;
            default:
                throw new IllegalArgumentException(mqttMsgDirection.name() + " is illegal");
        }
        return mqttMsg;
    }

    private MqttMsg removeReceiveMsg(String clientId, int msgId) {
        MqttMsg mqttMsg = null;
        MqttMsgMap mqttMsgMap = clientReceiveMsgMap.get(clientId);
        if (mqttMsgMap != null) {
            mqttMsg = mqttMsgMap.remove(msgId);
        }
        return mqttMsg;
    }

    private MqttMsg removeSendMsg(String clientId, int msgId) {
        MqttMsg mqttMsg = null;
        MqttMsgMap mqttMsgMap = clientSendMsgMap.get(clientId);
        if (mqttMsgMap != null) {
            mqttMsg = mqttMsgMap.remove(msgId);
        }
        return mqttMsg;
    }

    @Override
    public List<MqttMsg> getMsgList(MqttMsgDirection mqttMsgDirection, String clientId) {
        AssertUtils.notNull(clientId, "clientId is null");
        AssertUtils.notNull(mqttMsgDirection, "mqttMsgStoreType is null");
        List<MqttMsg> mqttMsgList = new ArrayList<>();
        List<MqttMsg> sentMsgList = getSendMsgList(clientId);
        List<MqttMsg> receiveMsgList = getReceiveMsgList(clientId);
        switch (mqttMsgDirection) {
            case SEND:
                mqttMsgList.addAll(sentMsgList);
                break;
            case RECEIVE:
                mqttMsgList.addAll(receiveMsgList);
                break;
            default:
                throw new IllegalArgumentException(mqttMsgDirection.name() + " is illegal");
        }
        return mqttMsgList;
    }

    private List<MqttMsg> getReceiveMsgList(String clientId) {
        List<MqttMsg> receiveMsgList;
        MqttMsgMap mqttMsgMap = clientReceiveMsgMap.get(clientId);
        if (mqttMsgMap != null) {
            receiveMsgList = mqttMsgMap.getList();
        } else {
            receiveMsgList = new ArrayList<>();
        }
        return receiveMsgList;
    }

    private List<MqttMsg> getSendMsgList(String clientId) {
        List<MqttMsg> sentMsgList;
        MqttMsgMap mqttMsgMap = clientSendMsgMap.get(clientId);
        if (mqttMsgMap != null) {
            sentMsgList = mqttMsgMap.getList();
        } else {
            sentMsgList = new ArrayList<>();
        }
        return sentMsgList;
    }

    @Override
    public void clearMsg(MqttMsgDirection mqttMsgDirection, String clientId) {
        AssertUtils.notNull(clientId, "clientId is null");
        AssertUtils.notNull(mqttMsgDirection, "mqttMsgStoreType is null");
        switch (mqttMsgDirection) {
            case SEND:
                clientSendMsgMap.remove(clientId);
                break;
            case RECEIVE:
                clientReceiveMsgMap.remove(clientId);
                break;
            default:
                throw new IllegalArgumentException(mqttMsgDirection.name() + " is illegal");
        }
    }

    /**
     * MQTT的消息Map对象（封装Map好操作）
     */
    private static class MqttMsgMap {
        /**
         * 存储的MQTT消息Map，key是消息ID，value是MQTT消息
         */
        private final Map<Integer, MqttMsg> mqttMsgMap = new ConcurrentHashMap<>();

        private MqttMsg get(int msgId) {
            return mqttMsgMap.get(msgId);
        }

        private MqttMsg remove(int msgId) {
            return mqttMsgMap.remove(msgId);
        }

        private void put(MqttMsg mqttMsg) {
            mqttMsgMap.put(mqttMsg.getMsgId(), mqttMsg);
        }

        private List<MqttMsg> getList() {
            return new ArrayList<>(mqttMsgMap.values());
        }
    }
}
