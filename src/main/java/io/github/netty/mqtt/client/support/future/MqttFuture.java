package io.github.netty.mqtt.client.support.future;

import io.github.netty.mqtt.client.support.util.AssertUtils;
import io.netty.util.internal.PlatformDependent;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeoutException;

/**
 * MqttFuture基类
 * @param <T> 响应结果类
 * @author: xzc-coder
 */
public abstract class MqttFuture<T> {

    /**
     * Future的Map
     */
    private static final Map<MqttFutureKey, MqttFuture> FUTURES = new ConcurrentHashMap();

    /**
     * future的ID，不能同时存在相同的ID
     */
    protected final MqttFutureKey futureKey;

    /**
     * 参数
     */
    protected final Object parameter;

    /**
     * 监听器
     */
    protected final Set<MqttFutureListener<T>> listeners = new CopyOnWriteArraySet<>();


    public MqttFuture(MqttFutureKey mqttFutureKey) {
        this(mqttFutureKey, null);
    }

    public MqttFuture(MqttFutureKey mqttFutureKey, Object parameter) {
        AssertUtils.notNull(mqttFutureKey, "mqttFutureKey is null");
        if (FUTURES.containsKey(mqttFutureKey)) {
            throw new IllegalStateException("client: " + mqttFutureKey.getClientId() + " ,key: " + mqttFutureKey.getKey() + " ,already exists");
        }
        FUTURES.put(mqttFutureKey, this);
        this.futureKey = mqttFutureKey;
        this.parameter = parameter;
    }


    public MqttFuture(String clientId, Object key) {
        this(clientId, key, null);
    }

    public MqttFuture(String clientId, Object key, Object parameter) {
        this(new MqttFutureKey(clientId, key), parameter);
    }


    public static MqttFuture getFuture(MqttFutureKey mqttFutureKey) {
        return FUTURES.get(mqttFutureKey);
    }

    public static MqttFuture getFuture(String clientId, Object key) {
        return FUTURES.get(new MqttFutureKey(clientId, key));
    }

    public MqttFutureKey getFutureKey() {
        return futureKey;
    }

    /**
     * 获取参数
     *
     * @return 参数
     */
    public Object getParameter() {
        return parameter;
    }

    /**
     * 移除一个Future
     *
     * @param clientId 客户端ID
     * @param key Future的key
     * @return 被移除的Future
     */
    public static MqttFuture removeFuture(String clientId, String key) {
        return FUTURES.remove(new MqttFutureKey(clientId, key));
    }

    /**
     * 阻塞等待至响应
     *
     * @throws InterruptedException 打断异常
     */
    public abstract void awaitComplete() throws InterruptedException;

    /**
     * 阻塞等待至超时
     *
     * @param timeout 超时时间 单位毫秒
     * @return 是否完成
     * @throws InterruptedException 打断异常
     */
    public abstract boolean awaitComplete(long timeout) throws InterruptedException;

    /**
     * 阻塞等待至超时，并且忽略打断异常
     *
     * @return 是否完成
     */
    public abstract boolean awaitCompleteUninterruptibly();

    /**
     * 阻塞等待至超时时间，并且忽略打断异常
     *
     * @param timeout 超时时间 单位毫秒
     * @return 是否完成
     */
    public abstract boolean awaitCompleteUninterruptibly(long timeout);

    /**
     * 设置响应结果并且唤醒之前等待的线程
     *
     * @param result 响应结果
     * @return 是否设置成功，如果先被超时唤醒，则返回 false，否则返回 true，不管结果如何，都会被唤醒。
     */
    public boolean setSuccess(T result) {
        try {
            return doSetSuccess(result);
        } finally {
            FUTURES.remove(this.futureKey);
        }
    }

    /**
     * 设值成功值
     *
     * @param result 值
     * @return 是否成功
     */
    protected abstract boolean doSetSuccess(T result);

    /**
     * 返回成功的结果
     *
     * @return 成功的结果
     */
    public abstract T getResult();

    /**
     * 设置执行失败并且唤醒之前等待的线程
     *
     * @param cause 异常原因
     * @return 操作是否成功
     */
    public boolean setFailure(Throwable cause) {
        try {
            return doSetFailure(cause);
        } finally {
            FUTURES.remove(this.futureKey);
        }
    }

    /**
     * 设置成功或者失败
     *
     * @param success 是否成功
     * @param result  成功时的结果
     * @param cause   失败时的异常信息
     * @return 操作是否成功
     */
    public boolean set(boolean success, T result, Throwable cause) {
        boolean operateSuccess = false;
        if (success) {
            operateSuccess = setSuccess(result);
        } else {
            operateSuccess = setFailure(cause);
        }
        return operateSuccess;
    }

    /**
     * 阻塞等待至完成（如果失败则会抛出异常）
     *
     * @return MqttFuture
     * @throws InterruptedException 打断异常
     * @throws TimeoutException 超时异常
     */
    public abstract MqttFuture sync() throws InterruptedException, TimeoutException;

    /**
     * 阻塞等待至完成（如果失败则会抛出异常）
     *
     * @param timeout 超时毫秒
     * @return MqttFuture
     * @throws InterruptedException 打断异常
     * @throws TimeoutException 超时异常
     */
    public abstract MqttFuture sync(long timeout) throws InterruptedException, TimeoutException;


    /**
     * 阻塞等待至完成（如果失败则会抛出异常），忽略打断异常
     *
     * @return MqttFuture
     */
    public abstract MqttFuture syncUninterruptibly();

    /**
     * 阻塞等待至完成（如果失败则会抛出异常），忽略打断异常
     *
     * @param timeout 超时的毫秒
     * @return MqttFuture
     * @throws TimeoutException 超时异常
     */
    public abstract MqttFuture syncUninterruptibly(long timeout) throws TimeoutException;

    /**
     * 设失败值
     *
     * @param cause 异常原因
     * @return 是否成功
     */
    protected abstract boolean doSetFailure(Throwable cause);

    /**
     * 是否完成
     *
     * @return 是否被唤醒
     */
    public abstract boolean isDone();

    /**
     * 是否执行成功
     *
     * @return 操作是否成功
     */
    public abstract boolean isSuccess();

    /**
     * 获取失败异常
     *
     * @return 异常
     */
    public abstract Throwable getCause();


    /**
     * 添加一个监听器
     *
     * @param listener 要添加的监听器
     */
    public abstract void addListener(MqttFutureListener<T> listener);

    /**
     * 移除一个监听器
     *
     * @param listener 要移除的监听器
     * @return 如果包含此listener则返回true
     */
    public abstract boolean removeListener(MqttFutureListener<T> listener);

    /**
     /**
     * 取消任务
     *
     * @return 取消操作是否成功
     * @throws Exception 异常
     */
    public abstract boolean cancel() throws Exception;

    /**
     * 是否取消成功
     *
     * @return 是否取消成功
     */
    public abstract boolean isCancelled();


    protected void rethrowIfFailed() {
        Throwable cause = getCause();
        if (cause == null) {
            return;
        }
        PlatformDependent.throwException(cause);
    }


}
