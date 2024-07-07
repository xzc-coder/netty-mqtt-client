package io.github.netty.mqtt.client;

import io.github.netty.mqtt.client.connector.MqttAuthenticator;
import io.github.netty.mqtt.client.constant.MqttConstant;
import io.github.netty.mqtt.client.constant.MqttVersion;
import io.github.netty.mqtt.client.msg.MqttWillMsg;
import io.github.netty.mqtt.client.support.util.AssertUtils;
import io.netty.handler.codec.mqtt.MqttProperties;

import java.io.File;
import java.math.BigDecimal;
import java.util.Arrays;

/**
 * MQTT连接配置
 * @author: xzc-coder
 */
public class MqttConnectParameter {

    /**
     * 客户端ID，不能为null
     */
    private final String clientId;

    /**
     * MQTT版本
     */
    private MqttVersion mqttVersion = MqttConstant.DEFAULT_MQTT_VERSION;

    /**
     * 服务器Host，默认为 localhost
     */
    private String host = MqttConstant.DEFAULT_HOST;

    /**
     * 服务器端口，默认为 1883
     */
    private int port = MqttConstant.DEFAULT_PORT;
    /**
     * 连接认证时用户名
     */
    private String username;
    /**
     * 连接认证时密码
     */
    private char[] password;

    /**
     * 遗嘱消息
     */
    private MqttWillMsg willMsg;

    /**
     * 重试间隔基本值，第一次会以该间隔重试，然后增加递增值，达到最大时，使用最大值
     */
    private long retryIntervalMillis = MqttConstant.DEFAULT_RETRY_INTERVAL_MILLIS;

    /**
     * 重试间隔递增值，重试失败后增加的间隔值
     */
    private long retryIntervalIncreaseMillis = MqttConstant.DEFAULT_MSG_RETRY_INCREASE_MILLS;

    /**
     * 重试间隔的最大值
     */
    private long retryIntervalMaxMillis = MqttConstant.DEFAULT_MSG_RETRY_MAX_MILLS;

    /**
     * 心跳间隔，默认 30秒，如果设置了自动重连，也是自动重连间隔
     */
    private int keepAliveTimeSeconds = MqttConstant.DEFAULT_KEEP_ALIVE_TIME_SECONDS;

    /**
     * 心跳间隔的系数，默认0.75，执行心跳的定时任务会乘以该系数，因为网络传输有一定的间隔，特别是网络不好的情况，更需要该参数
     */
    private BigDecimal keepAliveTimeCoefficient = MqttConstant.DEFAULT_KEEP_ALIVE_TIME_COEFFICIENT;

    /**
     * 连接超时时间，默认 30秒
     */
    private long connectTimeoutSeconds = MqttConstant.DEFAULT_CONNECT_TIMEOUT_SECONDS;
    /**
     * 是否自动重连，默认 false
     */
    private boolean autoReconnect = MqttConstant.DEFAULT_AUTO_RECONNECT;

    /**
     * 是否清理会话，默认 true
     */
    private boolean cleanSession = MqttConstant.DEFAULT_CLEAR_SESSION;

    /**
     * ssl，默认 false
     */
    private boolean ssl;

    /**
     * 根证书
     * 如果服务器是权威CA颁发的证书，则不需要该证书文件;
     * 如果是自签名的证书，需要给自签名的证书授权，必须填入该证书文件
     */
    private File rootCertificateFile;
    /**
     * 客户端的私钥文件
     */
    private File clientPrivateKeyFile;
    /**
     * 客户端的证书文件
     */
    private File clientCertificateFile;

    /**
     * MQTT5
     * 会话过期间隔 单位 秒
     */
    private Integer sessionExpiryIntervalSeconds;

    /**
     * MQTT5
     * 认证方法
     */
    private String authenticationMethod;

    /**
     * MQTT5
     * 数据
     */
    private byte[] authenticationData;

    /**
     * MQTT5
     * 请求问题信息标识符
     */
    private Integer requestProblemInformation = MqttConstant.DEFAULT_REQUEST_PROBLEM_INFORMATION;

    /**
     * MQTT5
     * 请求响应标识符
     */
    private Integer requestResponseInformation = MqttConstant.DEFAULT_REQUEST_RESPONSE_INFORMATION;

    /**
     * MQTT5
     * 响应信息
     */
    private String responseInformation;

    /**
     * MQTT5
     * 接收最大数量
     */
    private Integer receiveMaximum;

    /**
     * MQTT5
     * 主题别名最大长度
     */
    private Integer topicAliasMaximum;

    /**
     * MQTT5
     * 最大报文长度
     */
    private Integer maximumPacketSize;

    /**
     * MQTT5
     * 用户属性
     */
    private final MqttProperties.UserProperties mqttUserProperties = new MqttProperties.UserProperties();

    /**
     * MQTT5
     * 认证器，当有认证方法时使用
     */
    private MqttAuthenticator mqttAuthenticator;

    public MqttConnectParameter(String clientId) {
        AssertUtils.notNull(clientId, "clientId is null");
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

    public int getKeepAliveTimeSeconds() {
        return keepAliveTimeSeconds;
    }

    public void setKeepAliveTimeSeconds(int keepAliveTimeSeconds) {
        if (keepAliveTimeSeconds > 0) {
            this.keepAliveTimeSeconds = keepAliveTimeSeconds;
        }
    }

    public long getConnectTimeoutSeconds() {
        return connectTimeoutSeconds;
    }

    public void setConnectTimeoutSeconds(long connectTimeoutSeconds) {
        if (connectTimeoutSeconds > 0) {
            this.connectTimeoutSeconds = connectTimeoutSeconds;
        }
    }

    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    public void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public void setPassword(String password) {
        if (password != null) {
            this.password = password.toCharArray();
        }
    }

    public boolean isCleanSession() {
        return cleanSession;
    }

    public void setCleanSession(boolean cleanSession) {
        this.cleanSession = cleanSession;
    }

    public MqttWillMsg getWillMsg() {
        return willMsg;
    }

    public void setWillMsg(MqttWillMsg willMsg) {
        this.willMsg = willMsg;
    }

    public boolean hasWill() {
        return willMsg != null;
    }


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        AssertUtils.notNull(host, "host is null");
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        if (port < 0 || port > MqttConstant.MAX_PORT) {
            throw new IllegalArgumentException("port out of range:" + port);
        }
        this.port = port;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public long getRetryIntervalMillis() {
        return retryIntervalMillis;
    }

    public void setRetryIntervalMillis(long retryIntervalMillis) {
        if (retryIntervalMillis > 0) {
            this.retryIntervalMillis = retryIntervalMillis;
        }
    }

    public MqttVersion getMqttVersion() {
        return mqttVersion;
    }

    public void setMqttVersion(MqttVersion mqttVersion) {
        if (mqttVersion != null) {
            this.mqttVersion = mqttVersion;
        }
    }

    public Integer getSessionExpiryIntervalSeconds() {
        return sessionExpiryIntervalSeconds;
    }

    public void setSessionExpiryIntervalSeconds(Integer sessionExpiryIntervalSeconds) {
        this.sessionExpiryIntervalSeconds = sessionExpiryIntervalSeconds;
    }

    public String getAuthenticationMethod() {
        return authenticationMethod;
    }

    public void setAuthenticationMethod(String authenticationMethod) {
        this.authenticationMethod = authenticationMethod;
    }

    public byte[] getAuthenticationData() {
        return authenticationData;
    }

    public void setAuthenticationData(byte[] authenticationData) {
        this.authenticationData = authenticationData;
    }

    public Integer getRequestProblemInformation() {
        return requestProblemInformation;
    }

    public void setRequestProblemInformation(Integer requestProblemInformation) {
        this.requestProblemInformation = requestProblemInformation;
    }

    public String getResponseInformation() {
        return responseInformation;
    }

    public void setResponseInformation(String responseInformation) {
        this.responseInformation = responseInformation;
    }

    public Integer getReceiveMaximum() {
        return receiveMaximum;
    }

    public void setReceiveMaximum(Integer receiveMaximum) {
        this.receiveMaximum = receiveMaximum;
    }

    public Integer getTopicAliasMaximum() {
        return topicAliasMaximum;
    }

    public void setTopicAliasMaximum(Integer topicAliasMaximum) {
        this.topicAliasMaximum = topicAliasMaximum;
    }

    public Integer getMaximumPacketSize() {
        return maximumPacketSize;
    }

    public void setMaximumPacketSize(Integer maximumPacketSize) {
        this.maximumPacketSize = maximumPacketSize;
    }


    public MqttProperties.UserProperties getMqttUserProperties() {
        return mqttUserProperties;
    }


    /**
     * MQTT5
     * 添加一个MQTT用户属性
     *
     * @param key   key
     * @param value value
     */
    public void addMqttUserProperty(String key, String value) {
        if (key != null && value != null) {
            mqttUserProperties.add(key, value);
        }
    }

    /**
     * MQTT5
     * 添加一个MQTT用户属性
     *
     * @param stringPair key value对象
     */
    public void addMqttUserProperty(MqttProperties.StringPair stringPair) {
        if (stringPair != null) {
            mqttUserProperties.add(stringPair);
        }
    }

    /**
     * MQTT5
     * 添加一个MQTT用户属性
     *
     * @param mqttUserProperties MQTT用户属性
     */
    private void addMqttUserProperties(MqttProperties.UserProperties mqttUserProperties) {
        if (mqttUserProperties != null) {
            for (MqttProperties.StringPair stringPair : mqttUserProperties.value()) {
                this.mqttUserProperties.add(stringPair);
            }
        }
    }

    public Integer getRequestResponseInformation() {
        return requestResponseInformation;
    }

    public void setRequestResponseInformation(Integer requestResponseInformation) {
        this.requestResponseInformation = requestResponseInformation;
    }

    public MqttAuthenticator getMqttAuthenticator() {
        return mqttAuthenticator;
    }

    public void setMqttAuthenticator(MqttAuthenticator mqttAuthenticator) {
        this.mqttAuthenticator = mqttAuthenticator;
    }

    public BigDecimal getKeepAliveTimeCoefficient() {
        return keepAliveTimeCoefficient;
    }

    public void setKeepAliveTimeCoefficient(BigDecimal keepAliveTimeCoefficient) {
        if (keepAliveTimeCoefficient != null && keepAliveTimeCoefficient.compareTo(BigDecimal.ZERO) > 0) {
            this.keepAliveTimeCoefficient = keepAliveTimeCoefficient;
        }
    }

    public long getRetryIntervalIncreaseMillis() {
        return retryIntervalIncreaseMillis;
    }

    public void setRetryIntervalIncreaseMillis(long retryIntervalIncreaseMillis) {
        this.retryIntervalIncreaseMillis = retryIntervalIncreaseMillis;
    }

    public long getRetryIntervalMaxMillis() {
        return retryIntervalMaxMillis;
    }

    public void setRetryIntervalMaxMillis(long retryIntervalMaxMillis) {
        this.retryIntervalMaxMillis = retryIntervalMaxMillis;
    }

    public File getRootCertificateFile() {
        return rootCertificateFile;
    }

    public void setRootCertificateFile(File rootCertificateFile) {
        this.rootCertificateFile = rootCertificateFile;
    }

    public File getClientPrivateKeyFile() {
        return clientPrivateKeyFile;
    }

    public void setClientPrivateKeyFile(File clientPrivateKeyFile) {
        this.clientPrivateKeyFile = clientPrivateKeyFile;
    }

    public File getClientCertificateFile() {
        return clientCertificateFile;
    }

    public void setClientCertificateFile(File clientCertificateFile) {
        this.clientCertificateFile = clientCertificateFile;
    }

    @Override
    public String toString() {
        return "MqttConnectParameter{" +
                "clientId='" + clientId + '\'' +
                ", mqttVersion=" + mqttVersion +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", username='" + username + '\'' +
                ", password=" + Arrays.toString(password) +
                ", willMsg=" + willMsg +
                ", retryIntervalMillis=" + retryIntervalMillis +
                ", retryIntervalIncreaseMillis=" + retryIntervalIncreaseMillis +
                ", retryIntervalMaxMillis=" + retryIntervalMaxMillis +
                ", keepAliveTimeSeconds=" + keepAliveTimeSeconds +
                ", keepAliveTimeCoefficient=" + keepAliveTimeCoefficient +
                ", connectTimeoutSeconds=" + connectTimeoutSeconds +
                ", autoReconnect=" + autoReconnect +
                ", cleanSession=" + cleanSession +
                ", ssl=" + ssl +
                ", rootCertificateFile=" + rootCertificateFile +
                ", clientPrivateKeyFile=" + clientPrivateKeyFile +
                ", clientCertificateFile=" + clientCertificateFile +
                ", sessionExpiryIntervalSeconds=" + sessionExpiryIntervalSeconds +
                ", authenticationMethod='" + authenticationMethod + '\'' +
                ", authenticationData=" + Arrays.toString(authenticationData) +
                ", requestProblemInformation=" + requestProblemInformation +
                ", requestResponseInformation=" + requestResponseInformation +
                ", responseInformation='" + responseInformation + '\'' +
                ", receiveMaximum=" + receiveMaximum +
                ", topicAliasMaximum=" + topicAliasMaximum +
                ", maximumPacketSize=" + maximumPacketSize +
                ", mqttUserProperties=" + mqttUserProperties +
                ", mqttAuthenticator=" + mqttAuthenticator +
                '}';
    }
}
