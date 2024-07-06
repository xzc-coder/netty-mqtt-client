package com.github.netty.mqtt.client.connector;

import com.github.netty.mqtt.client.support.util.AssertUtils;
import io.netty.handler.codec.mqtt.MqttProperties;

import java.util.Arrays;

/**
 * 认证指示
 */
public class MqttAuthInstruct {


    public enum Instruct {
        /**
         * 重新认证
         */
        RE_AUTH,
        /**
         * 继续认证
         */
        AUTH_CONTINUE,
        /**
         * 认证失败
         */
        AUTH_FAIL;
    }

    /**
     * 认证指示，下一步要怎么操作
     */
    private final Instruct nextInstruct;
    /**
     * 认证数据
     */
    private byte[] authenticationData;
    /**
     * 如果认证失败的话，原因字符串
     */
    private String reasonString;
    /**
     * 用户属性
     */
    private final MqttProperties.UserProperties mqttUserProperties = new MqttProperties.UserProperties();

    public MqttAuthInstruct(Instruct nextInstruct) {
        this(nextInstruct, null);
    }

    public MqttAuthInstruct(Instruct nextInstruct, byte[] authenticationData) {
        AssertUtils.notNull(nextInstruct, "nextInstruct is null");
        this.nextInstruct = nextInstruct;
        this.authenticationData = authenticationData;
    }

    public byte[] getAuthenticationData() {
        return authenticationData;
    }


    public void setAuthenticationData(byte[] authenticationData) {
        this.authenticationData = authenticationData;
    }

    public Instruct getNextInstruct() {
        return nextInstruct;
    }

    public String getReasonString() {
        return reasonString;
    }

    public void setReasonString(String reasonString) {
        this.reasonString = reasonString;
    }

    public MqttProperties.UserProperties getMqttUserProperties() {
        return mqttUserProperties;
    }

    public void addMqttUserProperty(String key, String value) {
        if (key != null && value != null) {
            mqttUserProperties.add(key, value);
        }
    }

    public void addMqttUserProperty(MqttProperties.StringPair stringPair) {
        if (stringPair != null) {
            mqttUserProperties.add(stringPair);
        }
    }

    @Override
    public String toString() {
        return "MqttAuthInstruct{" +
                "nextInstruct=" + nextInstruct +
                ", authenticationData=" + Arrays.toString(authenticationData) +
                ", reasonString='" + reasonString + '\'' +
                ", mqttUserProperties=" + mqttUserProperties +
                '}';
    }
}
