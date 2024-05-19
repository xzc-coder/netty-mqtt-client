package com.github.netty.mqtt.client.retry;

import com.github.netty.mqtt.client.constant.MqttConstant;
import com.github.netty.mqtt.client.support.future.MqttFuture;
import com.github.netty.mqtt.client.support.util.AssertUtils;
import io.netty.channel.EventLoopGroup;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @Date: 2023/8/28 11:31
 * @Description: MQTT消息重试器
 * @author: xzc-coder
 */
public class MqttRetrier {

    /**
     * 线程组
     */
    private final EventLoopGroup eventLoopGroup;

    public MqttRetrier(EventLoopGroup eventLoopGroup) {
        AssertUtils.notNull(eventLoopGroup, "eventLoopGroup is null");
        this.eventLoopGroup = eventLoopGroup;
    }

    /**
     * 重试
     * @param msgFuture Future，用来判断是否完成
     * @param intervalMills 间隔毫秒
     * @param task 要执行的任务
     * @param nowExecute 是否立即执行
     */
    public void retry(MqttFuture msgFuture, long intervalMills, Runnable task, boolean nowExecute) {
        if (!eventLoopGroup.isShutdown()) {
            if (nowExecute) {
                eventLoopGroup.execute(task);
            }
            eventLoopGroup.schedule(() -> {
                //如果没有完成，则执行任务
                if (!msgFuture.isDone()) {
                    task.run();
                    //下一次执行间隔
                    long nextDelayMills = intervalMills + MqttConstant.MSG_RETRY_INCREASE_MILLS;
                    if (nextDelayMills > MqttConstant.MSG_RETRY_MAX_MILLS) {
                        nextDelayMills = MqttConstant.MSG_RETRY_MAX_MILLS;
                    }
                    retry(msgFuture, nextDelayMills, task, false);
                }
            }, intervalMills, TimeUnit.MILLISECONDS);
        }
    }

}
