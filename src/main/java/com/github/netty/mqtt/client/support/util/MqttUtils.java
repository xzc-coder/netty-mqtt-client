package com.github.netty.mqtt.client.support.util;

import com.github.netty.mqtt.client.MqttConnectParameter;
import com.github.netty.mqtt.client.constant.MqttConstant;
import com.github.netty.mqtt.client.constant.MqttMsgDirection;
import com.github.netty.mqtt.client.constant.MqttMsgState;
import com.github.netty.mqtt.client.constant.MqttVersion;
import com.github.netty.mqtt.client.exception.MqttException;
import com.github.netty.mqtt.client.msg.MqttDisconnectMsg;
import com.github.netty.mqtt.client.msg.MqttMsg;
import com.github.netty.mqtt.client.msg.MqttMsgInfo;
import com.github.netty.mqtt.client.msg.MqttWillMsg;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttProperties;
import io.netty.handler.codec.mqtt.MqttQoS;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @Date: 2024/6/23 15:56
 * @Description:
 * @author: xzc-coder
 */
public class MqttUtils {

    /**
     * Mqtt 发布消息中相关的字符属性
     */
    private static final Set<Integer> MQTT_PROPERTY_STRING_ID_SET = new HashSet<>();
    /**
     * Mqtt 发布消息中相关的数值属性
     */
    private static final Set<Integer> MQTT_PROPERTY_INTEGER_ID_SET = new HashSet<>();
    /**
     * Mqtt 发布消息中相关的二进制属性
     */
    private static final Set<Integer> MQTT_PROPERTY_BINARY_ID_SET = new HashSet<>();

    static {
        //字符串
        MQTT_PROPERTY_STRING_ID_SET.add(MqttProperties.MqttPropertyType.CONTENT_TYPE.value());
        MQTT_PROPERTY_STRING_ID_SET.add(MqttProperties.MqttPropertyType.RESPONSE_TOPIC.value());
        MQTT_PROPERTY_STRING_ID_SET.add(MqttProperties.MqttPropertyType.REASON_STRING.value());
        MQTT_PROPERTY_STRING_ID_SET.add(MqttProperties.MqttPropertyType.USER_PROPERTY.value());
        //二进制
        MQTT_PROPERTY_BINARY_ID_SET.add(MqttProperties.MqttPropertyType.CORRELATION_DATA.value());
        //整型
        MQTT_PROPERTY_INTEGER_ID_SET.add(MqttProperties.MqttPropertyType.PAYLOAD_FORMAT_INDICATOR.value());
        MQTT_PROPERTY_INTEGER_ID_SET.add(MqttProperties.MqttPropertyType.TOPIC_ALIAS.value());
        MQTT_PROPERTY_INTEGER_ID_SET.add(MqttProperties.MqttPropertyType.PUBLICATION_EXPIRY_INTERVAL.value());
        MQTT_PROPERTY_INTEGER_ID_SET.add(MqttProperties.MqttPropertyType.SUBSCRIPTION_IDENTIFIER.value());
    }

    private MqttUtils() {
    }

    /**
     * 获取一个连接时用的 MqttProperties
     *
     * @param mqttConnectParameter MQTT连接参数
     * @return MqttProperties
     */
    public static MqttProperties getConnectMqttProperties(MqttConnectParameter mqttConnectParameter) {
        AssertUtils.notNull(mqttConnectParameter, "mqttConnectParameter is null");
        MqttVersion mqttVersion = mqttConnectParameter.getMqttVersion();
        MqttProperties mqttProperties;
        if (mqttVersion == MqttVersion.MQTT_5_0_0) {
            mqttProperties = new MqttProperties();
            Integer sessionExpiryIntervalSeconds = mqttConnectParameter.getSessionExpiryIntervalSeconds();
            String authenticationMethod = mqttConnectParameter.getAuthenticationMethod();
            byte[] authenticationData = mqttConnectParameter.getAuthenticationData();
            Integer requestProblemInformation = mqttConnectParameter.getRequestProblemInformation();
            String responseInformation = mqttConnectParameter.getResponseInformation();
            Integer receiveMaximum = mqttConnectParameter.getReceiveMaximum();
            Integer topicAliasMaximum = mqttConnectParameter.getTopicAliasMaximum();
            Integer maximumPacketSize = mqttConnectParameter.getMaximumPacketSize();
            MqttProperties.UserProperties mqttUserProperties = mqttConnectParameter.getMqttUserProperties();
            if (sessionExpiryIntervalSeconds != null && sessionExpiryIntervalSeconds >= 0) {
                mqttProperties.add(new MqttProperties.IntegerProperty(MqttProperties.MqttPropertyType.SESSION_EXPIRY_INTERVAL.value(), sessionExpiryIntervalSeconds));
            }
            if (authenticationMethod != null) {
                mqttProperties.add(new MqttProperties.StringProperty(MqttProperties.MqttPropertyType.AUTHENTICATION_METHOD.value(), authenticationMethod));
            }
            if (authenticationData != null && authenticationData.length > 0) {
                mqttProperties.add(new MqttProperties.BinaryProperty(MqttProperties.MqttPropertyType.AUTHENTICATION_DATA.value(), authenticationData));
            }
            if (requestProblemInformation != null) {
                mqttProperties.add(new MqttProperties.IntegerProperty(MqttProperties.MqttPropertyType.REQUEST_PROBLEM_INFORMATION.value(), requestProblemInformation));
            }
            if (responseInformation != null) {
                mqttProperties.add(new MqttProperties.StringProperty(MqttProperties.MqttPropertyType.RESPONSE_INFORMATION.value(), responseInformation));
            }
            if (receiveMaximum != null && receiveMaximum > 0) {
                mqttProperties.add(new MqttProperties.IntegerProperty(MqttProperties.MqttPropertyType.RECEIVE_MAXIMUM.value(), receiveMaximum));
            }
            if (topicAliasMaximum != null && topicAliasMaximum > 0) {
                mqttProperties.add(new MqttProperties.IntegerProperty(MqttProperties.MqttPropertyType.TOPIC_ALIAS_MAXIMUM.value(), topicAliasMaximum));
            }
            if (maximumPacketSize != null && maximumPacketSize > 0) {
                mqttProperties.add(new MqttProperties.IntegerProperty(MqttProperties.MqttPropertyType.MAXIMUM_PACKET_SIZE.value(), maximumPacketSize));
            }
            addUserProperty(mqttProperties, mqttUserProperties);
        } else {
            mqttProperties = MqttProperties.NO_PROPERTIES;
        }
        return mqttProperties;
    }

    /**
     * 获取断开连接的MqttProperties
     *
     * @param mqttDisconnectMsg MQTT断开消息
     * @return MqttProperties
     */
    public static MqttProperties getDisconnectMqttProperties(MqttDisconnectMsg mqttDisconnectMsg) {
        AssertUtils.notNull(mqttDisconnectMsg, "mqttDisconnectMsg is null");
        MqttProperties mqttProperties = new MqttProperties();
        String reasonString = mqttDisconnectMsg.getReasonString();
        Integer sessionExpiryIntervalSeconds = mqttDisconnectMsg.getSessionExpiryIntervalSeconds();
        MqttProperties.UserProperties mqttUserProperties = mqttDisconnectMsg.getMqttUserProperties();
        if (EmptyUtils.isNotEmpty(reasonString)) {
            mqttProperties.add(new MqttProperties.StringProperty(MqttProperties.MqttPropertyType.REASON_STRING.value(), reasonString));
        }
        if (sessionExpiryIntervalSeconds != null && sessionExpiryIntervalSeconds > 0) {
            mqttProperties.add(new MqttProperties.IntegerProperty(MqttProperties.MqttPropertyType.SESSION_EXPIRY_INTERVAL.value(), sessionExpiryIntervalSeconds));
        }
        addUserProperty(mqttProperties, mqttUserProperties);
        return mqttProperties;

    }

    /**
     * 获取遗嘱的MqttProperties
     *
     * @param mqttVersion MQTT版本
     * @param mqttWillMsg 遗嘱消息
     * @return MqttProperties
     */
    public static MqttProperties getWillMqttProperties(MqttVersion mqttVersion, MqttWillMsg mqttWillMsg) {
        AssertUtils.notNull(mqttVersion, "mqttVersion is null");
        AssertUtils.notNull(mqttWillMsg, "mqttWillMsg is null");
        MqttProperties mqttProperties;
        if (mqttVersion == MqttVersion.MQTT_5_0_0) {
            mqttProperties = new MqttProperties();
            Integer willDelayIntervalSeconds = mqttWillMsg.getWillDelayIntervalSeconds();
            Integer payloadFormatIndicator = mqttWillMsg.getPayloadFormatIndicator();
            Integer messageExpiryIntervalSeconds = mqttWillMsg.getMessageExpiryIntervalSeconds();
            String contentType = mqttWillMsg.getContentType();
            String responseTopic = mqttWillMsg.getResponseTopic();
            byte[] correlationData = mqttWillMsg.getCorrelationData();
            MqttProperties.UserProperties mqttUserProperties = mqttWillMsg.getMqttUserProperties();
            if (willDelayIntervalSeconds != null && willDelayIntervalSeconds > 0) {
                mqttProperties.add(new MqttProperties.IntegerProperty(MqttProperties.MqttPropertyType.WILL_DELAY_INTERVAL.value(), willDelayIntervalSeconds));
            }
            if (payloadFormatIndicator != null) {
                mqttProperties.add(new MqttProperties.IntegerProperty(MqttProperties.MqttPropertyType.PAYLOAD_FORMAT_INDICATOR.value(), payloadFormatIndicator));
            }
            if (messageExpiryIntervalSeconds != null && messageExpiryIntervalSeconds > 0) {
                mqttProperties.add(new MqttProperties.IntegerProperty(MqttProperties.MqttPropertyType.PUBLICATION_EXPIRY_INTERVAL.value(), messageExpiryIntervalSeconds));
            }
            if (EmptyUtils.isNotEmpty(contentType)) {
                mqttProperties.add(new MqttProperties.StringProperty(MqttProperties.MqttPropertyType.CONTENT_TYPE.value(), contentType));
            }

            if (EmptyUtils.isNotEmpty(responseTopic)) {
                mqttProperties.add(new MqttProperties.StringProperty(MqttProperties.MqttPropertyType.RESPONSE_TOPIC.value(), responseTopic));
            }

            if (correlationData != null && correlationData.length > 0) {
                mqttProperties.add(new MqttProperties.BinaryProperty(MqttProperties.MqttPropertyType.CORRELATION_DATA.value(), correlationData));
            }
            addUserProperty(mqttProperties, mqttUserProperties);
        } else {
            mqttProperties = MqttProperties.NO_PROPERTIES;
        }
        return mqttProperties;
    }

    private static void addUserProperty(MqttProperties mqttProperties, MqttProperties.UserProperties mqttUserProperties) {
        if (mqttUserProperties != null && !mqttUserProperties.value().isEmpty()) {
            mqttProperties.add(mqttUserProperties);
        }
    }

    /**
     * 获取认证报文的MqttProperties
     *
     * @param authenticationMethod 认证方法
     * @param authenticationData   认证数据
     * @param reasonString         原因字符串
     * @param mqttUserProperties   MQTT用户属性
     * @return MqttProperties
     */
    public static MqttProperties getAuthMqttProperties(String authenticationMethod, byte[] authenticationData, String reasonString, MqttProperties.UserProperties mqttUserProperties) {
        AssertUtils.notNull(authenticationMethod, "authenticationMethod is null");
        MqttProperties mqttProperties = new MqttProperties();
        mqttProperties.add(new MqttProperties.StringProperty(MqttProperties.MqttPropertyType.AUTHENTICATION_METHOD.value(), authenticationMethod));
        if (authenticationData != null && authenticationData.length > 0) {
            mqttProperties.add(new MqttProperties.BinaryProperty(MqttProperties.MqttPropertyType.AUTHENTICATION_DATA.value(), authenticationData));
        }
        if (reasonString != null) {
            mqttProperties.add(new MqttProperties.StringProperty(MqttProperties.MqttPropertyType.REASON_STRING.value(), reasonString));
        }
        addUserProperty(mqttProperties, mqttUserProperties);
        return mqttProperties;
    }

    /**
     * 从MqttProperties中获取原因字符串
     *
     * @param mqttProperties MqttProperties
     * @return 原因字符串
     */
    public static String getReasonString(MqttProperties mqttProperties) {
        String reasonString = null;
        if (mqttProperties != null) {
            MqttProperties.StringProperty reasonStringProperty = (MqttProperties.StringProperty) mqttProperties.getProperty(MqttProperties.MqttPropertyType.REASON_STRING.value());
            if (reasonStringProperty != null) {
                reasonString = reasonStringProperty.value();
            }
        }
        return reasonString;
    }

    /**
     * 获取活跃时间（秒）
     *
     * @param clientKeepAlive 客户端设置的活跃时间
     * @param mqttProperties  broker返回的MqttProperties中的活跃时间
     * @return 活跃时间
     */
    public static int getKeepAlive(int clientKeepAlive, MqttProperties mqttProperties) {
        int keepAlive = clientKeepAlive;
        if (mqttProperties != null) {
            MqttProperties.IntegerProperty mqttProperty = (MqttProperties.IntegerProperty) mqttProperties.getProperty(MqttProperties.MqttPropertyType.SERVER_KEEP_ALIVE.value());
            if (mqttProperty != null) {
                Integer value = mqttProperty.value();
                if (value != null) {
                    keepAlive = value;
                }
            }
        }
        return keepAlive;
    }

    /**
     * 获取MQTT中的某个用户属性
     *
     * @param mqttProperties MqttProperties
     * @param key            key
     * @return 用户属性集合
     */
    public static List<String> getUserMqttPropertyValues(MqttProperties mqttProperties, String key) {
        List<String> userMqttPropertyValues = new ArrayList<>();
        if (mqttProperties != null && EmptyUtils.isNotEmpty(key)) {
            MqttProperties.UserProperties mqttUserProperties = (MqttProperties.UserProperties) mqttProperties.getProperty(MqttProperties.MqttPropertyType.USER_PROPERTY.value());
            if (mqttUserProperties != null) {
                List<MqttProperties.StringPair> stringPairList = mqttUserProperties.value();
                if (EmptyUtils.isNotEmpty(stringPairList)) {
                    for (MqttProperties.StringPair stringPair : stringPairList) {
                        if (key.equals(stringPair.key)) {
                            userMqttPropertyValues.add(stringPair.value);
                        }
                    }
                }
            }
        }
        return userMqttPropertyValues;
    }

    /**
     * 获取MQTT中的某个用户属性值
     *
     * @param mqttProperties MqttProperties
     * @param key            key
     * @return 单个值
     */
    public static String getUserMqttPropertyValue(MqttProperties mqttProperties, String key) {
        String value = null;
        List<String> userMqttPropertyValues = getUserMqttPropertyValues(mqttProperties, key);
        if (EmptyUtils.isNotEmpty(userMqttPropertyValues)) {
            value = userMqttPropertyValues.get(0);
        }
        return value;
    }

    /**
     * 获取MQTT中的某个用户属性值
     *
     * @param mqttProperties MqttProperties
     * @param key            key
     * @param defaultValue   获取不到时的默认值
     * @return 单个值
     */
    public static String getUserMqttPropertyValue(MqttProperties mqttProperties, String key, String defaultValue) {
        String value = null;
        List<String> userMqttPropertyValues = getUserMqttPropertyValues(mqttProperties, key);
        if (EmptyUtils.isNotEmpty(userMqttPropertyValues)) {
            value = userMqttPropertyValues.get(0);
            if (EmptyUtils.isEmpty(value)) {
                value = defaultValue;
            }
        }
        return value;
    }

    /**
     * 获取整型的MqttProperty值
     *
     * @param mqttProperties   MqttProperties
     * @param mqttPropertyType MQTT属性类型
     * @return 整型值
     */
    public static Integer getIntegerMqttPropertyValue(MqttProperties mqttProperties, MqttProperties.MqttPropertyType mqttPropertyType) {
        Integer value = null;
        if (mqttProperties != null && mqttPropertyType != null) {
            MqttProperties.MqttProperty mqttProperty = mqttProperties.getProperty(mqttPropertyType.value());
            if (mqttProperty instanceof MqttProperties.IntegerProperty) {
                value = ((MqttProperties.IntegerProperty) mqttProperty).value();
            }
        }
        return value;
    }

    /**
     * 获取整型的MqttProperty值
     *
     * @param mqttProperties   MqttProperties
     * @param mqttPropertyType MQTT属性类型
     * @param defaultValue     获取不到时的默认值
     * @return 整型值
     */
    public static Integer getIntegerMqttPropertyValue(MqttProperties mqttProperties, MqttProperties.MqttPropertyType mqttPropertyType, int defaultValue) {
        Integer value;
        value = getIntegerMqttPropertyValue(mqttProperties, mqttPropertyType);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    /**
     * 获取字符串的MqttProperty值
     *
     * @param mqttProperties   MqttProperties
     * @param mqttPropertyType MQTT属性类型
     * @return 字符串
     */
    public static String getStringMqttPropertyValue(MqttProperties mqttProperties, MqttProperties.MqttPropertyType mqttPropertyType) {
        String value = null;
        if (mqttProperties != null && mqttPropertyType != null) {
            MqttProperties.MqttProperty mqttProperty = mqttProperties.getProperty(mqttPropertyType.value());
            if (mqttProperty instanceof MqttProperties.StringProperty) {
                value = ((MqttProperties.StringProperty) mqttProperty).value();
            }
        }
        return value;
    }


    /**
     * 获取字符串的MqttProperty值
     *
     * @param mqttProperties   MqttProperties
     * @param mqttPropertyType MQTT属性类型
     * @param defaultValue     获取不到时的默认值
     * @return 字符串
     */
    public static String getStringMqttPropertyValue(MqttProperties mqttProperties, MqttProperties.MqttPropertyType mqttPropertyType, String defaultValue) {
        String value;
        value = getStringMqttPropertyValue(mqttProperties, mqttPropertyType);
        if (EmptyUtils.isEmpty(value)) {
            value = defaultValue;
        }
        return value;
    }

    /**
     * 获取二进制的MqttProperty值
     *
     * @param mqttProperties   MqttProperties
     * @param mqttPropertyType MQTT属性类型
     * @return 二进制
     */
    public static byte[] getBinaryMqttPropertyValue(MqttProperties mqttProperties, MqttProperties.MqttPropertyType mqttPropertyType) {
        byte[] value = null;
        if (mqttProperties != null && mqttPropertyType != null) {
            MqttProperties.MqttProperty mqttProperty = mqttProperties.getProperty(mqttPropertyType.value());
            if (mqttProperty instanceof MqttProperties.BinaryProperty) {
                value = ((MqttProperties.BinaryProperty) mqttProperty).value();
            }
        }
        return value;
    }


    /**
     * 获取二进制的MqttProperty值
     *
     * @param mqttProperties   MqttProperties
     * @param mqttPropertyType MQTT属性类型
     * @param defaultValue     获取不到时的默认值
     * @return 二进制
     */
    public static byte[] getBinaryMqttPropertyValue(MqttProperties mqttProperties, MqttProperties.MqttPropertyType mqttPropertyType, byte[] defaultValue) {
        byte[] value;
        value = getBinaryMqttPropertyValue(mqttProperties, mqttPropertyType);
        if (value == null || value.length == 0) {
            value = defaultValue;
        }
        return value;
    }

    /**
     * 获取活跃时间（秒）
     *
     * @param channel                    Channel
     * @param clientKeepAliveTimeSeconds 客户端的活跃时间
     * @return 活跃时间（
     */
    public static int getKeepAliveTimeSeconds(Channel channel, int clientKeepAliveTimeSeconds) {
        Integer keepAliveTimeSeconds = null;
        if (channel != null) {
            keepAliveTimeSeconds = channel.attr(MqttConstant.KEEP_ALIVE_TIME_ATTRIBUTE_KEY).get();
        }
        if (keepAliveTimeSeconds == null) {
            keepAliveTimeSeconds = clientKeepAliveTimeSeconds;
        }
        return keepAliveTimeSeconds;
    }

    /**
     * 获取发布消息的MqttProperties
     *
     * @param mqttMsgInfo MQTT消息信息
     * @return MqttProperties
     */
    public static MqttProperties getPublishMqttProperties(MqttMsgInfo mqttMsgInfo) {
        AssertUtils.notNull(mqttMsgInfo, "mqttMsgInfo is null");
        MqttProperties mqttProperties = new MqttProperties();
        Integer payloadFormatIndicator = mqttMsgInfo.getPayloadFormatIndicator();
        Integer messageExpiryIntervalSeconds = mqttMsgInfo.getMessageExpiryIntervalSeconds();
        Integer topicAlias = mqttMsgInfo.getTopicAlias();
        String responseTopic = mqttMsgInfo.getResponseTopic();
        byte[] correlationData = mqttMsgInfo.getCorrelationData();
        Integer subscriptionIdentifier = mqttMsgInfo.getSubscriptionIdentifier();
        String contentType = mqttMsgInfo.getContentType();
        MqttProperties.UserProperties mqttUserProperties = mqttMsgInfo.getMqttUserProperties();
        if (payloadFormatIndicator != null) {
            mqttProperties.add(new MqttProperties.IntegerProperty(MqttProperties.MqttPropertyType.PAYLOAD_FORMAT_INDICATOR.value(), payloadFormatIndicator));
        }
        if (messageExpiryIntervalSeconds != null) {
            mqttProperties.add(new MqttProperties.IntegerProperty(MqttProperties.MqttPropertyType.PUBLICATION_EXPIRY_INTERVAL.value(), messageExpiryIntervalSeconds));
        }
        if (topicAlias != null) {
            mqttProperties.add(new MqttProperties.IntegerProperty(MqttProperties.MqttPropertyType.TOPIC_ALIAS.value(), topicAlias));
        }
        if (responseTopic != null) {
            mqttProperties.add(new MqttProperties.StringProperty(MqttProperties.MqttPropertyType.RESPONSE_TOPIC.value(), responseTopic));
        }
        if (correlationData != null) {
            mqttProperties.add(new MqttProperties.BinaryProperty(MqttProperties.MqttPropertyType.CORRELATION_DATA.value(), correlationData));
        }
        if (subscriptionIdentifier != null) {
            mqttProperties.add(new MqttProperties.IntegerProperty(MqttProperties.MqttPropertyType.SUBSCRIPTION_IDENTIFIER.value(), subscriptionIdentifier));
        }
        if (contentType != null) {
            mqttProperties.add(new MqttProperties.StringProperty(MqttProperties.MqttPropertyType.CONTENT_TYPE.value(), contentType));
        }
        addUserProperty(mqttProperties, mqttUserProperties);
        return mqttProperties;
    }

    /**
     * 序列化MqttMsg，
     * 按顺序为：2字节MsgId-2字节主题长度-主题内容-1字节的qos-1字节是否保留消息-1字节是否重复消息-1字节消息状态-4字节的载荷长度-载荷-1字节消息方向-1字节原因码-4字节MqttProperties长度-MqttProperties
     *
     * @param mqttMsg MQTT消息
     * @return 二进制
     */
    public static byte[] serializableMsg(MqttMsg mqttMsg) {
        AssertUtils.notNull(mqttMsg, "mqttMsg is null");
        ByteBuf byteBuf = Unpooled.buffer();
        int msgId = mqttMsg.getMsgId();
        String topic = mqttMsg.getTopic();
        byte[] topicBytes = topic.getBytes(StandardCharsets.UTF_8);
        int topicLength = topicBytes.length;
        int qos = mqttMsg.getQos().value();
        boolean retain = mqttMsg.isRetain();
        boolean dup = mqttMsg.isDup();
        int msgState = mqttMsg.getMsgState().getState();
        byte[] payload = mqttMsg.getPayload();
        int payloadLength = payload.length;
        int mqttMsgDirection = mqttMsg.getMqttMsgDirection().getDirection();
        byte reasonCode = mqttMsg.getReasonCode();
        MqttProperties mqttProperties = mqttMsg.getMqttProperties();
        boolean existMqttProperties = mqttProperties != null && !mqttProperties.listAll().isEmpty();
        byte[] mqttPropertiesBytes = new byte[0];
        if (existMqttProperties) {
            //解析MqttProperties
            mqttPropertiesBytes = mqttPropertiesToBytes(mqttProperties);
        }
        long createTimestamp = mqttMsg.getCreateTimestamp();
        byteBuf.writeShort(msgId);
        byteBuf.writeShort(topicLength);
        byteBuf.writeBytes(topicBytes);
        byteBuf.writeByte(qos);
        byteBuf.writeBoolean(retain);
        byteBuf.writeBoolean(dup);
        byteBuf.writeByte(msgState);
        byteBuf.writeInt(payloadLength);
        byteBuf.writeBytes(payload);
        byteBuf.writeByte(mqttMsgDirection);
        byteBuf.writeByte(reasonCode);
        byteBuf.writeInt(mqttPropertiesBytes.length);
        byteBuf.writeBytes(mqttPropertiesBytes);
        byteBuf.writeLong(createTimestamp);
        //2个字节的crc校验码
        byte[] data = ByteBufUtil.getBytes(byteBuf);
        byte[] crc16Bytes = CRC16Utils.getCRC16Bytes(data);
        byteBuf.writeBytes(crc16Bytes);
        return ByteBufUtil.getBytes(byteBuf);
    }

    /**
     * 反序列化二进制为MQTT消息
     *
     * @param bytes 二进制
     * @return MQTT消息
     */
    public static MqttMsg deserializableMsg(byte[] bytes) {
        AssertUtils.notNull(bytes, "bytes is null");
        ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
        //先进行CRC校验，直接读的话浪费性能和时间
        crcCheck(byteBuf);
        //读取数据
        int mgsId = byteBuf.readUnsignedShort();
        int topicLength = byteBuf.readUnsignedShort();
        byte[] topicBytes = new byte[topicLength];
        byteBuf.readBytes(topicBytes);
        String topic = new String(topicBytes, StandardCharsets.UTF_8);
        byte qosByte = byteBuf.readByte();
        MqttQoS qos = MqttQoS.valueOf(qosByte);
        boolean retain = byteBuf.readBoolean();
        boolean dup = byteBuf.readBoolean();
        MqttMsgState msgState = MqttMsgState.findMqttMsgState(byteBuf.readByte());
        int payloadLength = byteBuf.readInt();
        byte[] payload = new byte[payloadLength];
        byteBuf.readBytes(payload);
        MqttMsgDirection mqttMsgDirection = MqttMsgDirection.findMqttMsgDirection(byteBuf.readByte());
        byte reasonCode = byteBuf.readByte();
        int mqttPropertiesLength = byteBuf.readInt();
        byte[] mqttPropertiesBytes = new byte[mqttPropertiesLength];
        byteBuf.readBytes(mqttPropertiesBytes);
        MqttProperties mqttProperties = bytesToMqttProperties(mqttPropertiesBytes);
        long createTimestamp = byteBuf.readLong();
        MqttMsg mqttMsg = new MqttMsg(mgsId, payload, topic, qos, retain, dup, msgState, mqttMsgDirection);
        mqttMsg.setReasonCode(reasonCode);
        mqttMsg.setMqttProperties(mqttProperties);
        mqttMsg.setCreateTimestamp(createTimestamp);
        return mqttMsg;
    }

    /**
     * 序列化MQTT消息为Base64字符串
     *
     * @param mqttMsg MQTT消息
     * @return Base64字符串
     */
    public static String serializableMsgBase64(MqttMsg mqttMsg) {
        byte[] bytes = serializableMsg(mqttMsg);
        return Base64.getEncoder().encodeToString(bytes);
    }


    /**
     * 反序列化Base64字符串为MQTT消息
     *
     * @param base64 base64字符串
     * @return MQTT消息
     */
    public static MqttMsg deserializableMsgBase64(String base64) {
        return deserializableMsg(Base64.getDecoder().decode(base64));
    }


    /**
     * MqttProperties转为二进制
     *
     * @param mqttProperties MqttProperties
     * @return 二进制
     */
    private static byte[] mqttPropertiesToBytes(MqttProperties mqttProperties) {
        ByteBuf byteBuf = Unpooled.buffer();
        if (mqttProperties != null && !mqttProperties.listAll().isEmpty()) {
            Collection<? extends MqttProperties.MqttProperty> mqttPropertyList = mqttProperties.listAll();
            //遍历所有MqttProperty
            for (MqttProperties.MqttProperty mqttProperty : mqttPropertyList) {
                int propertyId = mqttProperty.propertyId();
                //整型MqttProperty
                if (MQTT_PROPERTY_INTEGER_ID_SET.contains(propertyId)) {
                    MqttProperties.IntegerProperty mqttIntegerProperty = (MqttProperties.IntegerProperty) mqttProperty;
                    byteBuf.writeByte(propertyId);
                    byteBuf.writeInt(mqttIntegerProperty.value());
                } else if (MQTT_PROPERTY_STRING_ID_SET.contains(propertyId)) {
                    //字符型MqttProperty
                    byteBuf.writeByte(propertyId);
                    if (propertyId == MqttProperties.MqttPropertyType.USER_PROPERTY.value()) {
                        //如果是用户属性，则需要单独处理
                        ByteBuf userPropertyByteBuf = ByteBufAllocator.DEFAULT.heapBuffer();
                        MqttProperties.UserProperties userProperties = (MqttProperties.UserProperties) mqttProperty;
                        List<MqttProperties.StringPair> stringPairList = userProperties.value();
                        if (!stringPairList.isEmpty()) {
                            for (MqttProperties.StringPair stringPair : stringPairList) {
                                byte[] keyBytes = stringPair.key.getBytes(StandardCharsets.UTF_8);
                                byte[] valueBytes = stringPair.value.getBytes(StandardCharsets.UTF_8);
                                //写 key 长度
                                userPropertyByteBuf.writeInt(keyBytes.length);
                                //写 key 值
                                userPropertyByteBuf.writeBytes(keyBytes);
                                //写 value长度
                                userPropertyByteBuf.writeInt(valueBytes.length);
                                //写 value 值
                                userPropertyByteBuf.writeBytes(valueBytes);
                            }
                        }
                        int userPropertiesBytesLength = userPropertyByteBuf.readableBytes();
                        //写用户属性总长度
                        byteBuf.writeInt(userPropertiesBytesLength);
                        byteBuf.writeBytes(userPropertyByteBuf);
                    } else {
                        //不是用户属性直接处理
                        MqttProperties.StringProperty mqttStringProperty = (MqttProperties.StringProperty) mqttProperty;
                        byte[] data = mqttStringProperty.value().getBytes(StandardCharsets.UTF_8);
                        //多写4个字节的字符串长度
                        byteBuf.writeInt(data.length);
                        byteBuf.writeBytes(data);
                    }
                } else if (MQTT_PROPERTY_BINARY_ID_SET.contains(propertyId)) {
                    //二进制类型MqttProperty
                    MqttProperties.BinaryProperty mqttBinaryProperty = (MqttProperties.BinaryProperty) mqttProperty;
                    byte[] data = mqttBinaryProperty.value();
                    byteBuf.writeByte(propertyId);
                    //多写4字节的数据长度
                    byteBuf.writeInt(data.length);
                    byteBuf.writeBytes(data);
                }
            }
        }
        return ByteBufUtil.getBytes(byteBuf);
    }

    /**
     * 二进制转为MqttProperties
     *
     * @param bytes 二进制
     * @return MqttProperties
     */
    private static MqttProperties bytesToMqttProperties(byte[] bytes) {
        MqttProperties mqttProperties = null;
        if (bytes != null && bytes.length > 0) {
            mqttProperties = new MqttProperties();
            //包装byte数组，不需要读写，直接使用byte数组的引用
            ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
            while (byteBuf.isReadable()) {
                int propertyId = byteBuf.readByte();
                //整型MqttProperty
                if (MQTT_PROPERTY_INTEGER_ID_SET.contains(propertyId)) {
                    //整形直接读4个字节
                    int value = byteBuf.readInt();
                    mqttProperties.add(new MqttProperties.IntegerProperty(propertyId, value));
                } else if (MQTT_PROPERTY_STRING_ID_SET.contains(propertyId)) {
                    //字符型MqttProperty
                    if (propertyId == MqttProperties.MqttPropertyType.USER_PROPERTY.value()) {
                        //用户属性的话单独处理
                        int userPropertiesBytesLength = byteBuf.readInt();
                        MqttProperties.UserProperties mqttUserProperties = new MqttProperties.UserProperties();
                        while (userPropertiesBytesLength > 0) {
                            //用户属性的key长度
                            int keyLength = byteBuf.readInt();
                            byte[] keyBytes = new byte[keyLength];
                            byteBuf.readBytes(keyBytes);
                            String key = new String(keyBytes, StandardCharsets.UTF_8);
                            //用户属性的value长度
                            int valueLength = byteBuf.readInt();
                            byte[] valueBytes = new byte[valueLength];
                            byteBuf.readBytes(valueBytes);
                            String value = new String(valueBytes, StandardCharsets.UTF_8);
                            mqttUserProperties.add(key, value);
                            userPropertiesBytesLength = userPropertiesBytesLength - 8 - keyLength - valueLength;
                        }
                        mqttProperties.add(mqttUserProperties);
                    } else {
                        //其他的则直接处理
                        int length = byteBuf.readInt();
                        byte[] data = new byte[length];
                        byteBuf.readBytes(data);
                        mqttProperties.add(new MqttProperties.StringProperty(propertyId, new String(data, StandardCharsets.UTF_8)));
                    }
                } else if (MQTT_PROPERTY_BINARY_ID_SET.contains(propertyId)) {
                    //二进制类型MqttProperty
                    int length = byteBuf.readInt();
                    byte[] data = new byte[length];
                    byteBuf.readBytes(data);
                    mqttProperties.add(new MqttProperties.BinaryProperty(propertyId, data));
                } else {
                    throw new MqttException("MqttMsg.MqttProperties parse exception,the propertyId " + propertyId + " does not exist");
                }
            }
        }
        return mqttProperties;
    }

    /**
     * crc校验
     *
     * @param byteBuf ByteBuf
     */
    private static void crcCheck(ByteBuf byteBuf) {
        //标记一下当前位置
        byteBuf.markReaderIndex();
        int crcStartIndex = byteBuf.readableBytes() - 2;
        //读取数据计算crc
        byte[] data = new byte[crcStartIndex];
        byteBuf.readBytes(data);
        byte[] crc16 = CRC16Utils.getCRC16Bytes(data);
        byte[] crc16BytesCheck = new byte[2];
        //读取crc
        byteBuf.readBytes(crc16BytesCheck);
        //判断是否一致
        boolean pass = Arrays.equals(crc16, crc16BytesCheck);
        //重置
        byteBuf.resetReaderIndex();
        if (!pass) {
            throw new MqttException("bytes parse exception,crc check failed,crc value:" + Arrays.toString(crc16) + ",expect value:" + Arrays.toString(crc16BytesCheck));
        }
    }
}
