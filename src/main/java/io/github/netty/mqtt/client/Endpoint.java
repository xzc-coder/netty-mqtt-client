package io.github.netty.mqtt.client;

import java.net.InetSocketAddress;
import java.net.URL;

/**
 * 端点，即客户端和服务端的连接信息
 * @author: xzc-coder
 */
public interface Endpoint {

    /**
     * 获取本机的地址，Channel是open时才有值
     *
     * @return InetSocketAddress
     */
    InetSocketAddress getLocalAddress();

    /**
     * 获取服务器的地址，Channel是open时才有值
     *
     * @return InetSocketAddress
     */
    InetSocketAddress getRemoteAddress();

}
