package com.github.netty.mqtt.client.msg;


import com.github.netty.mqtt.client.support.util.AssertUtils;
import io.netty.handler.codec.mqtt.MqttQoS;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @Date: 2023/8/26 10:05
 * @Description: MQTT遗嘱消息
 * @author: xzc-coder
 */
public class MqttWillMsg {

    /**
     * 遗嘱主题
     */
    private final String willTopic;
    /**
     * 遗嘱消息
     */
    private final byte[] willMessageBytes;
    /**
     * 遗嘱Qos
     */
    private final MqttQoS willQos;
    /**
     * 遗嘱消息是否保留
     */
    private final boolean isWillRetain;


    public MqttWillMsg(String willTopic, byte[] willMessageBytes, MqttQoS willQos) {
        this(willTopic, willMessageBytes, willQos, false);
    }

    public MqttWillMsg(String willTopic, byte[] willMessageBytes, MqttQoS willQos, boolean isWillRetain) {
        AssertUtils.notNull(willTopic, "willTopic is null");
        AssertUtils.notNull(willMessageBytes, "willMessageBytes is null");
        AssertUtils.notNull(willQos, "willQos is null");
        this.willTopic = willTopic;
        this.willMessageBytes = willMessageBytes;
        this.willQos = willQos;
        this.isWillRetain = isWillRetain;
    }


    public String getWillTopic() {
        return willTopic;
    }

    public byte[] getWillMessageBytes() {
        return willMessageBytes;
    }

    public MqttQoS getWillQos() {
        return willQos;
    }

    public boolean isWillRetain() {
        return isWillRetain;
    }


    @Override
    public String toString() {
        return "MqttWillMsg{" +
                "willTopic='" + willTopic + '\'' +
                ", willMessageBytes=" + Arrays.toString(willMessageBytes) +
                ", willQos=" + willQos +
                ", isWillRetain=" + isWillRetain +
                '}';
    }
}
