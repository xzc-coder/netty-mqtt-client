package com.github.netty.mqtt.client;

import com.github.netty.mqtt.client.constant.MqttConstant;
import com.github.netty.mqtt.client.constant.MqttVersion;
import com.github.netty.mqtt.client.msg.MqttWillMsg;
import com.github.netty.mqtt.client.support.util.AssertUtils;

import java.io.File;
import java.io.Serializable;

/**
 * @Date: 2023/1/3 16:47
 * @Description: MQTT连接配置
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

    public long getRetryIntervalIncreaseMillis() {
        return retryIntervalIncreaseMillis;
    }

    public void setRetryIntervalIncreaseMillis(long retryIntervalIncreaseMillis) {
        if(retryIntervalIncreaseMillis >= 0) {
            this.retryIntervalIncreaseMillis = retryIntervalIncreaseMillis;
        }
    }

    public long getRetryIntervalMaxMillis() {
        return retryIntervalMaxMillis;
    }

    public void setRetryIntervalMaxMillis(long retryIntervalMaxMillis) {
        if(retryIntervalMaxMillis > 0) {
            this.retryIntervalMaxMillis = retryIntervalMaxMillis;
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
}
