package io.github.netty.mqtt.client.support.future;

import io.github.netty.mqtt.client.support.util.AssertUtils;

import java.util.Objects;

/**
 * MqttFuture的Key
 * @author: xzc-coder
 */
public class MqttFutureKey {

    /**
     * 客户端ID
     */
    private final String clientId;
    /**
     * Future的key
     */
    protected final Object key;

    public MqttFutureKey(String clientId, Object key) {
        AssertUtils.notNull(clientId,"clientId is null");
        AssertUtils.notNull(key,"key is null");
        this.clientId = clientId;
        this.key = key;
    }

    public String getClientId() {
        return clientId;
    }

    public Object getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MqttFutureKey)) {
            return false;
        }
        MqttFutureKey that = (MqttFutureKey) o;
        return Objects.equals(clientId, that.clientId) && Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, key);
    }


    @Override
    public String toString() {
        return "MqttFutureKey{" +
                "clientId='" + clientId + '\'' +
                ", key=" + key +
                '}';
    }
}
