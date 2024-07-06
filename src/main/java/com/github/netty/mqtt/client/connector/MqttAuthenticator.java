package com.github.netty.mqtt.client.connector;

public interface MqttAuthenticator {

    /**
     * 继续认证（server返回成功则不会调用该方法，只有返回继续认证才会）
     *
     * @param authenticationMethod     认证方法
     * @param serverAuthenticationData 服务器返回的认证数据
     * @return 继续认证要传递的信息
     */
    MqttAuthInstruct authing(String authenticationMethod, byte[] serverAuthenticationData);
}
