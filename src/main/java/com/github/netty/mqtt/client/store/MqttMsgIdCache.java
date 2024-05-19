package com.github.netty.mqtt.client.store;

import com.github.netty.mqtt.client.constant.MqttConstant;
import com.github.netty.mqtt.client.support.util.AssertUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @Date: 2023/8/28 17:46
 * @Description: MQTT的消息ID缓存器
 * @author: xzc-coder
 */
public class MqttMsgIdCache {


    /**
     * MQTT的消息ID的Map
     */
    private static final Map<String, MqttMsgIdInfo> MSG_ID_INFO_MAP = new ConcurrentHashMap<>();

    private MqttMsgIdCache() {

    }

    /**
     * 占用客户端的消息ID，需要在MQTT的客户端创建时占用（其他时候占用会产生不可预知问题），如果是不清理会话，则调用该方法让其占用一些未释放的消息ID，
     * 该方法是非线程安全的
     *
     * @param clientId     客户端ID
     * @param msgIdCollect 消息ID集合
     */
    public static void occupyMsgId(String clientId, Collection<Integer> msgIdCollect) {
        AssertUtils.notNull(clientId, "clientId is null");
        AssertUtils.notEmpty(msgIdCollect, "msgIdCollect is empty");
        msgIdCollect = msgIdCollect.stream()
                .filter(msgId -> (msgId >= MqttConstant.MQTT_MIN_MSG_ID && msgId <= MqttConstant.MQTT_MAX_MSG_ID))
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toSet());
        if(msgIdCollect.size() >= MqttConstant.MQTT_MAX_MSG_ID_NUMBER) {
            throw new IllegalArgumentException("msgIdCollect size to long");
        }
        MqttMsgIdInfo mqttMsgIdInfo = getMqttMsgIdInfo(clientId);
        mqttMsgIdInfo.putMsgIds(msgIdCollect);
    }

    /**
     * 获取客户端一个可用的消息ID
     *
     * @param clientId 客户端ID
     * @return 消息ID
     */
    public static int nextMsgId(String clientId) {
        AssertUtils.notNull(clientId, "clientId is null");
        MqttMsgIdInfo mqttMsgIdInfo = getMqttMsgIdInfo(clientId);
        return mqttMsgIdInfo.getNextId();
    }

    /**
     * 释放客户端的一个消息ID
     *
     * @param clientId 客户端ID
     * @param msgId    消息ID
     */
    public static void releaseMsgId(String clientId, int msgId) {
        AssertUtils.notNull(clientId, "clientId is null");
        MqttMsgIdInfo mqttMsgIdInfo = MSG_ID_INFO_MAP.get(clientId);
        if (mqttMsgIdInfo != null) {
            mqttMsgIdInfo.releaseMsgId(msgId);
        }
    }

    /**
     * 清理客户端的消息ID
     *
     * @param clientId 客户端ID
     */
    public static void clearMsgId(String clientId) {
        AssertUtils.notNull(clientId, "clientId is null");
        MSG_ID_INFO_MAP.remove(clientId);
    }

    /**
     * 获取客户端对应的MqttMsgIdInfo
     *
     * @param clientId 客户端ID
     * @return MqttMsgIdInfo
     */
    private static MqttMsgIdInfo getMqttMsgIdInfo(String clientId) {
        MqttMsgIdInfo mqttMsgIdInfo = MSG_ID_INFO_MAP.get(clientId);
        if (mqttMsgIdInfo == null) {
            synchronized (clientId.intern()) {
                mqttMsgIdInfo = MSG_ID_INFO_MAP.getOrDefault(clientId, new MqttMsgIdInfo());
                MSG_ID_INFO_MAP.putIfAbsent(clientId, mqttMsgIdInfo);
            }
        }
        return mqttMsgIdInfo;
    }

    /**
     * MQTT的消息ID信息类，一个客户端对应一个，存储和生成相关的消息ID
     */
    private static class MqttMsgIdInfo {
        /**
         * 被使用的消息ID的Set
         */
        private final Set<Integer> useMsgIdSet = ConcurrentHashMap.newKeySet();
        /**
         * 下一个可用的消息ID，从0开始到65535
         */
        private final AtomicInteger nextMsgId = new AtomicInteger(MqttConstant.MQTT_MIN_MSG_ID);

        /**
         * 获取一个的消息ID
         *
         * @return 可用的消息ID
         */
        private int getNextId() {
            //cas获取值，保证并发安全
            for (; ; ) {
                //当前可用消息ID
                int msgId = nextMsgId.get();
                //下一个值
                int next = msgId;
                do {
                    next++;
                    //如果下一个值大于最大值，则从最小值开始
                    next = (next > MqttConstant.MQTT_MAX_MSG_ID ? MqttConstant.MQTT_MIN_MSG_ID : next);
                } while (useMsgIdSet.contains(next));
                //cas设值成功，则表明操作成功
                if (nextMsgId.compareAndSet(msgId, next)) {
                    //添加到占用列表
                    useMsgIdSet.add(msgId);
                    return msgId;
                }
            }
        }

        /**
         * 释放消息ID
         *
         * @param msgId
         */
        private void releaseMsgId(int msgId) {
            useMsgIdSet.remove(msgId);
        }

        /**
         * 添加消息ID
         *
         * @param msgIdCollect 消息ID集合
         */
        private void putMsgIds(Collection<Integer> msgIdCollect) {
            for (Integer msgId : msgIdCollect) {
                if (msgId != null) {
                    useMsgIdSet.add(msgId);
                    if (msgId.equals(nextMsgId.get())) {
                        nextMsgId.incrementAndGet();
                    }
                }
            }
        }
    }
}
