package com.github.netty.mqtt.client.store;

import com.github.netty.mqtt.client.constant.MqttMsgDirection;
import com.github.netty.mqtt.client.exception.MqttException;
import com.github.netty.mqtt.client.msg.MqttMsg;
import com.github.netty.mqtt.client.support.util.AssertUtils;
import com.github.netty.mqtt.client.support.util.EmptyUtils;
import com.github.netty.mqtt.client.support.util.MqttUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @Date: 2024/6/11 17:36
 * @Description:
 * @author: xzc-coder
 */
public class FileMqttMsgStore implements MqttMsgStore {

    private final File propertiesFile;
    private final Properties properties;

    /**
     * MQTT 发送消息的前缀 后面接 -客户端ID-消息ID
     */
    private static final String MQTT_SEND_MSG_PREFIX = "send";
    /**
     * MQTT properties文件中的key的分隔符号
     */
    private static final String KEY_SEPARATOR = "-";
    /**
     * MQTT 接收消息的前缀 后面接 -客户端ID-消息ID
     */
    private static final String MQTT_RECEIVE_MSG_PREFIX = "receive";

    public FileMqttMsgStore(File propertiesFile) {
        AssertUtils.notNull(propertiesFile, "propertiesFile is null");
        this.propertiesFile = propertiesFile;
        this.properties = new Properties();
        loadProperties();
    }

    @Override
    public MqttMsg getMsg(MqttMsgDirection mqttMsgDirection, String clientId, int msgId) {
        AssertUtils.notNull(clientId, "clientId is null");
        AssertUtils.notNull(mqttMsgDirection, "mqttMsgStoreType is null");
        MqttMsg mqttMsg;
        String msgPropertyKey = getMsgPropertyKey(clientId, mqttMsgDirection, msgId);
        mqttMsg = getMqttMsg(msgPropertyKey);
        return mqttMsg;
    }

    @Override
    public void putMsg(MqttMsgDirection mqttMsgDirection, String clientId, MqttMsg mqttMsg) {
        AssertUtils.notNull(clientId, "clientId is null");
        AssertUtils.notNull(mqttMsg, "mqttMsg is null");
        AssertUtils.notNull(mqttMsgDirection, "mqttMsgStoreType is null");
        String msgKey = getMsgPropertyKey(clientId, mqttMsgDirection, mqttMsg.getMsgId());
        properties.put(msgKey, MqttUtils.serializableMsgBase64(mqttMsg));
        writeProperties();
    }

    @Override
    public MqttMsg removeMsg(MqttMsgDirection mqttMsgDirection, String clientId, int msgId) {
        AssertUtils.notNull(clientId, "clientId is null");
        AssertUtils.notNull(mqttMsgDirection, "mqttMsgStoreType is null");
        MqttMsg mqttMsg = null;
        String msgKey = getMsgPropertyKey(clientId, mqttMsgDirection, msgId);
        Object value = properties.remove(msgKey);
        writeProperties();
        if (value != null) {
            mqttMsg = MqttUtils.deserializableMsgBase64((String)value);
        }
        return mqttMsg;
    }

    @Override
    public List<MqttMsg> getMsgList(MqttMsgDirection mqttMsgDirection, String clientId) {
        AssertUtils.notNull(clientId, "clientId is null");
        AssertUtils.notNull(mqttMsgDirection, "mqttMsgStoreType is null");
        List<MqttMsg> mqttMsgList = new ArrayList<>();
        Set<String> propertyNameSet = properties.stringPropertyNames();
        if (EmptyUtils.isNotEmpty(propertyNameSet)) {
            String clientKeyPrefix;
            switch (mqttMsgDirection) {
                case SEND:
                    clientKeyPrefix = MQTT_SEND_MSG_PREFIX + KEY_SEPARATOR + clientId + KEY_SEPARATOR;
                    break;
                case RECEIVE:
                    clientKeyPrefix = MQTT_RECEIVE_MSG_PREFIX + KEY_SEPARATOR + clientId + KEY_SEPARATOR;
                    break;
                default:
                    throw new IllegalArgumentException(mqttMsgDirection.name() + " is illegal");
            }
            for (String propertyName : propertyNameSet) {
                if (propertyName.startsWith(clientKeyPrefix)) {
                    MqttMsg mqttMsg = getMqttMsg(propertyName);
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
        String clientSendKeyPrefix = MQTT_SEND_MSG_PREFIX + KEY_SEPARATOR + clientId + KEY_SEPARATOR;
        String clientReceiveKeyPrefix = MQTT_RECEIVE_MSG_PREFIX + KEY_SEPARATOR + clientId + KEY_SEPARATOR;
        Set<String> propertyNameSet = properties.stringPropertyNames();
        if (EmptyUtils.isNotEmpty(propertyNameSet)) {
            for (String propertyName : propertyNameSet) {
                if (propertyName.startsWith(clientSendKeyPrefix) || propertyName.startsWith(clientReceiveKeyPrefix)) {
                    properties.remove(propertyName);
                }
            }
            writeProperties();
        }

    }

    private String getMsgPropertyKey(String clientId, MqttMsgDirection mqttMsgDirection, int msgId) {
        String key;
        switch (mqttMsgDirection) {
            case SEND:
                key = MQTT_SEND_MSG_PREFIX + KEY_SEPARATOR + clientId + KEY_SEPARATOR + msgId;
                break;
            case RECEIVE:
                key = MQTT_RECEIVE_MSG_PREFIX + KEY_SEPARATOR + clientId + KEY_SEPARATOR + msgId;
                break;
            default:
                throw new IllegalArgumentException(mqttMsgDirection.name() + " is illegal");
        }
        return key;
    }

    private MqttMsg getMqttMsg(String msgPropertyKey) {
        Object value = properties.get(msgPropertyKey);
        MqttMsg mqttMsg = null;
        if (value != null) {
            mqttMsg = MqttUtils.deserializableMsgBase64((String) value);
        }
        return mqttMsg;
    }

    private void writeProperties() {
        try {
            properties.store(new FileOutputStream(propertiesFile), null);
        } catch (IOException e) {
            throw new MqttException(e);
        }
    }

    private void loadProperties() {
        try {
            properties.load(new FileInputStream(propertiesFile));
        } catch (IOException e) {
            throw new MqttException(e);
        }
    }
}
