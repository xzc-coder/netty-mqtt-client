package io.github.netty.mqtt.client.constant;

import io.github.netty.mqtt.client.msg.MqttMsg;
import io.netty.util.AttributeKey;

import java.math.BigDecimal;
import java.util.Map;

/**
 * MQTT常量
 * @author: xzc-coder
 */
public class MqttConstant {

    private MqttConstant() {
    }


    public static final String AUTH_STATE_KEY = "NETTY_MQTT_AUTH_STATE_KEY";

    public static final String SEND_MSG_MAP_KEY = "NETTY_MQTT_SEND_SET_MAP_KEY";

    public static final String RECEIVE_MSG_MAP_KEY = "NETTY_MQTT_RECEIVE_SET_MAP_KEY";

    public static final String MQTT_CLIENT_ID_KEY = "NETTY_MQTT_MQTT_CLIENT_ID_KEY";

    public static final String KEEP_ALIVE_TIME_KEY = "NETTY_KEEP_ALIVE_TIME_KEY";

    public static final String TOPIC_ALIAS_MAP_KEY = "NETTY_TOPIC_ALIAS_MAP_KEY";

    public static final String DISCONNECT_REASON_CODE_KEY = "NETTY_DISCONNECT_REASON_CODE_KEY";


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
     * Channel中的MQTT活跃间隔，MQTT 5中是需要使用Broker返回的值
     */
    public static final AttributeKey<Integer> KEEP_ALIVE_TIME_ATTRIBUTE_KEY = AttributeKey.newInstance(MqttConstant.KEEP_ALIVE_TIME_KEY);

    /**
     * Channel中的MQTT主题别名映射Map，MQTT 5中使用
     */
    public static final AttributeKey<Map<String,Integer>> TOPIC_ALIAS_MAP_ATTRIBUTE_KEY = AttributeKey.newInstance(MqttConstant.TOPIC_ALIAS_MAP_KEY);

    /**
     * Channel中断开连接的reasonCode，如果是正常断开reasonCode则为0X00
     */
    public static final AttributeKey<Byte> DISCONNECT_REASON_CODE_ATTRIBUTE_KEY = AttributeKey.newInstance(DISCONNECT_REASON_CODE_KEY);

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
     * MQTT最小的主题长度
     */
    public static final int MQTT_MIN_TOPIC_LEN = 1;
    /**
     * MQTT最大的主题长度
     */
    public static final int MQTT_MAX_TOPIC_LEN = 65535;

    /**
     * MQTT多级通配符
     */
    public static final String MQTT_MULTI_LEVEL_WILDCARD = "#";

    /**
     * MQTT主题级别的分隔符
     */
    public static final String MQTT_TOPIC_LEVEL_SEPARATOR = "/";

    /**
     * MQTT单级通配符
     */
    public static final String MQTT_SINGLE_LEVEL_WILDCARD = "+";

    /**
     * MQTT默认的字符集编码
     */
    public static final String MQTT_DEFAULT_CHARACTER = "UTF-8";

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
     * 空字符
     */
    public static final char NUL = '\u0000';

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
    public static final long DEFAULT_RETRY_INTERVAL_MILLIS = 1000L;

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
     * 请求问题标识符
     */
    public static final int DEFAULT_REQUEST_PROBLEM_INFORMATION = 1;

    /**
     * 请求响应标识符
     */
    public static final int DEFAULT_REQUEST_RESPONSE_INFORMATION = 0;

    /**
     * 消息重试间隔，初始2000毫秒，每次失败+1000
     */
    public static final long MSG_RETRY_MILLS = 2000;
    /**
     * 默认的消息重试递增值
     */
    public static final long MSG_RETRY_INCREASE_MILLS = 1000;

    /**
     * 默认的消息重试最大时间
     */
    public static final long MSG_RETRY_MAX_MILLS = 15000;

    /**
     * 无效的消息ID值，用于qos 0的消息占位
     */
    public static final int INVALID_MSG_ID = 0;

    /**
     * Netty中的线程池名
     */
    public static final String THREAD_FACTORY_POOL_NAME = "netty-mqtt-client-eventLoop";

    /**
     * 空字符串
     */
    public static final String EMPTY_STR = "";


    public static final String NETTY_SSL_HANDLER_NAME = "sslHandler";

    public static final String NETTY_IDLE_HANDLER_NAME = "idleStateHandler";

    public static final String NETTY_DECODER_HANDLER_NAME = "mqttDecoder";

    public static final String NETTY_ENCODER_HANDLER_NAME = "mqttEncoder";

    public static final String NETTY_CHANNEL_HANDLER_NAME = "mqttChannelHandler";


    /**
     * 认证原因码：继续认证
     */
    public static final byte AUTH_CONTINUE_REASON_CODE = 0x18;
    /**
     * 认证原因码：重新认证
     */
    public static final byte AUTH_RE_AUTH_REASON_CODE = 0x19;

    /**
     * 取消订阅成功码
     */
    public static final short UNSUBSCRIPTION_SUCCESS_REASON_CODE = 0x00;

    /**
     * 消息成功码
     */
    public static final byte MESSAGE_SUCCESS_REASON_CODE = 0x00;

    /**
     * 正常断开连接成功码
     */
    public static final byte DISCONNECT_SUCCESS_REASON_CODE = 0x00;


}
