package com.github.netty.mqtt.client.support.future;

import com.github.netty.mqtt.client.support.util.AssertUtils;

import java.util.concurrent.TimeoutException;

/**
 * @Date: 2023/8/28 11:38
 * @Description: MqttFuture的包装类
 * @author: xzc-coder
 */
public class MqttFutureWrapper {

    private final MqttFuture mqttFuture;

    public MqttFutureWrapper(MqttFuture mqttFuture) {
        AssertUtils.notNull(mqttFuture, "mqttFuture is null");
        this.mqttFuture = mqttFuture;
    }

    /**
     * 阻塞等待至响应
     *
     * @throws InterruptedException 打断异常
     */
    public void awaitComplete() throws InterruptedException {
        mqttFuture.awaitComplete();
    }

    /**
     * 阻塞等待至超时
     *
     * @param timeout 超时时间 单位毫秒
     * @return 是否完成
     * @throws InterruptedException 打断异常
     */
    public boolean awaitComplete(long timeout) throws InterruptedException {
        return mqttFuture.awaitComplete(timeout);
    }

    /**
     * 阻塞等待值超时，并且忽略打断异常
     *
     * @return 是否完成
     */
    public boolean awaitCompleteUninterruptibly() {
        return mqttFuture.awaitCompleteUninterruptibly();
    }

    /**
     * 阻塞等待值超时时间，并且忽略打断异常
     *
     * @param timeout 超时时间 单位毫秒
     * @return 是否完成
     */
    public boolean awaitCompleteUninterruptibly(long timeout) {
        return mqttFuture.awaitCompleteUninterruptibly(timeout);
    }

    public MqttFuture sync() throws InterruptedException, TimeoutException {
        return mqttFuture.sync();
    }


    public MqttFuture sync(long timeout) throws InterruptedException, TimeoutException {
        return mqttFuture.sync(timeout);
    }


    /**
     * 阻塞等待至完成（如果失败则会抛出异常），忽略打断异常
     *
     * @return 响应结果
     */
    public MqttFuture syncUninterruptibly() {
        return mqttFuture.syncUninterruptibly();
    }

    /**
     * 阻塞等待至完成（如果失败则会抛出异常），忽略打断异常
     *
     * @return 响应结果
     */
    public MqttFuture syncUninterruptibly(long timeout) throws TimeoutException {
        return mqttFuture.syncUninterruptibly(timeout);
    }


    /**
     * 是否完成
     *
     * @return 是否被唤醒
     */
    public boolean isDone() {
        return mqttFuture.isDone();
    }

    /**
     * 是否执行成功
     *
     * @return 操作是否成功
     */
    public boolean isSuccess() {
        return mqttFuture.isSuccess();
    }

    /**
     * 获取失败异常
     *
     * @return 异常
     */
    public Throwable getCause() {
        return mqttFuture.getCause();
    }


    /**
     * 添加一个监听器
     *
     * @param listener 要添加的监听器
     */
    public void addListener(MqttFutureListener listener) {
        this.mqttFuture.addListener(listener);
    }

}
