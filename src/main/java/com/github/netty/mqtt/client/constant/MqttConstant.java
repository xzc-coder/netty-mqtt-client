package com.github.netty.mqtt.client.constant;

import com.github.netty.mqtt.client.msg.MqttMsg;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;

/**
 * @Date: 2021/12/24 10:51
 * @Description: MQTT常量
 * @author: xzc-coder
 */
public class MqttConstant {

    private MqttConstant() {
    }


    public static final String AUTH_STATE_KEY = "NETTY_MQTT_AUTH_STATE_KEY";

    public static final String SEND_MSG_MAP_KEY = "NETTY_MQTT_SEND_SET_MAP_KEY";

    public static final String RECEIVE_MSG_MAP_KEY = "NETTY_MQTT_RECEIVE_SET_MAP_KEY";

    public static final String MQTT_CLIENT_ID_KEY = "NETTY_MQTT_MQTT_CLIENT_ID_KEY";

    /**
     * Channel中的MQTT认证值
     */
    public static final AttributeKey<MqttAuthState> AUTH_STATE_ATTRIBUTE_KEY = AttributeKey.newInstance(MqttConstant.AUTH_STATE_KEY);
    /**
     * Channel中的发送消息存储MAP，存储清理会话的发布消息、订阅消息、取消订阅消息和不清理会话的订阅消息、取消订阅消息
     */
    public static final AttributeKey<Map<Integer, Object>> SEND_MSG_MAP_ATTRIBUTE_KEY = AttributeKey.newInstance(MqttConstant.SEND_MSG_MAP_KEY);
    /**
     * Channel中的接收消息存储MAP，存储清理会话的接收到的qos2的消息
     */
    public static final AttributeKey<Map<Integer, MqttMsg>> RECEIVE_MSG_MAP_ATTRIBUTE_KEY = AttributeKey.newInstance(MqttConstant.RECEIVE_MSG_MAP_KEY);
    /**
     * Channel中的MQTT的客户端ID
     */
    public static final AttributeKey<String> MQTT_CLIENT_ID_ATTRIBUTE_KEY = AttributeKey.newInstance(MqttConstant.MQTT_CLIENT_ID_KEY);
    /**
     * 最大端口号 65535,2个字节
     */
    public static final int MAX_PORT = 0xFFFF;

    /**
     * MQTT最小的消息ID值
     */
    public static final int MQTT_MIN_MSG_ID = 1;
    /**
     * MQTT最大的消息ID值
     */
    public static final int MQTT_MAX_MSG_ID = 65535;

    /**
     * MQTT最大的消息数量
     */
    public static final int MQTT_MAX_MSG_ID_NUMBER = 65535;

    /**
     * cglib创建的代理对象类名包含的内容
     */
    public static final String CGLIB_CONTAIN_CONTENT = "ByCGLIB$$";
    /**
     * jdk创建的代理对象类名包含的内容
     */
    public static final String JDK_PROXY_CONTAIN_CONTENT = "$Proxy";

    /**
     * 代理类型 cglib
     */
    public static final String PROXY_TYPE_CGLIB = "cglib";
    /**
     * 代理类型 jdk
     */
    public static final String PROXY_TYPE_JDK = "jdk";

    /**
     * MQTT版本默认值
     */
    public static final MqttVersion DEFAULT_MQTT_VERSION = MqttVersion.MQTT_3_1_1;

    /**
     * 连接默认超时时间（秒）
     */
    public static final int DEFAULT_CONNECT_TIMEOUT_SECONDS = 30;

    /**
     * 自动重连默认值
     */
    public static final boolean DEFAULT_AUTO_RECONNECT = false;

    /**
     * 重试毫秒间隔
     */
    public static final long DEFAULT_RETRY_INTERVAL_MILLIS = 1000;

    /**
     * 默认的消息重试递增值
     */
    public static final long DEFAULT_MSG_RETRY_INCREASE_MILLS = 1000;

    /**
     * 默认的消息重试最大时间
     */
    public static final long DEFAULT_MSG_RETRY_MAX_MILLS = 15000;

    /**
     * 默认心跳间隔
     */
    public static final int DEFAULT_KEEP_ALIVE_TIME_SECONDS = 30;

    /**
     * 默认的心跳间隔系数
     */
    public static final BigDecimal DEFAULT_KEEP_ALIVE_TIME_COEFFICIENT = new BigDecimal("0.75");

    /**
     * 默认清理会话
     */
    public static final boolean DEFAULT_CLEAR_SESSION = true;
    /**
     * Netty中的MQTT连接的响应码，转换为16进制
     */
    public static final int NETTY_MQTT_CONNECT_RETURN_CODE_RADIX = 16;

    /**
     * 默认的host
     */
    public static final String DEFAULT_HOST = "localhost";
    /**
     * 默认的端口
     */
    public static final int DEFAULT_PORT = 1883;


    /**
     * 无效的消息ID值，用于qos 0的消息占位
     */
    public static final int INVALID_MSG_ID = 0;

    /**
     * Netty中的线程池名
     */
    public static final String THREAD_FACTORY_POOL_NAME = "netty-mqtt-client-eventLoop";
    /**
     * Netty中添加的的SSL处理器名
     */
    public static final String NETTY_SSL_HANDLER_NAME = "sslHandler";
    /**
     * Netty中添加的的空闲检测处理器名
     */
    public static final String NETTY_IDLE_HANDLER_NAME = "idleStateHandler";
    /**
     * Netty中添加的的MQTT协议解码器名
     */
    public static final String NETTY_DECODER_HANDLER_NAME = "mqttDecoder";
    /**
     * Netty中添加的的MQTT协议编码器名
     */
    public static final String NETTY_ENCODER_HANDLER_NAME = "mqttEncoder";
    /**
     * Netty中添加的的MQTT处理器名
     */
    public static final String NETTY_CHANNEL_HANDLER_NAME = "mqttChannelHandler";
}
