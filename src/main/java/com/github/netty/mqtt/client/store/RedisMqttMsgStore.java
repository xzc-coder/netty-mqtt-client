package com.github.netty.mqtt.client.store;

import com.github.netty.mqtt.client.constant.MqttMsgDirection;
import com.github.netty.mqtt.client.exception.MqttException;
import com.github.netty.mqtt.client.msg.MqttMsg;
import com.github.netty.mqtt.client.support.util.AssertUtils;
import com.github.netty.mqtt.client.support.util.EmptyUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @program: netty-mqtt-client
 * @author: 1178794144
 * @create: 2024-06-01 01:12:23
 **/
public class RedisMqttMsgStore implements MqttMsgStore {


    /**
     * 获取redis发送消息的key，后面跟客户端ID
     */
    private static final String MQTT_SEND_MSG_REDIS_PREFIX = "mqtt-client:send-message:";
    /**
     * 获取redis接受消息的key，后面跟客户端ID
     */
    private static final String MQTT_RECEIVE_MSG_REDIS_PREFIX = "mqtt-client:receive-message:";

    private final JedisPool jedisPool;


    public RedisMqttMsgStore(JedisPool jedisPool) {
        AssertUtils.notNull(jedisPool, "jedisPool is null");
        this.jedisPool = jedisPool;
    }


    @Override
    public MqttMsg getMsg(MqttMsgDirection mqttMsgDirection, String clientId, int msgId) {
        AssertUtils.notNull(clientId, "clientId is null");
        AssertUtils.notNull(mqttMsgDirection, "mqttMsgStoreType is null");
        MqttMsg mqttMsg;
        try (Jedis jedis = jedisPool.getResource()) {
            byte[] key = getKey(mqttMsgDirection, clientId);
            byte[] field = String.valueOf(msgId).getBytes(StandardCharsets.UTF_8);
            mqttMsg = deserialize(jedis.hget(key, field));
            return mqttMsg;
        }
    }

    @Override
    public void putMsg(MqttMsgDirection mqttMsgDirection, String clientId, MqttMsg mqttMsg) {
        AssertUtils.notNull(clientId, "clientId is null");
        AssertUtils.notNull(mqttMsg, "mqttMsg is null");
        AssertUtils.notNull(mqttMsgDirection, "mqttMsgStoreType is null");
        try (Jedis jedis = jedisPool.getResource()) {
            byte[] key = getKey(mqttMsgDirection, clientId);
            byte[] field = String.valueOf(mqttMsg.getMsgId()).getBytes(StandardCharsets.UTF_8);
            jedis.hset(key, field, serialize(mqttMsg));
        }
    }

    @Override
    public MqttMsg removeMsg(MqttMsgDirection mqttMsgDirection, String clientId, int msgId) {
        AssertUtils.notNull(clientId, "clientId is null");
        AssertUtils.notNull(mqttMsgDirection, "mqttMsgStoreType is null");
        MqttMsg mqttMsg;
        try (Jedis jedis = jedisPool.getResource()) {
            byte[] key = getKey(mqttMsgDirection, clientId);
            byte[] field = String.valueOf(msgId).getBytes(StandardCharsets.UTF_8);
            mqttMsg = deserialize(jedis.hget(key, field));
            jedis.hdel(key, field);
            return mqttMsg;
        }
    }

    @Override
    public List<MqttMsg> getMsgList(MqttMsgDirection mqttMsgDirection, String clientId) {
        AssertUtils.notNull(clientId, "clientId is null");
        AssertUtils.notNull(mqttMsgDirection, "mqttMsgStoreType is null");
        List<MqttMsg> mqttMsgList = new ArrayList<>();
        try (Jedis jedis = jedisPool.getResource()) {
            byte[] key = getKey(mqttMsgDirection, clientId);
            Map<byte[], byte[]> msgMap = jedis.hgetAll(key);
            if (EmptyUtils.isNotEmpty(msgMap)) {
                for (byte[] msgBytes : msgMap.values()) {
                    MqttMsg mqttMsg = deserialize(msgBytes);
                    if (mqttMsg != null) {
                        mqttMsgList.add(mqttMsg);
                    }
                }
            }
        }
        return mqttMsgList;
    }


    @Override
    public void clearMsg(MqttMsgDirection mqttMsgDirection, String clientId) {
        AssertUtils.notNull(clientId, "clientId is null");
        AssertUtils.notNull(mqttMsgDirection, "mqttMsgStoreType is null");
        byte[] key = getKey(mqttMsgDirection, clientId);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }
    }

    @Override
    public void close() {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }

    /**
     * 把MQTT消息序列化
     * @param mqttMsg MQTT消息
     * @return 序列化后的二进制
     */
    private byte[] serialize(MqttMsg mqttMsg) {
        if (mqttMsg == null) {
            return null;
        }
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(mqttMsg);
            byte[] bytes = bos.toByteArray();
            return bytes;
        } catch (Exception e) {
            throw new MqttException(e);
        }
    }


    /**
     * 把MQTT消息反序列化
     * @param bytes 序列化后的二进制
     * @return MQTT消息
     */
    private MqttMsg deserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes); ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (MqttMsg) ois.readObject();
        } catch (Exception e) {
            throw new MqttException(e);
        }
    }


    private byte[] getKey(MqttMsgDirection mqttMsgDirection, String clientId) {
        byte[] key;
        switch (mqttMsgDirection) {
            case SEND:
                key = (MQTT_SEND_MSG_REDIS_PREFIX + clientId).getBytes(StandardCharsets.UTF_8);
                break;
            case RECEIVE:
                key = (MQTT_RECEIVE_MSG_REDIS_PREFIX + clientId).getBytes(StandardCharsets.UTF_8);
                break;
            default:
                throw new IllegalArgumentException(mqttMsgDirection.name() + " is illegal");
        }
        return key;
    }
}
