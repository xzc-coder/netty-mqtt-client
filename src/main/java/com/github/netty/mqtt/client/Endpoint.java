package com.github.netty.mqtt.client;

import java.net.InetSocketAddress;
import java.net.URL;

/**
 * @Date: 2023/8/24 13:32
 * @Description: 端点
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
